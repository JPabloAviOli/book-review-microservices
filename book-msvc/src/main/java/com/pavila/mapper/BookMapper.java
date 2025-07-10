package com.pavila.mapper;

import com.pavila.dto.BookRequestDTO;
import com.pavila.dto.BookResponseDTO;
import com.pavila.dto.PaginatedBooksResponseDTO;
import com.pavila.entity.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;

public class BookMapper {

    public static Book mapToEntity(BookRequestDTO bookRequestDTO) {

        if(bookRequestDTO == null){
            return null;
        }
        return Book.builder()
                .title(bookRequestDTO.getTitle())
                .title(bookRequestDTO.getTitle())
                .author(bookRequestDTO.getAuthor())
                .publicationYear(bookRequestDTO.getPublicationYear())
                .isbn(bookRequestDTO.getIsbn())
                .averageRating(0.0)
                .reviewCount(0)
                .build();
    }

    public static BookResponseDTO mapToDTO(Book bookEntity) {

        if(bookEntity == null){
            return null;
        }
        return BookResponseDTO.builder()
                .id(bookEntity.getId())
                .title(bookEntity.getTitle())
                .title(bookEntity.getTitle())
                .author(bookEntity.getAuthor())
                .publicationYear(bookEntity.getPublicationYear())
                .isbn(bookEntity.getIsbn())
                .averageRating(bookEntity.getAverageRating())
                .reviewCount(bookEntity.getReviewCount())
                .build();
    }



    public static Page<BookResponseDTO> mapToList(Page<Book> books) {
        if (books.isEmpty()) {
            return Page.empty(books.getPageable());
        }

        List<BookResponseDTO> bookDTOs = books.stream()
                .map(BookMapper::mapToDTO)
                .toList();

        return new PageImpl<>(bookDTOs, books.getPageable(), books.getTotalElements());
    }

    public static PaginatedBooksResponseDTO mapToPaginatedDTO(Page<Book> books) {
        List<BookResponseDTO> bookDTOs = books.stream()
                .map(BookMapper::mapToDTO)
                .toList();

        PaginatedBooksResponseDTO response = new PaginatedBooksResponseDTO();
        response.setContent(bookDTOs);
        response.setPage(books.getNumber());
        response.setSize(books.getSize());
        response.setTotalElements(books.getTotalElements());
        response.setTotalPages(books.getTotalPages());
        response.setHasNext(books.hasNext());
        response.setHasPrevious(books.hasPrevious());
        response.setFirst(books.isFirst());
        response.setLast(books.isLast());

        return response;
    }


}
