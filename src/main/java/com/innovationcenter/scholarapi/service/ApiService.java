package com.innovationcenter.scholarapi.service;

import com.innovationcenter.scholarapi.model.AuthorSearchResult;
import java.io.IOException;
import java.util.List;

/**
 * Interface for API service operations.
 * Defines contract for external API communication.
 */
public interface ApiService {
    
    /**
     * Search for authors by name.
     * @param query The search query
     * @return List of search results
     * @throws IOException If API request fails
     */
    List<AuthorSearchResult> searchAuthors(String query) throws IOException;
    
    /**
     * Check if API service is properly configured.
     * @return true if configured, false otherwise
     */
    boolean isConfigured();
    
    /**
     * Get the service name for logging purposes.
     * @return Service name
     */
    String getServiceName();
}