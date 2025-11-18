package com.bank.account_service.exception;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatusCode;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    
    // Builder
    public static ErrorResponseBuilder builder(Throwable ex, HttpStatusCode status, String message) {
        return new ErrorResponseBuilder(ex, status, message);
    }
    
    public static class ErrorResponseBuilder {
        private LocalDateTime timestamp;
        private int status;
        private String error;
        private String message;
        private String path;
        
        public ErrorResponseBuilder(Throwable ex, HttpStatusCode status, String message) {
            this.status = status.value();  // Assuming this comes from the HTTP status
            this.error = "Not Found";
            this.message = message;
            this.timestamp = LocalDateTime.now();
        }
        
        public ErrorResponseBuilder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        
        public ErrorResponseBuilder path(String path) {
            this.path = path;
            return this;
        }
        
        public ErrorResponse build() {
            ErrorResponse response = new ErrorResponse();
            response.timestamp = this.timestamp;
            response.status = this.status;
            response.error = this.error;
            response.message = this.message;
            response.path = this.path;
            return response;
        }
    }
}