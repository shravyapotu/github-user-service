package com.example.github_user_service.exception;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import java.util.Map;
import java.util.Set;

import jakarta.validation.ConstraintViolation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler handler;

    @BeforeEach
    void setup() {
        handler = new GlobalExceptionHandler();
    }
//Tests mapping of ResourceNotFoundException to HTTP 404 (Not Found)

    @Test
    void testHandleNotFound() {
        ResourceNotFoundException e = new ResourceNotFoundException("not found");
        ResponseEntity<?> response = handler.handleNotFound(e);

        assertEquals(404, response.getStatusCode().value());
        assertTrue(((Map<?, ?>) response.getBody()).get("error").toString().contains("not found"));
    }
//Tests mapping of ConstraintViolationException (input validation failure) to HTTP 400 (Bad Request).

    @Test
    void testHandleValidationException() {
        ConstraintViolation<?> violation = mock(ConstraintViolation.class);
        when(violation.getMessage()).thenReturn("username must not be blank");

        ConstraintViolationException e =
                new ConstraintViolationException(Set.of(violation));

        ResponseEntity<?> response = handler.handleValidation(e);

        assertEquals(400, response.getStatusCode().value());
        assertTrue(response.getBody().toString().contains("username must not be blank"));
    }
//Tests mapping of ExternalServiceException (GitHub API failure) to HTTP 503 (Service Unavailable).

    @Test
    void testHandleExternalServiceException() {
        ExternalServiceException e = new ExternalServiceException("service down");
        ResponseEntity<?> response = handler.handleExternal(e);

        assertEquals(503, response.getStatusCode().value());
        assertTrue(response.getBody().toString().contains("service down"));
    }
//Tests the fallback handler for generic Exceptions, ensuring it returns HTTP 500 (Internal Server Error)
    @Test
    void testHandleGeneric() {
        Exception e = new Exception("random failure");
        ResponseEntity<?> response = handler.handleGeneric(e);

        assertEquals(500, response.getStatusCode().value());
        assertTrue(response.getBody().toString().contains("Internal server error"));
    }
}
