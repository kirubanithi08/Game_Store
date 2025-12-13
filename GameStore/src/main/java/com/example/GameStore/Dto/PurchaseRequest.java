package com.example.GameStore.Dto;

import lombok.Data;

@Data
public class PurchaseRequest {
    private Long gameId;
    private String paymentMethod;
}

