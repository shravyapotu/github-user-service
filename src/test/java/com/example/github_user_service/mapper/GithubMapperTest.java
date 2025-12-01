package com.example.github_user_service.mapper;

import com.example.github_user_service.model.GithubRepo;
import com.example.github_user_service.model.GithubUserApi;
import com.example.github_user_service.model.GithubUserResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GithubMapperTest {

    private final GithubMapper mapper = new GithubMapper();

    @Test
    void toGithubUserResponse_success() {
        GithubUserApi user = new GithubUserApi();
        user.setLogin("john");
        user.setName("John Doe");
        user.setAvatarUrl("img");
        user.setCreatedAt("2020-01-01T00:00:00Z");

        GithubRepo repo = new GithubRepo();
        repo.setName("repo1");
        repo.setHtmlUrl("http://html");

        GithubUserResponse resp = mapper.toGithubUserResponse(user, List.of(repo));

        assertEquals("john", resp.getUser_name());
        assertEquals("John Doe", resp.getDisplay_name());
        assertEquals("img", resp.getAvatar());
        assertNotNull(resp.getCreated_at());
        assertEquals(1, resp.getRepos().size());
        assertEquals("repo1", resp.getRepos().get(0).getName());
        assertEquals("http://html", resp.getRepos().get(0).getUrl());
    }

    @Test
    void toGithubUserResponse_nullReposShouldReturnEmptyList() {
        GithubUserApi user = new GithubUserApi();
        user.setLogin("john");

        GithubUserResponse resp = mapper.toGithubUserResponse(user, null);

        assertNotNull(resp.getRepos());
        assertTrue(resp.getRepos().isEmpty());
    }

    @Test
    void toGithubUserResponse_invalidDate_shouldFallback() {
        GithubUserApi user = new GithubUserApi();
        user.setLogin("john");
        user.setCreatedAt("not-a-date");

        GithubUserResponse resp = mapper.toGithubUserResponse(user, List.of());

        assertEquals("not-a-date", resp.getCreated_at());
    }
}
