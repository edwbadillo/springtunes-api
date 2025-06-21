package com.edwindev.springtunes_api.modules.artist.profile;

import com.edwindev.springtunes_api.config.jpa.AuditorAwareImpl;
import com.edwindev.springtunes_api.config.jpa.JpaAuditingConfig;
import com.edwindev.springtunes_api.modules.artist.profile.repository.ArtistProfile;
import com.edwindev.springtunes_api.modules.artist.profile.repository.ArtistProfileRepository;
import com.edwindev.springtunes_api.modules.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import({JpaAuditingConfig.class, AuditorAwareImpl.class})
@ActiveProfiles("test")
public class ArtistProfileRepositoryTest {

    @Autowired
    private ArtistProfileRepository repository;

    @Test
    void shouldCreateArtistProfileWithoutUser() {
        ArtistProfile profile = ArtistProfile.builder()
                .status(ArtistProfile.Status.APPROVED)
                .artistName("ALE")
                .bio("BIO")
                .build();

        profile = repository.save(profile);

        assertThat(profile.getId()).isNotNull();
        assertThat(profile.getStatus()).isEqualTo(ArtistProfile.Status.APPROVED);
        assertThat(profile.getArtistName()).isEqualTo("ALE");
        assertThat(profile.getBio()).isEqualTo("BIO");
        assertThat(profile.hasUser()).isFalse();
        assertThat(profile.getCreatedAt()).isNotNull();
    }

    @Test
    void shouldCreateArtistProfileWithUser() {
        // given
        User user = User.builder()
                .id("firebase-uid-123")
                .displayName("Edwin")
                .email("edwin@example.com")
                .role(User.Role.ARTIST)
                .build();

        ArtistProfile profile = ArtistProfile.builder()
                .artistName("Edwin Beats")
                .bio("Producer and DJ")
                .status(ArtistProfile.Status.APPROVED)
                .user(user)
                .build();

        // when
        profile = repository.save(profile);

        // then
        assertThat(profile.getId()).isNotNull();
        assertThat(profile.getArtistName()).isEqualTo("Edwin Beats");
        assertThat(profile.getUser()).isNotNull();
        assertThat(profile.getUser().getId()).isEqualTo("firebase-uid-123");
        assertThat(profile.hasUser()).isTrue();
    }


    @Test
    void shouldSearchByArtistNameContainingIgnoreCase() {
        // given
        ArtistProfile profile = ArtistProfile.builder()
                .artistName("Nova Luna")
                .status(ArtistProfile.Status.APPROVED)
                .build();
        repository.save(profile);

        // when
        Page<ArtistProfile> results = repository.searchByArtistName("nova", Pageable.ofSize(10));

        // then
        assertThat(results).hasSize(1);
        assertThat(results.getContent().getFirst().getArtistName()).isEqualTo("Nova Luna");
    }

    @Test
    void shouldFindAllByStatus() {
        // given
        repository.save(ArtistProfile.builder().artistName("Art1").status(ArtistProfile.Status.PENDING).build());
        repository.save(ArtistProfile.builder().artistName("Art2").status(ArtistProfile.Status.PENDING).build());
        repository.save(ArtistProfile.builder().artistName("Art3").status(ArtistProfile.Status.REJECTED).build());

        // when
        Page<ArtistProfile> result = repository.findAllByStatus(ArtistProfile.Status.PENDING, Pageable.ofSize(10));

        // then
        assertThat(result).hasSize(2);
        assertThat(result.getContent())
                .extracting(ArtistProfile::getStatus)
                .containsOnly(ArtistProfile.Status.PENDING);
    }

    @Test
    void shouldFindAllByStatusIn() {
        // given
        repository.save(ArtistProfile.builder().artistName("A1").status(ArtistProfile.Status.PENDING).build());
        repository.save(ArtistProfile.builder().artistName("A2").status(ArtistProfile.Status.APPROVED).build());
        repository.save(ArtistProfile.builder().artistName("A3").status(ArtistProfile.Status.REJECTED).build());

        // when
        Page<ArtistProfile> result = repository.findAllByStatusIn(
                List.of(ArtistProfile.Status.PENDING, ArtistProfile.Status.APPROVED),
                Pageable.ofSize(10)
        );

        // then
        assertThat(result).hasSize(2);
        assertThat(result.getContent())
                .extracting(ArtistProfile::getStatus)
                .containsExactlyInAnyOrder(ArtistProfile.Status.PENDING, ArtistProfile.Status.APPROVED);
    }

    @Test
    void shouldCheckExistenceByArtistNameIgnoreCase() {
        // given
        repository.save(ArtistProfile.builder().artistName("Nova Luna").status(ArtistProfile.Status.APPROVED).build());

        // when
        boolean exists = repository.existsByArtistNameIgnoreCase("nova luna");

        // then
        assertThat(exists).isTrue();
    }

    @Test
    void shouldCheckExistenceByArtistNameAndNotId() {
        // given
        ArtistProfile existing = repository.save(ArtistProfile.builder()
                .artistName("Nova Luna")
                .status(ArtistProfile.Status.APPROVED)
                .build());

        // when
        boolean exists = repository.existsByArtistNameIgnoreCaseAndIdNot("nova luna", UUID.randomUUID());

        // then
        assertThat(exists).isTrue();

        // when (check using same ID)
        boolean existsWithSameId = repository.existsByArtistNameIgnoreCaseAndIdNot("nova luna", existing.getId());

        // then
        assertThat(existsWithSameId).isFalse();
    }

}
