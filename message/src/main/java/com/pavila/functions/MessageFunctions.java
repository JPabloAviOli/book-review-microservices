package com.pavila.functions;

import com.pavila.dto.BookEvent;
import com.pavila.dto.ReviewEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class MessageFunctions {

    private static final Logger log = LoggerFactory.getLogger(MessageFunctions.class);

    @Bean
    public Function<ReviewEvent, ReviewEvent> reviewEventPublisher() {
        return event -> {
            log.info("Publishing review event '{}' for bookId: {}", event.eventType(), event.bookId());
            return event;
        };
    }

    @Bean
    public Function<BookEvent, BookEvent> bookDeletedEvent(){
        return event -> {
            log.info("Publishing book event '{}' for bookId: {}", event.eventType(), event.bookId());
            return event;
        };
    }
}
