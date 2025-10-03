package com.innovationcenter.scholarapi.repository.impl;

import com.innovationcenter.scholarapi.model.SimpleAuthor;
import com.innovationcenter.scholarapi.repository.SimpleAuthorRepository;
import com.innovationcenter.scholarapi.service.DatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * MySQL implementation of SimpleAuthorRepository.
 * Handles CRUD operations for authors using JDBC.
 */
public class MySQLSimpleAuthorRepository implements SimpleAuthorRepository {
    
    private static final Logger logger = LoggerFactory.getLogger(MySQLSimpleAuthorRepository.class);
    
    private final DatabaseService databaseService;
    
    public MySQLSimpleAuthorRepository(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }
    
    @Override
    public SimpleAuthor save(SimpleAuthor author) throws SQLException {
        // Check if author already exists
        Optional<SimpleAuthor> existing = findByFullName(author.getFullName());
        if (existing.isPresent()) {
            logger.info("Author already exists: {}", author.getFullName());
            return existing.get();
        }
        
        String sql = "INSERT INTO authors (full_name, article_count, total_citations) VALUES (?, ?, ?)";
        
        try (Connection connection = databaseService.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setString(1, author.getFullName());
            statement.setInt(2, author.getArticleCount() != null ? author.getArticleCount() : 0);
            statement.setInt(3, author.getTotalCitations() != null ? author.getTotalCitations() : 0);
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows == 0) {
                throw new SQLException("Creating author failed, no rows affected.");
            }
            
            try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    author.setId(generatedKeys.getLong(1));
                } else {
                    throw new SQLException("Creating author failed, no ID obtained.");
                }
            }
            
            logger.info("Author saved successfully: {} (ID: {})", author.getFullName(), author.getId());
            return author;
            
        } catch (SQLException e) {
            logger.error("Failed to save author: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public Optional<SimpleAuthor> findById(Long id) throws SQLException {
        String sql = "SELECT * FROM authors WHERE id = ?";
        
        try (Connection connection = databaseService.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, id);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToAuthor(resultSet));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            logger.error("Failed to find author by ID: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public Optional<SimpleAuthor> findByFullName(String fullName) throws SQLException {
        String sql = "SELECT * FROM authors WHERE full_name = ? AND deleted_at IS NULL";
        
        try (Connection connection = databaseService.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, fullName);
            
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return Optional.of(mapResultSetToAuthor(resultSet));
                }
            }
            
            return Optional.empty();
            
        } catch (SQLException e) {
            logger.error("Failed to find author by name: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public List<SimpleAuthor> findByNamePattern(String namePattern) throws SQLException {
        String sql = "SELECT * FROM authors WHERE full_name LIKE ? AND deleted_at IS NULL ORDER BY total_citations DESC";
        
        try (Connection connection = databaseService.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setString(1, "%" + namePattern + "%");
            
            return executeQueryAndMapResults(statement);
            
        } catch (SQLException e) {
            logger.error("Failed to search authors by pattern: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public List<SimpleAuthor> findTopByCitations(int limit) throws SQLException {
        String sql = "SELECT * FROM authors WHERE article_count > 0 AND deleted_at IS NULL ORDER BY total_citations DESC LIMIT ?";
        
        try (Connection connection = databaseService.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, limit);
            
            return executeQueryAndMapResults(statement);
            
        } catch (SQLException e) {
            logger.error("Failed to find top authors by citations: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public List<SimpleAuthor> findTopByArticleCount(int limit) throws SQLException {
        String sql = "SELECT * FROM authors WHERE article_count > 0 AND deleted_at IS NULL ORDER BY article_count DESC, total_citations DESC LIMIT ?";
        
        try (Connection connection = databaseService.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, limit);
            
            return executeQueryAndMapResults(statement);
            
        } catch (SQLException e) {
            logger.error("Failed to find top authors by article count: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public boolean updateStatistics(Long authorId, int articleCount, int totalCitations) throws SQLException {
        String sql = "UPDATE authors SET article_count = ?, total_citations = ? WHERE id = ?";
        
        try (Connection connection = databaseService.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, articleCount);
            statement.setInt(2, totalCitations);
            statement.setLong(3, authorId);
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows > 0) {
                logger.info("Updated statistics for author ID {}: {} articles, {} citations", 
                    authorId, articleCount, totalCitations);
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            logger.error("Failed to update author statistics: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public boolean linkToArticle(Long articleId, Long authorId, int position) throws SQLException {
        String sql = "INSERT INTO article_authors (article_id, author_id, author_position) VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE author_position = ?";
        
        try (Connection connection = databaseService.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, articleId);
            statement.setLong(2, authorId);
            statement.setInt(3, position);
            statement.setInt(4, position);
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows > 0) {
                logger.debug("Linked article {} to author {} at position {}", articleId, authorId, position);
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            logger.error("Failed to link article to author: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public List<SimpleAuthor> findByArticleId(Long articleId) throws SQLException {
        String sql = "SELECT a.* FROM authors a " +
                    "JOIN article_authors aa ON a.id = aa.author_id " +
                    "WHERE aa.article_id = ? AND a.deleted_at IS NULL " +
                    "ORDER BY aa.author_position";
        
        try (Connection connection = databaseService.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, articleId);
            
            return executeQueryAndMapResults(statement);
            
        } catch (SQLException e) {
            logger.error("Failed to find authors for article: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public long count() throws SQLException {
        String sql = "SELECT COUNT(*) FROM authors WHERE deleted_at IS NULL";
        
        try (Connection connection = databaseService.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            
            if (resultSet.next()) {
                return resultSet.getLong(1);
            }
            
            return 0;
            
        } catch (SQLException e) {
            logger.error("Failed to count authors: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public boolean deleteById(Long id) throws SQLException {
        String sql = "UPDATE authors SET deleted_at = CURRENT_TIMESTAMP WHERE id = ? AND deleted_at IS NULL";
        
        try (Connection connection = databaseService.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setLong(1, id);
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows > 0) {
                logger.info("Soft deleted author with ID: {}", id);
                return true;
            }
            
            return false;
            
        } catch (SQLException e) {
            logger.error("Failed to delete author: {}", e.getMessage());
            throw e;
        }
    }
    
    /**
     * Execute query and map results to SimpleAuthor list.
     */
    private List<SimpleAuthor> executeQueryAndMapResults(PreparedStatement statement) throws SQLException {
        List<SimpleAuthor> authors = new ArrayList<>();
        
        try (ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                authors.add(mapResultSetToAuthor(resultSet));
            }
        }
        
        return authors;
    }
    
    /**
     * Map database result set to SimpleAuthor object.
     */
    private SimpleAuthor mapResultSetToAuthor(ResultSet resultSet) throws SQLException {
        SimpleAuthor author = new SimpleAuthor();
        
        author.setId(resultSet.getLong("id"));
        author.setFullName(resultSet.getString("full_name"));
        author.setFirstSeen(resultSet.getTimestamp("first_seen"));
        author.setLastUpdated(resultSet.getTimestamp("last_updated"));
        author.setDeletedAt(resultSet.getTimestamp("deleted_at"));
        author.setArticleCount(resultSet.getInt("article_count"));
        author.setTotalCitations(resultSet.getInt("total_citations"));
        
        return author;
    }
}
