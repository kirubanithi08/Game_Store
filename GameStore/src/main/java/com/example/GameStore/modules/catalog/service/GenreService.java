package com.example.GameStore.modules.catalog.service;

import com.example.GameStore.modules.catalog.entity.Genre;
import com.example.GameStore.modules.catalog.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GenreService {

    private final GenreRepository genreRepository;

    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }

    public Genre createGenre(Genre genre) {
        if (genre.getName() != null && genreRepository.findByName(genre.getName()).isPresent()) {
            throw new IllegalArgumentException("Genre already exists");
        }
        return genreRepository.save(genre);
    }

    public Genre getGenreById(Long id) {
        return genreRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Genre not found"));
    }
}
