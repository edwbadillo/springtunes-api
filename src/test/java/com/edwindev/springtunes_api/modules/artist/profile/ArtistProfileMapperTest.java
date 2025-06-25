package com.edwindev.springtunes_api.modules.artist.profile;

import com.edwindev.springtunes_api.modules.artist.profile.dto.ArtistProfileMapper;
import com.edwindev.springtunes_api.modules.artist.profile.dto.ArtistProfileResponse;
import com.edwindev.springtunes_api.modules.artist.profile.repository.ArtistProfile;
import com.edwindev.springtunes_api.modules.user.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class ArtistProfileMapperTest {

    private final ArtistProfileMapper mapper = Mappers.getMapper(ArtistProfileMapper.class);

    @Test
    void shouldMapArtistProfileToResponse_withUser() {
        // given
        User user = User.builder()
                .id("firebase-uid")
                .displayName("Edwin")
                .email("edwin@example.com")
                .profilePictureUrl("http://localhost/profile-test.jpg")
                .role(User.Role.ARTIST)
                .build();

        ArtistProfile profile = ArtistProfile.builder()
                .id(UUID.randomUUID())
                .artistName("Nova Luna")
                .bio("Proyecto de electrónica ambiental")
                .user(user)
                .status(ArtistProfile.Status.APPROVED)
                .createdAt(LocalDateTime.now())
                .build();

        // when
        ArtistProfileResponse response = mapper.toArtistProfileResponse(profile);

        // then
        assertThat(response.id()).isEqualTo(profile.getId());
        assertThat(response.artistName()).isEqualTo("Nova Luna");
        assertThat(response.bio()).isEqualTo("Proyecto de electrónica ambiental");
        assertThat(response.status()).isEqualTo("APPROVED");
        assertThat(response.profilePictureUrl()).isEqualTo("http://localhost/profile-test.jpg");
        assertThat(response.hasUser()).isTrue();
        assertThat(response.createdAt()).isEqualTo(profile.getCreatedAt());
    }

    @Test
    void shouldMapArtistProfileToResponse_withoutUser() {
        // given
        ArtistProfile profile = ArtistProfile.builder()
                .id(UUID.randomUUID())
                .artistName("Nova Luna")
                .bio("Ambient y downtempo")
                .user(null)
                .status(ArtistProfile.Status.PENDING)
                .createdAt(LocalDateTime.now())
                .build();

        // when
        ArtistProfileResponse response = mapper.toArtistProfileResponse(profile);

        // then
        assertThat(response.profilePictureUrl()).isNull();
        assertThat(response.hasUser()).isFalse();
        assertThat(response.status()).isEqualTo("PENDING");
    }
}
