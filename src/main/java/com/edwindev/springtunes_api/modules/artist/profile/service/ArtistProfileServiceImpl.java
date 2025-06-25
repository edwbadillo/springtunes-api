package com.edwindev.springtunes_api.modules.artist.profile.service;

import com.edwindev.springtunes_api.common.dto.InvalidData;
import com.edwindev.springtunes_api.common.exception.ErrorCode;
import com.edwindev.springtunes_api.common.exception.InvalidDataException;
import com.edwindev.springtunes_api.modules.artist.profile.dto.*;
import com.edwindev.springtunes_api.modules.artist.profile.exception.ArtistProfileNotFoundException;
import com.edwindev.springtunes_api.modules.artist.profile.repository.ArtistProfile;
import com.edwindev.springtunes_api.modules.artist.profile.repository.ArtistProfileRepository;
import com.edwindev.springtunes_api.modules.user.User;
import com.edwindev.springtunes_api.modules.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArtistProfileServiceImpl implements ArtistProfileService {

    private final ArtistProfileRepository artistProfileRepository;
    private final UserRepository userRepository;
    private final ArtistProfileMapper mapper;

    @Override
    public ArtistProfileResponse create(ArtistCreateRequest request) {
        if (artistProfileRepository.existsByArtistNameIgnoreCase(request.artistName())) {
            throw new InvalidDataException(
                    InvalidData.builder()
                            .type(ErrorCode.UNIQUE_ERROR.code())
                            .field("artistName")
                            .value(request.artistName())
                            .message("The artist name already exists.")
                            .build()
            );
        }

        ArtistProfile artistProfile = ArtistProfile.builder()
                .artistName(request.artistName())
                .bio(request.bio())
                .build();

        artistProfile = artistProfileRepository.save(artistProfile);
        return mapper.toArtistProfileResponse(artistProfile);
    }

    @Override
    @Transactional
    public ArtistProfileResponse update(UUID id, ArtistUpdateRequest request) {
        ArtistProfile artistProfile = artistProfileRepository.findById(id)
                .orElseThrow(() -> new ArtistProfileNotFoundException("Artist profile with id " + id + " not found."));

        updateArtistUser(artistProfile, request);

        artistProfile.setBio(request.bio());
        artistProfile = artistProfileRepository.save(artistProfile);

        return mapper.toArtistProfileResponse(artistProfile);
    }

    @Override
    public Page<ArtistProfileResponse> getAll(String name, Pageable pageable) {
        if (Objects.nonNull(name) && !name.isEmpty()) {
            return artistProfileRepository.searchByArtistName(name, pageable)
                    .map(mapper::toArtistProfileResponse);
        }

        return artistProfileRepository.findAll(pageable)
                .map(mapper::toArtistProfileResponse);
    }

    @Override
    public ArtistProfileResponse getById(UUID id) {
        ArtistProfile artistProfile = artistProfileRepository.findById(id)
                .orElseThrow(() -> new ArtistProfileNotFoundException("Artist profile with id " + id + " not found."));

        return mapper.toArtistProfileResponse(artistProfile);
    }

    @Override
    public Page<ArtistProfileResponse> getByStatus(ArtistProfile.Status status, Pageable pageable) {
        if (Objects.nonNull(status))
            return artistProfileRepository.findAllByStatus(status, pageable)
                    .map(mapper::toArtistProfileResponse);

        return Page.empty(pageable);
    }

    @Override
    public ArtistProfileResponse updateStatus(ArtistStatusRequest request) {
        ArtistProfile artistProfile = artistProfileRepository.findById(request.id())
                .orElseThrow(() -> new ArtistProfileNotFoundException("Artist profile with id " + request.id() + " not found."));

        artistProfile.setStatus(ArtistProfile.Status.valueOf(request.status()));
        artistProfile = artistProfileRepository.save(artistProfile);

        return mapper.toArtistProfileResponse(artistProfile);
    }

    private void updateArtistUser(ArtistProfile artistProfile, ArtistUpdateRequest request) {
        if (!artistProfile.hasUser()) return;

        User user = artistProfile.getUser();
        user.setProfilePictureUrl(request.profilePictureUrl());
        userRepository.save(user);
    }
}
