package com.innovationcenter.scholarapi.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Author model class.
 */
class AuthorTest {
    
    private Author author;

    @BeforeEach
    void setUp() {
        author = new Author();
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(author);
        assertNotNull(author.getPublications());
        assertTrue(author.getPublications().isEmpty());
    }

    @Test
    void testConstructorWithParameters() {
        Author authorWithParams = new Author("123", "Dr. Smith", "University ABC");
        
        assertEquals("123", authorWithParams.getAuthorId());
        assertEquals("Dr. Smith", authorWithParams.getName());
        assertEquals("University ABC", authorWithParams.getAffiliation());
        assertNotNull(authorWithParams.getPublications());
    }

    @Test
    void testSettersAndGetters() {
        author.setAuthorId("456");
        author.setName("Dr. Jane Doe");
        author.setAffiliation("Tech Institute");
        author.setEmail("jane.doe@tech.edu");
        author.setInterests("AI, Machine Learning");
        author.setCitedBy(100);
        author.setHIndex(10);
        author.setI10Index(5);
        author.setThumbnail("http://example.com/photo.jpg");

        assertEquals("456", author.getAuthorId());
        assertEquals("Dr. Jane Doe", author.getName());
        assertEquals("Tech Institute", author.getAffiliation());
        assertEquals("jane.doe@tech.edu", author.getEmail());
        assertEquals("AI, Machine Learning", author.getInterests());
        assertEquals(100, author.getCitedBy());
        assertEquals(10, author.getHIndex());
        assertEquals(5, author.getI10Index());
        assertEquals("http://example.com/photo.jpg", author.getThumbnail());
    }

    @Test
    void testAddPublication() {
        Publication publication = new Publication("Test Paper", "Dr. Jane Doe", 2023);
        author.addPublication(publication);

        assertEquals(1, author.getPublications().size());
        assertEquals(publication, author.getPublications().get(0));
    }

    @Test
    void testAddNullPublication() {
        author.addPublication(null);
        assertTrue(author.getPublications().isEmpty());
    }

    @Test
    void testSetPublications() {
        Publication pub1 = new Publication("Paper 1", "Author 1", 2022);
        Publication pub2 = new Publication("Paper 2", "Author 2", 2023);
        
        java.util.List<Publication> publications = new java.util.ArrayList<>();
        publications.add(pub1);
        publications.add(pub2);
        
        author.setPublications(publications);
        
        assertEquals(2, author.getPublications().size());
        assertTrue(author.getPublications().contains(pub1));
        assertTrue(author.getPublications().contains(pub2));
    }

    @Test
    void testSetNullPublications() {
        author.setPublications(null);
        assertNotNull(author.getPublications());
        assertTrue(author.getPublications().isEmpty());
    }

    @Test
    void testHasValidData() {
        // Initially invalid (no name or ID)
        assertFalse(author.hasValidData());
        
        // Still invalid (only name)
        author.setName("Dr. Smith");
        assertFalse(author.hasValidData());
        
        // Still invalid (only ID)
        author.setName(null);
        author.setAuthorId("123");
        assertFalse(author.hasValidData());
        
        // Now valid (both name and ID)
        author.setName("Dr. Smith");
        assertTrue(author.hasValidData());
        
        // Invalid with empty strings
        author.setName("");
        assertFalse(author.hasValidData());
        
        author.setName("Dr. Smith");
        author.setAuthorId("");
        assertFalse(author.hasValidData());
    }

    @Test
    void testToString() {
        author.setAuthorId("123");
        author.setName("Dr. Test");
        author.setAffiliation("Test University");
        author.setCitedBy(50);
        author.setHIndex(5);
        author.setI10Index(3);
        
        String result = author.toString();
        
        assertTrue(result.contains("123"));
        assertTrue(result.contains("Dr. Test"));
        assertTrue(result.contains("Test University"));
        assertTrue(result.contains("50"));
        assertTrue(result.contains("5"));
        assertTrue(result.contains("3"));
    }

    @Test
    void testEquals() {
        Author author1 = new Author("123", "Dr. Smith", "University A");
        Author author2 = new Author("123", "Dr. Jones", "University B");
        Author author3 = new Author("456", "Dr. Smith", "University A");
        
        // Same author ID should be equal
        assertEquals(author1, author2);
        
        // Different author ID should not be equal
        assertNotEquals(author1, author3);
        
        // Null comparison
        assertNotEquals(author1, null);
        
        // Same object
        assertEquals(author1, author1);
    }

    @Test
    void testHashCode() {
        author.setAuthorId("123");
        Author anotherAuthor = new Author();
        anotherAuthor.setAuthorId("123");
        
        assertEquals(author.hashCode(), anotherAuthor.hashCode());
    }

    @Test
    void testHashCodeWithNullId() {
        Author author1 = new Author();
        Author author2 = new Author();
        
        assertEquals(author1.hashCode(), author2.hashCode());
    }
}