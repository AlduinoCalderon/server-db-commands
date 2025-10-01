package com.innovationcenter.scholarapi.service;

import com.innovationcenter.scholarapi.model.Article;
import com.innovationcenter.scholarapi.model.ScholarSearchResponse;
import com.innovationcenter.scholarapi.util.PublicationInfoParser;
import com.innovationcenter.scholarapi.repository.ArticleRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.sql.SQLException;

/**
 * Service layer for Article operations.
 * Handles business logic for article management and data conversion.
 */
public class ArticleService {
    private static final Logger logger = Logger.getLogger(ArticleService.class.getName());
    
    private final ArticleRepository articleRepository;
    
    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }
    
    /**
     * Converts API search response to Article entities and saves them.
     */
    public List<Article> processSearchResponse(ScholarSearchResponse response, int maxArticles) {
        List<Article> processedArticles = new ArrayList<>();
        
        if (response == null || response.getOrganicResults() == null) {
            logger.warning("Empty or invalid search response received");
            return processedArticles;
        }
        
        ScholarSearchResponse.OrganicResult[] results = response.getOrganicResults();
        int articlesToProcess = Math.min(results.length, maxArticles);
        
        logger.info("Processing " + articlesToProcess + " articles from search response");
        
        for (int i = 0; i < articlesToProcess; i++) {
            try {
                ScholarSearchResponse.OrganicResult result = results[i];
                Article article = convertOrganicResultToArticle(result);
                
                if (article.isValidForDatabase()) {
                    Article savedArticle = saveArticle(article);
                    processedArticles.add(savedArticle);
                    logger.fine("Successfully processed article: " + article.getPaperTitle());
                } else {
                    logger.warning("Article failed validation: " + article.getPaperTitle());
                }
                
            } catch (Exception e) {
                logger.log(Level.WARNING, "Failed to process article at index " + i, e);
            }
        }
        
        logger.info("Successfully processed " + processedArticles.size() + " articles");
        return processedArticles;
    }
    
    /**
     * Converts OrganicResult from API to Article entity using Technical Report mapping.
     */
    private Article convertOrganicResultToArticle(ScholarSearchResponse.OrganicResult result) {
        // Extract basic fields from API response
        String paperTitle = result.getTitle() != null ? result.getTitle() : "Unknown Title";
        String articleUrl = result.getLink();
        String abstractText = result.getSnippet();
        String googleScholarId = PublicationInfoParser.extractGoogleScholarId(result.getResultId());
        
        // Parse publication info using Technical Report parsing strategy
        String publicationSummary = result.getPublicationInfo() != null ? 
            result.getPublicationInfo().getSummary() : "";
        
        PublicationInfoParser.ParsedPublicationInfo parsedInfo = 
            PublicationInfoParser.parsePublicationSummary(publicationSummary);
        
        // Extract citation information
        int citationCount = 0;
        String citesId = null;
        
        if (result.getInlineLinks() != null && result.getInlineLinks().getCitedBy() != null) {
            citationCount = result.getInlineLinks().getCitedBy().getTotal();
            citesId = result.getInlineLinks().getCitedBy().getCitesId();
        }
        
        // Extract PDF URL
        String pdfUrl = PublicationInfoParser.extractPdfUrl(result.getResources());
        
        // Create Article entity with parsed data
        return new Article(
            paperTitle,
            parsedInfo.getAuthors(),
            parsedInfo.getYear(),
            parsedInfo.getSource(),
            articleUrl,
            abstractText,
            googleScholarId,
            citationCount,
            citesId,
            pdfUrl,
            parsedInfo.getPublisher()
        );
    }
    
    /**
     * Saves an article to the repository with duplicate checking.
     */
    public Article saveArticle(Article article) throws SQLException {
        if (!article.isValidForDatabase()) {
            throw new IllegalArgumentException("Article data is not valid for database storage");
        }
        
        // Check for existing article by Google Scholar ID
        if (article.getGoogleScholarId() != null) {
            List<Article> existingArticles = findByGoogleScholarId(article.getGoogleScholarId());
            if (!existingArticles.isEmpty()) {
                logger.info("Article already exists with Scholar ID: " + article.getGoogleScholarId());
                return existingArticles.get(0);
            }
        }
        
        // Save new article
        return articleRepository.save(article);
    }
    
    /**
     * Finds articles by Google Scholar ID.
     */
    public List<Article> findByGoogleScholarId(String googleScholarId) throws SQLException {
        if (googleScholarId == null || googleScholarId.trim().isEmpty()) {
            return new ArrayList<>();
        }
        
        // This assumes the repository has this method - if not, we'll implement it
        try {
            List<Article> allArticles = articleRepository.findAll();
            List<Article> matchingArticles = new ArrayList<>();
            
            for (Article article : allArticles) {
                if (googleScholarId.equals(article.getGoogleScholarId())) {
                    matchingArticles.add(article);
                }
            }
            
            return matchingArticles;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error searching for articles by Scholar ID", e);
            throw e;
        }
    }
    
    /**
     * Finds articles by author name.
     */
    public List<Article> findByAuthor(String authorName) throws SQLException {
        return articleRepository.findByAuthor(authorName);
    }
    
    /**
     * Finds articles by publication year.
     */
    public List<Article> findByYear(int year) throws SQLException {
        List<Article> allArticles = articleRepository.findAll();
        List<Article> yearArticles = new ArrayList<>();
        
        for (Article article : allArticles) {
            if (article.getPublicationYear() != null && article.getPublicationYear() == year) {
                yearArticles.add(article);
            }
        }
        
        return yearArticles;
    }
    
    /**
     * Finds articles with citation count greater than specified threshold.
     */
    public List<Article> findByCitationsGreaterThan(int minCitations) throws SQLException {
        return articleRepository.findByCitationsGreaterThan(minCitations);
    }
    
    /**
     * Gets total count of articles in repository.
     */
    public long getTotalArticleCount() throws SQLException {
        return articleRepository.count();
    }
    
    /**
     * Updates an existing article.
     */
    public Article updateArticle(Article article) throws SQLException {
        if (article.getId() == null) {
            throw new IllegalArgumentException("Article must have an ID for updates");
        }
        
        article.updateTimestamp();
        return articleRepository.update(article);
    }
    
    /**
     * Deletes an article by ID.
     */
    public boolean deleteArticle(Long id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("Article ID cannot be null");
        }
        
        return articleRepository.deleteById(id);
    }
    
    /**
     * Validates article data for consistency.
     */
    public boolean validateArticleData(Article article) {
        if (!article.isValidForDatabase()) {
            return false;
        }
        
        // Additional business validation rules
        if (article.getPublicationYear() != null) {
            int currentYear = java.time.LocalDate.now().getYear();
            if (article.getPublicationYear() < 1900 || article.getPublicationYear() > currentYear + 1) {
                logger.warning("Invalid publication year: " + article.getPublicationYear());
                return false;
            }
        }
        
        if (article.getCitationCount() < 0) {
            logger.warning("Invalid citation count: " + article.getCitationCount());
            return false;
        }
        
        return true;
    }
}