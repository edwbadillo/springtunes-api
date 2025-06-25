package com.edwindev.springtunes_api.modules.artist.profile;

import com.edwindev.springtunes_api.common.dto.InvalidData;
import com.edwindev.springtunes_api.common.exception.ErrorCode;
import com.edwindev.springtunes_api.common.exception.InvalidDataException;
import com.edwindev.springtunes_api.modules.artist.profile.dto.*;
import com.edwindev.springtunes_api.modules.artist.profile.exception.ArtistProfileNotFoundException;
import com.edwindev.springtunes_api.modules.artist.profile.repository.ArtistProfile;
import com.edwindev.springtunes_api.modules.artist.profile.repository.ArtistProfileRepository;
import com.edwindev.springtunes_api.modules.artist.profile.service.ArtistProfileServiceImpl;
import com.edwindev.springtunes_api.modules.user.User;
import com.edwindev.springtunes_api.modules.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ArtistProfileServiceTest {

    @InjectMocks
    ArtistProfileServiceImpl service;

    @Mock
    ArtistProfileRepository artistProfileRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ArtistProfileMapper artistProfileMapper;

    @Test
    void shouldCreateArtistProfileSuccessfully() {
        // given
        ArtistCreateRequest request = new ArtistCreateRequest("Nova Luna", "Proyecto de electrónica ambiental");
        ArtistProfile savedProfile = ArtistProfile.builder()
                .id(UUID.randomUUID())
                .artistName("Nova Luna")
                .bio("Proyecto de electrónica ambiental")
                .status(ArtistProfile.Status.PENDING)
                .build();

        ArtistProfileResponse expectedResponse = ArtistProfileResponse.builder()
                .id(savedProfile.getId())
                .artistName("Nova Luna")
                .bio("Proyecto de electrónica ambiental")
                .status("PENDING")
                .hasUser(false)
                .createdAt(LocalDateTime.now())
                .build();

        given(artistProfileRepository.existsByArtistNameIgnoreCase("Nova Luna")).willReturn(false);
        given(artistProfileRepository.save(any(ArtistProfile.class))).willReturn(savedProfile);
        given(artistProfileMapper.toArtistProfileResponse(savedProfile)).willReturn(expectedResponse);

        // when
        ArtistProfileResponse result = service.create(request);

        // then
        assertThat(result).isEqualTo(expectedResponse);
        verify(artistProfileRepository).save(any(ArtistProfile.class));
    }

    @Test
    void shouldThrowInvalidDataExceptionWhenArtistNameExists() {
        // given
        String duplicateName = "Nova Luna";
        ArtistCreateRequest request = new ArtistCreateRequest(duplicateName, "Ya existe");

        given(artistProfileRepository.existsByArtistNameIgnoreCase(duplicateName)).willReturn(true);

        // when / then
        assertThatThrownBy(() -> service.create(request))
                .isInstanceOf(InvalidDataException.class)
                .hasMessageContaining("The artist name already exists.")
                .extracting("errorData")
                .satisfies(data -> {
                    InvalidData invalidData = (InvalidData) data;
                    assertThat(invalidData.field()).isEqualTo("artistName");
                    assertThat(invalidData.value()).isEqualTo(duplicateName);
                    assertThat(invalidData.type()).isEqualTo(ErrorCode.UNIQUE_ERROR.code());
                });

        verify(artistProfileRepository, never()).save(any());
    }

    @Test
    void shouldUpdateArtistProfileSuccessfully() {
        // given
        UUID id = UUID.randomUUID();
        ArtistUpdateRequest request = new ArtistUpdateRequest("Nueva bio", "https://cdn.com/profile.png");

        User user = User.builder()
                .id("firebase-uid")
                .email("artist@email.com")
                .displayName("Artist")
                .profilePictureUrl("https://old-url.com/profile.png")
                .role(User.Role.ARTIST)
                .build();

        ArtistProfile existing = ArtistProfile.builder()
                .id(id)
                .artistName("Nova Luna")
                .bio("Bio antigua")
                .user(user)
                .status(ArtistProfile.Status.APPROVED)
                .build();

        ArtistProfile updated = ArtistProfile.builder()
                .id(id)
                .artistName("Nova Luna")
                .bio("Nueva bio")
                .user(user)
                .status(ArtistProfile.Status.APPROVED)
                .build();

        ArtistProfileResponse response = ArtistProfileResponse.builder()
                .id(id)
                .artistName("Nova Luna")
                .bio("Nueva bio")
                .status("APPROVED")
                .profilePictureUrl("https://cdn.com/profile.png")
                .hasUser(true)
                .createdAt(LocalDateTime.now())
                .build();

        given(artistProfileRepository.findById(id)).willReturn(Optional.of(existing));
        given(artistProfileRepository.save(any(ArtistProfile.class))).willReturn(updated);
        given(userRepository.save(any(User.class))).willReturn(user);
        given(artistProfileMapper.toArtistProfileResponse(updated)).willReturn(response);

        // when
        ArtistProfileResponse result = service.update(id, request);

        // then
        assertThat(result).isEqualTo(response);
        assertThat(existing.getBio()).isEqualTo("Nueva bio");
        assertThat(user.getProfilePictureUrl()).isEqualTo("https://cdn.com/profile.png");

        verify(userRepository).save(user);
        verify(artistProfileRepository).save(existing);
    }

    @Test
    void shouldThrowWhenArtistProfileNotFound() {
        // given
        UUID id = UUID.randomUUID();
        ArtistUpdateRequest request = new ArtistUpdateRequest("Bio", "https://cdn.com/profile.png");

        given(artistProfileRepository.findById(id)).willReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> service.update(id, request))
                .isInstanceOf(ArtistProfileNotFoundException.class)
                .hasMessageContaining("Artist profile with id " + id + " not found.");

        verify(artistProfileRepository, never()).save(any());
        verify(userRepository, never()).save(any());
    }

    @Test
    void shouldReturnAllArtistProfiles_whenNameIsNullOrEmpty() {
        // given
        Pageable pageable = PageRequest.of(0, 5);
        List<ArtistProfile> artists = List.of(
                ArtistProfile.builder().id(UUID.randomUUID()).artistName("Artist A").build(),
                ArtistProfile.builder().id(UUID.randomUUID()).artistName("Artist B").build()
        );
        Page<ArtistProfile> page = new PageImpl<>(artists);

        List<ArtistProfileResponse> responses = artists.stream()
                .map(a -> ArtistProfileResponse.builder()
                        .id(a.getId())
                        .artistName(a.getArtistName())
                        .bio(null)
                        .status(null)
                        .profilePictureUrl(null)
                        .hasUser(false)
                        .createdAt(null)
                        .build())
                .toList();

        given(artistProfileRepository.findAll(pageable)).willReturn(page);
        given(artistProfileMapper.toArtistProfileResponse(any())).willAnswer(invocation -> {
            ArtistProfile a = invocation.getArgument(0);
            return responses.stream()
                    .filter(r -> r.id().equals(a.getId()))
                    .findFirst()
                    .orElse(null);
        });

        // when
        Page<ArtistProfileResponse> result = service.getAll(null, pageable);

        // then
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).artistName()).isEqualTo("Artist A");
        assertThat(result.getContent().get(1).artistName()).isEqualTo("Artist B");
    }

    @Test
    void shouldSearchArtistProfiles_whenNameIsProvided() {
        // given
        Pageable pageable = PageRequest.of(0, 5);
        String name = "nova";
        ArtistProfile artist = ArtistProfile.builder()
                .id(UUID.randomUUID())
                .artistName("Nova Luna")
                .build();

        ArtistProfileResponse response = ArtistProfileResponse.builder()
                .id(artist.getId())
                .artistName("Nova Luna")
                .bio(null)
                .status(null)
                .profilePictureUrl(null)
                .hasUser(false)
                .createdAt(null)
                .build();

        Page<ArtistProfile> page = new PageImpl<>(List.of(artist));

        given(artistProfileRepository.searchByArtistName(name, pageable)).willReturn(page);
        given(artistProfileMapper.toArtistProfileResponse(artist)).willReturn(response);

        // when
        Page<ArtistProfileResponse> result = service.getAll(name, pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).artistName()).isEqualTo("Nova Luna");
    }

    @Test
    void shouldReturnEmptyPage_whenNoArtistProfilesFound() {
        // given
        Pageable pageable = PageRequest.of(0, 5);
        given(artistProfileRepository.findAll(pageable)).willReturn(Page.empty());

        // when
        Page<ArtistProfileResponse> result = service.getAll("", pageable);

        // then
        assertThat(result).isEmpty();
    }

    @Test
    void shouldReturnArtistProfile_whenIdExists() {
        // given
        UUID id = UUID.randomUUID();
        ArtistProfile artist = ArtistProfile.builder()
                .id(id)
                .artistName("Luna Waves")
                .status(ArtistProfile.Status.APPROVED)
                .build();

        ArtistProfileResponse response = ArtistProfileResponse.builder()
                .id(id)
                .artistName("Luna Waves")
                .status("APPROVED")
                .bio(null)
                .hasUser(false)
                .profilePictureUrl(null)
                .createdAt(null)
                .build();

        given(artistProfileRepository.findById(id)).willReturn(Optional.of(artist));
        given(artistProfileMapper.toArtistProfileResponse(artist)).willReturn(response);

        // when
        ArtistProfileResponse result = service.getById(id);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(id);
        assertThat(result.artistName()).isEqualTo("Luna Waves");
    }

    @Test
    void shouldThrowNotFoundException_whenIdDoesNotExist() {
        // given
        UUID id = UUID.randomUUID();
        given(artistProfileRepository.findById(id)).willReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> service.getById(id))
                .isInstanceOf(ArtistProfileNotFoundException.class)
                .hasMessageContaining("Artist profile with id " + id + " not found.");
    }

    @Test
    void shouldReturnArtistProfiles_whenStatusIsProvided() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        ArtistProfile.Status status = ArtistProfile.Status.APPROVED;

        ArtistProfile artist = ArtistProfile.builder()
                .id(UUID.randomUUID())
                .artistName("Luna Waves")
                .status(status)
                .build();

        ArtistProfileResponse response = ArtistProfileResponse.builder()
                .id(artist.getId())
                .artistName("Luna Waves")
                .status("APPROVED")
                .hasUser(false)
                .build();

        Page<ArtistProfile> artistPage = new PageImpl<>(List.of(artist));

        given(artistProfileRepository.findAllByStatus(status, pageable)).willReturn(artistPage);
        given(artistProfileMapper.toArtistProfileResponse(artist)).willReturn(response);

        // when
        Page<ArtistProfileResponse> result = service.getByStatus(status, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().getFirst().artistName()).isEqualTo("Luna Waves");
    }

    @Test
    void shouldReturnEmptyPage_whenStatusIsNull() {
        // given
        Pageable pageable = PageRequest.of(0, 10);

        // when
        Page<ArtistProfileResponse> result = service.getByStatus(null, pageable);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }

    @Test
    void shouldUpdateArtistProfileStatusSuccessfully() {
        // given
        UUID artistId = UUID.randomUUID();
        ArtistStatusRequest request = new ArtistStatusRequest(artistId, "APPROVED");

        ArtistProfile artistProfile = ArtistProfile.builder()
                .id(artistId)
                .artistName("Luna Waves")
                .status(ArtistProfile.Status.PENDING)
                .build();

        ArtistProfile updatedProfile = ArtistProfile.builder()
                .id(artistId)
                .artistName("Luna Waves")
                .status(ArtistProfile.Status.APPROVED)
                .build();

        ArtistProfileResponse expectedResponse = ArtistProfileResponse.builder()
                .id(artistId)
                .artistName("Luna Waves")
                .status("APPROVED")
                .build();

        given(artistProfileRepository.findById(artistId)).willReturn(Optional.of(artistProfile));
        given(artistProfileRepository.save(any(ArtistProfile.class))).willReturn(updatedProfile);
        given(artistProfileMapper.toArtistProfileResponse(updatedProfile)).willReturn(expectedResponse);

        // when
        ArtistProfileResponse result = service.updateStatus(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(artistId);
        assertThat(result.status()).isEqualTo("APPROVED");
    }

    @Test
    void shouldThrowException_whenArtistProfileNotFound() {
        // given
        UUID artistId = UUID.randomUUID();
        ArtistStatusRequest request = new ArtistStatusRequest(artistId, "REJECTED");

        given(artistProfileRepository.findById(artistId)).willReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> service.updateStatus(request))
                .isInstanceOf(ArtistProfileNotFoundException.class)
                .hasMessage("Artist profile with id " + artistId + " not found.");
    }

}
