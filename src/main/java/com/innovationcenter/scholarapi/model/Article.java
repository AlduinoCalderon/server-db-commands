package com.innovationcenter.scholarapi.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Entity representing an academic article for database storage.
 * Maps to database schema as specified in Technical Report.
 */
public class Article {
    private Long id;
    private String paperTitle;           
    private String authors;              
    private Integer publicationYear;     
    private String journal;              
    private String articleUrl;           
    private String abstractText;         
    private String googleScholarId;      
    private int citationCount;           
    private String citesId;              
    private String pdfUrl;               
    private String publisher;            
    private LocalDate createdAt;         
    private LocalDate updatedAt;
    
    // Default constructor
    public Article() {}
    
    // Constructor for new articles (without ID) - matches Technical Report mapping
    public Article(String paperTitle, String authors, Integer publicationYear, String journal,
                   String articleUrl, String abstractText, String googleScholarId, 
                   int citationCount, String citesId, String pdfUrl, String publisher) {
        this.paperTitle = paperTitle;
        this.authors = authors;
        this.publicationYear = publicationYear;
        this.journal = journal;
        this.articleUrl = articleUrl;
        this.abstractText = abstractText;
        this.googleScholarId = googleScholarId;
        this.citationCount = citationCount;
        this.citesId = citesId;
        this.pdfUrl = pdfUrl;
        this.publisher = publisher;
        this.createdAt = LocalDate.now();
        this.updatedAt = LocalDate.now();
    }
    
    // Constructor for existing articles (with ID)
    public Article(Long id, String paperTitle, String authors, Integer publicationYear, String journal,
                   String articleUrl, String abstractText, String googleScholarId, 
                   int citationCount, String citesId, String pdfUrl, String publisher,
                   LocalDate createdAt, LocalDate updatedAt) {
        this.id = id;
        this.paperTitle = paperTitle;
        this.authors = authors;
        this.publicationYear = publicationYear;
        this.journal = journal;
        this.articleUrl = articleUrl;
        this.abstractText = abstractText;
        this.googleScholarId = googleScholarId;
        this.citationCount = citationCount;
        this.citesId = citesId;
        this.pdfUrl = pdfUrl;
        this.publisher = publisher;
        this.createdAt = createdAt != null ? createdAt : LocalDate.now();
        this.updatedAt = updatedAt != null ? updatedAt : LocalDate.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getPaperTitle() { return paperTitle; }
    public void setPaperTitle(String paperTitle) { this.paperTitle = paperTitle; }
    
    public String getAuthors() { return authors; }
    public void setAuthors(String authors) { this.authors = authors; }
    
    public Integer getPublicationYear() { return publicationYear; }
    public void setPublicationYear(Integer publicationYear) { this.publicationYear = publicationYear; }
    
    public String getJournal() { return journal; }
    public void setJournal(String journal) { this.journal = journal; }
    
    public String getArticleUrl() { return articleUrl; }
    public void setArticleUrl(String articleUrl) { this.articleUrl = articleUrl; }
    
    public String getAbstractText() { return abstractText; }
    public void setAbstractText(String abstractText) { this.abstractText = abstractText; }
    
    public String getGoogleScholarId() { return googleScholarId; }
    public void setGoogleScholarId(String googleScholarId) { this.googleScholarId = googleScholarId; }
    
    public int getCitationCount() { return citationCount; }
    public void setCitationCount(int citationCount) { this.citationCount = citationCount; }
    
    public String getCitesId() { return citesId; }
    public void setCitesId(String citesId) { this.citesId = citesId; }
    
    public String getPdfUrl() { return pdfUrl; }
    public void setPdfUrl(String pdfUrl) { this.pdfUrl = pdfUrl; }
    
    public String getPublisher() { return publisher; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    
    public LocalDate getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDate createdAt) { this.createdAt = createdAt; }
    
    public LocalDate getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDate updatedAt) { this.updatedAt = updatedAt; }
    
    // Legacy compatibility methods
    public String getTitle() { return paperTitle; }
    public void setTitle(String title) { this.paperTitle = title; }
    
    public LocalDate getPublicationDate() { 
        return publicationYear != null ? LocalDate.of(publicationYear, 1, 1) : LocalDate.now(); 
    }
    
    public void setPublicationDate(LocalDate publicationDate) { 
        this.publicationYear = publicationDate != null ? publicationDate.getYear() : null; 
    }
    
    public String getLink() { return articleUrl; }
    public void setLink(String link) { this.articleUrl = link; }
    
    public String getKeywords() { return journal; }
    public void setKeywords(String keywords) { this.journal = keywords; }
    
    public int getCitedBy() { return citationCount; }
    public void setCitedBy(int citedBy) { this.citationCount = citedBy; }
    
    // Utility methods
    public boolean isValidForDatabase() {
        return paperTitle != null && !paperTitle.trim().isEmpty() &&
               authors != null && !authors.trim().isEmpty();
    }
    
    public void updateTimestamp() {
        this.updatedAt = LocalDate.now();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Article article = (Article) obj;
        
        // Use Google Scholar ID as primary identifier if available
        if (googleScholarId != null && article.googleScholarId != null) {
            return Objects.equals(googleScholarId, article.googleScholarId);
        }
        
        // Fallback to title and authors comparison
        return Objects.equals(paperTitle, article.paperTitle) && 
               Objects.equals(authors, article.authors);
    }
    
    @Override
    public int hashCode() {
        if (googleScholarId != null) {
            return Objects.hash(googleScholarId);
        }
        return Objects.hash(paperTitle, authors);
    }
    
    @Override
    public String toString() {
        return String.format("Article{id=%d, paperTitle='%s', authors='%s', year=%d, journal='%s', citations=%d}",
                           id, paperTitle, authors, publicationYear, journal, citationCount);
    }
}