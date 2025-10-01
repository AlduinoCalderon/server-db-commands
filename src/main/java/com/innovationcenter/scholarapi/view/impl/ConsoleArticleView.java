package com.innovationcenter.scholarapi.view.impl;

import com.innovationcenter.scholarapi.model.Article;
import com.innovationcenter.scholarapi.view.ArticleView;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Console-based implementation of ArticleView.
 * Provides text-based output for article information display.
 */
public class ConsoleArticleView implements ArticleView {
    
    private static final String SEPARATOR = "=" .repeat(80);
    private static final String SMALL_SEPARATOR = "-".repeat(40);
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    @Override
    public void showSearchStarted(String query) {
        System.out.println(SEPARATOR);
        System.out.println("🔍 Starting search for: \"" + query + "\"");
        System.out.println(SEPARATOR);
    }
    
    @Override
    public void showSearchResults(String query, List<Article> articles) {
        System.out.println("✅ Search completed for: \"" + query + "\"");
        System.out.println("📊 Found and saved " + articles.size() + " articles:");
        System.out.println();
        
        for (int i = 0; i < articles.size(); i++) {
            Article article = articles.get(i);
            System.out.println((i + 1) + ". " + formatArticleDisplay(article));
        }
        System.out.println(SEPARATOR);
    }
    
    @Override
    public void showNoResults(String query) {
        System.out.println("❌ No results found for: \"" + query + "\"");
        System.out.println("   Try modifying your search terms or check spelling.");
        System.out.println(SEPARATOR);
    }
    
    @Override
    public void showError(String message) {
        System.err.println("❌ ERROR: " + message);
        System.err.println(SEPARATOR);
    }
    
    @Override
    public void showBatchProcessStarted(int researcherCount, int articlesPerResearcher) {
        System.out.println(SEPARATOR);
        System.out.println("🚀 Starting batch processing:");
        System.out.println("   Researchers: " + researcherCount);
        System.out.println("   Articles per researcher: " + articlesPerResearcher);
        System.out.println("   Total expected: " + (researcherCount * articlesPerResearcher));
        System.out.println(SEPARATOR);
    }
    
    @Override
    public void showResearcherProcessed(String researcherName, int articleCount) {
        System.out.println("✅ Processed: " + researcherName + " (" + articleCount + " articles)");
    }
    
    @Override
    public void showResearcherSkipped(String researcherName, String reason) {
        System.out.println("⏭️  Skipped: " + researcherName + " - " + reason);
    }
    
    @Override
    public void showBatchProcessCompleted(List<Article> allArticles) {
        System.out.println(SEPARATOR);
        System.out.println("🎉 Batch processing completed!");
        System.out.println("📈 Total articles saved: " + allArticles.size());
        System.out.println(SEPARATOR);
    }
    
    @Override
    public void showStoredArticles(String authorName, List<Article> articles) {
        System.out.println(SEPARATOR);
        System.out.println("📚 Stored articles for: " + authorName);
        System.out.println("📊 Count: " + articles.size());
        System.out.println();
        
        for (int i = 0; i < articles.size(); i++) {
            Article article = articles.get(i);
            System.out.println((i + 1) + ". " + formatArticleDisplay(article));
        }
        System.out.println(SEPARATOR);
    }
    
    @Override
    public void showNoStoredArticles(String authorName) {
        System.out.println("📭 No stored articles found for: " + authorName);
        System.out.println("   Try searching for this author first.");
        System.out.println(SEPARATOR);
    }
    
    @Override
    public void showArticlesByYear(int year, List<Article> articles) {
        System.out.println(SEPARATOR);
        System.out.println("📅 Articles from year: " + year);
        System.out.println("📊 Count: " + articles.size());
        System.out.println();
        
        for (int i = 0; i < articles.size(); i++) {
            Article article = articles.get(i);
            System.out.println((i + 1) + ". " + formatArticleDisplay(article));
        }
        System.out.println(SEPARATOR);
    }
    
