package com.pavila.service.impl;

import com.pavila.constants.BookConstants;
import com.pavila.dto.*;
import com.pavila.entity.Book;
import com.pavila.exception.ResourceNotFoundException;
import com.pavila.mapper.BookMapper;
import com.pavila.repository.IBookRepository;
import com.pavila.service.IBookService;
import com.pavila.service.client.ReviewFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImpl implements IBookService {

    private final IBookRepository bookRepository;
    private final ReviewFeignClient reviewFeignClient;
    private final StreamBridge streamBridge;

    @Transactional
    @Override
    public void createBook(BookRequestDTO bookRequestDTO) {
        log.info("Attempting to create a new Book with data: {}", bookRequestDTO);
        Book book = BookMapper.mapToEntity(bookRequestDTO);
        book = bookRepository.save(book);
        log.info("Book saved successfully with ID: {}", book.getId());
    }

    @Transactional(readOnly = true)
    @Override
    public PaginatedBooksResponseDTO findAllBook(Pageable pageable) {
        log.info("Fetching books with pagination: page={}, size={}", pageable.getPageNumber(), pageable.getPageSize());

        Page<Book> books = bookRepository.findAll(pageable);

        if (books.isEmpty()) {
            log.warn("No books found for the given pagination.");
            throw new ResourceNotFoundException("No books found.");
        }

        PaginatedBooksResponseDTO response = BookMapper.mapToPaginatedDTO(books);
        log.info("Found {} books", response.getTotalElements());

        return response;
    }


    @Transactional(readOnly = true)
    @Override
    public BookDetailsDTO findById(Long id) {
        log.info("Attempting to find book with ID: {}", id);

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Book not found while trying to retrieve ID: {}", id);
                    return new ResourceNotFoundException("Book not found with id: " + id);
                });

        log.info("Book found: {}", book.getId());

        BookResponseDTO bookDTO = BookMapper.mapToDTO(book);
        ResponseEntity<List<ReviewResponseDTO>> reviewsDTO = reviewFeignClient.findAllReviewsByBookId(id);

        BookDetailsDTO bookDetailsDTO = new BookDetailsDTO();
        bookDetailsDTO.setBook(bookDTO);
        bookDetailsDTO.setReviews(reviewsDTO.getBody());
        return bookDetailsDTO;

    }

    @Transactional
    @Override
    public void updateBook(Long id, BookRequestDTO bookRequestDTO) {
        log.info("Attempting to update book with ID: {} with data {}", id, bookRequestDTO);

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Book not found while attempting to update ID: {}", id);
                    return new ResourceNotFoundException("Book not found with id: " + id);
                });

        log.debug("Update data received: {}", bookRequestDTO);

        BeanUtils.copyProperties(bookRequestDTO, book);
        bookRepository.save(book);

        log.info("Book with ID: {} updated successfully", id);
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        log.info("Attempting to delete book with ID: {}", id);

        Book book = bookRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Book not found while attempting to delete ID: {}", id);
                    return new ResourceNotFoundException("Book not found with id: " + id);
                });

        bookRepository.delete(book);

        log.info("Book with ID: {} deleted successfully", id);
        sendCommunication(id, BookConstants.BOOK_DELETED);
    }

    private void sendCommunication(Long id, String eventType) {
        var event = new BookEvent(id, eventType);
        log.info("Sending '{}' event for bookId: {}", eventType, id);
        boolean result = streamBridge.send("sendBookEvent-out-0", event);
        log.info("Event '{}' sent successfully? {}", eventType, result);
    }

    @Transactional
    @Override
    public void updateBookRatingAndCount(Long bookId) {
        log.info("Starting update of averageRating and reviewCount for bookId: {}", bookId);
        ResponseEntity<List<ReviewResponseDTO>> response = reviewFeignClient.findAllReviewsByBookId(bookId);
        List<ReviewResponseDTO> reviews = response.getBody();

        int count = reviews != null ? reviews.size() : 0;

        double average = (reviews != null && !reviews.isEmpty()) ?
                BigDecimal.valueOf(
                        reviews.stream()
                                .mapToInt(ReviewResponseDTO::getRating)
                                .average()
                                .orElse(0.0)
                ).setScale(1, RoundingMode.HALF_UP).doubleValue()
                : 0.0;
        log.info("Calculated averageRating: {} and reviewCount: {} for bookId: {}", average, count, bookId);

        Book book = bookRepository.findById(bookId)
                .orElseThrow( () -> new ResourceNotFoundException("Book not found with id: " + bookId));

        book.setAverageRating(average);
        book.setReviewCount(count);
        bookRepository.save(book);
        log.info("Book with id {} updated successfully with new averageRating: {} and reviewCount: {}",
                bookId, average, count);
    }

    @Transactional(readOnly = true)
    @Override
    public boolean existsById(Long id) {
        return bookRepository.existsById(id);
    }

    @Transactional
    @Override
    public void updateBookRating(Long bookId) {
        log.info("Starting update of average rating for bookId: {}", bookId);

        ResponseEntity<List<ReviewResponseDTO>> response = reviewFeignClient.findAllReviewsByBookId(bookId);
        List<ReviewResponseDTO> reviews = response.getBody();

        double average = (reviews != null && !reviews.isEmpty()) ?
                BigDecimal.valueOf(
                        reviews.stream()
                                .mapToInt(ReviewResponseDTO::getRating)
                                .average()
                                .orElse(0.0)
                ).setScale(1, RoundingMode.HALF_UP).doubleValue()
                : 0.0;

        log.info("Calculated average rating: {} for bookId: {}", average, bookId);

        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found with id: " + bookId));

        book.setAverageRating(average);
        bookRepository.save(book);

        log.info("Book with id {} updated successfully with new average rating: {}", bookId, average);
    }

}
