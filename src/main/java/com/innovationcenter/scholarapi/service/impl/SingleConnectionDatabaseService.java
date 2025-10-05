package com.innovationcenter.scholarapi.service.impl;

import com.innovationcenter.scholarapi.service.ConfigurationService;
import com.innovationcenter.scholarapi.service.DatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * DatabaseService implementation that opens a fresh JDBC Connection per request
 * and enforces at most one simultaneous open connection for the whole application.
 *
 * This class is useful when you want strict single-connection semantics: every
 * repository call should open and close a connection and only one operation can
 * run at a time. It uses a Semaphore(1) to limit concurrent connections and
 * returns a proxy Connection which releases the semaphore when closed.
 */
public class SingleConnectionDatabaseService implements DatabaseService {

    private static final Logger logger = LoggerFactory.getLogger(SingleConnectionDatabaseService.class);

    private final String jdbcUrl;
    private final String username;
    private final String password;
    private final Semaphore semaphore = new Semaphore(1, true);

    public SingleConnectionDatabaseService(ConfigurationService configService) {
        Objects.requireNonNull(configService, "configService");
        String host = configService.getProperty("DB_HOST");
        String port = configService.getProperty("DB_PORT");
        String database = configService.getProperty("DB_NAME");
        this.username = configService.getProperty("DB_USER");
        this.password = configService.getProperty("DB_PASSWORD");
        this.jdbcUrl = String.format("jdbc:mysql://%s:%s/%s", host, port, database);
        logger.info("SingleConnectionDatabaseService configured for {}:{}/{}", host, port, database);
    }

    @Override
    public Connection getConnection() throws SQLException {
        try {
            // Acquire the single-permit semaphore. This will block until the previous
            // connection has been closed and released.
            semaphore.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new SQLException("Interrupted while waiting for DB connection permit", e);
        }

        // Open a fresh physical connection for this operation
        Connection realConn = DriverManager.getConnection(jdbcUrl, username, password);

        // Wrap the connection so that when close() is called we release the semaphore
        return new ConnectionReleaseProxy(realConn, semaphore);
    }

    @Override
    public DataSource getDataSource() {
        // Return a minimal DataSource wrapper that delegates to getConnection().
        return new DataSource() {
            @Override
            public Connection getConnection() throws SQLException {
                return SingleConnectionDatabaseService.this.getConnection();
            }

            @Override
            public Connection getConnection(String username, String password) throws SQLException {
                return DriverManager.getConnection(jdbcUrl, username, password);
            }

            // The rest of DataSource methods are left unimplemented for brevity and will
            // throw UnsupportedOperationException if used. This implementation is sufficient
            // for code that only calls getConnection().
            @Override
            public <T> T unwrap(Class<T> iface) { throw new UnsupportedOperationException(); }

            @Override
            public boolean isWrapperFor(Class<?> iface) { return false; }

            @Override
            public java.io.PrintWriter getLogWriter() { throw new UnsupportedOperationException(); }

            @Override
            public void setLogWriter(java.io.PrintWriter out) { throw new UnsupportedOperationException(); }

            @Override
            public void setLoginTimeout(int seconds) { throw new UnsupportedOperationException(); }

            @Override
            public int getLoginTimeout() { throw new UnsupportedOperationException(); }

            @Override
            public java.util.logging.Logger getParentLogger() { throw new UnsupportedOperationException(); }
        };
    }

