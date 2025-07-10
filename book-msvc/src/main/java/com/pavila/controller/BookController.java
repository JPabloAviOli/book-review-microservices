package com.pavila.controller;

import com.pavila.constants.BookConstants;
import com.pavila.dto.*;
import com.pavila.service.IBookService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Tag(
        name = "CRUD REST APIs for Books",
        description = "CRUD REST APIs for managing books: Create, Read, Update and Delete book records"
)
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping( path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
public class BookController {

    private final IBookService iBookService;

    @Value("${build.version}")
    private String version;

    @Autowired
    private BookInfo bookInfo;

    @Operation(
            summary = "Create Book REST API",
            description = "REST API to create a new Book in the Book Management System"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "HTTP Status CREATED"),
            @ApiResponse(responseCode = "400", description = "HTTP Status BAD REQUEST",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "HTTP Status INTERNAL SERVER ERROR",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PostMapping("/books")
    public ResponseEntity<ResponseDTO> createBook(@Valid @RequestBody BookRequestDTO bookRequestDTO){
        iBookService.createBook(bookRequestDTO);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseDTO.builder()
                        .statusCode(BookConstants.STATUS_201)
                        .statusMsg(BookConstants.MESSAGE_201)
                        .build());
    }


    @Operation(
            summary = "Get All Books REST API",
            description = "REST API to retrieve all books from the Book Management System"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Books retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Books not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/books")
    public ResponseEntity<PaginatedBooksResponseDTO> findAll(@ParameterObject Pageable pageable){
        return ResponseEntity.ok().body(iBookService.findAllBook(pageable));
    }

    @Operation(
            summary = "Get Book By ID REST API",
            description = "REST API to retrieve a book by its ID from the Book Management System"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @GetMapping("/books/{id}")
    public ResponseEntity<BookDetailsDTO> fetchBookDetails(@PathVariable @Positive Long id){
        return ResponseEntity.ok().body(iBookService.findById(id));
    }


    @Operation(
            summary = "Update Book REST API",
            description = "REST API to update an existing book by its ID in the Book Management System"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Book updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @PutMapping("/books/{id}")
    public ResponseEntity<ResponseDTO> updateBook(@PathVariable @Positive Long id, @RequestBody BookRequestDTO bookRequestDTO){
        iBookService.updateBook(id, bookRequestDTO);
        return ResponseEntity
                .ok(ResponseDTO.builder()
                        .statusCode(BookConstants.STATUS_200)
                        .statusMsg(BookConstants.MESSAGE_200_UPDATE)
                        .build());
    }

    @Operation(
            summary = "Delete Book REST API",
            description = "REST API to delete a book by its ID from the Book Management System"
    )
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Book deleted successfully (No Content)"),
            @ApiResponse(responseCode = "404", description = "Book not found",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error",
                    content = @Content(schema = @Schema(implementation = ErrorResponseDTO.class)))
    })
    @DeleteMapping("/books/{id}")
    public ResponseEntity<?> deleteBook(@PathVariable @Positive Long id){
        iBookService.deleteById(id);
        return ResponseEntity
                .noContent().build();
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
    public ResponseEntity<BookInfo> getInfo(){
        return ResponseEntity.ok().body(bookInfo);
    }

    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> exists(@PathVariable Long id) {
        boolean exists = iBookService.existsById(id);
        return ResponseEntity.ok(exists);
    }


}
