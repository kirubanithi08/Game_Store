package com.example.GameStore.Dto;


import lombok.Data;

import java.util.List;

@Data
public class CartDTO {
    private Long id;
    private String name;
    private String img;
    private String cover;
    private String description;
    private int price;
    List<Long> genres;
}

