package com.innovationcenter.scholarapi.repository.impl;

import com.innovationcenter.scholarapi.model.Article;
import com.innovationcenter.scholarapi.repository.ArticleRepository;
import com.innovationcenter.scholarapi.service.DatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * MySQL implementation of ArticleRepository.
 * Handles CRUD operations for articles using JDBC.
 */
public class MySQLArticleRepository implements ArticleRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(MySQLArticleRepository.class);
    
    private final DatabaseService databaseService;
    
    public MySQLArticleRepository(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }
    
    @Override
    public Article save(Article article) throws SQLException {
        String sql = "INSERT INTO articles (paper_title, authors, publication_year, abstract_text, article_url, google_scholar_id, citation_count) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection connection = databaseService.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setString(1, article.getPaperTitle());
            statement.setString(2, article.getAuthors());
            
            // Handle NULL publication year
            if (article.getPublicationYear() != null) {
                statement.setInt(3, article.getPublicationYear());
            } else {
                statement.setNull(3, Types.INTEGER);
            }
            
            statement.setString(4, article.getAbstractText());
            statement.setString(5, article.getArticleUrl());
            statement.setString(6, article.getGoogleScholarId());
            statement.setInt(7, article.getCitationCount());
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating article failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    article.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating article failed, no ID obtained.");
                }
            }
            
            logger.info("Article saved successfully with ID: {}", article.getId());
            return article;
            
        } catch (SQLException e) {
            logger.error("Failed to save article: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public Optional<Article> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM articles WHERE id = ?";
        
        try (Connection connection = databaseService.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToArticle(resultSet));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            logger.error("Failed to find article by ID {}: {}", id, e.getMessage());
            throw e;
        }
    }
    
    @Override
    public List<Article> findByAuthor(String authorName) throws SQLException {
        String sql = "SELECT * FROM articles WHERE authors LIKE ? AND deleted_at IS NULL ORDER BY publication_year DESC";
        
        try (Connection connection = databaseService.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, "%" + authorName + "%");
            
            return executeQueryAndMapResults(statement);
            
        } catch (SQLException e) {
            logger.error("Failed to find articles by author {}: {}", authorName, e.getMessage());
            throw e;
        }
    }
    
    @Override
    public List<Article> findAll() throws SQLException {
        String sql = "SELECT * FROM articles WHERE deleted_at IS NULL ORDER BY publication_year DESC, citation_count DESC";
        
        try (Connection connection = databaseService.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            return executeQueryAndMapResults(statement);
            
        } catch (SQLException e) {
            logger.error("Failed to find all articles: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public Article update(Article article) throws SQLException {
        String sql = "UPDATE articles SET paper_title = ?, authors = ?, publication_year = ?, " +
                    "abstract_text = ?, article_url = ?, google_scholar_id = ?, citation_count = ? WHERE id = ?";
        
        try (Connection connection = databaseService.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, article.getPaperTitle());
            statement.setString(2, article.getAuthors());
            statement.setInt(3, article.getPublicationYear());
            statement.setString(4, article.getAbstractText());
            statement.setString(5, article.getArticleUrl());
            statement.setString(6, article.getGoogleScholarId());
            statement.setInt(7, article.getCitationCount());
            statement.setLong(8, article.getId());
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Updating article failed, no rows affected.");
            }
            
            logger.info("Article updated successfully: {}", article.getId());
            return article;
            
        } catch (SQLException e) {
            logger.error("Failed to update article {}: {}", article.getId(), e.getMessage());
            throw e;
        }
    }
    
    @Override
    public boolean deleteById(Long id) throws SQLException {
        String sql = "UPDATE articles SET deleted_at = CURRENT_TIMESTAMP WHERE id = ? AND deleted_at IS NULL";
        
        try (Connection connection = databaseService.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, id);
            
            int affectedRows = statement.executeUpdate();
            boolean deleted = affectedRows > 0;
            
            if (deleted) {
                logger.info("Article soft deleted successfully: {}", id);
            } else {
                logger.warn("No active article found with ID: {}", id);
            }
            
            return deleted;
            
        } catch (SQLException e) {
            logger.error("Failed to delete article {}: {}", id, e.getMessage());
            throw e;
        }
    }
    
    @Override
    public long count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM articles WHERE deleted_at IS NULL";
        
        try (Connection connection = databaseService.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
            
            return 0;
            
        } catch (SQLException e) {
            logger.error("Failed to count articles: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public List<Article> findByCitationsGreaterThan(int minCitations) throws SQLException {
        String sql = "SELECT * FROM articles WHERE citation_count > ? AND deleted_at IS NULL ORDER BY citation_count DESC, publication_year DESC";
        
        try (Connection connection = databaseService.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, minCitations);
            
            return executeQueryAndMapResults(statement);
            
        } catch (SQLException e) {
            logger.error("Failed to find articles with citations > {}: {}", minCitations, e.getMessage());
            throw e;
        }
    }
    
    /**
     * Execute query and map results to Article list.
     */
    private List<Article> executeQueryAndMapResults(PreparedStatement statement) throws SQLException {
        List<Article> articles = new ArrayList<>();
        
        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                articles.add(mapResultSetToArticle(resultSet));
            }
        }
        
        return articles;
    }
    
    /**
     * Map database result set to Article object.
     */
    private Article mapResultSetToArticle(ResultSet resultSet) throws SQLException {
        Article article = new Article();
        
        article.setId(resultSet.getLong("id"));
        article.setPaperTitle(resultSet.getString("paper_title"));
        article.setAuthors(resultSet.getString("authors"));
        article.setPublicationYear(resultSet.getInt("publication_year"));
        article.setJournal(resultSet.getString("journal"));
        article.setArticleUrl(resultSet.getString("article_url"));
        article.setAbstractText(resultSet.getString("abstract_text"));
        article.setGoogleScholarId(resultSet.getString("google_scholar_id"));
        article.setCitationCount(resultSet.getInt("citation_count"));
        article.setCitesId(resultSet.getString("cites_id"));
        article.setPdfUrl(resultSet.getString("pdf_url"));
        article.setPublisher(resultSet.getString("publisher"));
        
        return article;
    }
}