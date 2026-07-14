package com.jurajvetrak.taskmanagement.common.error;

import com.jurajvetrak.taskmanagement.support.ApiIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ApiIntegrationTest
@Import(ErrorProbeController.class)
class ErrorContractIntegrationTests {

  private final MockMvc mockMvc;

  ErrorContractIntegrationTests(MockMvc mockMvc) {
    this.mockMvc = mockMvc;
  }

  @Test
  void unexpectedFailureReturnsGenericProblemDetail() throws Exception {
    mockMvc.perform(get("/test/unexpected-error"))
        .andExpect(status().isInternalServerError())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.type").value("about:blank"))
        .andExpect(jsonPath("$.title").value("Internal server error"))
        .andExpect(jsonPath("$.status").value(500))
        .andExpect(jsonPath("$.detail").value("An unexpected error occurred"))
        .andExpect(jsonPath("$.instance").value("/test/unexpected-error"))
        .andExpect(jsonPath("$.code").value(ApiErrorCode.INTERNAL_ERROR.name()))
        .andExpect(content().string(not(containsString("must-not-leak"))));
  }
}
