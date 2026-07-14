package com.jurajvetrak.taskmanagement.auth.api.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.nio.charset.StandardCharsets;

public class BcryptLengthValidator implements ConstraintValidator<BcryptLength, String> {

  private static final int MAXIMUM_BYTES = 72;

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    return value == null
        || value.getBytes(StandardCharsets.UTF_8).length <= MAXIMUM_BYTES;
  }
}
