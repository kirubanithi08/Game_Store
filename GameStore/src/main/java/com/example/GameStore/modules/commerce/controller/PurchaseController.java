package com.example.GameStore.modules.commerce.controller;

import com.example.GameStore.modules.commerce.dto.PurchaseResponse;
import com.example.GameStore.modules.commerce.service.PurchaseService;
import com.example.GameStore.modules.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/purchase")
@RequiredArgsConstructor
public class PurchaseController {

    private final PurchaseService purchaseService;

    @PostMapping("/{gameId}")
    public ResponseEntity<ApiResponse<Void>> addPurchase(@PathVariable Long gameId) {
        purchaseService.addPurchase(gameId);
        return ResponseEntity.ok(ApiResponse.success("Game purchased successfully!"));
    }

    @DeleteMapping("/{gameId}")
    public ResponseEntity<ApiResponse<Void>> removePurchase(@PathVariable Long gameId) {
        purchaseService.removePurchase(gameId);
        return ResponseEntity.ok(ApiResponse.success("Game removed from purchased!"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<PurchaseResponse>>> getPurchase(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(purchaseService.getPurchase(page, size)));
    }

    @GetMapping("/exists/{gameId}")
    public ResponseEntity<ApiResponse<Boolean>> isPurchased(@PathVariable Long gameId) {
        return ResponseEntity.ok(ApiResponse.success(purchaseService.isPurchased(gameId)));
    }
}
