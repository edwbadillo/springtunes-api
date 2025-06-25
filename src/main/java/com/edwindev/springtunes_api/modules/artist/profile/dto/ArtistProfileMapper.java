package com.edwindev.springtunes_api.modules.artist.profile.dto;

import com.edwindev.springtunes_api.modules.artist.profile.repository.ArtistProfile;
import com.edwindev.springtunes_api.modules.user.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Objects;

@Mapper(componentModel = "spring")
public interface ArtistProfileMapper {
    @Mapping(source = "status", target = "status", qualifiedByName = "statusToString")
    @Mapping(source = "user", target = "profilePictureUrl", qualifiedByName = "mapProfilePictureUrl")
    @Mapping(source = ".", target = "hasUser", qualifiedByName = "hasUser")
    ArtistProfileResponse toArtistProfileResponse(ArtistProfile artistProfile);

    @Named("statusToString")
    default String statusToString(ArtistProfile.Status status) {
        return Objects.nonNull(status) ? status.name() : null;
    }

    @Named("mapProfilePictureUrl")
    default String mapProfilePictureUrl(User user) {
        return Objects.nonNull(user) ? user.getProfilePictureUrl() : null;
    }

    @Named("hasUser")
    default boolean hasUser(ArtistProfile artistProfile) {
        return artistProfile.hasUser();
    }
}
