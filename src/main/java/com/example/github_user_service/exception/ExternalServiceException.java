package com.example.github_user_service.exception;

//Runtime exception signaling errors (e.g., connection issues, rate limits) originating from the external GitHub API
public class ExternalServiceException extends RuntimeException {
    public ExternalServiceException(String message) {
        super(message);
    }
}
