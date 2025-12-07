package com.example.GameStore.Service;

import com.example.GameStore.Entity.Game;
import com.example.GameStore.Entity.User;
import com.example.GameStore.Repository.GameRepository;
import com.example.GameStore.Repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class FavoriteService {

    private final UserRepository userRepository;
    private final GameRepository gameRepository;

    public FavoriteService(UserRepository userRepository, GameRepository gameRepository) {
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
    }

    // Get current logged-in user
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName(); // assuming username is in JWT principal
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public String addFavorite(Long gameId) {
        User user = getCurrentUser();
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        if (user.getFavorite().contains(game)) {
            return "Game already in favorites!";
        }

        user.getFavorite().add(game);
        userRepository.save(user);
        return "Game added to favorites!";
    }

    public String removeFavorite(Long gameId) {
        User user = getCurrentUser();
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        if (!user.getFavorite().contains(game)) {
            return "Game not in favorites!";
        }

        user.getFavorite().remove(game);
        userRepository.save(user);
        return "Game removed from favorites!";
    }

    public Set<Game> getFavorites() {
        User user = getCurrentUser();
        return user.getFavorite();
    }
}
