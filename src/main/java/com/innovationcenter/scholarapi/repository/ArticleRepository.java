package com.innovationcenter.scholarapi.repository;

import com.innovationcenter.scholarapi.model.Article;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Interface for Article data access operations.
 * Defines contract for CRUD operations on articles.
 */
public interface ArticleRepository {
    
    /**
     * Save a new article to the database.
     * @param article Article to save
     * @return Saved article with generated ID
     * @throws SQLException If save operation fails
     */
    Article save(Article article) throws SQLException;
    
    /**
     * Find an article by its ID.
     * @param id Article ID
     * @return Optional containing the article if found, empty otherwise
     * @throws SQLException If query fails
     */
    Optional<Article> findById(Long id) throws SQLException;
    
    /**
     * Find all articles by a specific author.
     * @param authorName Author name to search for
     * @return List of articles by the author
     * @throws SQLException If query fails
     */
    List<Article> findByAuthor(String authorName) throws SQLException;
    
    /**
     * Find all articles in the database.
     * @return List of all articles
     * @throws SQLException If query fails
     */
    List<Article> findAll() throws SQLException;
    
    /**
     * Update an existing article.
     * @param article Article with updated data
     * @return Updated article
     * @throws SQLException If update fails
     */
    Article update(Article article) throws SQLException;
    
    /**
     * Delete an article by its ID.
     * @param id Article ID to delete
     * @return true if article was deleted, false if not found
     * @throws SQLException If delete operation fails
     */
    boolean deleteById(Long id) throws SQLException;
    
    /**
     * Count total number of articles in the database.
     * @return Total article count
     * @throws SQLException If count operation fails
     */
    long count() throws SQLException;
    
    /**
     * Find articles with citation count greater than specified value.
     * @param minCitations Minimum citation count
     * @return List of highly cited articles
     * @throws SQLException If query fails
     */
    List<Article> findByCitationsGreaterThan(int minCitations) throws SQLException;
}