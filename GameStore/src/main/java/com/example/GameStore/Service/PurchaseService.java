package com.example.GameStore.Service;

import com.example.GameStore.Entity.Game;
import com.example.GameStore.Entity.Purchase;
import com.example.GameStore.Entity.PurchaseStatus;
import com.example.GameStore.Entity.User;
import com.example.GameStore.Repository.GameRepository;
import com.example.GameStore.Repository.PurchaseRepository;
import com.example.GameStore.Repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;



@Service
@RequiredArgsConstructor
public class PurchaseService {

    private final PurchaseRepository purchaseRepository;
    private final GameRepository gameRepository;

    /* ===============================
       BUY GAME
    =============================== */
    public void purchaseGame(User user, Long gameId) {

        if (purchaseRepository.existsByUserIdAndGameId(user.getId(), gameId)) {
            throw new RuntimeException("Game already purchased");
        }

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new RuntimeException("Game not found"));

        Purchase purchase = Purchase.builder()
                .user(user)
                .game(game)
                .build();

        purchaseRepository.save(purchase);
    }

    /* ===============================
       CHECK PURCHASED
    =============================== */
    public boolean isPurchased(User user, Long gameId) {
        return purchaseRepository.existsByUserIdAndGameId(user.getId(), gameId);
    }

    /* ===============================
       USER LIBRARY
    =============================== */
    public Object getUserPurchases(User user) {
        return purchaseRepository.findAllByUserId(user.getId());
    }
}
