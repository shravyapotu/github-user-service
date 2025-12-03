package com.example.github_user_service.controller;

import com.example.github_user_service.exception.ExternalServiceException;
import com.example.github_user_service.exception.ResourceNotFoundException;
import com.example.github_user_service.model.UserResponse;
import com.example.github_user_service.service.GitHubService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GitHubController.class)
class GitHubControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GitHubService service;

    // 1. SUCCESS CASE
    @Test
    void getUserSuccess() throws Exception {
        UserResponse mockResponse = new UserResponse();
        mockResponse.setUser_name("john");

        Mockito.when(service.getUser("john"))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/api/github/user/john")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_name").value("john"));
    }

    // 2. USER NOT FOUND
    @Test
    void getUserNotFound() throws Exception {
        Mockito.when(service.getUser("unknown"))
                .thenThrow(new ResourceNotFoundException("User not found"));

        mockMvc.perform(get("/api/github/user/unknown"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("User not found"));
    }

    // 3. INVALID USERNAME FORMAT
    @Test
    void getUserInvalidUsernamePattern() throws Exception {
        mockMvc.perform(get("/api/github/user/john@doe"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("invalid GitHub username"));
    }

    // 4. MISSING USERNAME (/user or /user/)
    @Test
    void getUserMissingUsername() throws Exception {
        mockMvc.perform(get("/api/github/user"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("username must not be blank"));
    }

    @Test
    void getUserMissingUsernameTrailingSlash() throws Exception {
        mockMvc.perform(get("/api/github/user/"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("username must not be blank"));
    }

    // 5. EXTERNAL SERVICE ERROR (GitHub API Down)
    @Test
    void getUserExternalServiceError() throws Exception {
        Mockito.when(service.getUser(anyString()))
                .thenThrow(new ExternalServiceException("GitHub API is unavailable"));

        mockMvc.perform(get("/api/github/user/john"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.error").value("GitHub API is unavailable"));
    }

    // 6. INTERNAL ERROR
    @Test
    void getUserInternalServerError() throws Exception {
        Mockito.when(service.getUser("john"))
                .thenThrow(new RuntimeException("Unexpected failure"));

        mockMvc.perform(get("/api/github/user/john"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Internal server error"));
    }
}
