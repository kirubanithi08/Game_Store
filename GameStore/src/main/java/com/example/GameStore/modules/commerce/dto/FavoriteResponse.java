package com.example.GameStore.modules.commerce.dto;

import java.util.List;

public record FavoriteResponse(
    Long id,
    String name,
    String img,
    String cover,
    String description,
    int price,
    List<String> genres
) {}
