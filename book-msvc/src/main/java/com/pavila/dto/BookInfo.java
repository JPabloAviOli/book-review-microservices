package com.pavila.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "books")
@Setter
@Getter
public class BookInfo {
    private String message;
    private String email;
}
