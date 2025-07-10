package com.pavila;

import com.pavila.dto.BookInfo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
@EnableConfigurationProperties(value = {BookInfo.class})
public class BookMsvcApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookMsvcApplication.class, args);
	}

}
