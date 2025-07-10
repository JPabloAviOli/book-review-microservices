package com.pavila.functions;

import com.pavila.dto.BookEvent;
import com.pavila.service.IReviewService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@Slf4j
public class ReviewFunctions {

    @Bean
    public Consumer<BookEvent> deleteReviewsFromDeletedBook(IReviewService iReviewService) {
        return event -> {
            Long bookId = event.bookId();
            String eventType = event.eventType();
            log.info("Received review event '{}' for bookId: {}", eventType, bookId);
            iReviewService.deleteAllByBookId(bookId);
        };
    }
}
