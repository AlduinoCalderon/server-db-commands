package com.innovationcenter.scholarapi.controller;

import com.innovationcenter.scholarapi.model.AuthorSearchResult;
import com.innovationcenter.scholarapi.service.ApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Author Search Controller following SOLID principles.
 * Uses Dependency Inversion Principle by depending on abstractions.
 */
public class AuthorSearchController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthorSearchController.class);
    
    private final ApiService apiService;
    
    /**
     * Constructor with dependency injection following Dependency Inversion Principle.
     */
    public AuthorSearchController(ApiService apiService) {
        this.apiService = apiService;
    }
    
    /**
     * Search for authors by name.
     * Single Responsibility: Only coordinates API calls and error handling.
     * 
     * @param query Search query
     * @return List of author search results
     * @throws IOException If search fails
     */
    public List<AuthorSearchResult> searchAuthors(String query) throws IOException {
        validateQuery(query);
        
        if (!apiService.isConfigured()) {
            throw new IllegalStateException("API service is not configured");
        }
        
        logger.info("Searching for authors matching: {}", query);
        
        try {
            List<AuthorSearchResult> results = apiService.searchAuthors(query);
            logger.info("Found {} author results", results.size());
            return results;
            
        } catch (IOException e) {
            logger.error("Failed to search authors: {}", e.getMessage());
            throw new IOException("Author search failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Check if the API service is properly configured.
     * @return true if configured, false otherwise
     */
    public boolean isApiConfigured() {
        return apiService.isConfigured();
    }
    
    /**
     * Get API service name for display purposes.
     * @return Service name
     */
    public String getApiServiceName() {
        return apiService.getServiceName();
    }
    
    /**
     * Validate search query following input validation principles.
     */
    private void validateQuery(String query) {
        if (query == null || query.trim().isEmpty()) {
            throw new IllegalArgumentException("Search query cannot be null or empty");
        }
        
        if (query.length() < 2) {
            throw new IllegalArgumentException("Search query must be at least 2 characters long");
        }
        
        if (query.length() > 100) {
            throw new IllegalArgumentException("Search query cannot exceed 100 characters");
        }
    }
}