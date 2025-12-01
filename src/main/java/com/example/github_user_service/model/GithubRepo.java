package com.example.github_user_service.model;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Minimal mapping for fields we use from GitHub repos API.
 */
@Component
public class GithubRepo {

    private String name;

    // GitHub returns both "url" (api url) and "html_url" (web url). Prefer html_url for public response.
    private String url;

    @JsonProperty("html_url")
    private String htmlUrl;

    public GithubRepo() { }

    // getters / setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getHtmlUrl() { return htmlUrl; }
    public void setHtmlUrl(String htmlUrl) { this.htmlUrl = htmlUrl; }
}
