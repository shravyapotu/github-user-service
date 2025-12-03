package com.example.github_user_service.model;

import org.springframework.stereotype.Component;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

//DTO for deserializing user profile data from the external GitHub API, isolated from the public contract.
@Component
@Data
public class GithubUser {

    private String login;
    private String name;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    private String location;
    private String email;
    private String url;

    @JsonProperty("created_at")
    private String createdAt;

    public GithubUser() { }
}
