package com.example.GameStore.modules.catalog.controller;

import com.example.GameStore.modules.catalog.dto.GameRequest;
import com.example.GameStore.modules.catalog.entity.Game;
import com.example.GameStore.modules.catalog.service.GameService;
import com.example.GameStore.modules.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@RequiredArgsConstructor
public class GameController {

    private final GameService gameService;

    @PostMapping
    public ResponseEntity<ApiResponse<Game>> createGame(@RequestBody GameRequest request) {
        Game game = gameService.createGame(request);
        return ResponseEntity.ok(ApiResponse.success(game, "Game created successfully"));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Game>>> getGames(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(ApiResponse.success(gameService.getGames(page, size)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Game>> getGameById(@PathVariable Long id) {
        return gameService.getGameById(id)
                .map(game -> ResponseEntity.ok(ApiResponse.success(game)))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Game>> updateGame(@PathVariable Long id, @RequestBody GameRequest request) {
        Game game = gameService.updateGame(id, request);
        return ResponseEntity.ok(ApiResponse.success(game, "Game updated successfully"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteGame(@PathVariable Long id) {
        gameService.deleteGame(id);
        return ResponseEntity.ok(ApiResponse.success("Game deleted successfully"));
    }

    @GetMapping("/featured")
    public ResponseEntity<ApiResponse<List<Game>>> getFeaturedGames() {
        return ResponseEntity.ok(ApiResponse.success(gameService.getFeaturedGames()));
    }

    @GetMapping("/trending")
    public ResponseEntity<ApiResponse<List<Game>>> getTrendingGames(@RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(ApiResponse.success(gameService.getTrendingGames(limit)));
    }

    @GetMapping("/new")
    public ResponseEntity<ApiResponse<List<Game>>> getNewGames(@RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(ApiResponse.success(gameService.getNewGames(limit)));
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Game>>> searchGames(@RequestParam String query) {
        return ResponseEntity.ok(ApiResponse.success(gameService.searchGames(query)));
    }
}
