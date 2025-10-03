package com.innovationcenter.scholarapi.repository;

import com.innovationcenter.scholarapi.model.SimpleAuthor;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for SimpleAuthor operations.
 * Handles database operations for author records.
 */
public interface SimpleAuthorRepository {
    
    /**
     * Save a new author or return existing one if name already exists.
     * 
     * @param author The author to save
     * @return The saved author with ID
     * @throws SQLException if database operation fails
     */
    SimpleAuthor save(SimpleAuthor author) throws SQLException;
    
    /**
     * Find author by ID.
     * 
     * @param id The author ID
     * @return Optional containing the author if found
     * @throws SQLException if database operation fails
     */
    Optional<SimpleAuthor> findById(Long id) throws SQLException;
    
    /**
     * Find author by exact full name.
     * 
     * @param fullName The author's full name
     * @return Optional containing the author if found
     * @throws SQLException if database operation fails
     */
    Optional<SimpleAuthor> findByFullName(String fullName) throws SQLException;
    
    /**
     * Find authors by name pattern (case-insensitive LIKE search).
     * 
     * @param namePattern The search pattern
     * @return List of matching authors
     * @throws SQLException if database operation fails
     */
    List<SimpleAuthor> findByNamePattern(String namePattern) throws SQLException;
    
    /**
     * Find all authors ordered by total citations (descending).
     * 
     * @param limit Maximum number of results
     * @return List of top authors
     * @throws SQLException if database operation fails
     */
    List<SimpleAuthor> findTopByCitations(int limit) throws SQLException;
    
    /**
     * Find all authors ordered by article count (descending).
     * 
     * @param limit Maximum number of results
     * @return List of most productive authors
     * @throws SQLException if database operation fails
     */
    List<SimpleAuthor> findTopByArticleCount(int limit) throws SQLException;
    
    /**
     * Update author statistics (article count and total citations).
     * 
     * @param authorId The author ID
     * @param articleCount New article count
     * @param totalCitations New total citations
     * @return true if update successful
     * @throws SQLException if database operation fails
     */
    boolean updateStatistics(Long authorId, int articleCount, int totalCitations) throws SQLException;
    
    /**
     * Link an author to an article.
     * 
     * @param articleId The article ID
     * @param authorId The author ID
     * @param position The author's position in the author list (0-based)
     * @return true if link created successfully
     * @throws SQLException if database operation fails
     */
    boolean linkToArticle(Long articleId, Long authorId, int position) throws SQLException;
    
    /**
     * Get all authors for a specific article.
     * 
     * @param articleId The article ID
     * @return List of authors ordered by position
     * @throws SQLException if database operation fails
     */
    List<SimpleAuthor> findByArticleId(Long articleId) throws SQLException;
    
    /**
     * Get total count of authors in database.
     * 
     * @return Total number of authors
     * @throws SQLException if database operation fails
     */
    long count() throws SQLException;
    
    /**
     * Delete author by ID.
     * 
     * @param id The author ID
     * @return true if deletion successful
     * @throws SQLException if database operation fails
     */
    boolean deleteById(Long id) throws SQLException;
}
