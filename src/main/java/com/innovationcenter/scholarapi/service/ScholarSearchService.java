package com.innovationcenter.scholarapi.service;

import com.innovationcenter.scholarapi.model.ScholarSearchResponse;
import java.io.IOException;

/**
 * Interface for scholar search operations.
 * Defines contract for external API communication.
 */
public interface ScholarSearchService {
    
    /**
     * Search for scholarly articles using the provided query.
     * 
     * @param query The search query string
     * @param maxResults Maximum number of results to retrieve
     * @return Complete search response with organic results
     * @throws IOException If API request fails
     */
    ScholarSearchResponse searchArticles(String query, int maxResults) throws IOException;
    
    /**
     * Search for articles by a specific author.
     * 
     * @param authorName The author name to search for
     * @param maxResults Maximum number of results to retrieve
     * @return Complete search response with organic results
     * @throws IOException If API request fails
     */
    ScholarSearchResponse searchByAuthor(String authorName, int maxResults) throws IOException;
    
    /**
     * Search for articles citing a specific paper.
     * 
     * @param citesId The citation ID from Google Scholar
     * @param maxResults Maximum number of results to retrieve
     * @return Complete search response with citing articles
     * @throws IOException If API request fails
     */
    ScholarSearchResponse searchCitingArticles(String citesId, int maxResults) throws IOException;
    
    /**
     * Search with pagination support.
     * 
     * @param query The search query string
     * @param startIndex Starting index for results (0-based)
     * @param pageSize Number of results per page
     * @return Complete search response with pagination info
     * @throws IOException If API request fails
     */
    ScholarSearchResponse searchWithPagination(String query, int startIndex, int pageSize) throws IOException;
    
    /**
     * Check if the service is properly configured and ready to use.
     * 
     * @return true if configured correctly, false otherwise
     */
    boolean isConfigured();
    
    /**
     * Get the service name for identification.
     * 
     * @return Service implementation name
     */
    String getServiceName();
    
    /**
     * Test the API connection and authentication.
     * 
     * @return true if connection successful, false otherwise
     */
    boolean testConnection();
}