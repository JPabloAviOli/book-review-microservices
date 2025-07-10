package com.pavila.functions;

import com.pavila.dto.ReviewEvent;
import com.pavila.service.IBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@Slf4j
public class BookFunctions {

    @Bean
    public Consumer<ReviewEvent> updateBookFromReviewEvent(IBookService iBookService) {
        return event -> {
            Long bookId = event.bookId();
            String eventType = event.eventType();

            log.info("Received review event '{}' for bookId: {}", eventType, bookId);

            switch (eventType) {
                case "REVIEW_CREATED":
                case "REVIEW_DELETED":
                    iBookService.updateBookRatingAndCount(bookId);
                    break;

                case "RATING_UPDATE":
                    iBookService.updateBookRating(bookId);
                    break;

                default:
                    log.warn("Unhandled event type '{}' for bookId: {}", eventType, bookId);
            }
        };
    }
}
