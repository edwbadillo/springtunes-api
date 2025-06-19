package com.edwindev.springtunes_api.modules.music.genre.dto;

import com.edwindev.springtunes_api.modules.music.genre.repository.Genre;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GenreMapper {

    GenreResponse toGenreResponse(Genre genre);

    List<GenreResponse> toGenreResponseList(List<Genre> genres);
}
