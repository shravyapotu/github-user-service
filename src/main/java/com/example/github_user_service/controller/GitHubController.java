package com.example.github_user_service.controller;

import com.example.github_user_service.model.UserResponse;
import jakarta.validation.constraints.Pattern;
import com.example.github_user_service.service.GitHubService;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

//REST Controller for the GitHub User Service.
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
     * Returns the merged user + repos info for the given GitHub username.
     */
    @GetMapping("/user/{username:.+}")
public ResponseEntity<UserResponse> getUser(
        @PathVariable
        @Pattern(regexp = "^[a-zA-Z0-9-]+$", message = "invalid GitHub username")
        String username) {

    UserResponse response = gitHubService.getUser(username.trim());
    return ResponseEntity.ok(response);
}
//Custom handler for requests missing the {username} path variable.
@GetMapping({"/user", "/user/"})
    public ResponseEntity<?> handleMissingUsername() {
        return ResponseEntity.badRequest()
                .body(Map.of("error", "username must not be blank"));
    }
}
