package com.pavila.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Getter
@ToString
@Setter
@AllArgsConstructor
@Schema(
        name = "BookRequestDTO",
        description = "Schema representing the request data to create or update a book"
)
public class BookRequestDTO {

    @Schema(
            description = "Title of the book",
            example = "Effective Java"
    )
    @NotBlank(message = "Title must not be blank")
    @Size(max = 100, message = "Title must not exceed 100 characters")
    private String title;

    @Schema(
            description = "Author of the book",
            example = "Joshua Bloch"
    )
    @NotBlank(message = "Author must not be blank")
    @Size(max = 50, message = "Author must not exceed 50 characters")
    private String author;

    @Schema(
            description = "Year the book was published (between 1900 and 2099)",
            example = "2018"
    )
    @NotBlank(message = "Publication year must not be blank")
    @Pattern(
            regexp = "^(19|20)\\d{2}$",
            message = "Publication year must be a valid year between 1900 and 2099"
    )
    private String publicationYear;

    @Schema(
            description = "ISBN number of the book (10 or 13 digits)",
            example = "9780134685991"
    )
    @NotBlank(message = "ISBN must not be blank")
    @Pattern(
            regexp = "^(?:\\d{10}|\\d{13})$",
            message = "ISBN must be 10 or 13 digits"
    )
    private String isbn;
}
