package com.pavila.service.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "books", path = "/api")
public interface BookFeignClient {

    @GetMapping( value = "/{id}/exists", consumes = "application/json")
    ResponseEntity<Boolean> exists(@PathVariable Long id);
}
