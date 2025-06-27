package com.edwindev.springtunes_api.modules.user;

import com.edwindev.springtunes_api.auth.AppUserDetails;
import com.edwindev.springtunes_api.common.exception.ErrorCode;
import com.edwindev.springtunes_api.modules.user.dto.AuthenticatedUserData;
import com.edwindev.springtunes_api.modules.user.dto.UserCreateData;
import com.edwindev.springtunes_api.modules.user.dto.UserMapper;
import com.edwindev.springtunes_api.modules.user.exception.UserCreateException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserMapper authUserMapper;

    @Test
    void shouldCreateVerifiedUserSuccessfully() {
        // Arrange
        String uid = "abc123";
        String email = "test@example.com";

        UserCreateData token = UserCreateData.builder()
                .userID(uid)
                .email(email)
                .build();
        when(userRepository.existsByEmailIgnoreCase(email)).thenReturn(false);

        User userToSave = User.builder()
                .id(uid)
                .displayName(email)
                .email(email)
                .role(User.Role.USER)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(userToSave);

        // Act
        AppUserDetails result = userService.createVerifiedUser(token);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getUser())
                .extracting(User::getId, User::getEmail, User::getRole)
                .containsExactly(uid, email, User.Role.USER);

        verify(userRepository).existsByEmailIgnoreCase(email);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void shouldThrowExceptionWhenUserAlreadyExists() {
        // Arrange
        String email = "existing@example.com";
        UserCreateData token = UserCreateData.builder()
                .email(email)
                .build();
        when(userRepository.existsByEmailIgnoreCase(email)).thenReturn(true);

        // Act & Assert
        assertThatThrownBy(() -> userService.createVerifiedUser(token))
                .isInstanceOf(UserCreateException.class)
                .hasMessage("User already exists.")
                .extracting("errorCode")
                .isEqualTo(ErrorCode.CREATE_USER);

        verify(userRepository).existsByEmailIgnoreCase(email);
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void shouldReturnAuthenticatedUserData_whenUserIsInContext() {
        // Arrange
        User user = User.builder()
                .id("xyz")
                .displayName("Test User")
                .email("test@example.com")
                .role(User.Role.USER)
                .build();

        AppUserDetails userDetails = new AppUserDetails(user);
        Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);

        AuthenticatedUserData expectedDto = new AuthenticatedUserData(
                user.getId(), user.getDisplayName(), user.getEmail(), user.getProfilePictureUrl(), user.getRole().name()
        );

        given(authUserMapper.toAuthenticatedUserData(user)).willReturn(expectedDto);

        // Act
        AuthenticatedUserData result = userService.getAuthenticatedUserData();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo("xyz");
        assertThat(result.email()).isEqualTo("test@example.com");
        assertThat(result.role()).isEqualTo("USER");
    }
}
