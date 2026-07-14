package com.jurajvetrak.taskmanagement.auth.api;

import com.jurajvetrak.taskmanagement.auth.application.RegistrationService;
import com.jurajvetrak.taskmanagement.user.api.UserResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final RegistrationService registrationService;

  @PostMapping("/register")
  public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
    var response = registrationService.register(request);
    var location = URI.create("/api/v1/users/" + response.id());
    return ResponseEntity.created(location).body(response);
  }
}
