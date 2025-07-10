package com.pavila.dto;

import lombok.Data;

import java.util.List;

@Data
public class BookDetailsDTO {
    private BookResponseDTO book;
    private List<ReviewResponseDTO> reviews;
}
