package com.innovationcenter.scholarapi.service;

import com.innovationcenter.scholarapi.model.Article;
import com.innovationcenter.scholarapi.model.ScholarSearchResponse;
import com.innovationcenter.scholarapi.model.SimpleAuthor;
import com.innovationcenter.scholarapi.util.PublicationInfoParser;
import com.innovationcenter.scholarapi.util.AuthorParser;
import com.innovationcenter.scholarapi.repository.ArticleRepository;
import com.innovationcenter.scholarapi.repository.SimpleAuthorRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Service layer for Article operations.
 * Handles business logic for article management and data conversion.
 */
public class ArticleService {
    private static final Logger logger = Logger.getLogger(ArticleService.class.getName());
    
    private final ArticleRepository articleRepository;
    private final SimpleAuthorRepository authorRepository;
    
    public ArticleService(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
        this.authorRepository = null; // For backward compatibility
    }
    
    public ArticleService(ArticleRepository articleRepository, SimpleAuthorRepository authorRepository) {
        this.articleRepository = articleRepository;
        this.authorRepository = authorRepository;
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
        Article savedArticle = articleRepository.save(article);
        
        // Extract and save authors if author repository is available
        if (authorRepository != null && savedArticle.getId() != null) {
            try {
                saveAuthorsForArticle(savedArticle);
            } catch (Exception e) {
                logger.log(Level.WARNING, "Failed to save authors for article: " + savedArticle.getId(), e);
                // Don't fail the whole operation if author extraction fails
            }
        }
        
        return savedArticle;
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
     * Searches articles by title keyword.
     */
    public List<Article> searchByTitle(String keyword) throws SQLException {
        try {
            List<Article> allArticles = articleRepository.findAll();
            List<Article> matchingArticles = new ArrayList<>();
            
            String lowerKeyword = keyword.toLowerCase();
            
            for (Article article : allArticles) {
                if (article.getPaperTitle() != null && 
                    article.getPaperTitle().toLowerCase().contains(lowerKeyword)) {
                    matchingArticles.add(article);
                }
            }
            
            logger.info("Found " + matchingArticles.size() + " articles matching title keyword: " + keyword);
            return matchingArticles;
            
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error searching articles by title", e);
            throw e;
        }
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
     * Deletes an author by ID (soft delete).
     */
    public boolean deleteAuthor(Long id) throws SQLException {
        if (id == null) {
            throw new IllegalArgumentException("Author ID cannot be null");
        }
        
        if (authorRepository == null) {
            throw new IllegalStateException("Author repository not initialized");
        }
        
        return authorRepository.deleteById(id);
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
    
    /**
     * Extract authors from article and save them to database.
     * Creates author records and links them to the article.
     */
    private void saveAuthorsForArticle(Article article) throws SQLException {
        if (article.getAuthors() == null || article.getAuthors().trim().isEmpty()) {
            logger.fine("No authors to extract for article: " + article.getId());
            return;
        }
        
        // Parse authors from the author string
        List<SimpleAuthor> authors = AuthorParser.parseAuthors(article.getAuthors());
        
        if (authors.isEmpty()) {
            logger.fine("No valid authors parsed for article: " + article.getId());
            return;
        }
        
        logger.info("Extracting " + authors.size() + " authors for article: " + article.getPaperTitle());
        
        int position = 0;
        for (SimpleAuthor author : authors) {
            try {
                // Save author (or get existing one)
                SimpleAuthor savedAuthor = authorRepository.save(author);
                
                // Link author to article with position
                authorRepository.linkToArticle(article.getId(), savedAuthor.getId(), position);
                
                // Update author statistics
                // Get current stats
                Optional<SimpleAuthor> currentAuthor = authorRepository.findById(savedAuthor.getId());
                if (currentAuthor.isPresent()) {
                    SimpleAuthor current = currentAuthor.get();
                    int newArticleCount = current.getArticleCount() + 1;
                    int newTotalCitations = current.getTotalCitations() + article.getCitationCount();
                    
                    authorRepository.updateStatistics(
                        savedAuthor.getId(),
                        newArticleCount,
                        newTotalCitations
                    );
                    
                    logger.fine("Updated statistics for author: " + savedAuthor.getFullName());
                }
                
                position++;
                
            } catch (SQLException e) {
                logger.log(Level.WARNING, "Failed to save author: " + author.getFullName(), e);
                // Continue with next author
            }
        }
        
        logger.info("Successfully saved " + position + " authors for article: " + article.getId());
    }
    
    /**
     * Get all authors for a specific article.
     */
    public List<SimpleAuthor> getAuthorsForArticle(Long articleId) throws SQLException {
        if (authorRepository == null) {
            return new ArrayList<>();
        }
        return authorRepository.findByArticleId(articleId);
    }
    
    /**
     * Search authors by name pattern.
     */
    public List<SimpleAuthor> searchAuthors(String namePattern) throws SQLException {
        if (authorRepository == null) {
            return new ArrayList<>();
        }
        return authorRepository.findByNamePattern(namePattern);
    }
    
    /**
     * Get top authors by citation count.
     */
    public List<SimpleAuthor> getTopAuthorsByCitations(int limit) throws SQLException {
        if (authorRepository == null) {
            return new ArrayList<>();
        }
        return authorRepository.findTopByCitations(limit);
    }
    
    /**
     * Get top authors by article count.
     */
    public List<SimpleAuthor> getTopAuthorsByArticleCount(int limit) throws SQLException {
        if (authorRepository == null) {
            return new ArrayList<>();
        }
        return authorRepository.findTopByArticleCount(limit);
    }
}