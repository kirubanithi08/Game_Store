package com.example.GameStore.modules.commerce.controller;

import com.example.GameStore.modules.commerce.dto.FavoriteResponse;
import com.example.GameStore.modules.commerce.service.FavoriteService;
import com.example.GameStore.modules.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("/{gameId}")
    public ResponseEntity<ApiResponse<Void>> addFavorite(@PathVariable Long gameId) {
        favoriteService.addFavorite(gameId);
        return ResponseEntity.ok(ApiResponse.success("Game added to favorites!"));
    }

    @DeleteMapping("/{gameId}")
    public ResponseEntity<ApiResponse<Void>> removeFavorite(@PathVariable Long gameId) {
        favoriteService.removeFavorite(gameId);
        return ResponseEntity.ok(ApiResponse.success("Game removed from favorites!"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<FavoriteResponse>>> getFavorites(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return ResponseEntity.ok(ApiResponse.success(favoriteService.getFavorites(page, size)));
    }

    @GetMapping("/exists/{gameId}")
    public ResponseEntity<ApiResponse<Boolean>> isFavorited(@PathVariable Long gameId) {
        return ResponseEntity.ok(ApiResponse.success(favoriteService.isFavorited(gameId)));
    }
}
