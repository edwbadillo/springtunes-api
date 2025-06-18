package com.edwindev.springtunes_api.modules.user.dto;

import com.edwindev.springtunes_api.modules.user.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    AuthenticatedUserData toAuthenticatedUserData(User user);
}
