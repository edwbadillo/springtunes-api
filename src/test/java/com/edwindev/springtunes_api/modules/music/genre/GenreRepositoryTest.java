package com.edwindev.springtunes_api.modules.music.genre;

import com.edwindev.springtunes_api.config.jpa.AuditorAwareImpl;
import com.edwindev.springtunes_api.config.jpa.JpaAuditingConfig;
import com.edwindev.springtunes_api.modules.music.genre.repository.Genre;
import com.edwindev.springtunes_api.modules.music.genre.repository.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({JpaAuditingConfig.class, AuditorAwareImpl.class})
@ActiveProfiles("test")
public class GenreRepositoryTest {

    @Autowired
    private GenreRepository genreRepository;

    @BeforeEach
    void setup() {
        genreRepository.saveAll(List.of(
                Genre.builder().name("House").description("House music").build(),
                Genre.builder().name("Techno").description("Techno music").build(),
                Genre.builder().name("Deep House").description("Subgenre").build(),
                Genre.builder().name("Progressive").description("Another subgenre").build()
        ));
    }

    @Test
    void shouldFindTop10ByNameContainingOrderedByNameAsc() {
        List<Genre> results = genreRepository
                .searchByName("house", PageRequest.of(0, 10));

        assertThat(results)
                .hasSize(2)
                .extracting(Genre::getName)
                .containsExactly("Deep House", "House");
    }

    @Test
    void shouldReturnTrueIfNameExistsIgnoringCase() {
        boolean exists = genreRepository.existsByNameIgnoreCase("tEchno");
        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnTrueIfNameExistsAndIdIsNot() {
        Genre genre = genreRepository.findAll().stream()
                .filter(g -> g.getName().equalsIgnoreCase("House"))
                .findFirst()
                .orElseThrow();

        boolean exists = genreRepository.existsByNameIgnoreCaseAndIdNot("House", genre.getId() + 1);
        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseIfNameExistsButSameId() {
        Genre genre = genreRepository.findAll().stream()
                .filter(g -> g.getName().equalsIgnoreCase("House"))
                .findFirst()
                .orElseThrow();

        boolean exists = genreRepository.existsByNameIgnoreCaseAndIdNot("House", genre.getId());
        assertThat(exists).isFalse();
    }
}
