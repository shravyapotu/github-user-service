package com.example.github_user_service.client;

import com.example.github_user_service.exception.ExternalServiceException;
import com.example.github_user_service.model.GithubRepo;
import com.example.github_user_service.model.GithubUserApi;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

/**
 * Responsible for calling GitHub API. Keeps HTTP concerns isolated from business logic.
 */
@Component
public class GitHubClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String githubBaseUrl;
    private final String authToken; // optional

    public GitHubClient(RestTemplate restTemplate,
                        ObjectMapper objectMapper,
                        @Nullable @org.springframework.beans.factory.annotation.Value("${github.api.base:https://api.github.com}") String githubBaseUrl,
                        @Nullable @org.springframework.beans.factory.annotation.Value("${github.api.token:}") String authToken) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.githubBaseUrl = githubBaseUrl;
        this.authToken = authToken;
    }

    public Optional<GithubUserApi> fetchUser(String username) {
        String url = String.format("%s/users/%s", githubBaseUrl, username);
        try {
            ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(createHeaders()), String.class);
            if (resp.getStatusCode() == HttpStatus.OK && resp.getBody() != null) {
                GithubUserApi api = objectMapper.readValue(resp.getBody(), GithubUserApi.class);
                return Optional.of(api);
            } else if (resp.getStatusCode() == HttpStatus.NOT_FOUND) {
                return Optional.empty();
            } else {
                throw new ExternalServiceException("Unexpected response from GitHub users endpoint: " + resp.getStatusCode());
            }
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (HttpClientErrorException.TooManyRequests e) {
            throw new ExternalServiceException("GitHub rate limit exceeded");
        } catch (HttpClientErrorException e) {
            throw new ExternalServiceException("GitHub returned error: " + e.getStatusCode());
        } catch (Exception e) {
            throw new ExternalServiceException("Failed to call GitHub users API: " + e.getMessage());
        }
    }

    public List<GithubRepo> fetchRepos(String username) {
        String url = String.format("%s/users/%s/repos", githubBaseUrl, username);
        try {
            ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(createHeaders()), String.class);
            if (resp.getStatusCode() == HttpStatus.OK && resp.getBody() != null) {
                return objectMapper.readValue(resp.getBody(), new TypeReference<List<GithubRepo>>() {});
            } else if (resp.getStatusCode() == HttpStatus.NOT_FOUND) {
                return List.of();
            } else {
                throw new ExternalServiceException("Unexpected response from GitHub repos endpoint: " + resp.getStatusCode());
            }
        } catch (HttpClientErrorException.TooManyRequests e) {
            throw new ExternalServiceException("GitHub rate limit exceeded");
        } catch (HttpClientErrorException e) {
            throw new ExternalServiceException("GitHub returned error: " + e.getStatusCode());
        } catch (Exception e) {
            throw new ExternalServiceException("Failed to call GitHub repos API: " + e.getMessage());
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);
        if (StringUtils.hasText(authToken)) {
            headers.set(HttpHeaders.AUTHORIZATION, "token " + authToken);
        }
        return headers;
    }
}
