package com.example.GameStore.Dto;

import lombok.Data;
import java.util.List;

@Data
public class PurchaseDTO {
    private Long id;
    private String name;
    private String img;
    private String cover;
    private String description;
    private int price;
    List<Long> genres;
}
