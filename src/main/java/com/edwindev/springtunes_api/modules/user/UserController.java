package com.edwindev.springtunes_api.modules.user;

import com.edwindev.springtunes_api.modules.user.dto.AuthenticatedUserData;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller for user-related operations.
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Retrieves the authenticated user's data.
     *
     * @return The authenticated user's data.
     */
    @GetMapping("/me")
    public AuthenticatedUserData getAuthenticatedUserData() {
        return userService.getAuthenticatedUserData();
    }
}