    @Override
    public void showNoArticlesForYear(int year) {
        System.out.println("📅 No articles found for year: " + year);
        System.out.println(SEPARATOR);
    }
    
    @Override
    public void showHighlyCitedArticles(int minCitations, List<Article> articles) {
        System.out.println(SEPARATOR);
        System.out.println("⭐ Highly cited articles (>" + minCitations + " citations):");
        System.out.println("📊 Count: " + articles.size());
        System.out.println();
        
        // Sort by citation count (descending)
        articles.sort((a, b) -> Integer.compare(b.getCitationCount(), a.getCitationCount()));
        
        for (int i = 0; i < articles.size(); i++) {
            Article article = articles.get(i);
            System.out.println((i + 1) + ". " + formatArticleDisplay(article));
        }
        System.out.println(SEPARATOR);
    }
    
    @Override
    public void showNoHighlyCitedArticles(int minCitations) {
        System.out.println("⭐ No articles found with more than " + minCitations + " citations.");
        System.out.println(SEPARATOR);
    }
    
    @Override
    public void showDatabaseStatistics(long totalArticles) {
        System.out.println(SEPARATOR);
        System.out.println("📊 DATABASE STATISTICS");
        System.out.println(SMALL_SEPARATOR);
        System.out.println("Total articles stored: " + totalArticles);
        System.out.println("Last updated: " + java.time.LocalDate.now().format(DATE_FORMATTER));
        System.out.println(SEPARATOR);
    }
    
    @Override
    public void showConnectivityTestStarted() {
        System.out.println(SEPARATOR);
        System.out.println("🔧 Testing system connectivity...");
        System.out.println(SMALL_SEPARATOR);
    }
    
    @Override
    public void showApiConnectionResult(boolean connected) {
        String status = connected ? "✅ CONNECTED" : "❌ FAILED";
        System.out.println("API Service: " + status);
    }
    
    @Override
    public void showDatabaseConnectionResult(boolean connected) {
        String status = connected ? "✅ CONNECTED" : "❌ FAILED";
        System.out.println("Database: " + status);
    }
    
    @Override
    public void showOverallSystemStatus(boolean operational) {
        System.out.println(SMALL_SEPARATOR);
        String status = operational ? "✅ OPERATIONAL" : "❌ SYSTEM ISSUES DETECTED";
        System.out.println("Overall Status: " + status);
        System.out.println(SEPARATOR);
    }
    
    /**
     * Formats an article for display with key information.
     */
    private String formatArticleDisplay(Article article) {
        StringBuilder display = new StringBuilder();
        
        // Title
        display.append("📄 ").append(article.getPaperTitle()).append("\n");
        
        // Authors
        display.append("   👥 Authors: ").append(article.getAuthors()).append("\n");
        
        // Publication info
        if (article.getPublicationYear() != null) {
            display.append("   📅 Year: ").append(article.getPublicationYear());
        }
        
        if (article.getJournal() != null && !article.getJournal().isEmpty()) {
            display.append(" | 📖 Journal: ").append(article.getJournal());
        }
        display.append("\n");
        
        // Citation count
        display.append("   📈 Citations: ").append(article.getCitationCount());
        
        // Database ID
        if (article.getId() != null) {
            display.append(" | 🆔 ID: ").append(article.getId());
        }
        
        // Scholar ID
        if (article.getGoogleScholarId() != null) {
            display.append(" | 🔗 Scholar ID: ").append(article.getGoogleScholarId());
        }
        display.append("\n");
        
        // URL (truncated if too long)
        if (article.getArticleUrl() != null) {
            String url = article.getArticleUrl();
            if (url.length() > 60) {
                url = url.substring(0, 57) + "...";
            }
            display.append("   🌐 URL: ").append(url).append("\n");
        }
        
        display.append("\n");
        return display.toString();
    }
}