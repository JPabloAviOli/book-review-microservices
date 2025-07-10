package com.pavila.service.client;

import com.pavila.dto.ReviewResponseDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(value = "reviews", path = "/api")
public interface ReviewFeignClient {

    @GetMapping( value = "/reviews/{bookId}", consumes = "application/json")
    ResponseEntity<List<ReviewResponseDTO>> findAllReviewsByBookId(@PathVariable Long bookId);
}
