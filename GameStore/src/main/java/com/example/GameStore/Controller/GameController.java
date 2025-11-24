package com.example.GameStore.Controller;

import com.example.GameStore.Dto.GameRequest;
import com.example.GameStore.Entity.Game;
import com.example.GameStore.Service.GameService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
public class GameController {

    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping
    public Game createGame(@RequestBody GameRequest request) {
        return gameService.createGame(request);
    }


    @GetMapping
    public List<Game> getAllGames() {
        return gameService.getAllGames();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Game> getGameById(@PathVariable Long id) {
        return gameService.getGameById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    @PutMapping("/{id}")
    public ResponseEntity<Game> updateGame(@PathVariable Long id, @RequestBody GameRequest request) {
        try {
            return ResponseEntity.ok(gameService.updateGame(id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGame(@PathVariable Long id) {
        gameService.deleteGame(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/featured")
    public List<Game> getFeaturedGames() {
        return gameService.getFeaturedGames();
    }

    @GetMapping("/trending")
    public List<Game> getTrendingGames(@RequestParam(defaultValue = "5") int limit) {
        return gameService.getTrendingGames(limit);
    }

    @GetMapping("/new")
    public List<Game> getNewGames(@RequestParam(defaultValue = "5") int limit) {
        return gameService.getNewGames(limit);
    }

    @GetMapping("/search")
    public List<Game> searchGames(@RequestParam String query) {
        return gameService.searchGames(query);
    }
}
