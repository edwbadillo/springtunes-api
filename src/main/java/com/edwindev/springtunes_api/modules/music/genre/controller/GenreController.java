package com.edwindev.springtunes_api.modules.music.genre.controller;

import com.edwindev.springtunes_api.common.dto.ResourceCreatedResponse;
import com.edwindev.springtunes_api.modules.music.genre.dto.GenreRequest;
import com.edwindev.springtunes_api.modules.music.genre.dto.GenreResponse;
import com.edwindev.springtunes_api.modules.music.genre.service.GenreService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/music/genres")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    public Page<GenreResponse> getAll(Pageable pageable) {
        return genreService.getAll(pageable);
    }

    @GetMapping("/{id}")
    public GenreResponse getById(@PathVariable Integer id) {
        return genreService.getById(id);
    }

    @PostMapping
    @ResponseStatus(value = HttpStatus.CREATED)
    public ResourceCreatedResponse<GenreResponse> create(@Valid @RequestBody GenreRequest request) {
        return new ResourceCreatedResponse<>(genreService.create(request));
    }

    @PutMapping("/{id}")
    public void update(@PathVariable Integer id, @Valid @RequestBody GenreRequest request) {
        genreService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(value = HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Integer id) {
        genreService.deleteById(id);
    }
}
