package com.example.GameStore.modules.commerce.repository;

import com.example.GameStore.modules.commerce.entity.Cart;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    boolean existsByUserIdAndGameId(Long userId, Long gameId);
    void deleteByUserIdAndGameId(Long userId, Long gameId);
    Page<Cart> findByUserId(Long userId, Pageable pageable);
}
