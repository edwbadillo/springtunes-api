package com.edwindev.springtunes_api.modules.user;

import com.edwindev.springtunes_api.config.ControllerTest;
import com.edwindev.springtunes_api.modules.user.dto.AuthenticatedUserData;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ControllerTest(controllers = UserController.class)
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    @WithMockUser(username = "admin")
    void shouldReturnAuthenticatedUserData() throws Exception {
        // Arrange
        AuthenticatedUserData userData = new AuthenticatedUserData(
                "firebase-uid",
                "Test User",
                "test@example.com",
                null,
                "USER"
        );

        given(userService.getAuthenticatedUserData()).willReturn(userData);

        // Act & Assert
        mockMvc.perform(get("/api/v1/users/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("firebase-uid"))
                .andExpect(jsonPath("$.displayName").value("Test User"))
                .andExpect(jsonPath("$.email").value("test@example.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }
}
