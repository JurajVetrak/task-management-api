package com.jurajvetrak.taskmanagement.user.api;

import com.jurajvetrak.taskmanagement.user.persistence.Role;

import java.time.Instant;
import java.util.UUID;

public record UserResponse(
    UUID id,
    String firstName,
    String surname,
    String email,
    Role role,
    Instant createdAt
) {

}
