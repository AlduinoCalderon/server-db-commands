package com.innovationcenter.scholarapi.service.impl;

import com.innovationcenter.scholarapi.model.Author;
import com.innovationcenter.scholarapi.model.Publication;
import com.innovationcenter.scholarapi.service.JsonParser;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of JsonParser following SOLID principles.
 * Handles parsing of Google Scholar JSON responses.
 */
public class GoogleScholarJsonParser implements JsonParser {
    
    private static final Logger logger = LoggerFactory.getLogger(GoogleScholarJsonParser.class);
    
    @Override
    public Author parseAuthor(JSONObject result) {
        if (!isValidAuthorData(result)) {
            return null;
        }
        
        try {
            Author author = new Author();
            
            // Extract basic information
            author.setName(extractTitle(result));
            author.setAffiliation(extractAffiliation(result));
            
            // Extract citation metrics
            extractCitationMetrics(result, author);
            
            // Extract publications
            List<Publication> publications = extractPublications(result);
            author.setPublications(publications);
            
            return author;
            
        } catch (Exception e) {
            logger.error("Error parsing author from JSON: {}", e.getMessage());
            return null;
        }
    }
    
    @Override
    public boolean isValidAuthorData(JSONObject jsonObject) {
        return jsonObject != null && 
               (jsonObject.has("title") || jsonObject.has("snippet"));
    }
    
    private String extractTitle(JSONObject result) {
        if (result.has("title")) {
            return result.getString("title");
        }
        return "Unknown Author";
    }
    
    private String extractAffiliation(JSONObject result) {
        if (result.has("snippet")) {
            String snippet = result.getString("snippet");
            // Clean up snippet for affiliation display
            return snippet.length() > 150 ? snippet.substring(0, 150) + "..." : snippet;
        }
        return "N/A";
    }
    
    private void extractCitationMetrics(JSONObject result, Author author) {
        // Extract citation count from snippet or link text
        String snippet = result.optString("snippet", "");
        String linkText = result.optString("link", "");
        
        // Look for citation patterns in the text
        String combinedText = snippet + " " + linkText;
        
        // Default values
        int citations = 0;
        int hIndex = 0;
        int i10Index = 0;
        
        // Try to extract citation count
        java.util.regex.Pattern citationPattern = 
            java.util.regex.Pattern.compile("(\\d+)");
        java.util.regex.Matcher matcher = citationPattern.matcher(combinedText);
        
        List<Integer> numbers = new ArrayList<>();
        while (matcher.find()) {
            try {
                numbers.add(Integer.parseInt(matcher.group(1)));
            } catch (NumberFormatException ignored) {}
        }
        
        // Use first number as citation count if available
        if (!numbers.isEmpty()) {
            citations = numbers.get(0);
        }
        
        author.setCitedBy(citations);
        author.setHIndex(hIndex);
        author.setI10Index(i10Index);
    }
    
    private List<Publication> extractPublications(JSONObject result) {
        List<Publication> publications = new ArrayList<>();
        
        // For now, create a single publication from the result
        if (result.has("title") || result.has("snippet")) {
            Publication pub = new Publication();
            pub.setTitle(extractTitle(result));
            pub.setAuthors(extractTitle(result)); // Using title as authors for now
            pub.setVenue("Google Scholar");
            String yearStr = extractYearFromText(result.optString("snippet", ""));
            pub.setYear(yearStr != null ? Integer.parseInt(yearStr) : 0);
            pub.setCitedBy(0); // Will be updated if available
            
            publications.add(pub);
        }
        
        return publications;
    }
    
    private String extractYearFromText(String text) {
        if (text == null) return null;
        
        // Look for 4-digit year between 1900 and current year + 5
        java.util.regex.Pattern yearPattern = 
            java.util.regex.Pattern.compile("\\b(19|20)\\d{2}\\b");
        java.util.regex.Matcher matcher = yearPattern.matcher(text);
        
        while (matcher.find()) {
            String year = matcher.group();
            int yearInt = Integer.parseInt(year);
            if (yearInt >= 1900 && yearInt <= java.time.Year.now().getValue() + 5) {
                return year;
            }
        }
        
        return null;
    }
}