package com.innovationcenter.scholarapi.view;

import com.innovationcenter.scholarapi.model.Article;
import java.util.List;

/**
 * Interface for displaying article-related information to users.
 * Defines contract for view layer in MVC pattern.
 */
public interface ArticleView {
    
    /**
     * Display search started message.
     */
    void showSearchStarted(String query);
    
    /**
     * Display search results.
     */
    void showSearchResults(String query, List<Article> articles);
    
    /**
     * Display no results message.
     */
    void showNoResults(String query);
    
    /**
     * Display error message.
     */
    void showError(String message);
    
    /**
     * Display batch processing started message.
     */
    void showBatchProcessStarted(int researcherCount, int articlesPerResearcher);
    
    /**
     * Display researcher processed message.
     */
    void showResearcherProcessed(String researcherName, int articleCount);
    
    /**
     * Display researcher skipped message.
     */
    void showResearcherSkipped(String researcherName, String reason);
    
    /**
     * Display batch processing completed message.
     */
    void showBatchProcessCompleted(List<Article> allArticles);
    
    /**
     * Display stored articles for an author.
     */
    void showStoredArticles(String authorName, List<Article> articles);
    
    /**
     * Display no stored articles message.
     */
    void showNoStoredArticles(String authorName);
    
    /**
     * Display articles by year.
     */
    void showArticlesByYear(int year, List<Article> articles);
    
    /**
     * Display no articles for year message.
     */
    void showNoArticlesForYear(int year);
    
    /**
     * Display highly cited articles.
     */
    void showHighlyCitedArticles(int minCitations, List<Article> articles);
    
    /**
     * Display no highly cited articles message.
     */
    void showNoHighlyCitedArticles(int minCitations);
    
    /**
     * Display database statistics.
     */
    void showDatabaseStatistics(long totalArticles);
    
    /**
     * Display a list of articles (general purpose).
     */
    void displayArticles(List<Article> articles);
    
    /**
     * Display connectivity test started message.
     */
    void showConnectivityTestStarted();
    
    /**
     * Display API connection test result.
     */
    void showApiConnectionResult(boolean connected);
    
    /**
     * Display database connection test result.
     */
    void showDatabaseConnectionResult(boolean connected);
    
    /**
     * Display overall system status.
     */
    void showOverallSystemStatus(boolean operational);
}