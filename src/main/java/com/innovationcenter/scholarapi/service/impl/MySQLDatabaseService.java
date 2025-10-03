package com.innovationcenter.scholarapi.service.impl;

import com.innovationcenter.scholarapi.service.ConfigurationService;
import com.innovationcenter.scholarapi.service.DatabaseService;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * MySQL implementation of DatabaseService using HikariCP for connection pooling.
 * Handles database connections, schema initialization, and resource management.
 */
public class MySQLDatabaseService implements DatabaseService {
    
    private static final Logger logger = LoggerFactory.getLogger(MySQLDatabaseService.class);
    
    private static final String CREATE_ARTICLES_TABLE = 
        "CREATE TABLE IF NOT EXISTS articles (" +
        "id BIGINT PRIMARY KEY AUTO_INCREMENT, " +
        "paper_title VARCHAR(500) NOT NULL, " +
        "authors TEXT NOT NULL, " +
        "publication_year INTEGER, " +
        "journal VARCHAR(255), " +
        "article_url VARCHAR(500), " +
        "abstract_text TEXT, " +
        "google_scholar_id VARCHAR(50), " +
        "citation_count INTEGER DEFAULT 0, " +
        "cites_id VARCHAR(50), " +
        "pdf_url VARCHAR(500), " +
        "publisher VARCHAR(255), " +
        "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
        "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP, " +
        "INDEX idx_google_scholar_id (google_scholar_id), " +
        "INDEX idx_authors (authors(100)), " +
        "INDEX idx_publication_year (publication_year), " +
        "INDEX idx_citation_count (citation_count)" +
        ")";
    
    private final HikariDataSource dataSource;
    
    public MySQLDatabaseService(ConfigurationService configService) {
        this.dataSource = createDataSource(configService);
    }
    
    private HikariDataSource createDataSource(ConfigurationService configService) {
        HikariConfig config = new HikariConfig();
        
        // Database connection properties
        String host = configService.getProperty("DB_HOST");
        String port = configService.getProperty("DB_PORT");
        String database = configService.getProperty("DB_NAME");
        String username = configService.getProperty("DB_USER");
        String password = configService.getProperty("DB_PASSWORD");
        
        String jdbcUrl = String.format("jdbc:mysql://%s:%s/%s", host, port, database);
        
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(username);
        config.setPassword(password);
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        
        // Connection pool settings
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(2);
        config.setConnectionTimeout(30000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);
        
        // Additional MySQL-specific settings
        config.addDataSourceProperty("useSSL", "true");
        config.addDataSourceProperty("requireSSL", "false");
        config.addDataSourceProperty("verifyServerCertificate", "false");
        config.addDataSourceProperty("useUnicode", "true");
        config.addDataSourceProperty("characterEncoding", "utf8");
        config.addDataSourceProperty("serverTimezone", "UTC");
        
        logger.info("Initializing MySQL connection pool to {}:{}/{}", host, port, database);
        
        return new HikariDataSource(config);
    }
    
    @Override
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
    
    @Override
    public DataSource getDataSource() {
        return dataSource;
    }
    
    @Override
    public void initializeSchema() throws SQLException {
        logger.info("Initializing database schema...");
        
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            
            statement.execute(CREATE_ARTICLES_TABLE);
            logger.info("Articles table created or verified successfully");
            
        } catch (SQLException e) {
            logger.error("Failed to initialize database schema: {}", e.getMessage());
            throw e;
        }
    }
    
    @Override
    public boolean testConnection() {
        try (Connection connection = getConnection()) {
            boolean isValid = connection.isValid(5); // 5 second timeout
            if (isValid) {
                logger.info("Database connection test successful");
            } else {
                logger.warn("Database connection test failed - connection not valid");
            }
            return isValid;
        } catch (SQLException e) {
            logger.error("Database connection test failed: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            logger.info("Closing database connection pool");
            dataSource.close();
        }
    }
}