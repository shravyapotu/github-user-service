package com.example.github_user_service.client;

import com.example.github_user_service.exception.ExternalServiceException;
import com.example.github_user_service.model.GithubRepo;
import com.example.github_user_service.model.GithubUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.*;

import java.util.List;
import java.util.Optional;

 //Client component to interact with the external GitHub REST API.

@Component
public class GitHubClient {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String githubBaseUrl;
    private final String authToken;
    
    public GitHubClient(RestTemplate restTemplate,
                        ObjectMapper objectMapper,
                        @Nullable @org.springframework.beans.factory.annotation.Value("${github.api.base:https://api.github.com}") String githubBaseUrl,
                        @Nullable @org.springframework.beans.factory.annotation.Value("${github.api.token:}") String authToken) {

        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
        this.githubBaseUrl = githubBaseUrl;
        // Token is used for rate limit handling if available in configuration.
        this.authToken = authToken;
    }

    
    //Fetches the user profile
    public Optional<GithubUser> fetchUser(String username) {

        String url = String.format("%s/users/%s", githubBaseUrl, username);

        try {
            ResponseEntity<String> resp = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(createHeaders()), String.class
            );

            int status = resp.getStatusCode().value();
            String body = resp.getBody();

            if (status == HttpStatus.OK.value() && body != null) {
                try {
                    return Optional.of(objectMapper.readValue(body, GithubUser.class));
                } catch (JsonProcessingException e) {
                    throw new ExternalServiceException("GitHub returned malformed user JSON");
                }
            }

            if (status == HttpStatus.NOT_FOUND.value()) {
                return Optional.empty();
            }

            throw new ExternalServiceException("Unexpected GitHub user response: " + status);

        } catch (ResourceAccessException e) {
            throw new ExternalServiceException("GitHub is unreachable (timeout or connection issue)");
        } catch (HttpServerErrorException e) {
            throw new ExternalServiceException("GitHub server error: " + e.getStatusCode().value());
        } catch (HttpClientErrorException.TooManyRequests e) {
            throw new ExternalServiceException("GitHub rate limit exceeded");
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (HttpClientErrorException e) {
            throw new ExternalServiceException("GitHub returned error: " + e.getStatusCode().value());
        } catch (Exception e) {
            throw new ExternalServiceException("GitHub users API call failed: " + e.getMessage());
        }
    }
     //Fetches the public repositories for a user
    public List<GithubRepo> fetchRepos(String username) {

        String url = String.format("%s/users/%s/repos", githubBaseUrl, username);

        try {
            ResponseEntity<String> resp = restTemplate.exchange(
                    url, HttpMethod.GET, new HttpEntity<>(createHeaders()), String.class
            );

            int status = resp.getStatusCode().value();
            String body = resp.getBody();

            if (status == HttpStatus.OK.value() && body != null) {
                try {
                    return objectMapper.readValue(body, new TypeReference<List<GithubRepo>>() {});
                } catch (JsonProcessingException e) {
                    throw new ExternalServiceException("GitHub returned malformed repos JSON");
                }
            }

            if (status == HttpStatus.NOT_FOUND.value()) {
                return List.of();
            }

            throw new ExternalServiceException("Unexpected GitHub repos response: " + status);

        } catch (ResourceAccessException e) {
            throw new ExternalServiceException("GitHub is unreachable (timeout or connection issue)");
        } catch (HttpServerErrorException e) {
            throw new ExternalServiceException("GitHub server error: " + e.getStatusCode().value());
        } catch (HttpClientErrorException.TooManyRequests e) {
            throw new ExternalServiceException("GitHub rate limit exceeded");
        } catch (HttpClientErrorException.NotFound e) {
            return List.of();
        } catch (HttpClientErrorException e) {
            throw new ExternalServiceException("GitHub returned error: " + e.getStatusCode().value());
        } catch (Exception e) {
            throw new ExternalServiceException("GitHub repos API call failed: " + e.getMessage());
        }
    }
    //Creates standard HTTP headers
     
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);

        if (StringUtils.hasText(authToken)) {
            headers.set(HttpHeaders.AUTHORIZATION, "token " + authToken);
        }
        return headers;
    }
}
