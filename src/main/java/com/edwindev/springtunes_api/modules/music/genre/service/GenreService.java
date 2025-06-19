package com.edwindev.springtunes_api.modules.music.genre.service;

import com.edwindev.springtunes_api.modules.music.genre.repository.Genre;
import com.edwindev.springtunes_api.modules.music.genre.dto.GenreRequest;
import com.edwindev.springtunes_api.modules.music.genre.dto.GenreResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * The service for {@link Genre} entities.
 */
public interface GenreService {
    Page<GenreResponse> getAll(Pageable pageable);

    GenreResponse getById(Integer id);

    GenreResponse create(GenreRequest request);

    GenreResponse update(Integer id, GenreRequest request);

    void deleteById(Integer id);
}
