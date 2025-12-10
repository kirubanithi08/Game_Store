
package com.example.GameStore.Controller;

import com.example.GameStore.Dto.FavoriteGameDTO;
import com.example.GameStore.Service.FavoriteService;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @PostMapping("/{gameId}")
    public String addFavorite(@PathVariable Long gameId) {
        return favoriteService.addFavorite(gameId);
    }

    @DeleteMapping("/{gameId}")
    public String removeFavorite(@PathVariable Long gameId) {
        return favoriteService.removeFavorite(gameId);
    }

    @GetMapping
    public Page<FavoriteGameDTO> getFavorites(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {
        return favoriteService.getFavorites(page, size);
    }

    @GetMapping("/exists/{gameId}")
    public boolean isFavorited(@PathVariable Long gameId) {
        return favoriteService.isFavorited(gameId);
    }
}
