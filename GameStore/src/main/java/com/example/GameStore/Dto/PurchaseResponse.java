package com.example.GameStore.Dto;

import com.example.GameStore.Entity.Purchase;
import com.example.GameStore.Entity.PurchaseStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PurchaseResponse {

    private Long id;
    private Long gameId;
    private String gameName;
    private Double price;
    private LocalDateTime purchasedAt;
    private PurchaseStatus status;

    public PurchaseResponse(Purchase purchase) {
        this.id = purchase.getId();
        this.gameId = purchase.getGame().getId();
        this.gameName = purchase.getGame().getName();
        this.price = purchase.getPrice();
        this.purchasedAt = purchase.getPurchasedAt();
        this.status = purchase.getStatus();
    }
}

