package com.pavila;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class ReviewMsvcApplication {

	public static void main(String[] args) {
		SpringApplication.run(ReviewMsvcApplication.class, args);
	}

}
