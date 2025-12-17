package com.example.GameStore.Service;


import com.example.GameStore.Dto.PurchaseDTO;
import com.example.GameStore.Entity.Game;
import com.example.GameStore.Entity.Purchase;
import com.example.GameStore.Entity.User;
import com.example.GameStore.Repository.GameRepository;
import com.example.GameStore.Repository.PurchaseRepository;
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
public class PurchaseService {

    private final UserRepository userRepository;
    private final GameRepository gameRepository;
    private final PurchaseRepository purchaseRepository;

    public PurchaseService(UserRepository userRepository,
                           GameRepository gameRepository,
                           PurchaseRepository purchaseRepository) {
        this.userRepository = userRepository;
        this.gameRepository = gameRepository;
        this.purchaseRepository = purchaseRepository;
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


    public String addPurchase(Long gameId) {
        User user = getCurrentUser();

        if (purchaseRepository.existsByUserIdAndGameId(user.getId(), gameId)) {
            return "Game already purchased!";
        }

        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Game not found"));

        Purchase purchase = new Purchase();
        purchase.setUser(user);
        purchase.setGame(game);

        purchaseRepository.save(purchase);
        return "Game purchased!";
    }

    @Transactional
    public String removePurchase(Long gameId) {
        User user = getCurrentUser();

        if (!purchaseRepository.existsByUserIdAndGameId(user.getId(), gameId)) {
            return "Game not purchased!";
        }

        purchaseRepository.deleteByUserIdAndGameId(user.getId(), gameId);
        return "Game removed from purchased!";
    }


    public Page<PurchaseDTO> getPurchase(int page, int size) {
        User user = getCurrentUser();

        Page<Purchase> purchase = purchaseRepository.findByUserId(
                user.getId(),
                PageRequest.of(page, size)
        );

        return purchase.map(pur -> {
            Game game = pur.getGame();

            PurchaseDTO dto = new PurchaseDTO();
            dto.setId(game.getId());
            dto.setName(game.getName());
            dto.setImg(game.getImg());
            dto.setCover(game.getCover());
            dto.setDescription(game.getDescription());
            dto.setPrice(game.getPrice());
            return dto;
        });
    }


    public boolean ispurchased(Long gameId) {
        User user = getCurrentUser();
        return purchaseRepository.existsByUserIdAndGameId(user.getId(), gameId);
    }
}
