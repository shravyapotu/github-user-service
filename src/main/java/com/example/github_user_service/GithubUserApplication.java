package com.example.github_user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
//Main entry point for the GitHub User Service Spring Boot application

@SpringBootApplication
@EnableCaching
public class GithubUserApplication {

    public static void main(String[] args) {
        SpringApplication.run(GithubUserApplication.class, args);
    }
}
