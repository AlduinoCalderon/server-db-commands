package com.innovationcenter.scholarapi.util;

import com.innovationcenter.scholarapi.model.SimpleAuthor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Utility class for parsing author names from article metadata.
 * Handles various author name formats from Google Scholar API.
 */
public class AuthorParser {
    
    // Common separators for author lists
    private static final Pattern AUTHOR_SEPARATOR = Pattern.compile("[,;]|\\sand\\s|\\s&\\s");
    
    // Pattern to detect truncated author lists (e.g., "... et al")
    private static final Pattern TRUNCATED_PATTERN = Pattern.compile(".*\\.{2,}.*|.*et\\s+al\\.?.*", Pattern.CASE_INSENSITIVE);
    
    /**
     * Parse author string from article metadata into list of SimpleAuthor objects.
     * 
     * Examples of input formats:
     * - "JF Ambros-Antemate, MDP Beristain-Colorado"
     * - "John Smith; Jane Doe; Bob Wilson"
     * - "A. Einstein and M. Curie"
     * - "J Smith, B Jones…" (truncated)
     * 
     * @param authorsString The authors string from article
     * @return List of SimpleAuthor objects
     */
    public static List<SimpleAuthor> parseAuthors(String authorsString) {
        List<SimpleAuthor> authors = new ArrayList<>();
        
        if (authorsString == null || authorsString.trim().isEmpty()) {
            return authors;
        }
        
        // Clean the input
        String cleaned = authorsString.trim();
        
        // Check if truncated (contains "..." or "et al")
        boolean isTruncated = TRUNCATED_PATTERN.matcher(cleaned).matches();
        
        // Remove truncation markers
        cleaned = cleaned.replaceAll("\\.{2,}.*$", ""); // Remove ... and everything after
        cleaned = cleaned.replaceAll("\\s*et\\s+al\\.?.*$", ""); // Remove et al and everything after
        
        // Split by common separators
        String[] authorNames = AUTHOR_SEPARATOR.split(cleaned);
        
        for (String name : authorNames) {
            String trimmedName = name.trim();
            
            // Skip empty names
            if (trimmedName.isEmpty()) {
                continue;
            }
            
            // Clean up the name
            trimmedName = cleanAuthorName(trimmedName);
            
            // Validate name (at least 2 characters, contains letters)
            if (isValidAuthorName(trimmedName)) {
                SimpleAuthor author = new SimpleAuthor(trimmedName);
                authors.add(author);
            }
        }
        
        return authors;
    }
    
    /**
     * Clean individual author name.
     */
    private static String cleanAuthorName(String name) {
        // Remove leading/trailing whitespace
        name = name.trim();
        
        // Remove extra spaces
        name = name.replaceAll("\\s+", " ");
        
        // Remove common artifacts
        name = name.replace("…", ""); // Ellipsis
        name = name.replace("...", "");
        
        // Trim again after cleaning
        return name.trim();
    }
    
    /**
     * Validate author name.
     */
    private static boolean isValidAuthorName(String name) {
        if (name == null || name.length() < 2) {
            return false;
        }
        
        // Must contain at least one letter
        if (!name.matches(".*[a-zA-Z].*")) {
            return false;
        }
        
        // Reject common non-name patterns
        if (name.matches("^[\\d\\s\\-\\.]+$")) { // Only numbers, spaces, dashes, dots
            return false;
        }
        
        return true;
    }
    
    /**
     * Extract author names as strings (for backward compatibility).
     */
    public static List<String> parseAuthorNames(String authorsString) {
        return parseAuthors(authorsString).stream()
            .map(SimpleAuthor::getFullName)
            .collect(Collectors.toList());
    }
    
    /**
     * Check if author list appears to be truncated.
     */
    public static boolean isTruncated(String authorsString) {
        if (authorsString == null) {
            return false;
        }
        return TRUNCATED_PATTERN.matcher(authorsString).matches();
    }
    
    /**
     * Count estimated number of authors from string.
     */
    public static int countAuthors(String authorsString) {
        return parseAuthors(authorsString).size();
    }
}
