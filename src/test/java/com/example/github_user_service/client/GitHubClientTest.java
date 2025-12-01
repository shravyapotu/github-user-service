package com.example.github_user_service.client;

import com.example.github_user_service.exception.ExternalServiceException;
import com.example.github_user_service.exception.ResourceNotFoundException;
import com.example.github_user_service.model.GithubRepo;
import com.example.github_user_service.model.GithubUserApi;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GitHubClientTest {

    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;
    private GitHubClient client;

    private final String BASE_URL = "https://api.github.com";

    @BeforeEach
    void setup() {
        restTemplate = mock(RestTemplate.class);
        objectMapper = new ObjectMapper();
        client = new GitHubClient(restTemplate, objectMapper, BASE_URL, "");
    }

    // ------------------------------
    // fetchUser Tests
    // ------------------------------

    @Test
    void testFetchUserSuccess() throws Exception {
        String json = """
                {
                  "login": "octocat",
                  "name": "The Octocat",
                  "avatar_url": "http://img",
                  "location": "Earth",
                  "email": "octo@example.com",
                  "url": "http://api.github.com/users/octocat",
                  "created_at": "2020-01-01T00:00:00Z"
                }
                """;

        ResponseEntity<String> response =
                new ResponseEntity<>(json, HttpStatus.OK);

        when(restTemplate.exchange(
                ArgumentMatchers.eq(BASE_URL + "/users/octocat"),
                ArgumentMatchers.eq(HttpMethod.GET),
                any(HttpEntity.class),
                ArgumentMatchers.eq(String.class)
        )).thenReturn(response);

        Optional<GithubUserApi> result = client.fetchUser("octocat");

        assertTrue(result.isPresent());
        assertEquals("octocat", result.get().getLogin());
        assertEquals("The Octocat", result.get().getName());
    }

    @Test
    void testFetchUserNotFound() {
        
        when(restTemplate.exchange(
                ArgumentMatchers.eq(BASE_URL + "/users/octocat"),
                ArgumentMatchers.eq(HttpMethod.GET),
                any(HttpEntity.class),
                ArgumentMatchers.eq(String.class)
        )).thenReturn(new ResponseEntity<>("1234", HttpStatus.NOT_FOUND));

         Optional<GithubUserApi> result = client.fetchUser("octocat");
assertFalse(result.isPresent());
    }

    @Test
    void testFetchUserOtherError() {
        when(restTemplate.exchange(
                ArgumentMatchers.eq(BASE_URL + "/users/errorUser"),
                ArgumentMatchers.eq(HttpMethod.GET),
                any(HttpEntity.class),
                ArgumentMatchers.eq(String.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_GATEWAY));

        assertThrows(ExternalServiceException.class,
                () -> client.fetchUser("errorUser"));
    }

    // ------------------------------
    // fetchRepos Tests
    // ------------------------------

    @Test
    void testFetchReposSuccess() throws Exception {
        String json = """
                [
                  { "name": "repo1", "html_url": "http://github.com/x/repo1" },
                  { "name": "repo2", "html_url": "http://github.com/x/repo2" }
                ]
                """;

        ResponseEntity<String> response =
                new ResponseEntity<>(json, HttpStatus.OK);

        when(restTemplate.exchange(
                ArgumentMatchers.eq(BASE_URL + "/users/octocat/repos"),
                ArgumentMatchers.eq(HttpMethod.GET),
                any(HttpEntity.class),
                ArgumentMatchers.eq(String.class)
        )).thenReturn(response);

        List<GithubRepo> repos = client.fetchRepos("octocat");

        assertEquals(2, repos.size());
        assertEquals("repo1", repos.get(0).getName());
    }

    @Test
    void testFetchReposNotFoundReturnsEmpty() {
        when(restTemplate.exchange(
                ArgumentMatchers.eq(BASE_URL + "/users/missing/repos"),
                ArgumentMatchers.eq(HttpMethod.GET),
                any(HttpEntity.class),
                ArgumentMatchers.eq(String.class)
        )).thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        List<GithubRepo> result = client.fetchRepos("missing");

        assertTrue(result.isEmpty());
    }

    @Test
    void testFetchReposOtherError() {
        when(restTemplate.exchange(
                ArgumentMatchers.eq(BASE_URL + "/users/error/repos"),
                ArgumentMatchers.eq(HttpMethod.GET),
                any(HttpEntity.class),
                ArgumentMatchers.eq(String.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_GATEWAY));

        assertThrows(ExternalServiceException.class,
                () -> client.fetchRepos("error"));
    }
}
