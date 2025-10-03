package com.innovationcenter.scholarapi.controller;

import com.innovationcenter.scholarapi.model.Article;
import com.innovationcenter.scholarapi.model.ScholarSearchResponse;
import com.innovationcenter.scholarapi.service.ArticleService;
import com.innovationcenter.scholarapi.service.ScholarSearchService;
import com.innovationcenter.scholarapi.view.ArticleView;

import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Controller for handling scholar article search and management operations.
 * Coordinates between service layer and view layer following MVC pattern.
 */
public class ScholarArticleController {
    private static final Logger logger = Logger.getLogger(ScholarArticleController.class.getName());
    
    private final ScholarSearchService searchService;
    private final ArticleService articleService;
    private final ArticleView articleView;
    
    public ScholarArticleController(ScholarSearchService searchService, 
                                  ArticleService articleService,
                                  ArticleView articleView) {
        this.searchService = searchService;
        this.articleService = articleService;
        this.articleView = articleView;
    }
    
    /**
     * Handles search request for articles by researcher name.
     */
    public void searchArticlesByResearcher(String researcherName, int maxArticles) {
        try {
            articleView.showSearchStarted(researcherName);
            
            // Validate input
            if (!isValidSearchInput(researcherName, maxArticles)) {
                return;
            }
            
            // Perform search through service layer
            ScholarSearchResponse response = searchService.searchByAuthor(researcherName, maxArticles);
            
            if (response == null || response.getOrganicResults() == null || response.getOrganicResults().length == 0) {
                articleView.showNoResults(researcherName);
                return;
            }
            
            // Process and save articles
            List<Article> savedArticles = articleService.processSearchResponse(response, maxArticles);
            
            // Display results through view
            articleView.showSearchResults(researcherName, savedArticles);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error searching articles for researcher: " + researcherName, e);
            articleView.showError("Search failed: " + e.getMessage());
        }
    }
    
    /**
     * Handles general article search by query.
     */
    public void searchArticlesByQuery(String query, int maxResults) {
        try {
            articleView.showSearchStarted(query);
            
            if (!isValidSearchInput(query, maxResults)) {
                return;
            }
            
            ScholarSearchResponse response = searchService.searchArticles(query, maxResults);
            
            if (response == null || response.getOrganicResults() == null || response.getOrganicResults().length == 0) {
                articleView.showNoResults(query);
                return;
            }
            
            List<Article> savedArticles = articleService.processSearchResponse(response, maxResults);
            articleView.showSearchResults(query, savedArticles);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error searching articles for query: " + query, e);
            articleView.showError("Search failed: " + e.getMessage());
        }
    }
    
    /**
     * Handles search request for articles by title keyword.
     * Searches the database for articles matching the keyword in their title.
     */
    public void searchArticlesByTitle(String keyword) {
        try {
            System.out.println("\n" + "=".repeat(80));
            System.out.println("🔍 Searching articles by title: \"" + keyword + "\"");
            System.out.println("=".repeat(80));
            
            if (keyword == null || keyword.trim().isEmpty()) {
                articleView.showError("Keyword cannot be empty");
                return;
            }
            
            // Search in database
            List<Article> articles = articleService.searchByTitle(keyword);
            
            if (articles.isEmpty()) {
                System.out.println("⚠️ No articles found with title containing: \"" + keyword + "\"");
            } else {
                System.out.println("📚 Found " + articles.size() + " article(s):\n");
                articleView.displayArticles(articles);
            }
            
            System.out.println("=".repeat(80));
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error searching articles by title: " + keyword, e);
            articleView.showError("Title search failed: " + e.getMessage());
        }
    }
    
