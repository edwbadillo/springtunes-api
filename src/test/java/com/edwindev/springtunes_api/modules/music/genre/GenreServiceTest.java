package com.edwindev.springtunes_api.modules.music.genre;

import com.edwindev.springtunes_api.common.exception.InvalidDataException;
import com.edwindev.springtunes_api.modules.music.genre.dto.GenreMapper;
import com.edwindev.springtunes_api.modules.music.genre.dto.GenreRequest;
import com.edwindev.springtunes_api.modules.music.genre.dto.GenreResponse;
import com.edwindev.springtunes_api.modules.music.genre.exception.GenreNotFoundException;
import com.edwindev.springtunes_api.modules.music.genre.repository.Genre;
import com.edwindev.springtunes_api.modules.music.genre.repository.GenreRepository;
import com.edwindev.springtunes_api.modules.music.genre.service.GenreServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class GenreServiceTest {
    @Mock
    private GenreRepository repository;

    @InjectMocks
    private GenreServiceImpl service;

    @Mock
    private GenreMapper mapper;

    @Test
    void shouldReturnPagedGenres() {
        // Arrange
        Genre genre = Genre.builder().id(1).name("House").description("House music").build();
        GenreResponse response = new GenreResponse(1, "House", "House music");

        Page<Genre> genrePage = new PageImpl<>(List.of(genre));
        given(repository.findAll(any(Pageable.class))).willReturn(genrePage);
        given(mapper.toGenreResponse(genre)).willReturn(response);

        // Act
        Page<GenreResponse> result = service.getAll(PageRequest.of(0, 10));

        // Assert
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst()).isEqualTo(response);
    }

    @Test
    void shouldReturnGenreById() {
        Genre genre = Genre.builder().id(1).name("House").description("House music").build();
        GenreResponse response = new GenreResponse(1, "House", "House music");

        given(repository.findById(1)).willReturn(Optional.of(genre));
        given(mapper.toGenreResponse(genre)).willReturn(response);

        GenreResponse result = service.getById(1);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void shouldThrowWhenGenreNotFound() {
        given(repository.findById(99)).willReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(99))
                .isInstanceOf(GenreNotFoundException.class)
                .hasMessage("Genre music with id 99 not found.");
    }

    @Test
    void shouldCreateGenreSuccessfully() {
        GenreRequest request = new GenreRequest("Techno", "Electronic music");
        Genre saved = Genre.builder().id(1).name("Techno").description("Electronic music").build();
        GenreResponse response = new GenreResponse(1, "Techno", "Electronic music");

        given(repository.existsByNameIgnoreCase("Techno")).willReturn(false);
        given(repository.save(any(Genre.class))).willReturn(saved);
        given(mapper.toGenreResponse(saved)).willReturn(response);

        GenreResponse result = service.create(request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void shouldThrowWhenGenreNameExists() {
        GenreRequest request = new GenreRequest("Techno", "Some desc");

        given(repository.existsByNameIgnoreCase("Techno")).willReturn(true);

        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Genre with name Techno already exists.");
    }

    @Test
    void shouldUpdateGenreSuccessfully() {
        GenreRequest request = new GenreRequest("Techno", "Updated desc");
        Genre existing = Genre.builder().id(1).name("OldName").description("Old desc").build();
        Genre updated = Genre.builder().id(1).name("Techno").description("Updated desc").build();
        GenreResponse response = new GenreResponse(1, "Techno", "Updated desc");

        given(repository.existsByNameIgnoreCaseAndIdNot("Techno", 1)).willReturn(false);
        given(repository.findById(1)).willReturn(Optional.of(existing));
        given(repository.save(existing)).willReturn(updated);
        given(mapper.toGenreResponse(updated)).willReturn(response);

        GenreResponse result = service.update(1, request);

        assertThat(result).isEqualTo(response);
    }

    @Test
    void shouldThrowWhenUpdatingWithDuplicateName() {
        GenreRequest request = new GenreRequest("Techno", "desc");

        given(repository.existsByNameIgnoreCaseAndIdNot("Techno", 1)).willReturn(true);

        assertThatThrownBy(() -> service.update(1, request))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("Genre with name Techno already exists.");
    }

    @Test
    void shouldDeleteGenreById() {
        service.deleteById(1);
        verify(repository).deleteById(1);
    }
}
