package com.pavila.controller;

import com.pavila.constants.ReviewConstants;
import com.pavila.dto.*;
import com.pavila.service.IReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Tag(
        name = "CRUD REST APIs for Reviews",
        description = "CRUD REST APIs for managing Reviews: Create, Read, Update and Delete review records"
)
@Validated
@RefreshScope
@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@RequiredArgsConstructor
public class ReviewController {

    private final IReviewService iReviewService;

    @Value("${build.version}")
    private String version;
    @Value("${books.message}")
    private String message;

    @Operation(
            summary = "Create Review REST API",
            description = "REST API to create a new Review in the Review Management System"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "HTTP Status CREATED"),
            @ApiResponse(responseCode = "400", description = "HTTP Status BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "HTTP Status INTERNAL SERVER ERROR",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/reviews")
    public ResponseEntity<ResponseDTO> createReview(@Valid @RequestBody
                                                        ReviewRequestDTO reviewRequestDTO){
        iReviewService.createReview(reviewRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDTO.builder()
                        .statusCode(ReviewConstants.STATUS_201)
                        .statusMsg(ReviewConstants.MESSAGE_201)
                        .build());
    }


    @Operation(
            summary = "Get All Reviews by book id REST API",
            description = "REST API to retrieve all reviews from the Review Management System"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Reviews retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Reviews not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/reviews/{bookId}")
    public ResponseEntity<List<ReviewResponseDTO>> findAllReviewsByBookId(@Positive @PathVariable Long bookId){
        return ResponseEntity.ok(iReviewService.findAllReviewsByBookId(bookId));
    }


    @Operation(
            summary = "Get Build information",
            description = "Get Build information that is deployed into books microservice"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "HTTP Status OK"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "HTTP Status Internal Server Error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDTO.class)
                    )
            )
    }
    )
    @GetMapping("/info")
    public ResponseEntity<String> getInfo(){
        return ResponseEntity.ok(this.message + " - " + this.version);
    }

    @Operation(
            summary = "Delete Review REST API",
            description = "REST API to delete a review by its ID from the Review Management System"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Review deleted successfully (No Content)"),
            @ApiResponse(responseCode = "404", description = "Review not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @DeleteMapping("/reviews/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable @Positive Long id){
        iReviewService.deleteReviewById(id);
        return ResponseEntity
                .noContent().build();
    }


    @Operation(
            summary = "Update Review REST API",
            description = "REST API to update an existing book by its ID in the Book Management System"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Review updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Review not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PutMapping("/reviews/{id}")
    public ResponseEntity<ResponseDTO> updateReview(@PathVariable @Positive Long id, @Valid @RequestBody ReviewUpdateDTO reviewUpdateDTO){
        iReviewService.updateReview(id, reviewUpdateDTO);
        return ResponseEntity
                .ok(ResponseDTO.builder()
                        .statusCode(ReviewConstants.STATUS_200)
                        .statusMsg(ReviewConstants.MESSAGE_200_UPDATE)
                        .build());
    }
}
