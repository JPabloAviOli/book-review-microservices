package com.pavila.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@Builder
@Schema(
        name = "BookResponseDTO",
        description = "Representation of a book including title, author, publication year, and ISBN"
)
public class BookResponseDTO {
    @Schema(
            description = "Unique identifier of the book",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Title of the book",
            example = "Effective Java"
    )
    private String title;

    @Schema(
            description = "Author of the book",
            example = "Joshua Bloch"
    )
    private String author;

    @Schema(
            description = "Year the book was published",
            example = "2018"
    )
    private String publicationYear;

    @Schema(
            description = "ISBN number of the book (10 or 13 digits)",
            example = "9780134685991"
    )
    private String isbn;

    @Schema(
            description = "Average rating (1 to 5) based on all reviews for the book",
            example = "4.5"
    )
    private Double averageRating;

    @Schema(
            description = "Total number of reviews the book has received",
            example = "25"
    )
    private Integer reviewCount;
}
