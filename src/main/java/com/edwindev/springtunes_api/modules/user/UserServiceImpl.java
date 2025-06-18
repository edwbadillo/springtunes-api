package com.edwindev.springtunes_api.modules.user;

import com.edwindev.springtunes_api.auth.AppUserDetails;
import com.edwindev.springtunes_api.common.exception.ErrorCode;
import com.edwindev.springtunes_api.modules.user.dto.AuthenticatedUserData;
import com.edwindev.springtunes_api.modules.user.dto.UserMapper;
import com.edwindev.springtunes_api.modules.user.exception.UserCreateException;
import com.google.firebase.auth.FirebaseToken;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Implementation of the {@link UserService} interface.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper authUserMapper;

    @Override
    public AppUserDetails createVerifiedUser(FirebaseToken firebaseToken) {
        firebaseToken.getUid();
        String email = firebaseToken.getEmail();
        if (userRepository.existsByEmailIgnoreCase(email))
            throw new UserCreateException(ErrorCode.CREATE_USER, "User already exists.");

        User user = User.builder()
                .id(firebaseToken.getUid())
                .displayName(firebaseToken.getEmail())
                .email(email)
                .role(User.Role.USER)
                .build();
        user = userRepository.save(user);
        return new AppUserDetails(user);
    }

    @Override
    public AuthenticatedUserData getAuthenticatedUserData() {
        AppUserDetails userDetails = (AppUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (Objects.nonNull(userDetails))
            return authUserMapper.toAuthenticatedUserData(userDetails.getUser());
        return null;
    }
}
