package com.example.GameStore.Service;

import com.example.GameStore.Dto.GameRequest;
import com.example.GameStore.Entity.Game;
import com.example.GameStore.Entity.Genre;
import com.example.GameStore.Repository.GameRepository;
import com.example.GameStore.Repository.GenreRepository;
import org.springframework.data.domain.Page;
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



    public Page<Game> getGames(int page, int size) {
        return gameRepository.findAll(PageRequest.of(page, size));
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


    public List<Game> getFeaturedGames() {
        return gameRepository.findByFeaturedTrue();
    }

    public List<Game> getTrendingGames(int limit) {
        return gameRepository.findTrendingGames(PageRequest.of(0, limit));
    }

    public List<Game> getNewGames(int limit) {
        return gameRepository.findAllByOrderByCreatedAtDesc(PageRequest.of(0, limit));
    }

    public List<Game> searchGames(String query) {
        return gameRepository.findByNameContainingIgnoreCase(query);
    }
}
