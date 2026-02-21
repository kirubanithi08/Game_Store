package com.example.GameStore.modules.commerce.service;

import com.example.GameStore.modules.catalog.entity.Game;
import com.example.GameStore.modules.catalog.entity.Genre;
import com.example.GameStore.modules.catalog.repository.GameRepository;
import com.example.GameStore.modules.commerce.dto.PurchaseResponse;
import com.example.GameStore.modules.commerce.entity.Purchase;
import com.example.GameStore.modules.commerce.repository.PurchaseRepository;
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
public class PurchaseService {

    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final PurchaseRepository purchaseRepository;

     private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    public void addPurchase(Long gameId) {
        User user = getCurrentUser();
        if (purchaseRepository.existsByUserIdAndGameId(user.getId(), gameId)) {
            throw new IllegalArgumentException("Game already purchased!");
        }

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new IllegalArgumentException("Game not found"));

        Purchase purchase = Purchase.builder()
                .user(user)
                .game(game)
                .build();

        purchaseRepository.save(purchase);
    }

    @Transactional
    public void removePurchase(Long gameId) {
        User user = getCurrentUser();
        if (!purchaseRepository.existsByUserIdAndGameId(user.getId(), gameId)) {
            throw new IllegalArgumentException("Game not purchased!");
        }
        purchaseRepository.deleteByUserIdAndGameId(user.getId(), gameId);
    }

    public Page<PurchaseResponse> getPurchase(int page, int size) {
        User user = getCurrentUser();
        Page<Purchase> purchasePage = purchaseRepository.findByUserId(user.getId(), PageRequest.of(page, size));

        return purchasePage.map(pur -> {
            Game game = pur.getGame();
            return new PurchaseResponse(
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

    public boolean isPurchased(Long gameId) {
        User user = getCurrentUser();
        return purchaseRepository.existsByUserIdAndGameId(user.getId(), gameId);
    }
}
