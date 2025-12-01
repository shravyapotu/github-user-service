package com.example.github_user_service.service;

import com.example.github_user_service.client.GitHubClient;
import com.example.github_user_service.exception.ResourceNotFoundException;
import com.example.github_user_service.model.GithubRepo;
import com.example.github_user_service.model.GithubUserApi;
import com.example.github_user_service.model.GithubUserResponse;
import com.example.github_user_service.mapper.GithubMapper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GitHubService {

    private final GitHubClient gitHubClient;
    private final GithubMapper mapper;

    public GitHubService(GitHubClient gitHubClient, GithubMapper mapper) {
        this.gitHubClient = gitHubClient;
        this.mapper = mapper;
    }

    /**
     * Business logic only: orchestrate calls and map to public DTO.
     * Caching applied at service level to avoid repeated network calls.
     */
    @Cacheable(value = "githubUsers", key = "#username")
    public GithubUserResponse getUser(String username) {
        // Fetch raw API DTOs from client/repository layer
        GithubUserApi userApi = gitHubClient.fetchUser(username)
                .orElseThrow(() -> new ResourceNotFoundException("GitHub user not found: " + username));

        List<GithubRepo> repos = gitHubClient.fetchRepos(username);

        // Map to public response DTO
        return mapper.toGithubUserResponse(userApi, repos);
    }
}
