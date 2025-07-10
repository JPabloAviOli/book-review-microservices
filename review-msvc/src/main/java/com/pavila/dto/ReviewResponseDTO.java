package com.pavila.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ReviewResponseDTO {
    private Long reviewId;
    private Integer rating;
    private String comment;
}
