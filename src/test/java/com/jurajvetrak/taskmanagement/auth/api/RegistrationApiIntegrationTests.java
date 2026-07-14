package com.jurajvetrak.taskmanagement.auth.api;

import com.jurajvetrak.taskmanagement.support.ApiIntegrationTest;
import com.jurajvetrak.taskmanagement.user.persistence.Role;
import com.jurajvetrak.taskmanagement.user.persistence.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ApiIntegrationTest
@Transactional
class RegistrationApiIntegrationTests {

  private static final String RAW_PASSWORD = "Demo123!";

  private final MockMvc mockMvc;
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  RegistrationApiIntegrationTests(
      MockMvc mockMvc,
      UserRepository userRepository,
      PasswordEncoder passwordEncoder
  ) {
    this.mockMvc = mockMvc;
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  @Test
  void registrationCreatesNormalizedUserAndReturnsSafeResponse() throws Exception {
    var result = mockMvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "firstName": "  Juraj  ",
                  "surname": "  Vetrak  ",
                  "email": "  JURAJ.REGISTRATION@EXAMPLE.COM  ",
                  "password": "Demo123!"
                }
                """))
        .andExpect(status().isCreated())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").isNotEmpty())
        .andExpect(jsonPath("$.firstName").value("Juraj"))
        .andExpect(jsonPath("$.surname").value("Vetrak"))
        .andExpect(jsonPath("$.email").value("juraj.registration@example.com"))
        .andExpect(jsonPath("$.role").value("USER"))
        .andExpect(jsonPath("$.createdAt").isNotEmpty())
        .andExpect(jsonPath("$.password").doesNotExist())
        .andExpect(jsonPath("$.passwordHash").doesNotExist())
        .andReturn();

    var user = userRepository.findByEmail("juraj.registration@example.com").orElseThrow();

    assertThat(user.getFirstName()).isEqualTo("Juraj");
    assertThat(user.getSurname()).isEqualTo("Vetrak");
    assertThat(user.getRole()).isEqualTo(Role.USER);
    assertThat(user.getCreatedAt()).isNotNull();
    assertThat(user.getPasswordHash()).isNotEqualTo(RAW_PASSWORD);
    assertThat(passwordEncoder.matches(RAW_PASSWORD, user.getPasswordHash())).isTrue();
    assertThat(result.getResponse().getHeader(HttpHeaders.LOCATION))
        .isEqualTo("/api/v1/users/" + user.getId());
    assertThat(result.getResponse().getContentAsString())
        .doesNotContain(RAW_PASSWORD, "passwordHash", "\"password\"");
  }

  @Test
  void duplicateEmailReturnsConflictProblemDetail() throws Exception {
    mockMvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "firstName": "Duplicate",
                  "surname": "User",
                  "email": "ADMIN@EXAMPLE.COM",
                  "password": "Demo123!"
                }
                """))
        .andExpect(status().isConflict())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.title").value("Conflict"))
        .andExpect(jsonPath("$.status").value(409))
        .andExpect(jsonPath("$.detail")
            .value("An account with this email already exists"))
        .andExpect(jsonPath("$.instance").value("/api/v1/auth/register"))
        .andExpect(jsonPath("$.code").value("EMAIL_ALREADY_EXISTS"));
  }

  @Test
  void invalidRegistrationReturnsFieldErrors() throws Exception {
    mockMvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "firstName": "   ",
                  "surname": "   ",
                  "email": "not-an-email",
                  "password": "short"
                }
                """))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.title").value("Validation failed"))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.detail").value("Request validation failed"))
        .andExpect(jsonPath("$.instance").value("/api/v1/auth/register"))
        .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
        .andExpect(jsonPath("$.fieldErrors.firstName").isArray())
        .andExpect(jsonPath("$.fieldErrors.surname").isArray())
        .andExpect(jsonPath("$.fieldErrors.email").isArray())
        .andExpect(jsonPath("$.fieldErrors.password").isArray());
  }

  @Test
  void multibytePasswordOverBcryptLimitReturnsPasswordFieldError() throws Exception {
    var multibytePassword = "😀".repeat(20);
    var body = """
        {
          "firstName": "Unicode",
          "surname": "Password",
          "email": "unicode.password@example.com",
          "password": "%s"
        }
        """.formatted(multibytePassword);

    mockMvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(body))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.code").value("VALIDATION_FAILED"))
        .andExpect(jsonPath("$.fieldErrors.password").isArray());
  }

  @Test
  void malformedJsonReturnsMalformedRequestProblemDetail() throws Exception {
    mockMvc.perform(post("/api/v1/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {
                  "firstName": "Broken",
                  "surname": "Json",
                  "email": "broken@example.com",
                  "password": "Demo123!"
                """))
        .andExpect(status().isBadRequest())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.title").value("Malformed request"))
        .andExpect(jsonPath("$.status").value(400))
        .andExpect(jsonPath("$.detail").value("Request body is missing or malformed"))
        .andExpect(jsonPath("$.instance").value("/api/v1/auth/register"))
        .andExpect(jsonPath("$.code").value("MALFORMED_REQUEST"));
  }
}
