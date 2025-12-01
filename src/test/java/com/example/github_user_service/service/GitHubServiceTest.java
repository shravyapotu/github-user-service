package com.example.github_user_service.service;

import com.example.github_user_service.client.GitHubClient;
import com.example.github_user_service.exception.ResourceNotFoundException;
import com.example.github_user_service.mapper.GithubMapper;
import com.example.github_user_service.model.GithubRepo;
import com.example.github_user_service.model.GithubUserApi;
import com.example.github_user_service.model.GithubUserResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GitHubServiceTest {

    @Mock
    private GitHubClient client;

    @Mock
    private GithubMapper mapper;

    @InjectMocks
    private GitHubService service;

    @Test
    void getUser_success() {
        GithubUserApi userApi = new GithubUserApi();
        userApi.setLogin("john");

        List<GithubRepo> repos = List.of();

        GithubUserResponse mappedResponse = new GithubUserResponse();
        mappedResponse.setUser_name("john");

        when(client.fetchUser("john")).thenReturn(Optional.of(userApi));
        when(client.fetchRepos("john")).thenReturn(repos);
        when(mapper.toGithubUserResponse(userApi, repos)).thenReturn(mappedResponse);

        GithubUserResponse result = service.getUser("john");

        assertNotNull(result);
        assertEquals("john", result.getUser_name());
    }

    @Test
    void getUser_userNotFound_shouldThrowException() {
        when(client.fetchUser("unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getUser("unknown"));
    }
}
