package com.example.github_user_service.service;

import com.example.github_user_service.client.GitHubClient;
import com.example.github_user_service.exception.ResourceNotFoundException;
import com.example.github_user_service.mapper.GithubMapper;
import com.example.github_user_service.model.GithubRepo;
import com.example.github_user_service.model.GithubUser;
import com.example.github_user_service.model.UserResponse;
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

    //Tests a successful end-to-end data flow

    @Test
    void getUserSuccess() {
        GithubUser userApi = new GithubUser();
        userApi.setLogin("john");

        List<GithubRepo> repos = List.of();

        UserResponse mappedResponse = new UserResponse();
        mappedResponse.setUser_name("shravya");

        when(client.fetchUser("shravya")).thenReturn(Optional.of(userApi));
        when(client.fetchRepos("shravya")).thenReturn(repos);
        when(mapper.toGithubUserResponse(userApi, repos)).thenReturn(mappedResponse);

        UserResponse result = service.getUser("shravya");

        assertNotNull(result);
        assertEquals("shravya", result.getUser_name());
    }
//Tests the core business logic rule

    @Test
    void testGetUserNotFoundThrowsException() {
        when(client.fetchUser("unknown")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> service.getUser("unknown"));
    }
}
