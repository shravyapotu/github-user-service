package com.example.github_user_service.controller;

import com.example.github_user_service.exception.ResourceNotFoundException;
import com.example.github_user_service.model.GithubUserResponse;
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

    @Test
    void getUser_success() throws Exception {
        GithubUserResponse mockResponse = new GithubUserResponse();
        mockResponse.setUser_name("john");

        Mockito.when(service.getUser("john"))
                .thenReturn(mockResponse);

        mockMvc.perform(get("/api/github/user/john")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_name").value("john"));
    }

    @Test
    void getUser_notFound() throws Exception {
        Mockito.when(service.getUser("unknown"))
                .thenThrow(new ResourceNotFoundException("GitHub user not found: unknown"));

        mockMvc.perform(get("/api/github/user/unknown"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getUser_blankUsername_shouldFailValidation() throws Exception {
        mockMvc.perform(get("/api/github/user/ "))
                .andExpect(status().isBadRequest());
    }
}
