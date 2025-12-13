package com.example.GameStore.Repository;


import com.example.GameStore.Entity.Purchase;

import org.springframework.data.jpa.repository.JpaRepository;


import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    boolean existsByUserIdAndGameId(Long userId, Long gameId);

    List<Purchase> findAllByUserId(Long userId);
}
