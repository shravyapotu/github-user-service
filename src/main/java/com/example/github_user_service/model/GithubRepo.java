package com.example.github_user_service.model;

import org.springframework.stereotype.Component;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * DTO for deserializing repository data from the external GitHub API, 
 * isolated from the public contract.
 */
@Component
@Data
public class GithubRepo {

    private String name;

    private String url;

    @JsonProperty("html_url")
    private String htmlUrl;

    public GithubRepo() { } 
}
