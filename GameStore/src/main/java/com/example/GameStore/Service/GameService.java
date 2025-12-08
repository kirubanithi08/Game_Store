package com.example.GameStore.Service;

import com.example.GameStore.Dto.GameRequest;
import com.example.GameStore.Entity.Game;
import com.example.GameStore.Entity.Genre;
import com.example.GameStore.Repository.GameRepository;
import com.example.GameStore.Repository.GenreRepository;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final GenreRepository genreRepository;

    public GameService(GameRepository gameRepository, GenreRepository genreRepository) {
        this.gameRepository = gameRepository;
        this.genreRepository = genreRepository;
    }

    public Game createGame(GameRequest req) {
        Game game = new Game();
        game.setName(req.name());
        game.setImg(req.img());
        game.setCover(req.cover());
        game.setDescription(req.description());
        game.setPrice(req.price());
        game.setFeatured(req.featured());

        Set<Genre> genres = new HashSet<>(genreRepository.findAllById(req.genres()));
        game.setGenres(genres);

        return gameRepository.save(game);
    }

    public List<Game> getGames(int page, int size) {
        return gameRepository.findAll(PageRequest.of(page, size)).getContent();
    }

    public Optional<Game> getGameById(Long id) {
        return gameRepository.findById(id);
    }

    public Game updateGame(Long id, GameRequest req) {
        return gameRepository.findById(id).map(game -> {
            game.setName(req.name());
            game.setImg(req.img());
            game.setCover(req.cover());
            game.setDescription(req.description());
            game.setPrice(req.price());
            game.setFeatured(req.featured());

            Set<Genre> genres = new HashSet<>(genreRepository.findAllById(req.genres()));
            game.setGenres(genres);

            return gameRepository.save(game);
        }).orElseThrow(() -> new RuntimeException("Game not found"));
    }

    public void deleteGame(Long id) {
        gameRepository.deleteById(id);
    }

    @Transactional
    public List<Game> getFeaturedGames() {
        try {
            List<Game> games = gameRepository.findFeaturedGamesWithGenres();
            return games != null ? games : Collections.emptyList();
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to fetch featured games: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @Transactional
    public List<Game> getTrendingGames(int limit) {
        try {
            return gameRepository.findTrendingGamesWithGenres(PageRequest.of(0, limit)).getContent();
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to fetch trending games: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    @Transactional
    public List<Game> getNewGames(int limit) {
        try {
            return gameRepository.findNewGamesWithGenres(PageRequest.of(0, limit)).getContent();
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to fetch new games: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public List<Game> searchGames(String query) {
        List<Game> games = gameRepository.findByNameContainingIgnoreCase(query);
        return games != null ? games : Collections.emptyList();
    }
}
