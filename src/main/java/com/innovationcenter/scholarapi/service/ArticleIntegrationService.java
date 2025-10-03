package com.innovationcenter.scholarapi.service;

import com.innovationcenter.scholarapi.model.Article;
import com.innovationcenter.scholarapi.model.Author;
import com.innovationcenter.scholarapi.model.AuthorSearchResult;
import com.innovationcenter.scholarapi.model.Publication;
import com.innovationcenter.scholarapi.repository.ArticleRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.sql.SQLException;
import java.io.IOException;

/**
 * Service for integrating Google Scholar API results with database storage.
 * Handles the conversion and persistence of researcher articles to the database.
 */
public class ArticleIntegrationService {
    private static final Logger logger = Logger.getLogger(ArticleIntegrationService.class.getName());
    
    private final ApiService apiService;
    private final ArticleRepository articleRepository;
    
    public ArticleIntegrationService(ApiService apiService, ArticleRepository articleRepository) {
        this.apiService = apiService;
        this.articleRepository = articleRepository;
    }
    
    /**
     * Searches for articles by researcher and stores them in the database.
     * 
     * @param researcherName The name of the researcher to search for
     * @param maxArticles Maximum number of articles to retrieve and store
     * @return List of articles that were successfully saved to the database
     */
    public List<Article> searchAndStoreArticles(String researcherName, int maxArticles) {
        List<Article> savedArticles = new ArrayList<>();
        
        try {
            logger.info("Searching for articles by researcher: " + researcherName);
            
            // Search for authors using the API service
            List<AuthorSearchResult> authorResults = apiService.searchAuthors(researcherName);
            
            if (authorResults.isEmpty()) {
                logger.warning("No authors found for: " + researcherName);
                return savedArticles;
            }
            
            // Process publications from the first matching author
            AuthorSearchResult firstResult = authorResults.get(0);
            Author author = firstResult.getAuthor();
            
            if (author == null || author.getPublications() == null || author.getPublications().isEmpty()) {
                logger.warning("No publications found for researcher: " + researcherName);
                return savedArticles;
            }
            
            List<Publication> publications = author.getPublications();
            int articlesToProcess = Math.min(publications.size(), maxArticles);
            logger.info("Processing " + articlesToProcess + " articles for " + researcherName);
            
            // Convert and save each publication as an article
            for (int i = 0; i < articlesToProcess; i++) {
                try {
                    Publication publication = publications.get(i);
                    Article article = convertPublicationToArticle(publication, author.getName());
                    Article savedArticle = articleRepository.save(article);
                    savedArticles.add(savedArticle);
                    logger.info("Saved article: " + savedArticle.getTitle());
                } catch (SQLException e) {
                    logger.log(Level.SEVERE, "Failed to save article for " + researcherName + 
                              ": " + e.getMessage(), e);
                    // Continue processing other articles even if one fails
                } catch (Exception e) {
                    logger.log(Level.WARNING, "Failed to convert publication for " + researcherName + 
                              ": " + e.getMessage(), e);
                }
            }
            
        } catch (IOException e) {
            logger.log(Level.SEVERE, "API error searching for researcher " + 
                      researcherName + ": " + e.getMessage(), e);
            throw new RuntimeException("Failed to search and store articles for " + researcherName, e);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Unexpected error processing researcher " + 
                      researcherName + ": " + e.getMessage(), e);
            throw new RuntimeException("Failed to search and store articles for " + researcherName, e);
        }
        
