package com.pavila.service.impl;

import com.pavila.constants.ReviewConstants;
import com.pavila.dto.ReviewEvent;
import com.pavila.dto.ReviewRequestDTO;
import com.pavila.dto.ReviewResponseDTO;
import com.pavila.dto.ReviewUpdateDTO;
import com.pavila.entity.Review;
import com.pavila.exception.ResourceNotFoundException;
import com.pavila.mapper.ReviewMapper;
import com.pavila.repository.IReviewRepository;
import com.pavila.service.IReviewService;
import com.pavila.service.client.BookFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReviewServiceImpl implements IReviewService {

    private final IReviewRepository iReviewRepository;
    private final StreamBridge streamBridge;
    private final BookFeignClient bookFeignClient;

    @Transactional
    @Override
    public void createReview(ReviewRequestDTO reviewRequestDTO) {
        log.info("Attempting to create a new Review with data: {}", reviewRequestDTO);
        Boolean exists = bookFeignClient.exists(reviewRequestDTO.getBookId()).getBody();
        if (!Boolean.TRUE.equals(exists)) {
            throw new ResourceNotFoundException("Book not found with id: " + reviewRequestDTO.getBookId());
        }
        Review review = ReviewMapper.mapToEntity(reviewRequestDTO);
        review = iReviewRepository.save(review);
        log.info("Review saved successfully with ID: {}", review.getId());
        sendCommunication(review.getBookId(), ReviewConstants.REVIEW_CREATED);
    }


    @Transactional(readOnly = true)
    @Override
    public List<ReviewResponseDTO> findAllReviewsByBookId(Long bookId) {
        List<Review> reviews = iReviewRepository.findByBookId(bookId);
        return ReviewMapper.mapToReviewResponseDTOList(reviews);
    }

    @Transactional
    public void deleteReviewById(Long reviewId) {
        log.info("Attempting to delete book with ID: {}", reviewId);
        Review review = iReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));
        iReviewRepository.delete(review);
        log.info("Review deleted: {}", reviewId);
        sendCommunication(review.getBookId(), ReviewConstants.REVIEW_DELETED);
    }

    @Transactional
    public void updateReview(Long reviewId, ReviewUpdateDTO reviewUpdateDTO) {
        log.info("Attempting to update review with ID {} with data: {}", reviewId, reviewUpdateDTO);
        Review review = iReviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found with id: " + reviewId));

        boolean ratingChanged = !Objects.equals(review.getRating(), reviewUpdateDTO.getRating());
        boolean commentChanged = !Objects.equals(review.getComment(), reviewUpdateDTO.getComment());

        if (!ratingChanged && !commentChanged) {
            log.info("No changes detected in rating or comment for review ID: {}", reviewId);
            return;
        }

        if (commentChanged && ratingChanged) {
            log.info("Both comment and rating changed for review ID: {}", reviewId);
        }

        if (commentChanged) {
            review.setComment(reviewUpdateDTO.getComment());
            log.info("Comment changed for review ID: {}", reviewId);
        }

        if (ratingChanged) {
            review.setRating(reviewUpdateDTO.getRating());
            log.info("Rating changed for review ID: {}", reviewId);
            sendCommunication(review.getBookId(), ReviewConstants.RATING_UPDATE);
        }

        iReviewRepository.save(review);
        log.info("Review with ID {} updated successfully", reviewId);
    }

    @Transactional
    @Override
    public void deleteAllByBookId(Long bookId) {
        log.info("Deleting all reviews for bookId={}", bookId);
        iReviewRepository.deleteByBookId(bookId);
    }

    private void sendCommunication(Long bookId, String eventType) {
        var event = new ReviewEvent(bookId, eventType);
        log.info("Sending '{}' event for bookId: {}", eventType, bookId);
        boolean result = streamBridge.send("sendReviewEvent-out-0", event);
        log.info("Event '{}' sent successfully? {}", eventType, result);
    }

}
