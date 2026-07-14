package com.jurajvetrak.taskmanagement.user.api;

import com.jurajvetrak.taskmanagement.config.CentralMapperConfig;
import com.jurajvetrak.taskmanagement.user.persistence.UserEntity;
import org.mapstruct.Mapper;

@Mapper(config = CentralMapperConfig.class)
public interface UserMapper {

  UserResponse toResponse(UserEntity user);
}
