package com.example.github_user_service.exception;

//Runtime exception used when a requested resource (e.g., a GitHub user) does not exist (HTTP 404)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
