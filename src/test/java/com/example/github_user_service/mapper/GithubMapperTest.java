package com.example.github_user_service.mapper;

import com.example.github_user_service.model.GithubRepo;
import com.example.github_user_service.model.GithubUser;
import com.example.github_user_service.model.UserResponse;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GithubMapperTest {

    private final GithubMapper mapper = new GithubMapper();
//Tests the complete mapping process with valid inputs
    @Test
    void toGithubUserResponseSuccess() {
        GithubUser user = new GithubUser();
        user.setLogin("shravya");
        user.setName("shravya");
        user.setAvatarUrl("img");
        user.setCreatedAt("2020-01-01T00:00:00Z");

        GithubRepo repo = new GithubRepo();
        repo.setName("repo123");
        repo.setHtmlUrl("http://html");

        UserResponse resp = mapper.toGithubUserResponse(user, List.of(repo));

        assertEquals("shravya", resp.getUser_name());
        assertEquals("shravya", resp.getDisplay_name());
        assertEquals("img", resp.getAvatar());
        assertNotNull(resp.getCreated_at());
        assertEquals(1, resp.getRepos().size());
        assertEquals("repo123", resp.getRepos().get(0).getName());
        assertEquals("http://html", resp.getRepos().get(0).getUrl());
    }

    /**
     * Tests the defensive handling when the repository list is explicitly null, 
     * ensuring it maps to an empty list instead of throwing an exception.
     */
    @Test
    void toGithubUserResponseNullRepos() {
        GithubUser user = new GithubUser();
        user.setLogin("shravya");

        UserResponse resp = mapper.toGithubUserResponse(user, null);

        assertNotNull(resp.getRepos());
        assertTrue(resp.getRepos().isEmpty());
    }
    /**
     * Tests the date parsing fallback logic, ensuring that if the date string is 
     * invalid, the mapper returns the raw, unparsed string instead of failing.
     */
    @Test
    void toGithubUserResponseInvalidDate() {
        GithubUser user = new GithubUser();
        user.setLogin("shravya");
        user.setCreatedAt("not-a-date");

        UserResponse resp = mapper.toGithubUserResponse(user, List.of());

        assertEquals("not-a-date", resp.getCreated_at());
    }
}
