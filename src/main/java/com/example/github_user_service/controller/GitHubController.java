package com.example.github_user_service.controller;

import com.example.github_user_service.model.GithubUserResponse;
//import com.example.github_user_service.model.GithubUserResponse;
import com.example.github_user_service.service.GitHubService;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/github")
public class GitHubController {

    private final GitHubService gitHubService;

    public GitHubController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    /**
     * GET /api/github/user/{username}
     * Returns merged user + repos information for given GitHub username.
     */
    @GetMapping("/user/{username}")
    public ResponseEntity<GithubUserResponse> getUser(
            @PathVariable @NotBlank(message = "username must not be blank") String username) {

        GithubUserResponse response = gitHubService.getUser(username.trim());
        return ResponseEntity.ok(response);
    }
}