    @Override
    public void initializeSchema() throws SQLException {
        logger.info("Initializing schema using SingleConnectionDatabaseService...");
        // For simplicity, delegate to the same schema used by MySQLDatabaseService
        final String CREATE_ARTICLES_TABLE = 
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

        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(CREATE_ARTICLES_TABLE);
        }
    }

    @Override
    public boolean testConnection() {
        try (Connection conn = getConnection()) {
            boolean valid = conn.isValid(5);
            logger.info("SingleConnection testConnection -> {}", valid);
            return valid;
        } catch (SQLException e) {
            logger.error("SingleConnection test failed: {}", e.getMessage());
            return false;
        }
    }

    @Override
    public void close() {
        // No pooled resources to close. If you want to force release of waiting threads,
        // you could implement additional logic here.
        logger.info("SingleConnectionDatabaseService shutdown (no pool to close)");
    }

    /**
     * Proxy Connection that releases the semaphore when close() is invoked.
     */
    private static class ConnectionReleaseProxy implements Connection {
        private final Connection delegate;
        private final Semaphore semaphore;
        private final AtomicBoolean closed = new AtomicBoolean(false);

        ConnectionReleaseProxy(Connection delegate, Semaphore semaphore) {
            this.delegate = delegate;
            this.semaphore = semaphore;
        }

        @Override
        public void close() throws SQLException {
            try {
                if (!closed.getAndSet(true)) {
                    delegate.close();
                }
            } finally {
                // Always release the permit so another operation may proceed
                semaphore.release();
            }
        }

        // Delegate all other Connection methods to the real connection
        // For brevity we implement the most used ones via delegation and
        // fallback to reflection only where necessary. Here we'll delegate
        // using an IDE-generated pattern. To keep this file concise, throw
        // UnsupportedOperationException for rarely used methods.

        @Override public <T> T unwrap(Class<T> iface) throws SQLException { return delegate.unwrap(iface); }
        @Override public boolean isWrapperFor(Class<?> iface) throws SQLException { return delegate.isWrapperFor(iface); }
        @Override public Statement createStatement() throws SQLException { return delegate.createStatement(); }
        @Override public java.sql.PreparedStatement prepareStatement(String sql) throws SQLException { return delegate.prepareStatement(sql); }
        @Override public java.sql.CallableStatement prepareCall(String sql) throws SQLException { return delegate.prepareCall(sql); }
        @Override public String nativeSQL(String sql) throws SQLException { return delegate.nativeSQL(sql); }
        @Override public void setAutoCommit(boolean autoCommit) throws SQLException { delegate.setAutoCommit(autoCommit); }
        @Override public boolean getAutoCommit() throws SQLException { return delegate.getAutoCommit(); }
        @Override public void commit() throws SQLException { delegate.commit(); }
        @Override public void rollback() throws SQLException { delegate.rollback(); }
        @Override public boolean isClosed() throws SQLException { return closed.get() || delegate.isClosed(); }
        @Override public java.sql.DatabaseMetaData getMetaData() throws SQLException { return delegate.getMetaData(); }
        @Override public void setReadOnly(boolean readOnly) throws SQLException { delegate.setReadOnly(readOnly); }
        @Override public boolean isReadOnly() throws SQLException { return delegate.isReadOnly(); }
        @Override public void setCatalog(String catalog) throws SQLException { delegate.setCatalog(catalog); }
        @Override public String getCatalog() throws SQLException { return delegate.getCatalog(); }
        @Override public void setTransactionIsolation(int level) throws SQLException { delegate.setTransactionIsolation(level); }
        @Override public int getTransactionIsolation() throws SQLException { return delegate.getTransactionIsolation(); }
        @Override public java.sql.SQLWarning getWarnings() throws SQLException { return delegate.getWarnings(); }
        @Override public void clearWarnings() throws SQLException { delegate.clearWarnings(); }
        @Override public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException { return delegate.createStatement(resultSetType, resultSetConcurrency); }
        @Override public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency) throws SQLException { return delegate.prepareStatement(sql, resultSetType, resultSetConcurrency); }
        @Override public java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException { return delegate.prepareCall(sql, resultSetType, resultSetConcurrency); }
        @Override public java.util.Map<String, Class<?>> getTypeMap() throws SQLException { return delegate.getTypeMap(); }
        @Override public void setTypeMap(java.util.Map<String, Class<?>> map) throws SQLException { delegate.setTypeMap(map); }
        @Override public void setHoldability(int holdability) throws SQLException { delegate.setHoldability(holdability); }
        @Override public int getHoldability() throws SQLException { return delegate.getHoldability(); }
        @Override public java.sql.Savepoint setSavepoint() throws SQLException { return delegate.setSavepoint(); }
        @Override public java.sql.Savepoint setSavepoint(String name) throws SQLException { return delegate.setSavepoint(name); }
        @Override public void rollback(java.sql.Savepoint savepoint) throws SQLException { delegate.rollback(savepoint); }
        @Override public void releaseSavepoint(java.sql.Savepoint savepoint) throws SQLException { delegate.releaseSavepoint(savepoint); }
        @Override public java.sql.Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException { return delegate.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability); }
        @Override public java.sql.PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException { return delegate.prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability); }
        @Override public java.sql.CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency, int resultSetHoldability) throws SQLException { return delegate.prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability); }
        @Override public java.sql.PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException { return delegate.prepareStatement(sql, autoGeneratedKeys); }
        @Override public java.sql.PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException { return delegate.prepareStatement(sql, columnIndexes); }
        @Override public java.sql.PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException { return delegate.prepareStatement(sql, columnNames); }
        @Override public java.sql.Clob createClob() throws SQLException { return delegate.createClob(); }
        @Override public java.sql.Blob createBlob() throws SQLException { return delegate.createBlob(); }
        @Override public java.sql.NClob createNClob() throws SQLException { return delegate.createNClob(); }
        @Override public java.sql.SQLXML createSQLXML() throws SQLException { return delegate.createSQLXML(); }
        @Override public boolean isValid(int timeout) throws SQLException { return delegate.isValid(timeout); }
        @Override public void setClientInfo(String name, String value) throws java.sql.SQLClientInfoException { delegate.setClientInfo(name, value); }
        @Override public void setClientInfo(java.util.Properties properties) throws java.sql.SQLClientInfoException { delegate.setClientInfo(properties); }
        @Override public String getClientInfo(String name) throws SQLException { return delegate.getClientInfo(name); }
        @Override public java.util.Properties getClientInfo() throws SQLException { return delegate.getClientInfo(); }
        @Override public java.sql.Array createArrayOf(String typeName, Object[] elements) throws SQLException { return delegate.createArrayOf(typeName, elements); }
        @Override public java.sql.Struct createStruct(String typeName, Object[] attributes) throws SQLException { return delegate.createStruct(typeName, attributes); }
        @Override public void setSchema(String schema) throws SQLException { delegate.setSchema(schema); }
        @Override public String getSchema() throws SQLException { return delegate.getSchema(); }
        @Override public void abort(java.util.concurrent.Executor executor) throws SQLException { delegate.abort(executor); }
        @Override public void setNetworkTimeout(java.util.concurrent.Executor executor, int milliseconds) throws SQLException { delegate.setNetworkTimeout(executor, milliseconds); }
        @Override public int getNetworkTimeout() throws SQLException { return delegate.getNetworkTimeout(); }
    }
}
