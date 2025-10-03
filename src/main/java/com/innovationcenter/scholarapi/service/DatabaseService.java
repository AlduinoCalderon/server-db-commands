package com.innovationcenter.scholarapi.service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Interface for database configuration and connection management.
 * Defines contract for database operations and connection pooling.
 */
public interface DatabaseService {
    
    /**
     * Get a database connection from the connection pool.
     * @return Database connection
     * @throws SQLException If connection cannot be established
     */
    Connection getConnection() throws SQLException;
    
    /**
     * Get the underlying DataSource for advanced operations.
     * @return DataSource instance
     */
    DataSource getDataSource();
    
    /**
     * Initialize database schema and tables if they don't exist.
     * @throws SQLException If schema creation fails
     */
    void initializeSchema() throws SQLException;
    
    /**
     * Test database connectivity.
     * @return true if connection is successful, false otherwise
     */
    boolean testConnection();
    
    /**
     * Close the database service and cleanup resources.
     */
    void close();
}