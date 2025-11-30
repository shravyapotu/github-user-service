package com.example.github_user_service.model;

import java.util.List;

import org.springframework.stereotype.Component;

/**
 * Public API response shape (POJO) that will be serialized to JSON for the client.
 */
@Component
public class GithubUserResponse {

    private String user_name;
    private String display_name;
    private String avatar;
    private String geo_location;
    private String email;
    private String url;
    private String created_at;

    private List<RepoInfo> repos;

    public GithubUserResponse() { }

    // getters / setters
    public String getUser_name() { return user_name; }
    public void setUser_name(String user_name) { this.user_name = user_name; }

    public String getDisplay_name() { return display_name; }
    public void setDisplay_name(String display_name) { this.display_name = display_name; }

    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }

    public String getGeo_location() { return geo_location; }
    public void setGeo_location(String geo_location) { this.geo_location = geo_location; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getCreated_at() { return created_at; }
    public void setCreated_at(String created_at) { this.created_at = created_at; }

    public List<RepoInfo> getRepos() { return repos; }
    public void setRepos(List<RepoInfo> repos) { this.repos = repos; }

    public static class RepoInfo {
        private String name;
        private String url;

        public RepoInfo() { }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
    }
}
