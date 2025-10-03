package com.innovationcenter.scholarapi.model;

/**
 * Wrapper class for individual search result items.
 * Represents a single author result with metadata.
 */
public class AuthorSearchResult {
    private Author author;
    private int rank;
    private String source;
    private double relevanceScore;
    
    public AuthorSearchResult() {}
    
    public AuthorSearchResult(Author author, int rank, String source) {
        this.author = author;
        this.rank = rank;
        this.source = source;
    }
    
    // Getters and Setters
    public Author getAuthor() {
        return author;
    }
    
    public void setAuthor(Author author) {
        this.author = author;
    }
    
    public int getRank() {
        return rank;
    }
    
    public void setRank(int rank) {
        this.rank = rank;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public double getRelevanceScore() {
        return relevanceScore;
    }
    
    public void setRelevanceScore(double relevanceScore) {
        this.relevanceScore = relevanceScore;
    }
}