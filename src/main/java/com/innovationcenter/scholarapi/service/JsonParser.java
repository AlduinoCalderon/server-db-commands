package com.innovationcenter.scholarapi.service;

import com.innovationcenter.scholarapi.model.Author;
import org.json.JSONObject;

/**
 * Interface for parsing JSON responses following Single Responsibility Principle.
 * Defines contract for data transformation operations.
 */
public interface JsonParser {
    
    /**
     * Parse author data from JSON object.
     * @param jsonObject The JSON object containing author data
     * @return Author object
     */
    Author parseAuthor(JSONObject jsonObject);
    
    /**
     * Validate if JSON object contains required fields for parsing.
     * @param jsonObject The JSON object to validate
     * @return true if valid, false otherwise
     */
    boolean isValidAuthorData(JSONObject jsonObject);
}