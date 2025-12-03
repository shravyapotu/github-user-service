// package com.example.github_user_service.model;

// import java.util.List;

// import org.springframework.stereotype.Component;

// import lombok.Data;
// //The final Data Transfer Object (DTO) representing the merged user and repository data.
// @Component
// @Data
// public class UserResponse {

//     private String user_name;
//     private String display_name;
//     private String avatar;
//     private String geo_location;
//     private String email;
//     private String url;
//     private String created_at;

//     private List<RepoInfo> repos;

//     //Default no-args constructor required for serialization/deserialization.
//     public UserResponse() { }

//     // public List<RepoInfo> getRepos() { return repos; }
//     // public void setRepos(List<RepoInfo> repos) { this.repos = repos; }

//     // public static class RepoInfo {
//     //     private String name;
//     //     private String url;
// //Default no-args constructor required for serialization/deserialization.
//         public RepoInfo() { }

//         public String getName() { return name; }
//         public void setName(String name) { this.name = name; }

//         public String getUrl() { return url; }
//         public void setUrl(String url) { this.url = url; }
//     }
// }
package com.example.github_user_service.model;

import java.util.List;
import org.springframework.stereotype.Component;
import lombok.Data;

//The final Data Transfer Object (DTO) representing the merged user and repository data.

@Component
@Data
public class UserResponse {

    private String user_name;
    private String display_name;
    private String avatar;
    private String geo_location;
    private String email;
    private String url;
    private String created_at;

    private List<RepoInfo> repos;

    //Default no-args constructor required for serialization/deserialization.
     
    public UserResponse() { }
    @Data
    public static class RepoInfo {
        private String name;
        private String url;


    //Default no-args constructor required for serialization/deserialization.

        public RepoInfo() { }
    }
}