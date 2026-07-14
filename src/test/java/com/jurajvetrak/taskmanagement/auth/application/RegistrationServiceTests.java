package com.jurajvetrak.taskmanagement.auth.application;

import com.jurajvetrak.taskmanagement.auth.api.RegisterRequest;
import com.jurajvetrak.taskmanagement.user.api.UserMapper;
import com.jurajvetrak.taskmanagement.user.persistence.UserEntity;
import com.jurajvetrak.taskmanagement.user.persistence.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegistrationServiceTests {

  @Mock
  private UserRepository userRepository;

  @Mock
  private PasswordEncoder passwordEncoder;

  @Mock
  private UserMapper userMapper;

  @InjectMocks
  private RegistrationService registrationService;

  @Test
  void insertTimeConstraintRaceIsTranslatedToDuplicateEmail() {
    var request = new RegisterRequest(
        "Race",
        "Condition",
        "race@example.com",
        "Demo123!"
    );

    when(userRepository.existsByEmail("race@example.com")).thenReturn(false);
    when(passwordEncoder.encode("Demo123!")).thenReturn("x".repeat(60));
    when(userRepository.saveAndFlush(any(UserEntity.class)))
        .thenThrow(new DataIntegrityViolationException("constraint"));

    assertThatThrownBy(() -> registrationService.register(request))
        .isInstanceOf(EmailAlreadyExistsException.class)
        .hasMessage("An account with this email already exists");
  }
}
