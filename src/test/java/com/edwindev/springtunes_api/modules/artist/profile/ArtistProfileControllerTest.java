package com.edwindev.springtunes_api.modules.artist.profile;

import com.edwindev.springtunes_api.common.exception.ErrorCode;
import com.edwindev.springtunes_api.config.ControllerTest;
import com.edwindev.springtunes_api.modules.artist.profile.controller.ArtistProfileController;
import com.edwindev.springtunes_api.modules.artist.profile.dto.ArtistCreateRequest;
import com.edwindev.springtunes_api.modules.artist.profile.dto.ArtistProfileResponse;
import com.edwindev.springtunes_api.modules.artist.profile.dto.ArtistStatusRequest;
import com.edwindev.springtunes_api.modules.artist.profile.dto.ArtistUpdateRequest;
import com.edwindev.springtunes_api.modules.artist.profile.exception.ArtistProfileNotFoundException;
import com.edwindev.springtunes_api.modules.artist.profile.repository.ArtistProfile;
import com.edwindev.springtunes_api.modules.artist.profile.service.ArtistProfileService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest(controllers = ArtistProfileController.class)
public class ArtistProfileControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    ArtistProfileService service;

    @Autowired
    ObjectMapper objectMapper;

    @Test
    void shouldReturnPaginatedListOfArtists() throws Exception {
        // given
        ArtistProfileResponse artist = ArtistProfileResponse.builder()
                .id(UUID.randomUUID())
                .artistName("Nova Luna")
                .bio("Experimental downtempo project")
                .status("APPROVED")
                .profilePictureUrl("https://example.com/pfp.jpg")
                .hasUser(true)
                .createdAt(null)
                .build();

        Page<ArtistProfileResponse> page = new PageImpl<>(List.of(artist));

        when(service.getAll(eq("Nova"), any())).thenReturn(page);

        // when / then
        mockMvc.perform(get("/api/v1/artists/profiles")
                        .param("q", "Nova")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].artistName").value("Nova Luna"))
                .andExpect(jsonPath("$.content[0].bio").value("Experimental downtempo project"))
                .andExpect(jsonPath("$.content[0].status").value("APPROVED"))
                .andExpect(jsonPath("$.content[0].hasUser").value(true));

        verify(service).getAll(eq("Nova"), any());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnArtistProfilesByStatus() throws Exception {
        // Given
        ArtistProfileResponse response1 = ArtistProfileResponse.builder()
                .id(UUID.randomUUID())
                .artistName("Nova Luna")
                .bio("Ambient Project")
                .status("APPROVED")
                .hasUser(false)
                .createdAt(LocalDateTime.now())
                .build();

        Page<ArtistProfileResponse> page = new PageImpl<>(List.of(response1));
        given(service.getByStatus(eq(ArtistProfile.Status.APPROVED), any(Pageable.class)))
                .willReturn(page);

        // When & Then
        mockMvc.perform(get("/api/v1/artists/profiles/status")
                        .param("status", "APPROVED")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].artistName").value("Nova Luna"))
                .andExpect(jsonPath("$.content[0].status").value("APPROVED"));
    }

    @Test
    void shouldReturnArtistProfileById() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        ArtistProfileResponse response = ArtistProfileResponse.builder()
                .id(id)
                .artistName("Nova Luna")
                .bio("Electronic Ambient")
                .status("APPROVED")
                .hasUser(false)
                .createdAt(LocalDateTime.now())
                .build();

        given(service.getById(id)).willReturn(response);

        // When & Then
        mockMvc.perform(get("/api/v1/artists/profiles/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.artistName").value("Nova Luna"));
    }

    @Test
    void shouldReturnNotFoundWhenArtistProfileDoesNotExist() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        given(service.getById(id))
                .willThrow(new ArtistProfileNotFoundException("Artist profile with id " + id + " not found."));

        // When & Then
        mockMvc.perform(get("/api/v1/artists/profiles/" + id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Artist profile with id " + id + " not found."))
                .andExpect(jsonPath("$.code").value(ErrorCode.NOT_FOUND.code()));
    }


    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateArtistProfile() throws Exception {
        // Given
        ArtistCreateRequest request = new ArtistCreateRequest("Nova Luna", "Electronic ambient artist");
        ArtistProfileResponse response = ArtistProfileResponse.builder()
                .id(UUID.randomUUID())
                .artistName(request.artistName())
                .bio(request.bio())
                .status("PENDING")
                .hasUser(false)
                .createdAt(LocalDateTime.now())
                .build();

        given(service.create(request)).willReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/artists/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.artistName").value("Nova Luna"))
                .andExpect(jsonPath("$.bio").value("Electronic ambient artist"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestWhenArtistNameIsEmpty() throws Exception {
        // Given: nombre vacío, inválido
        ArtistCreateRequest request = new ArtistCreateRequest("", "Some bio");

        // When & Then
        mockMvc.perform(post("/api/v1/artists/profiles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("artistName"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateArtistProfile() throws Exception {
        // Given
        UUID id = UUID.randomUUID();
        ArtistUpdateRequest request = new ArtistUpdateRequest("Updated bio", "https://cdn.example.com/profile.jpg");

        ArtistProfileResponse response = ArtistProfileResponse.builder()
                .id(id)
                .artistName("Nova Luna")
                .bio(request.bio())
                .profilePictureUrl(request.profilePictureUrl())
                .status("APPROVED")
                .hasUser(true)
                .createdAt(LocalDateTime.now())
                .build();

        given(service.update(eq(id), any())).willReturn(response);

        // When & Then
        mockMvc.perform(put("/api/v1/artists/profiles/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.bio").value("Updated bio"))
                .andExpect(jsonPath("$.profilePictureUrl").value("https://cdn.example.com/profile.jpg"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateArtistStatusSuccessfully() throws Exception {
        // Given
        UUID artistId = UUID.randomUUID();
        ArtistStatusRequest request = new ArtistStatusRequest(artistId, "APPROVED");

        ArtistProfileResponse response = ArtistProfileResponse.builder()
                .id(artistId)
                .artistName("Nova Luna")
                .status("APPROVED")
                .bio("Downtempo")
                .createdAt(LocalDateTime.now())
                .profilePictureUrl("https://cdn.example.com/pfp.jpg")
                .hasUser(true)
                .build();

        given(service.updateStatus(eq(request))).willReturn(response);

        // When & Then
        mockMvc.perform(put("/api/v1/artists/profiles/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(artistId.toString()))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldReturnBadRequestIfStatusIsMissing() throws Exception {
        // Given
        UUID artistId = UUID.randomUUID();
        String invalidRequestJson = """
                {
                    "id": "%s"
                }
                """.formatted(artistId);

        // When & Then
        mockMvc.perform(put("/api/v1/artists/profiles/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("status"));
    }

    @Test
    @WithMockUser(roles = "ARTIST")
    void shouldReturnForbiddenForNonAdmin() throws Exception {
        // Given
        ArtistStatusRequest request = new ArtistStatusRequest(UUID.randomUUID(), "REJECTED");

        // When & Then
        mockMvc.perform(put("/api/v1/artists/profiles/status")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

}
