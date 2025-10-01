package com.innovationcenter.scholarapi.service;

/**
 * Interface for configuration management following Single Responsibility Principle.
 * Defines contract for environment and configuration operations.
 */
public interface ConfigurationService {
    
    /**
     * Load configuration from environment.
     * @return true if loaded successfully, false otherwise
     */
    boolean loadConfiguration();
    
    /**
     * Get API key for external services.
     * @return API key string
     */
    String getApiKey();
    
    /**
     * Check if configuration is valid and complete.
     * @return true if valid, false otherwise
     */
    boolean isConfigurationValid();
    
    /**
     * Get configuration property by key.
     * @param key Property key
     * @return Property value or null if not found
     */
    String getProperty(String key);
}