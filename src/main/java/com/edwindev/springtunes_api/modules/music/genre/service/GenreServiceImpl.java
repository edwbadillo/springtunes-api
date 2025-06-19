package com.edwindev.springtunes_api.modules.music.genre.service;

import com.edwindev.springtunes_api.common.dto.InvalidData;
import com.edwindev.springtunes_api.common.exception.ErrorCode;
import com.edwindev.springtunes_api.common.exception.InvalidDataException;
import com.edwindev.springtunes_api.modules.music.genre.dto.GenreMapper;
import com.edwindev.springtunes_api.modules.music.genre.dto.GenreRequest;
import com.edwindev.springtunes_api.modules.music.genre.dto.GenreResponse;
import com.edwindev.springtunes_api.modules.music.genre.exception.GenreNotFoundException;
import com.edwindev.springtunes_api.modules.music.genre.repository.Genre;
import com.edwindev.springtunes_api.modules.music.genre.repository.GenreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GenreServiceImpl implements GenreService {

    private final GenreRepository genreRepository;
    private final GenreMapper genreMapper;

    @Override
    public Page<GenreResponse> getAll(Pageable pageable) {
        return genreRepository.findAll(pageable)
                .map(genreMapper::toGenreResponse);
    }

    @Override
    public GenreResponse getById(Integer id) {
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new GenreNotFoundException("Genre music with id " + id + " not found."));
        return genreMapper.toGenreResponse(genre);
    }

    @Override
    public GenreResponse create(GenreRequest request) {
        if (genreRepository.existsByNameIgnoreCase(request.name())) {
            throw new InvalidDataException(
                    InvalidData.builder()
                            .type(ErrorCode.UNIQUE_ERROR.code())
                            .field("name")
                            .value(request.name())
                            .message("Genre with name " + request.name() + " already exists.")
                            .build()
            );
        }
        Genre genre = new Genre();
        setFields(genre, request);
        genre = genreRepository.save(genre);
        return genreMapper.toGenreResponse(genre);
    }

    @Override
    public GenreResponse update(Integer id, GenreRequest request) {
        if (genreRepository.existsByNameIgnoreCaseAndIdNot(request.name(), id)) {
            throw new InvalidDataException(
                    InvalidData.builder()
                            .type(ErrorCode.UNIQUE_ERROR.code())
                            .field("name")
                            .value(request.name())
                            .message("Genre with name " + request.name() + " already exists.")
                            .build()
            );
        }
        Genre genre = genreRepository.findById(id)
                .orElseThrow(() -> new GenreNotFoundException("Genre music with id " + id + " not found."));
        setFields(genre, request);
        genre = genreRepository.save(genre);
        return genreMapper.toGenreResponse(genre);
    }

    @Override
    public void deleteById(Integer id) {
        genreRepository.deleteById(id);
    }

    private void setFields(Genre genre, GenreRequest request) {
        genre.setName(request.name());
        genre.setDescription(request.description());
    }
}
