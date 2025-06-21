package com.edwindev.springtunes_api.modules.artist.profile.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * The repository for {@link ArtistProfile} entities.
 */
public interface ArtistProfileRepository extends JpaRepository<ArtistProfile, UUID> {

    @Query("""
                SELECT a FROM ArtistProfile a
                WHERE LOWER(a.artistName) LIKE LOWER(CONCAT('%', :name, '%'))
            """)
    Page<ArtistProfile> searchByArtistName(@Param("name") String name, Pageable pageable);

    Page<ArtistProfile> findAllByStatus(ArtistProfile.Status status, Pageable pageable);

    Page<ArtistProfile> findAllByStatusIn(List<ArtistProfile.Status> statuses, Pageable pageable);

    boolean existsByArtistNameIgnoreCase(String artistName);

    boolean existsByArtistNameIgnoreCaseAndIdNot(String artistName, UUID id);
}
