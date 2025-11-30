package com.example.github_user_service.model;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Minimal mapping for fields we consume from GitHub /users API.
 */
@Component
public class GithubUserApi {

    private String login;
    private String name;

    @JsonProperty("avatar_url")
    private String avatarUrl;

    private String location;
    private String email;
    private String url;

    @JsonProperty("created_at")
    private String createdAt;

    public GithubUserApi() { }

    // getters / setters

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
