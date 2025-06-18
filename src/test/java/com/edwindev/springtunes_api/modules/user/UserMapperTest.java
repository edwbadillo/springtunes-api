package com.edwindev.springtunes_api.modules.user;

import com.edwindev.springtunes_api.modules.user.dto.AuthenticatedUserData;
import com.edwindev.springtunes_api.modules.user.dto.UserMapper;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.assertj.core.api.Assertions.assertThat;

public class UserMapperTest {
    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void shouldMapUserToAuthenticatedUserData() {
        // given
        User user = User.builder()
                .id("XYZ")
                .displayName("DisplayName")
                .email("a@b.com")
                .profilePictureUrl("https://google.com")
                .role(User.Role.USER)
                .build();

        // when
        AuthenticatedUserData authenticatedUserData = mapper.toAuthenticatedUserData(user);

        // then
        assertThat(authenticatedUserData).isEqualTo(new AuthenticatedUserData("XYZ", "DisplayName", "a@b.com", "https://google.com", "USER"));
    }
}
