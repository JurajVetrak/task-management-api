package com.jurajvetrak.taskmanagement.auth.application;

import com.jurajvetrak.taskmanagement.auth.api.RegisterRequest;
import com.jurajvetrak.taskmanagement.user.api.UserMapper;
import com.jurajvetrak.taskmanagement.user.api.UserResponse;
import com.jurajvetrak.taskmanagement.user.persistence.Role;
import com.jurajvetrak.taskmanagement.user.persistence.UserEntity;
import com.jurajvetrak.taskmanagement.user.persistence.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistrationService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;

  @Transactional
  public UserResponse register(RegisterRequest request) {
    if (userRepository.existsByEmail(request.email())) {
      throw new EmailAlreadyExistsException();
    }

    var user = new UserEntity(
        request.firstName(),
        request.surname(),
        request.email(),
        passwordEncoder.encode(request.password()),
        Role.USER
    );

    try {
      return userMapper.toResponse(userRepository.saveAndFlush(user));
    } catch (DataIntegrityViolationException exception) {
      throw new EmailAlreadyExistsException(exception);
    }
  }
}
