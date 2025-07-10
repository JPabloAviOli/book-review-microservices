package com.pavila.repository;

import com.pavila.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByBookId(Long bookId);
    void deleteByBookId(Long bookId);
}
