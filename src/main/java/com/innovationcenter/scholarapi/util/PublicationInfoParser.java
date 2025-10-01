package com.innovationcenter.scholarapi.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Utility class for parsing publication information from Google Scholar API responses.
 * Based on the Technical Report parsing strategy for publication_info.summary field.
 * 
 * Format: "Author1, Author2 - Source, Year - Publisher"
 * Example: "JL Harper - Population biology of plants., 1977 - cabdirect.org"
 */
public class PublicationInfoParser {
    private static final Logger logger = Logger.getLogger(PublicationInfoParser.class.getName());
    
    // Regex pattern to extract year from source_year field
    private static final Pattern YEAR_PATTERN = Pattern.compile("\\b(19|20)\\d{2}\\b");
    
    /**
     * Parsed publication information container.
     */
    public static class ParsedPublicationInfo {
        private String authors;
        private String source;
        private Integer year;
        private String publisher;
        private String originalSummary;
        
        public ParsedPublicationInfo(String authors, String source, Integer year, 
                                   String publisher, String originalSummary) {
            this.authors = authors;
            this.source = source;
            this.year = year;
            this.publisher = publisher;
            this.originalSummary = originalSummary;
        }
        
        // Getters
        public String getAuthors() { return authors; }
        public String getSource() { return source; }
        public Integer getYear() { return year; }
        public String getPublisher() { return publisher; }
        public String getOriginalSummary() { return originalSummary; }
        
        @Override
        public String toString() {
            return String.format("Authors: %s, Source: %s, Year: %s, Publisher: %s", 
                               authors, source, year, publisher);
        }
    }
    
    /**
     * Parses the publication_info.summary field according to Technical Report specifications.
     * 
     * @param summary The publication summary string from the API
     * @return ParsedPublicationInfo containing extracted components
     */
    public static ParsedPublicationInfo parsePublicationSummary(String summary) {
        if (summary == null || summary.trim().isEmpty()) {
            logger.warning("Empty or null publication summary provided");
            return createEmptyParseResult(summary);
        }
        
        try {
            // Split on " - " as specified in Technical Report
            String[] parts = summary.split(" - ");
            
            String authors = parts.length > 0 ? parts[0].trim() : "";
            String sourceYear = parts.length > 1 ? parts[1].trim() : "";
            String publisher = parts.length > 2 ? parts[2].trim() : "";
            
            // Extract year from source_year field
            Integer year = extractYearFromSourceYear(sourceYear);
            
            // Clean source by removing year if found
            String source = cleanSourceFromYear(sourceYear, year);
            
            // Validate and clean extracted data
            authors = validateAndCleanAuthors(authors);
            source = validateAndCleanSource(source);
            publisher = validateAndCleanPublisher(publisher);
            
            ParsedPublicationInfo result = new ParsedPublicationInfo(
                authors, source, year, publisher, summary);
            
            logger.fine("Successfully parsed publication: " + result.toString());
            return result;
            
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error parsing publication summary: " + summary, e);
            return createEmptyParseResult(summary);
        }
    }
    
    /**
     * Extracts publication year using regex pattern.
     */
    private static Integer extractYearFromSourceYear(String sourceYear) {
        if (sourceYear == null || sourceYear.trim().isEmpty()) {
            return null;
        }
        
        Matcher matcher = YEAR_PATTERN.matcher(sourceYear);
        if (matcher.find()) {
            try {
                int year = Integer.parseInt(matcher.group());
                // Validate reasonable year range
                if (year >= 1900 && year <= 2030) {
                    return year;
                }
            } catch (NumberFormatException e) {
                logger.fine("Could not parse year from: " + matcher.group());
            }
        }
        
        return null;
    }
    
    /**
     * Cleans the source field by removing the extracted year.
     */
    private static String cleanSourceFromYear(String sourceYear, Integer extractedYear) {
        if (sourceYear == null || extractedYear == null) {
            return sourceYear;
        }
        
        // Remove the year and common separators
        String cleaned = sourceYear.replaceAll("\\b" + extractedYear + "\\b", "")
                                  .replaceAll(",\\s*$", "")  // Remove trailing comma
                                  .replaceAll("^\\s*,", "")   // Remove leading comma
                                  .trim();
        
        return cleaned.isEmpty() ? sourceYear : cleaned;
    }
    
    /**
     * Validates and cleans author names.
     */
    private static String validateAndCleanAuthors(String authors) {
        if (authors == null || authors.trim().isEmpty()) {
            return "Unknown Author";
        }
        
        // Remove extra whitespace and normalize
        return authors.trim().replaceAll("\\s+", " ");
    }
    
    /**
     * Validates and cleans source/journal information.
     */
    private static String validateAndCleanSource(String source) {
        if (source == null || source.trim().isEmpty()) {
            return "Unknown Source";
        }
        
        // Remove extra whitespace and normalize
        return source.trim().replaceAll("\\s+", " ");
    }
    
    /**
     * Validates and cleans publisher information.
     */
    private static String validateAndCleanPublisher(String publisher) {
        if (publisher == null || publisher.trim().isEmpty()) {
            return "Unknown Publisher";
        }
        
        // Remove extra whitespace and normalize
        return publisher.trim().replaceAll("\\s+", " ");
    }
    
    /**
     * Creates a fallback parse result for failed parsing attempts.
     */
    private static ParsedPublicationInfo createEmptyParseResult(String originalSummary) {
        return new ParsedPublicationInfo(
            "Unknown Author",
            "Unknown Source", 
            null,
            "Unknown Publisher",
            originalSummary != null ? originalSummary : "No summary available"
        );
    }
    
    /**
     * Extracts Google Scholar ID from result_id for database mapping.
     */
    public static String extractGoogleScholarId(String resultId) {
        if (resultId == null || resultId.trim().isEmpty()) {
            return null;
        }
        
        // Clean and validate the result ID
        String cleaned = resultId.trim();
        
        // Result ID should be alphanumeric with possible underscores/hyphens
        if (cleaned.matches("^[a-zA-Z0-9_-]+$")) {
            return cleaned;
        }
        
        logger.warning("Invalid result ID format: " + resultId);
        return null;
    }
    
    /**
     * Extracts PDF URL from resources array for database mapping.
     */
    public static String extractPdfUrl(com.innovationcenter.scholarapi.model.ScholarSearchResponse.Resource[] resources) {
        if (resources == null || resources.length == 0) {
            return null;
        }
        
        // Find first PDF resource
        for (com.innovationcenter.scholarapi.model.ScholarSearchResponse.Resource resource : resources) {
            if (resource != null && "PDF".equalsIgnoreCase(resource.getFileFormat())) {
                return resource.getLink();
            }
        }
        
        return null;
    }
}