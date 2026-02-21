package com.example.GameStore.modules.commerce.service;

import com.example.GameStore.modules.catalog.entity.Game;
import com.example.GameStore.modules.catalog.entity.Genre;
import com.example.GameStore.modules.catalog.repository.GameRepository;
import com.example.GameStore.modules.commerce.dto.CartResponse;
import com.example.GameStore.modules.commerce.entity.Cart;
import com.example.GameStore.modules.commerce.repository.CartRepository;
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
public class CartService {

    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final CartRepository cartRepository;

     private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public void addCart(Long gameId) {
        User user = getCurrentUser();
        if (cartRepository.existsByUserIdAndGameId(user.getId(), gameId)) {
            throw new IllegalArgumentException("Game already in cart!");
        }

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        Cart cart = Cart.builder()
                .user(user)
                .game(game)
                .build();

        cartRepository.save(cart);
    }

    @Transactional
    public void removeCart(Long gameId) {
        User user = getCurrentUser();
        if (!cartRepository.existsByUserIdAndGameId(user.getId(), gameId)) {
            throw new IllegalArgumentException("Game not in cart!");
        }
        cartRepository.deleteByUserIdAndGameId(user.getId(), gameId);
    }

    public Page<CartResponse> getCart(int page, int size) {
        User user = getCurrentUser();
        Page<Cart> cartPage = cartRepository.findByUserId(user.getId(), PageRequest.of(page, size));

        return cartPage.map(ct -> {
            Game game = ct.getGame();
            return new CartResponse(
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

    public boolean isCart(Long gameId) {
        User user = getCurrentUser();
        return cartRepository.existsByUserIdAndGameId(user.getId(), gameId);
    }
}
