package com.jurajvetrak.taskmanagement.auth.application;

public final class EmailAlreadyExistsException extends RuntimeException {

  private static final String MESSAGE = "An account with this email already exists";

  public EmailAlreadyExistsException() {
    super(MESSAGE);
  }

  public EmailAlreadyExistsException(Throwable cause) {
    super(MESSAGE, cause);
  }
}
