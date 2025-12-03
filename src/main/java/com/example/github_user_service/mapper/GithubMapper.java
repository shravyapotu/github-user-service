package com.example.github_user_service.mapper;

import com.example.github_user_service.model.GithubRepo;
import com.example.github_user_service.model.GithubUser;
import com.example.github_user_service.model.UserResponse;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

//Component responsible for mapping raw data models received from the GitHub API
@Component
public class GithubMapper {

    private static final DateTimeFormatter OUTPUT_FORMATTER =
            DateTimeFormatter.RFC_1123_DATE_TIME.withZone(ZoneOffset.UTC);

//Aggregates user profile data and repository lists into the final UserResponse DTO

    public UserResponse toGithubUserResponse(GithubUser user, List<GithubRepo> repos) {
        UserResponse resp = new UserResponse();
        resp.setUser_name(user.getLogin());
        resp.setDisplay_name(user.getName());
        resp.setAvatar(user.getAvatarUrl());
        resp.setGeo_location(user.getLocation());
        resp.setEmail(user.getEmail());
        // Use API url if available else fallback to constructed url
        resp.setUrl(user.getUrl() != null ? user.getUrl() : String.format("https://api.github.com/users/%s", user.getLogin()));

        if (user.getCreatedAt() != null) {
            try {
                Instant inst = Instant.parse(user.getCreatedAt());
                resp.setCreated_at(OUTPUT_FORMATTER.format(inst));
            } catch (Exception e) {
                // fallback to raw string if parsing fails
                resp.setCreated_at(user.getCreatedAt());
            }
        }
// Repository Mapping
        List<UserResponse.RepoInfo> repoInfos = repos == null ? List.of() :
                repos.stream()
                        .map(r -> {
                            UserResponse.RepoInfo ri = new UserResponse.RepoInfo();
                            ri.setName(r.getName());
                            // prefer html_url if provided, otherwise api url
                            String repoUrl = r.getHtmlUrl() != null ? r.getHtmlUrl() :
                                    (r.getUrl() != null ? r.getUrl() :
                                            String.format("https://api.github.com/repos/%s/%s", user.getLogin(), r.getName()));
                            ri.setUrl(repoUrl);
                            return ri;
                        })
                        .collect(Collectors.toList());

        resp.setRepos(repoInfos);
        return resp;
    }
}
