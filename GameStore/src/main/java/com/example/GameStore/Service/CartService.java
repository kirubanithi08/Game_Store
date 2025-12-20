package com.example.GameStore.Service;

import com.example.GameStore.Dto.CartDTO;
import com.example.GameStore.Entity.Cart;
import com.example.GameStore.Entity.Game;
import com.example.GameStore.Entity.Genre;
import com.example.GameStore.Entity.User;
import com.example.GameStore.Repository.CartRepository;
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
public class CartService {

    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final CartRepository cartRepository;

    public CartService(UserRepository userRepository,
                           GameRepository gameRepository,
                       CartRepository cartRepository) {
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.cartRepository = cartRepository;
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


    public String addCart(Long gameId) {
        User user = getCurrentUser();

        if (cartRepository.existsByUserIdAndGameId(user.getId(), gameId)) {
            return "Game already in cart!";
        }

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setGame(game);

        cartRepository.save(cart);
        return "Game added to cart!";
    }

    @Transactional
    public String removeCart(Long gameId) {
        User user = getCurrentUser();

        if (!cartRepository.existsByUserIdAndGameId(user.getId(), gameId)) {
            return "Game not in cart!";
        }

        cartRepository.deleteByUserIdAndGameId(user.getId(), gameId);
        return "Game removed from cart!";
    }


    public Page<CartDTO> getCart(int page, int size) {
        User user = getCurrentUser();

        Page<Cart> cart = cartRepository.findByUserId(
                user.getId(),
                PageRequest.of(page, size)
        );

        return cart.map(ct -> {
            Game game = ct.getGame();

            CartDTO dto = new CartDTO();
            dto.setId(game.getId());
            dto.setName(game.getName());
            dto.setImg(game.getImg());
            dto.setCover(game.getCover());
            dto.setDescription(game.getDescription());
            dto.setPrice(game.getPrice());
            dto.setGenres(
                    game.getGenres()
                            .stream()
                            .map(Genre::getName)
                            .toList()
            );
            return dto;
        });
    }


    public boolean isCart(Long gameId) {
        User user = getCurrentUser();
        return cartRepository.existsByUserIdAndGameId(user.getId(), gameId);
    }
}
