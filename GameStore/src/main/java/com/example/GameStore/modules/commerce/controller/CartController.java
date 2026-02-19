package com.example.GameStore.modules.commerce.controller;

import com.example.GameStore.modules.commerce.dto.CartResponse;
import com.example.GameStore.modules.commerce.service.CartService;
import com.example.GameStore.modules.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/{gameId}")
    public ResponseEntity<ApiResponse<Void>> addCart(@PathVariable Long gameId) {
        cartService.addCart(gameId);
        return ResponseEntity.ok(ApiResponse.success("Game added to cart!"));
    }

    @DeleteMapping("/{gameId}")
    public ResponseEntity<ApiResponse<Void>> removeCart(@PathVariable Long gameId) {
        cartService.removeCart(gameId);
        return ResponseEntity.ok(ApiResponse.success("Game removed from cart!"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<CartResponse>>> getCart(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(cartService.getCart(page, size)));
    }

    @GetMapping("/exists/{gameId}")
    public ResponseEntity<ApiResponse<Boolean>> isCart(@PathVariable Long gameId) {
        return ResponseEntity.ok(ApiResponse.success(cartService.isCart(gameId)));
    }
}
