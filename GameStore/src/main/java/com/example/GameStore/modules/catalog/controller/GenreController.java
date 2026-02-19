package com.example.GameStore.modules.catalog.controller;

import com.example.GameStore.modules.catalog.entity.Genre;
import com.example.GameStore.modules.catalog.service.GenreService;
import com.example.GameStore.modules.shared.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/genres")
@RequiredArgsConstructor
public class GenreController {

    private final GenreService genreService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<Genre>>> getAllGenres() {
        return ResponseEntity.ok(ApiResponse.success(genreService.getAllGenres()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Genre>> createGenre(@RequestBody Genre genre) {
        Genre createdGenre = genreService.createGenre(genre);
        return ResponseEntity.ok(ApiResponse.success(createdGenre, "Genre created successfully"));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Genre>> getGenreById(@PathVariable Long id) {
        Genre genre = genreService.getGenreById(id);
        return ResponseEntity.ok(ApiResponse.success(genre));
    }
}
