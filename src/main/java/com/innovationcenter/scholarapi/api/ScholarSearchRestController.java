package com.innovationcenter.scholarapi.api;

import com.innovationcenter.scholarapi.model.ScholarSearchResponse;
import com.innovationcenter.scholarapi.service.ScholarSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * REST API Controller for Live Google Scholar Search via SerpAPI.
 * Provides endpoints for searching scholarly articles without importing to database.
 * 
 * Base URL: /api/search
 * 
 * Endpoints:
 * - GET /api/search/articles?query=&maxResults= - Search articles
 * - GET /api/search/author?name=&maxResults= - Search by author
 * - GET /api/search/citations?citesId=&maxResults= - Find citing articles
 * - GET /api/search/paginated?query=&start=&pageSize= - Paginated search
 */
@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*") // Will be restricted in production
public class ScholarSearchRestController {

    private static final Logger logger = LoggerFactory.getLogger(ScholarSearchRestController.class);

    private final ScholarSearchService searchService;

    public ScholarSearchRestController(ScholarSearchService searchService) {
        this.searchService = searchService;
    }

    /**
     * Search for scholarly articles.
     * GET /api/search/articles?query=machine%20learning&maxResults=10
     * 
     * @param query Search query string
     * @param maxResults Maximum number of results (default 10, max 20)
     * @return ScholarSearchResponse with organic results
     */
    @GetMapping("/articles")
    public ResponseEntity<?> searchArticles(
            @RequestParam String query,
            @RequestParam(defaultValue = "10") int maxResults) {
        
        try {
            logger.info("🔎 Searching articles with query: '{}' (maxResults: {})", query, maxResults);
            
            if (query == null || query.trim().isEmpty()) {
                logger.warn("⚠️  Empty query provided");
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Query parameter is required"));
            }
            
            if (maxResults < 1 || maxResults > 20) {
                logger.warn("⚠️  Invalid maxResults: {} (must be 1-20)", maxResults);
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("maxResults must be between 1 and 20"));
            }
            
            logger.info("🌐 Calling SerpAPI for query: '{}'...", query);
            ScholarSearchResponse response = searchService.searchArticles(query, maxResults);
            
            int resultCount = response.getOrganicResults() != null ? response.getOrganicResults().length : 0;
            logger.info("✅ Found {} results for query: '{}'", resultCount, query);
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            logger.error("❌ SerpAPI error for query '{}': {}", query, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(createErrorResponse("Failed to fetch search results from SerpAPI: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("❌ Unexpected error searching articles for query '{}': {}", query, e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("An unexpected error occurred: " + e.getMessage()));
        }
    }

    /**
     * Search articles by specific author.
     * GET /api/search/author?name=John%20Smith&maxResults=10
     * 
     * @param name Author name to search for
     * @param maxResults Maximum number of results (default 10, max 20)
     * @return ScholarSearchResponse with author's publications
     */
    @GetMapping("/author")
    public ResponseEntity<?> searchByAuthor(
            @RequestParam String name,
            @RequestParam(defaultValue = "10") int maxResults) {
        
        try {
            logger.info("Searching articles by author: {} (maxResults: {})", name, maxResults);
            
            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Author name parameter is required"));
            }
            
            if (maxResults < 1 || maxResults > 20) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("maxResults must be between 1 and 20"));
            }
            
            ScholarSearchResponse response = searchService.searchByAuthor(name, maxResults);
            
            logger.info("Found {} results for author: {}", 
                response.getOrganicResults() != null ? response.getOrganicResults().length : 0, 
                name);
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            logger.error("Error searching by author: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(createErrorResponse("Failed to fetch author results from SerpAPI: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error searching by author: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("An unexpected error occurred: " + e.getMessage()));
        }
    }

    /**
     * Search for articles citing a specific paper.
     * GET /api/search/citations?citesId=123456789&maxResults=10
     * 
     * @param citesId Google Scholar citation ID
     * @param maxResults Maximum number of results (default 10, max 20)
     * @return ScholarSearchResponse with citing articles
     */
    @GetMapping("/citations")
    public ResponseEntity<?> searchCitingArticles(
            @RequestParam String citesId,
            @RequestParam(defaultValue = "10") int maxResults) {
        
        try {
            logger.info("Searching citing articles for ID: {} (maxResults: {})", citesId, maxResults);
            
            if (citesId == null || citesId.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("citesId parameter is required"));
            }
            
            if (maxResults < 1 || maxResults > 20) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("maxResults must be between 1 and 20"));
            }
            
            ScholarSearchResponse response = searchService.searchCitingArticles(citesId, maxResults);
            
            logger.info("Found {} citing articles for ID: {}", 
                response.getOrganicResults() != null ? response.getOrganicResults().length : 0, 
                citesId);
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            logger.error("Error searching citing articles: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(createErrorResponse("Failed to fetch citation results from SerpAPI: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error searching citing articles: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("An unexpected error occurred: " + e.getMessage()));
        }
    }

    /**
     * Search with pagination support.
     * GET /api/search/paginated?query=AI&start=0&pageSize=10
     * 
     * @param query Search query string
     * @param start Starting index (0-based)
     * @param pageSize Number of results per page (max 20)
     * @return ScholarSearchResponse with pagination info
     */
    @GetMapping("/paginated")
    public ResponseEntity<?> searchWithPagination(
            @RequestParam String query,
            @RequestParam(defaultValue = "0") int start,
            @RequestParam(defaultValue = "10") int pageSize) {
        
        try {
            logger.info("Paginated search: query={}, start={}, pageSize={}", query, start, pageSize);
            
            if (query == null || query.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("Query parameter is required"));
            }
            
            if (start < 0) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("start must be >= 0"));
            }
            
            if (pageSize < 1 || pageSize > 20) {
                return ResponseEntity.badRequest()
                    .body(createErrorResponse("pageSize must be between 1 and 20"));
            }
            
            ScholarSearchResponse response = searchService.searchWithPagination(query, start, pageSize);
            
            logger.info("Found {} results for paginated query: {}", 
                response.getOrganicResults() != null ? response.getOrganicResults().length : 0, 
                query);
            
            return ResponseEntity.ok(response);
            
        } catch (IOException e) {
            logger.error("Error in paginated search: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(createErrorResponse("Failed to fetch paginated results from SerpAPI: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Unexpected error in paginated search: " + e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(createErrorResponse("An unexpected error occurred: " + e.getMessage()));
        }
    }

    /**
     * Health check endpoint for search service.
     * GET /api/search/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "ScholarSearchService");
        health.put("timestamp", System.currentTimeMillis());
        return ResponseEntity.ok(health);
    }

    /**
     * Helper method to create error response.
     */
    private Map<String, String> createErrorResponse(String message) {
        Map<String, String> error = new HashMap<>();
        error.put("error", message);
        error.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return error;
    }
}
