package com.example.GameStore.modules.catalog.service;

import com.example.GameStore.modules.catalog.dto.GameRequest;
import com.example.GameStore.modules.catalog.entity.Game;
import com.example.GameStore.modules.catalog.entity.Genre;
import com.example.GameStore.modules.catalog.repository.GameRepository;
import com.example.GameStore.modules.catalog.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameService {

    private final GameRepository gameRepository;
    private final GenreRepository genreRepository;

    @Transactional
    public Game createGame(GameRequest req) {
        Set<Genre> genres = new HashSet<>(genreRepository.findAllById(req.genres()));
        
        Game game = Game.builder()
                .name(req.name())
                .img(req.img())
                .cover(req.cover())
                .description(req.description())
                .price(req.price())
                .featured(req.featured())
                .genres(genres)
                .build();

        return gameRepository.save(game);
    }

    public List<Game> getGames(int page, int size) {
        return gameRepository.findAll(PageRequest.of(page, size)).getContent();
    }

    public Optional<Game> getGameById(Long id) {
        return gameRepository.findById(id);
    }

    @Transactional
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
        }).orElseThrow(() -> new IllegalArgumentException("Game not found"));
    }

    public void deleteGame(Long id) {
        gameRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<Game> getFeaturedGames() {
        return gameRepository.findFeaturedGamesWithGenres();
    }

    @Transactional(readOnly = true)
    public List<Game> getTrendingGames(int limit) {
        return gameRepository.findTrendingGamesWithGenres(PageRequest.of(0, limit)).getContent();
    }

    @Transactional(readOnly = true)
    public List<Game> getNewGames(int limit) {
        return gameRepository.findNewGamesWithGenres(PageRequest.of(0, limit)).getContent();
    }

    public List<Game> searchGames(String query) {
        return gameRepository.findByNameContainingIgnoreCase(query);
    }
}
