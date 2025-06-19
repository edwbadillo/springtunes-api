package com.edwindev.springtunes_api.modules.music.genre.dto;

/**
 * The response for getting a genre.
 *
 * @param id          the genre id
 * @param name        the genre name
 * @param description the genre description
 */
public record GenreResponse(
        Integer id,
        String name,
        String description
) {
}
