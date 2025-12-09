package com.example.GameStore.Service;

import com.example.GameStore.Entity.Favorite;
import com.example.GameStore.Entity.Game;
import com.example.GameStore.Entity.User;
import com.example.GameStore.Repository.FavoriteRepository;
import com.example.GameStore.Repository.GameRepository;
import com.example.GameStore.Repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class FavoriteService {

    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final FavoriteRepository favoriteRepository;

    public FavoriteService(UserRepository userRepository,
                           GameRepository gameRepository,
                           FavoriteRepository favoriteRepository) {
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.favoriteRepository = favoriteRepository;
    }

    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public String addFavorite(Long gameId) {
        User user = getCurrentUser();

        if (favoriteRepository.existsByUserIdAndGameId(user.getId(), gameId)) {
            return "Game already in favorites!";
        }

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setGame(game);

        favoriteRepository.save(favorite);

        return "Game added to favorites!";
    }

    public String removeFavorite(Long gameId) {
        User user = getCurrentUser();

        if (!favoriteRepository.existsByUserIdAndGameId(user.getId(), gameId)) {
            return "Game not in favorites!";
        }

        favoriteRepository.deleteByUserIdAndGameId(user.getId(), gameId);
        return "Game removed from favorites!";
    }

    public Page<Favorite> getFavorites(int page, int size) {
        User user = getCurrentUser();
        return favoriteRepository.findByUserId(
                user.getId(),
                PageRequest.of(page, size)
        );
    }

    public boolean isFavorited(Long gameId) {
        User user = getCurrentUser();
        return favoriteRepository.existsByUserIdAndGameId(user.getId(), gameId);
    }

}
