package com.example.GameStore.Service;

import com.example.GameStore.Dto.FavoriteGameDTO;
import com.example.GameStore.Entity.Favorite;
import com.example.GameStore.Entity.Game;
import com.example.GameStore.Entity.User;
import com.example.GameStore.Repository.FavoriteRepository;
import com.example.GameStore.Repository.GameRepository;
import com.example.GameStore.Repository.UserRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

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

        if (auth == null || !auth.isAuthenticated() ||
                auth.getPrincipal() == null ||
                "anonymousUser".equals(auth.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found"));
    }


    public String addFavorite(Long gameId) {
        User user = getCurrentUser();

        if (favoriteRepository.existsByUserIdAndGameId(user.getId(), gameId)) {
            return "Game already in favorites!";
        }

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));

        Favorite favorite = new Favorite();
        favorite.setUser(user);
        favorite.setGame(game);

        favoriteRepository.save(favorite);
        return "Game added to favorites!";
    }

    @Transactional
    public String removeFavorite(Long gameId) {
        User user = getCurrentUser();

        if (!favoriteRepository.existsByUserIdAndGameId(user.getId(), gameId)) {
            return "Game not in favorites!";
        }

        favoriteRepository.deleteByUserIdAndGameId(user.getId(), gameId);
        return "Game removed from favorites!";
    }

    /**
     * Returns paginated favorites of current user.
     */
    public Page<FavoriteGameDTO> getFavorites(int page, int size) {
        User user = getCurrentUser();

        Page<Favorite> favorites = favoriteRepository.findByUserId(
                user.getId(),
                PageRequest.of(page, size)
        );

        return favorites.map(fav -> {
            Game game = fav.getGame();

            FavoriteGameDTO dto = new FavoriteGameDTO();
            dto.setId(game.getId());
            dto.setName(game.getName());
            dto.setImg(game.getImg());
            dto.setCover(game.getCover());
            dto.setDescription(game.getDescription());
            dto.setPrice(game.getPrice());
            return dto;
        });
    }


    public boolean isFavorited(Long gameId) {
        User user = getCurrentUser();
        return favoriteRepository.existsByUserIdAndGameId(user.getId(), gameId);
    }
}
