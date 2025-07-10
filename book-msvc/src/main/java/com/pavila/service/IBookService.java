package com.pavila.service;

import com.pavila.dto.BookDetailsDTO;
import com.pavila.dto.BookRequestDTO;
import com.pavila.dto.BookResponseDTO;
import com.pavila.dto.PaginatedBooksResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IBookService {

    void createBook(BookRequestDTO bookRequestDTO);
    BookDetailsDTO findById(Long id);
    void updateBook(Long id, BookRequestDTO bookRequestDTO);
    void deleteById(Long id);
    void updateBookRatingAndCount(Long id);
    PaginatedBooksResponseDTO findAllBook(Pageable pageable);
    boolean existsById(Long id);
    void updateBookRating(Long bookId);
}
