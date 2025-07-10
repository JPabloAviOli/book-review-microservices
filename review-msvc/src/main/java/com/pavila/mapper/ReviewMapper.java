package com.pavila.mapper;

import com.pavila.dto.ReviewRequestDTO;
import com.pavila.dto.ReviewResponseDTO;
import com.pavila.entity.Review;

import java.util.List;

public class ReviewMapper {

    public static Review mapToEntity(ReviewRequestDTO reviewRequestDTO) {

        if(reviewRequestDTO == null){
            return null;
        }

        return Review.builder()
                .bookId(reviewRequestDTO.getBookId())
                .rating(reviewRequestDTO.getRating())
                .comment(reviewRequestDTO.getComment())
                .build();
    }

    public static List<ReviewResponseDTO> mapToReviewResponseDTOList(List<Review> reviews) {
        return reviews.stream().map(
                        review -> ReviewResponseDTO.builder()
                                .reviewId(review.getId())
                                .comment(review.getComment())
                                .rating(review.getRating())
                                .build()
                ).toList();

    }
}
