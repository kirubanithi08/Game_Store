package com.example.GameStore.Repository;

import com.example.GameStore.Entity.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {


    List<Game> findByFeaturedTrue();


    @Query("SELECT g FROM Game g ORDER BY g.id DESC")
    List<Game> findTrendingGames(Pageable pageable);


    List<Game> findAllByOrderByCreatedAtDesc(Pageable pageable);


    List<Game> findByNameContainingIgnoreCase(String name);
}
