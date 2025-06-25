package com.edwindev.springtunes_api.modules.artist.profile.service;

import com.edwindev.springtunes_api.modules.artist.profile.dto.ArtistCreateRequest;
import com.edwindev.springtunes_api.modules.artist.profile.dto.ArtistProfileResponse;
import com.edwindev.springtunes_api.modules.artist.profile.dto.ArtistStatusRequest;
import com.edwindev.springtunes_api.modules.artist.profile.dto.ArtistUpdateRequest;
import com.edwindev.springtunes_api.modules.artist.profile.repository.ArtistProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * The service for artist profiles.
 */
public interface ArtistProfileService {

    /**
     * Creates a new artist profile.
     *
     * @param request the request with the artist data.
     * @return the created artist profile.
     */
    ArtistProfileResponse create(ArtistCreateRequest request);

    /**
     * Updates an artist profile.
     *
     * @param id      the ID of the artist profile
     * @param request the request with the artist data
     * @return the updated artist profile
     */
    ArtistProfileResponse update(UUID id, ArtistUpdateRequest request);

    /**
     * Gets all artist profiles.
     *
     * @param name     if provided, will filter by artist name
     * @param pageable pagination data
     * @return a page of artist profiles
     */
    Page<ArtistProfileResponse> getAll(String name, Pageable pageable);

    /**
     * Gets an artist profile by ID.
     *
     * @param id the ID of the artist profile
     * @return the artist profile
     */
    ArtistProfileResponse getById(UUID id);

    /**
     * Gets all artist profiles by status
     *
     * @param status   the status of the artist profile
     * @param pageable pagination data
     * @return a page of artist profiles
     */
    Page<ArtistProfileResponse> getByStatus(ArtistProfile.Status status, Pageable pageable);

    /**
     * Updates the status of an artist profile
     *
     * @param request the request with the artist and status to update
     * @return the updated artist profile
     */
    ArtistProfileResponse updateStatus(ArtistStatusRequest request);
}
