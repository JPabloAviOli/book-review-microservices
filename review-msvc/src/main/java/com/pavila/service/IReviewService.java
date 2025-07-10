package com.pavila.service;

import com.pavila.dto.ReviewRequestDTO;
import com.pavila.dto.ReviewResponseDTO;
import com.pavila.dto.ReviewUpdateDTO;
import jakarta.validation.constraints.Positive;

import java.util.List;

public interface IReviewService {
    void createReview(ReviewRequestDTO reviewRequestDTO);
    List<ReviewResponseDTO> findAllReviewsByBookId(Long bookId);
    void deleteReviewById(Long id);
    void updateReview(Long id, ReviewUpdateDTO reviewUpdateDTO);
    void deleteAllByBookId(Long bookId);
}