        return savedArticles;
    }
    
    /**
     * Processes multiple researchers and stores their articles.
     * 
     * @param researchers List of researcher names
     * @param articlesPerResearcher Number of articles to store per researcher
     * @return List of all articles that were successfully saved
     */
    public List<Article> processMultipleResearchers(List<String> researchers, int articlesPerResearcher) {
        List<Article> allSavedArticles = new ArrayList<>();
        
        logger.info("Processing " + researchers.size() + " researchers with " + 
                   articlesPerResearcher + " articles each");
        
        for (String researcher : researchers) {
            try {
                List<Article> researcherArticles = searchAndStoreArticles(researcher, articlesPerResearcher);
                allSavedArticles.addAll(researcherArticles);
                
                logger.info("Successfully processed " + researcherArticles.size() + 
                           " articles for " + researcher);
                
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Failed to process researcher " + researcher + 
                          ": " + e.getMessage(), e);
                // Continue with next researcher
            }
        }
        
        logger.info("Total articles saved: " + allSavedArticles.size());
        return allSavedArticles;
    }
    
    /**
     * Converts a Publication entity to an Article entity for database storage.
     * 
     * @param publication The Publication from the API response
     * @param primaryAuthor The primary author name
     * @return Article entity ready for database storage
     */
    private Article convertPublicationToArticle(Publication publication, String primaryAuthor) {
        try {
            String title = publication.getTitle() != null ? publication.getTitle() : "Unknown Title";
            String authors = publication.getAuthors() != null ? publication.getAuthors() : primaryAuthor;
            
            // Convert year to LocalDate
            LocalDate publicationDate = LocalDate.now(); // Default to current date
            if (publication.getYear() > 0) {
                try {
                    publicationDate = LocalDate.of(publication.getYear(), 1, 1);
                } catch (Exception e) {
                    logger.warning("Could not parse year: " + publication.getYear());
                }
            }
            
            // Use snippet as abstract if available
            String abstractText = publication.getSnippet() != null ? 
                publication.getSnippet() : "Abstract not available";
            
            // Use publication link or generate Scholar link
            String link = publication.getLink() != null ? 
                publication.getLink() : generateScholarLink(title, primaryAuthor);
            
            // Extract keywords from title
            String keywords = extractKeywords(title);
            
            // Get citation count
            int citedBy = publication.getCitedBy();
            
            Article article = new Article(
                title,  // paperTitle
                authors, // authors
                publicationDate != null ? publicationDate.getYear() : null, // publicationYear
                "Unknown Journal", // journal
                link, // articleUrl
                abstractText, // abstractText
                null, // googleScholarId
                citedBy, // citationCount
                null, // citesId
                null, // pdfUrl
                "Unknown Publisher" // publisher
            );
            
            logger.fine("Converted publication: " + title + " by " + authors);
            return article;
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error converting publication: " + 
                      (publication != null ? publication.getTitle() : "null"), e);
            
            // Return a basic article with minimal information
            return new Article(
                "Article by " + primaryAuthor, // paperTitle
                primaryAuthor, // authors
                LocalDate.now().getYear(), // publicationYear
                "Unknown", // journal
                generateScholarLink("", primaryAuthor), // articleUrl
                "Parsing error occurred", // abstractText
                null, // googleScholarId
                0, // citationCount
                null, // citesId
                null, // pdfUrl
                primaryAuthor // publisher
            );
        }
    }
    
    /**
     * Generates a Google Scholar search link for the article.
     */
    private String generateScholarLink(String title, String author) {
        String searchQuery = (title + " " + author).replaceAll(" ", "+");
        return "https://scholar.google.com/scholar?q=" + searchQuery;
    }
    
    /**
     * Extracts keywords from the article title.
     * Simple implementation that removes common words and takes significant terms.
     */
    private String extractKeywords(String title) {
        if (title == null || title.trim().isEmpty()) {
            return "";
        }
        
        // Remove common words and extract meaningful terms
        String[] words = title.toLowerCase()
                              .replaceAll("[^a-zA-Z\\s]", "")
                              .split("\\s+");
        
        StringBuilder keywords = new StringBuilder();
        for (String word : words) {
            if (word.length() > 3 && !isCommonWord(word)) {
                if (keywords.length() > 0) {
                    keywords.append(", ");
                }
                keywords.append(word);
            }
        }
        
        return keywords.toString();
    }
    
    /**
     * Checks if a word is a common word that should be excluded from keywords.
     */
    private boolean isCommonWord(String word) {
        String[] commonWords = {"the", "and", "for", "are", "but", "not", "you", "all", 
                               "can", "had", "her", "was", "one", "our", "out", "day", 
                               "get", "has", "him", "his", "how", "its", "new", "now", 
                               "old", "see", "two", "who", "boy", "did", "may", "she", 
                               "use", "way", "will", "with"};
        
        for (String common : commonWords) {
            if (word.equals(common)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Retrieves statistics about stored articles.
     */
    public String getStorageStatistics() {
        try {
            long totalArticles = articleRepository.count();
            return "Total articles in database: " + totalArticles;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Error retrieving storage statistics", e);
            return "Error retrieving statistics: " + e.getMessage();
        }
    }
}