package com.jurajvetrak.taskmanagement.common.error;

import com.jurajvetrak.taskmanagement.auth.application.EmailAlreadyExistsException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);
  private static final URI ABOUT_BLANK = URI.create("about:blank");

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(
      MethodArgumentNotValidException exception,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request
  ) {
    var fieldErrors = exception.getBindingResult().getFieldErrors().stream()
        .collect(Collectors.groupingBy(
            FieldError::getField,
            LinkedHashMap::new,
            Collectors.mapping(
                fieldError -> Objects.requireNonNullElse(
                    fieldError.getDefaultMessage(),
                    "invalid value"
                ),
                Collectors.toList()
            )
        ));

    var problem = problem(
        HttpStatus.BAD_REQUEST,
        "Validation failed",
        "Request validation failed",
        ApiErrorCode.VALIDATION_FAILED,
        request
    );
    problem.setProperty("fieldErrors", fieldErrors);

    return handleExceptionInternal(
        exception,
        problem,
        headers,
        HttpStatus.BAD_REQUEST,
        request
    );
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException exception,
      HttpHeaders headers,
      HttpStatusCode status,
      WebRequest request
  ) {
    var problem = problem(
        HttpStatus.BAD_REQUEST,
        "Malformed request",
        "Request body is missing or malformed",
        ApiErrorCode.MALFORMED_REQUEST,
        request
    );

    return handleExceptionInternal(
        exception,
        problem,
        headers,
        HttpStatus.BAD_REQUEST,
        request
    );
  }

  @ExceptionHandler(EmailAlreadyExistsException.class)
  ResponseEntity<Object> handleEmailAlreadyExists(
      EmailAlreadyExistsException exception,
      WebRequest request
  ) {
    var problem = problem(
        HttpStatus.CONFLICT,
        "Conflict",
        "An account with this email already exists",
        ApiErrorCode.EMAIL_ALREADY_EXISTS,
        request
    );

    return handleExceptionInternal(
        exception,
        problem,
        new HttpHeaders(),
        HttpStatus.CONFLICT,
        request
    );
  }

  @ExceptionHandler(Exception.class)
  ResponseEntity<Object> handleUnexpectedException(Exception exception, WebRequest request) {
    LOG.error("Unexpected request processing failure: {}", exception.getClass().getName());

    var problem = problem(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "Internal server error",
        "An unexpected error occurred",
        ApiErrorCode.INTERNAL_ERROR,
        request
    );

    return handleExceptionInternal(
        exception,
        problem,
        new HttpHeaders(),
        HttpStatus.INTERNAL_SERVER_ERROR,
        request
    );
  }

  private ProblemDetail problem(
      HttpStatus status,
      String title,
      String detail,
      ApiErrorCode code,
      WebRequest request
  ) {
    var problem = ProblemDetail.forStatusAndDetail(status, detail);
    problem.setType(ABOUT_BLANK);
    problem.setTitle(title);
    problem.setProperty("code", code.name());

    if (request instanceof ServletWebRequest servletRequest) {
      problem.setInstance(URI.create(servletRequest.getRequest().getRequestURI()));
    }

    return problem;
  }
}
