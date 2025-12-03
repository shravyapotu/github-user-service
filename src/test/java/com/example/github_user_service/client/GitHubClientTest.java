package com.example.github_user_service.client;

import com.example.github_user_service.exception.ExternalServiceException;
import com.example.github_user_service.model.GithubRepo;
import com.example.github_user_service.model.GithubUser;
import com.fasterxml.jackson.databind.ObjectMapper;
//import lombok.launch.PatchFixesHider.Tests;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.*;
import org.springframework.web.client.*;
import java.util.List;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class GitHubClientTest {

    private RestTemplate restTemplate;
    private ObjectMapper objectMapper;
    private GitHubClient client;

    private final String BASE_URL = "https://api.github.com";

//Initializes mocks and the client instance before each test method runs.

    @BeforeEach
    void setup() {
        restTemplate = mock(RestTemplate.class);
        objectMapper = new ObjectMapper();
        client = new GitHubClient(restTemplate, objectMapper, BASE_URL, "");
    }

//Tests a successful retrieval of a user profile with valid JSON response (HTTP 200 OK).
    @Test
    void testUserSuccess() throws Exception {
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

        when(restTemplate.exchange(
                eq(BASE_URL + "/users/octocat"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>(json, HttpStatus.OK));

        Optional<GithubUser> result = client.fetchUser("octocat");

        assertTrue(result.isPresent());
        assertEquals("octocat", result.get().getLogin());
    }
//Tests that a user not found response (HTTP 404) correctly returns an empty Optional.
    @Test
    void testUserNotFound() {
        when(restTemplate.exchange(
                eq(BASE_URL + "/users/octocat"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>("x", HttpStatus.NOT_FOUND));

        assertFalse(client.fetchUser("octocat").isPresent());
    }
//Tests mapping a generic 4xx client error (e.g., 400 Bad Request) to ExternalServiceException
    @Test
    void testUserOtherClientError() {
        when(restTemplate.exchange(
                anyString(), any(), any(), eq(String.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        assertThrows(ExternalServiceException.class, () -> client.fetchUser("errorUser"));
    }
//Tests handling a null username input.

    @Test
    void testNullUsername() {
        assertThrows(ExternalServiceException.class, () -> client.fetchUser(null));
    }
//Tests handling an empty username input.

    @Test
    void testEmptyUsername() {
        assertThrows(ExternalServiceException.class, () -> client.fetchUser(""));
    }
//Tests error handling when the GitHub API returns malformed (invalid) JSON.

    @Test
    void testUserMalformedJson() {
        String invalidJson = "{ invalid }";

        when(restTemplate.exchange(
                anyString(),
                any(),
                any(),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>(invalidJson, HttpStatus.OK));

        assertThrows(ExternalServiceException.class, () -> client.fetchUser("octo"));
    }
//Tests error handling when the GitHub API returns malformed (invalid) JSON.

    @Test
    void testUserNullBody() {
        when(restTemplate.exchange(
                anyString(),
                any(),
                any(),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        assertThrows(ExternalServiceException.class, () -> client.fetchUser("octo"));
    }
//Tests mapping a ResourceAccessException (connection issue/timeout) to ExternalServiceException.

    @Test
    void testUserTimeout() {
        when(restTemplate.exchange(
                anyString(), any(), any(), eq(String.class)
        )).thenThrow(new ResourceAccessException("timeout"));

        assertThrows(ExternalServiceException.class, () -> client.fetchUser("octo"));
    }
//Tests mapping a 5xx server error from GitHub (HttpServerErrorException) to ExternalServiceException.

    @Test
    void testUserServerError() {
        when(restTemplate.exchange(
                anyString(), any(), any(), eq(String.class)
        )).thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(ExternalServiceException.class, () -> client.fetchUser("octo"));
    }
//Tests specific mapping for HTTP 429 (Too Many Requests/Rate Limit) error.

    @Test
void testUserRateLimit() {
    when(restTemplate.exchange(
            anyString(), any(), any(), eq(String.class)
    )).thenThrow(new HttpClientErrorException(
            HttpStatus.TOO_MANY_REQUESTS,
            "rate limit"
    ));

    assertThrows(ExternalServiceException.class, () -> client.fetchUser("octo"));
}
//Tests mapping any uncaught internal exception to the generic ExternalServiceException.
    @Test
    void testUserGenericException() {
        when(restTemplate.exchange(
                anyString(), any(), any(), eq(String.class)
        )).thenThrow(new RuntimeException("random"));

        assertThrows(ExternalServiceException.class, () -> client.fetchUser("octo"));
    }

    //Tests successful retrieval and deserialization of a list of repositories (HTTP 200 OK).

    @Test
    void testReposSuccess() throws Exception {
        String json = """
                [
                  { "name": "repo1", "html_url": "http://github.com/x/repo1" },
                  { "name": "repo2", "html_url": "http://github.com/x/repo2" }
                ]
                """;

        when(restTemplate.exchange(
                eq(BASE_URL + "/users/octocat/repos"),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(new ResponseEntity<>(json, HttpStatus.OK));

        List<GithubRepo> repos = client.fetchRepos("octocat");

        assertEquals(2, repos.size());
    }

//Tests that a 404 response for repos correctly returns an empty list
    @Test
    void testReposNotFoundReturnsEmpty() {
        when(restTemplate.exchange(
                anyString(), any(), any(), eq(String.class)
        )).thenReturn(new ResponseEntity<>(HttpStatus.NOT_FOUND));

        assertTrue(client.fetchRepos("missing").isEmpty());
    }

//Tests mapping a 5xx or other 4xx client error to ExternalServiceException.

    @Test
    void testReposOtherClientError() {
        when(restTemplate.exchange(
                anyString(), any(), any(), eq(String.class)
        )).thenThrow(new HttpClientErrorException(HttpStatus.BAD_GATEWAY));

        assertThrows(ExternalServiceException.class, () -> client.fetchRepos("error"));
    }
//Tests handling an empty username input for repos.

    @Test
    void testReposEmptyUsername() {
        assertThrows(ExternalServiceException.class, () -> client.fetchRepos(""));
    }
//Tests handling a null username input for repos.

    @Test
    void testReposNullUsername() {
        assertThrows(ExternalServiceException.class, () -> client.fetchRepos(null));
    }

    //Tests error handling when the API returns malformed repository JSON.

    @Test
    void testReposMalformedJson() {
        when(restTemplate.exchange(
                anyString(), any(), any(), eq(String.class)
        )).thenReturn(new ResponseEntity<>("[ invalid ]", HttpStatus.OK));

        assertThrows(ExternalServiceException.class, () -> client.fetchRepos("octo"));
    }

//Tests successful handling of an empty JSON array `[]` response.
    @Test
    void testReposEmptyArray() {
        when(restTemplate.exchange(
                anyString(), any(), any(), eq(String.class)
        )).thenReturn(new ResponseEntity<>("[]", HttpStatus.OK));

        assertTrue(client.fetchRepos("octo").isEmpty());
    }
//Tests mapping a network timeout (ResourceAccessException) for repos.

    @Test
    void testReposTimeout() {
        when(restTemplate.exchange(
                anyString(), any(), any(), eq(String.class)
        )).thenThrow(new ResourceAccessException("timeout"));

        assertThrows(ExternalServiceException.class, () -> client.fetchRepos("octo"));
    }

//Tests mapping a 5xx server error from GitHub (HttpServerErrorException) for repos.
    @Test
    void testReposServerError() {
        when(restTemplate.exchange(
                anyString(), any(), any(), eq(String.class)
        )).thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThrows(ExternalServiceException.class, () -> client.fetchRepos("octo"));
    }

//Tests specific mapping for HTTP 429 (Too Many Requests/Rate Limit) error for repos.

   @Test
void testReposRateLimit() {
    when(restTemplate.exchange(
            anyString(), any(), any(), eq(String.class)
    )).thenThrow(new HttpClientErrorException(
            HttpStatus.TOO_MANY_REQUESTS,
            "rate limit"
    ));

    assertThrows(ExternalServiceException.class, () -> client.fetchRepos("octo"));
}

//Tests mapping any uncaught generic exception for repos.
    @Test
    void testReposGenericException() {
        when(restTemplate.exchange(
                anyString(), any(), any(), eq(String.class)
        )).thenThrow(new RuntimeException("random"));

        assertThrows(ExternalServiceException.class, () -> client.fetchRepos("octo"));
    }
}
