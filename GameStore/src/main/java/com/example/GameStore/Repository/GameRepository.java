package com.example.GameStore.Repository;

import com.example.GameStore.Entity.Game;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {

    @Query("SELECT DISTINCT g FROM Game g LEFT JOIN FETCH g.genres WHERE g.featured = true")
    List<Game> findFeaturedGamesWithGenres();

    @Query(value = "SELECT DISTINCT g FROM Game g LEFT JOIN FETCH g.genres ORDER BY g.id DESC",
            countQuery = "SELECT COUNT(g) FROM Game g")
    Page<Game> findTrendingGamesWithGenres(Pageable pageable);

    @Query(value = "SELECT DISTINCT g FROM Game g LEFT JOIN FETCH g.genres ORDER BY g.createdAt DESC",
            countQuery = "SELECT COUNT(g) FROM Game g")
    Page<Game> findNewGamesWithGenres(Pageable pageable);

    List<Game> findByNameContainingIgnoreCase(String name);
}
