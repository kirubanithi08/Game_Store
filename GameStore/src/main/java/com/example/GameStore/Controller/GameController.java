package com.example.GameStore.Controller;

import com.example.GameStore.Dto.GameRequest;
import com.example.GameStore.Entity.Game;
import com.example.GameStore.Service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.List;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    public ResponseEntity<Game> createGame(@RequestBody GameRequest request) {
        try {
            Game game = gameService.createGame(request);
            return ResponseEntity.ok(game);
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to create game: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping
    public ResponseEntity<List<Game>> getGames(@RequestParam(defaultValue = "0") int page,
                                               @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(gameService.getGames(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Game> getGameById(@PathVariable Long id) {
        Game game = gameService.getGameById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));
        return ResponseEntity.ok(game);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Game> updateGame(@PathVariable Long id, @RequestBody GameRequest request) {
        try {
            return ResponseEntity.ok(gameService.updateGame(id, request));
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found");
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to update game: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable Long id) {
        try {
            gameService.deleteGame(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to delete game: " + e.getMessage());
            return ResponseEntity.status(500).build();
        }
    }

    @GetMapping("/featured")
    public ResponseEntity<List<Game>> getFeaturedGames() {
        return ResponseEntity.ok(gameService.getFeaturedGames());
    }

    @GetMapping("/trending")
    public ResponseEntity<List<Game>> getTrendingGames(@RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(gameService.getTrendingGames(limit));
    }

    @GetMapping("/new")
    public ResponseEntity<List<Game>> getNewGames(@RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(gameService.getNewGames(limit));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Game>> searchGames(@RequestParam String query) {
        return ResponseEntity.ok(gameService.searchGames(query));
    }
}
