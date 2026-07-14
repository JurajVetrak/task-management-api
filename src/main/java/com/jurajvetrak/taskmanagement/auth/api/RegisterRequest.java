package com.jurajvetrak.taskmanagement.auth.api;

import com.jurajvetrak.taskmanagement.auth.api.validation.BcryptLength;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.Locale;

public record RegisterRequest(
    @NotBlank @Size(max = 100) String firstName,
    @NotBlank @Size(max = 100) String surname,
    @NotBlank @Email @Size(max = 254) String email,
    @NotBlank @Size(min = 8, max = 72) @BcryptLength String password
) {

  public RegisterRequest {
    firstName = strip(firstName);
    surname = strip(surname);
    email = strip(email);
    if (email != null) {
      email = email.toLowerCase(Locale.ROOT);
    }
  }

  private static String strip(String value) {
    return value == null ? null : value.strip();
  }
}
