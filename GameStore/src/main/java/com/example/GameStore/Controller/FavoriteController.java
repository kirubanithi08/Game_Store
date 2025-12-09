package com.example.GameStore.Controller;

import com.example.GameStore.Entity.Game;
import com.example.GameStore.Service.FavoriteService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping("/{gameId}")
    public ResponseEntity<?> addFavorite(@PathVariable Long gameId) {
        return ResponseEntity.ok(favoriteService.addFavorite(gameId));
    }

    @DeleteMapping("/{gameId}")
    public ResponseEntity<?> removeFavorite(@PathVariable Long gameId) {
        return ResponseEntity.ok(favoriteService.removeFavorite(gameId));
    }

    @GetMapping
    public ResponseEntity<?> getFavorites(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {

        return ResponseEntity.ok(favoriteService.getFavorites(page, size));
    }


    @GetMapping("/exists/{gameId}")
    public ResponseEntity<Boolean> isFavorited(@PathVariable Long gameId) {
        return ResponseEntity.ok(favoriteService.isFavorited(gameId));
    }


}
