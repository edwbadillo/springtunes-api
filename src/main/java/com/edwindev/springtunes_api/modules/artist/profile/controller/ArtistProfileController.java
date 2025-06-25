package com.edwindev.springtunes_api.modules.artist.profile.controller;

import com.edwindev.springtunes_api.modules.artist.profile.dto.ArtistCreateRequest;
import com.edwindev.springtunes_api.modules.artist.profile.dto.ArtistProfileResponse;
import com.edwindev.springtunes_api.modules.artist.profile.dto.ArtistStatusRequest;
import com.edwindev.springtunes_api.modules.artist.profile.dto.ArtistUpdateRequest;
import com.edwindev.springtunes_api.modules.artist.profile.repository.ArtistProfile;
import com.edwindev.springtunes_api.modules.artist.profile.service.ArtistProfileService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/artists/profiles")
@RequiredArgsConstructor
public class ArtistProfileController {

    private final ArtistProfileService service;

    /**
     * Gets all artist profiles
     *
     * @param q        if provided, will filter by artist name
     * @param pageable pagination data
     * @return a page of artist profiles
     */
    @GetMapping
    public Page<ArtistProfileResponse> getAll(@RequestParam(required = false) String q, Pageable pageable) {
        return service.getAll(q, pageable);
    }

    /**
     * Gets all artist profiles by status
     *
     * @param status   the status of the artist profile
     * @param pageable pagination data
     * @return a page of artist profiles
     */
    @GetMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    public Page<ArtistProfileResponse> getByStatus(@RequestParam ArtistProfile.Status status, Pageable pageable) {
        return service.getByStatus(status, pageable);
    }

    /**
     * Gets an artist profile by ID
     *
     * @param id the ID of the artist profile
     * @return the artist profile
     */
    @GetMapping("/{id}")
    public ArtistProfileResponse getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    /**
     * Creates a new artist profile
     *
     * @param request the request with the artist data
     * @return the created artist profile
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ArtistProfileResponse create(@Valid @RequestBody ArtistCreateRequest request) {
        return service.create(request);
    }

    /**
     * Updates an artist profile
     *
     * @param id      the ID of the artist profile
     * @param request the request with the artist data
     * @return the updated artist profile
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('ARTIST')")
    public ArtistProfileResponse update(@PathVariable UUID id, @Valid @RequestBody ArtistUpdateRequest request) {
        return service.update(id, request);
    }

    /**
     * Updates the status of an artist profile
     *
     * @param request the request with the artist and status to update
     * @return the updated artist profile
     */
    @PutMapping("/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ArtistProfileResponse updateStatus(@Valid @RequestBody ArtistStatusRequest request) {
        return service.updateStatus(request);
    }
}
