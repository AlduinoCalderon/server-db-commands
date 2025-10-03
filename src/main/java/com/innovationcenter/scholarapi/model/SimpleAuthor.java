package com.innovationcenter.scholarapi.model;

import java.sql.Timestamp;

/**
 * Simple Author entity for database storage.
 * Represents individual researchers extracted from article metadata.
 * Different from the Author class which represents Google Scholar profiles.
 */
public class SimpleAuthor {
    
    private Long id;
    private String fullName;
    private Timestamp firstSeen;
    private Timestamp lastUpdated;
    private Timestamp deletedAt;
    private Integer articleCount;
    private Integer totalCitations;
    
    // Constructors
    
    public SimpleAuthor() {
        this.articleCount = 0;
        this.totalCitations = 0;
    }
    
    public SimpleAuthor(String fullName) {
        this();
        this.fullName = fullName;
    }
    
    public SimpleAuthor(Long id, String fullName, Integer articleCount, Integer totalCitations) {
        this.id = id;
        this.fullName = fullName;
        this.articleCount = articleCount != null ? articleCount : 0;
        this.totalCitations = totalCitations != null ? totalCitations : 0;
    }
    
    // Getters and Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getFullName() {
        return fullName;
    }
    
    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
    
    public Timestamp getFirstSeen() {
        return firstSeen;
    }
    
    public void setFirstSeen(Timestamp firstSeen) {
        this.firstSeen = firstSeen;
    }
    
    public Timestamp getLastUpdated() {
        return lastUpdated;
    }
    
    public void setLastUpdated(Timestamp lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
    
    public Timestamp getDeletedAt() {
        return deletedAt;
    }
    
    public void setDeletedAt(Timestamp deletedAt) {
        this.deletedAt = deletedAt;
    }
    
    public boolean isDeleted() {
        return deletedAt != null;
    }
    
    public Integer getArticleCount() {
        return articleCount;
    }
    
    public void setArticleCount(Integer articleCount) {
        this.articleCount = articleCount;
    }
    
    public Integer getTotalCitations() {
        return totalCitations;
    }
    
    public void setTotalCitations(Integer totalCitations) {
        this.totalCitations = totalCitations;
    }
    
    /**
     * Increment article count and add citations.
     */
    public void addArticle(int citations) {
        this.articleCount++;
        this.totalCitations += citations;
    }
    
    /**
     * Calculate average citations per paper.
     */
    public double getAverageCitations() {
        if (articleCount == null || articleCount == 0) {
            return 0.0;
        }
        return (double) totalCitations / articleCount;
    }
    
    /**
     * Check if author data is valid.
     */
    public boolean isValid() {
        return fullName != null && !fullName.trim().isEmpty();
    }
    
    @Override
    public String toString() {
        return String.format("SimpleAuthor{id=%d, name='%s', articles=%d, citations=%d, avg=%.2f}",
            id, fullName, articleCount, totalCitations, getAverageCitations());
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleAuthor author = (SimpleAuthor) o;
        return fullName != null && fullName.equals(author.fullName);
    }
    
    @Override
    public int hashCode() {
        return fullName != null ? fullName.hashCode() : 0;
    }
}
