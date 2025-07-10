package com.pavila.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ReviewUpdateDTO {

    @Min(1)
    @Max(5)
    @Schema(
            description = "Rating of the book from 1 to 5",
            example = "5"
    )
    private Integer rating;

    @NotBlank(message = "Comment must not be blank")
    @Size(max = 100, message = "Comment must not exceed 100 characters")
    @Schema(
            description = "User's comment about the book (max 100 characters)",
            example = "An excellent and practical guide to Java programming."
    )
    private String comment;
}
