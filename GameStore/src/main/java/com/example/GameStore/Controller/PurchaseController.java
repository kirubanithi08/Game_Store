package com.example.GameStore.Controller;


import com.example.GameStore.Service.PurchaseService;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;


import com.example.GameStore.Entity.User;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchases")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    /* ===============================
       BUY GAME
    =============================== */
    @PostMapping("/{gameId}")
    public void purchaseGame(
            @AuthenticationPrincipal User user,
            @PathVariable Long gameId
    ) {
        purchaseService.purchaseGame(user, gameId);
    }

    /* ===============================
       CHECK IF PURCHASED
    =============================== */
    @GetMapping("/exists/{gameId}")
    public boolean isPurchased(
            @AuthenticationPrincipal User user,
            @PathVariable Long gameId
    ) {
        return purchaseService.isPurchased(user, gameId);
    }

    /* ===============================
       USER LIBRARY
    =============================== */
    @GetMapping
    public Object myPurchases(
            @AuthenticationPrincipal User user
    ) {
        return purchaseService.getUserPurchases(user);
    }
}
