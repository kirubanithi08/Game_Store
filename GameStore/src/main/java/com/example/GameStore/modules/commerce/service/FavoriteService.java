package com.example.GameStore.modules.commerce.service;

import com.example.GameStore.modules.catalog.entity.Game;
import com.example.GameStore.modules.catalog.entity.Genre;
import com.example.GameStore.modules.catalog.repository.GameRepository;
import com.example.GameStore.modules.commerce.dto.FavoriteResponse;
import com.example.GameStore.modules.commerce.entity.Favorite;
import com.example.GameStore.modules.commerce.repository.FavoriteRepository;
import com.example.GameStore.modules.identity.entity.User;
import com.example.GameStore.modules.identity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final FavoriteRepository favoriteRepository;

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public void addFavorite(Long gameId) {
        User user = getCurrentUser();
        if (favoriteRepository.existsByUserIdAndGameId(user.getId(), gameId)) {
            throw new IllegalArgumentException("Game already in favorites!");
        }

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        Favorite favorite = Favorite.builder()
                .user(user)
                .game(game)
                .build();

        favoriteRepository.save(favorite);
    }

    @Transactional
    public void removeFavorite(Long gameId) {
        User user = getCurrentUser();
        if (!favoriteRepository.existsByUserIdAndGameId(user.getId(), gameId)) {
            throw new IllegalArgumentException("Game not in favorites!");
        }
        favoriteRepository.deleteByUserIdAndGameId(user.getId(), gameId);
    }

    public Page<FavoriteResponse> getFavorites(int page, int size) {
        User user = getCurrentUser();
        Page<Favorite> favoritesPage = favoriteRepository.findByUserId(user.getId(), PageRequest.of(page, size));

        return favoritesPage.map(fav -> {
            Game game = fav.getGame();
            return new FavoriteResponse(
                    game.getId(),
                    game.getName(),
                    game.getImg(),
                    game.getCover(),
                    game.getDescription(),
                    game.getPrice(),
                    game.getGenres().stream().map(Genre::getName).toList()
            );
        });
    }

    public boolean isFavorited(Long gameId) {
        User user = getCurrentUser();
        return favoriteRepository.existsByUserIdAndGameId(user.getId(), gameId);
    }
}