    /**
     * Handles request to process multiple researchers.
     */
    public void processMultipleResearchers(List<String> researchers, int articlesPerResearcher) {
        try {
            articleView.showBatchProcessStarted(researchers.size(), articlesPerResearcher);
            
            if (researchers == null || researchers.isEmpty()) {
                articleView.showError("No researchers provided");
                return;
            }
            
            List<Article> allArticles = new java.util.ArrayList<>();
            
            for (String researcher : researchers) {
                try {
                    ScholarSearchResponse response = searchService.searchByAuthor(researcher, articlesPerResearcher);
                    
                    if (response != null && response.getOrganicResults() != null) {
                        List<Article> researcherArticles = articleService.processSearchResponse(response, articlesPerResearcher);
                        allArticles.addAll(researcherArticles);
                        articleView.showResearcherProcessed(researcher, researcherArticles.size());
                    } else {
                        articleView.showResearcherSkipped(researcher, "No results found");
                    }
                    
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Failed to process researcher: " + researcher, e);
                    articleView.showResearcherSkipped(researcher, e.getMessage());
                }
            }
            
            articleView.showBatchProcessCompleted(allArticles);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error in batch processing", e);
            articleView.showError("Batch processing failed: " + e.getMessage());
        }
    }
    
    /**
     * Handles request to display articles by author.
     */
    public void displayArticlesByAuthor(String authorName) {
        try {
            List<Article> articles = articleService.findByAuthor(authorName);
            
            if (articles.isEmpty()) {
                articleView.showNoStoredArticles(authorName);
            } else {
                articleView.showStoredArticles(authorName, articles);
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving articles for author: " + authorName, e);
            articleView.showError("Failed to retrieve articles: " + e.getMessage());
        }
    }
    
    /**
     * Handles request to display articles by publication year.
     */
    public void displayArticlesByYear(int year) {
        try {
            List<Article> articles = articleService.findByYear(year);
            
            if (articles.isEmpty()) {
                articleView.showNoArticlesForYear(year);
            } else {
                articleView.showArticlesByYear(year, articles);
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving articles for year: " + year, e);
            articleView.showError("Failed to retrieve articles: " + e.getMessage());
        }
    }
    
    /**
     * Handles request to display highly cited articles.
     */
    public void displayHighlyCitedArticles(int minCitations) {
        try {
            List<Article> articles = articleService.findByCitationsGreaterThan(minCitations);
            
            if (articles.isEmpty()) {
                articleView.showNoHighlyCitedArticles(minCitations);
            } else {
                articleView.showHighlyCitedArticles(minCitations, articles);
            }
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving highly cited articles", e);
            articleView.showError("Failed to retrieve articles: " + e.getMessage());
        }
    }
    
    /**
     * Handles request to display database statistics.
     */
    public void displayDatabaseStatistics() {
        try {
            long totalCount = articleService.getTotalArticleCount();
            articleView.showDatabaseStatistics(totalCount);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error retrieving database statistics", e);
            articleView.showError("Failed to retrieve statistics: " + e.getMessage());
        }
    }
    
    /**
     * Handles request to test system connectivity.
     */
    public void testSystemConnectivity() {
        try {
            articleView.showConnectivityTestStarted();
            
            // Test API service
            boolean apiConnected = searchService.testConnection();
            articleView.showApiConnectionResult(apiConnected);
            
            // Test database connectivity by attempting a count operation
            boolean dbConnected = true;
            try {
                articleService.getTotalArticleCount();
            } catch (Exception e) {
                dbConnected = false;
                logger.log(Level.WARNING, "Database connectivity test failed", e);
            }
            
            articleView.showDatabaseConnectionResult(dbConnected);
            articleView.showOverallSystemStatus(apiConnected && dbConnected);
            
        } catch (Exception e) {
            logger.log(Level.SEVERE, "System connectivity test failed", e);
            articleView.showError("Connectivity test failed: " + e.getMessage());
        }
    }
    
    /**
     * Validates search input parameters.
     */
    private boolean isValidSearchInput(String query, int maxResults) {
        if (query == null || query.trim().isEmpty()) {
            articleView.showError("Search query cannot be empty");
            return false;
        }
        
        if (maxResults <= 0 || maxResults > 100) {
            articleView.showError("Max results must be between 1 and 100");
            return false;
        }
        
        if (!searchService.isConfigured()) {
            articleView.showError("Search service is not properly configured");
            return false;
        }
        
        return true;
    }
}