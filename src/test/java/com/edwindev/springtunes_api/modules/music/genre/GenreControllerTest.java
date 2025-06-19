package com.edwindev.springtunes_api.modules.music.genre;

import com.edwindev.springtunes_api.common.dto.InvalidData;
import com.edwindev.springtunes_api.common.exception.InvalidDataException;
import com.edwindev.springtunes_api.config.ControllerTest;
import com.edwindev.springtunes_api.modules.music.genre.controller.GenreController;
import com.edwindev.springtunes_api.modules.music.genre.dto.GenreRequest;
import com.edwindev.springtunes_api.modules.music.genre.dto.GenreResponse;
import com.edwindev.springtunes_api.modules.music.genre.exception.GenreNotFoundException;
import com.edwindev.springtunes_api.modules.music.genre.service.GenreService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ControllerTest(controllers = GenreController.class)
public class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private GenreService service;

    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setupSecurityContext() {
        var auth = new UsernamePasswordAuthenticationToken(
                "admin", null,
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @Test
    void shouldReturnGenresPage() throws Exception {
        var genres = List.of(new GenreResponse(1, "House", null));
        when(service.getAll(any(Pageable.class))).thenReturn(new PageImpl<>(genres));

        mockMvc.perform(get("/api/v1/music/genres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("House"));
    }

    @Test
    void shouldReturnGenreById() throws Exception {
        when(service.getById(1)).thenReturn(new GenreResponse(1, "Techno", "Electronic"));

        mockMvc.perform(get("/api/v1/music/genres/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Techno"));
    }

    @Test
    void shouldReturn404IfGenreNotFound() throws Exception {
        when(service.getById(99)).thenThrow(new GenreNotFoundException("Genre not found"));

        mockMvc.perform(get("/api/v1/music/genres/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Genre not found"));
    }

    @Test
    void shouldCreateGenreSuccessfully() throws Exception {
        var request = new GenreRequest("Trance", "Melodic");
        var response = new GenreResponse(3, "Trance", "Melodic");

        when(service.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/v1/music/genres")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.name").value("Trance"));
    }

    @Test
    void shouldReturn400WhenNameIsBlank() throws Exception {
        var invalidRequest = new GenreRequest("", "Missing name");

        mockMvc.perform(post("/api/v1/music/genres")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors[0].field").value("name"));
    }

    @Test
    void shouldReturn422WhenNameIsNotUnique() throws Exception {
        var request = new GenreRequest("House", null);
        when(service.create(any())).thenThrow(
                new InvalidDataException(
                        InvalidData.builder()
                                .type("UNIQUE_ERROR")
                                .field("name")
                                .value("House")
                                .message("Genre with name House already exists.")
                                .build()
                )
        );

        mockMvc.perform(post("/api/v1/music/genres")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error.type").value("UNIQUE_ERROR"))
                .andExpect(jsonPath("$.error.field").value("name"));
    }

    @Test
    void shouldUpdateGenreSuccessfully() throws Exception {
        var request = new GenreRequest("Ambient", "Soft sounds");

        mockMvc.perform(put("/api/v1/music/genres/1")
                        .contentType(APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(service).update(eq(1), any());
    }

    @Test
    void shouldDeleteGenreSuccessfully() throws Exception {
        mockMvc.perform(delete("/api/v1/music/genres/1"))
                .andExpect(status().isNoContent());

        verify(service).deleteById(1);
    }
}
