package com.edwindev.springtunes_api.modules.music.genre.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository for {@link Genre} entities.
 */
public interface GenreRepository extends JpaRepository<Genre, Integer> {

    @Query("""
                SELECT g FROM Genre g
                WHERE LOWER(g.name) LIKE LOWER(CONCAT('%', :name, '%'))
                ORDER BY g.name ASC
            """)
    List<Genre> searchByName(@Param("name") String name, Pageable pageable);

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Integer id);
}
