package com.example.GameStore.modules.catalog.dto;

import java.util.List;

public record GameRequest(
    String name,
    String img,
    String cover,
    String description,
    int price,
    boolean featured,
    List<Long> genres
) {}
