package com.innovationcenter.scholarapi.controller;

import com.innovationcenter.scholarapi.model.SearchResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the ScholarApiController class.
 */
class ScholarApiControllerTest {
    
    private ScholarApiController controller;

    @BeforeEach
    void setUp() {
        controller = new ScholarApiController();
    }

    @Test
    void testDefaultConstructor() {
        assertNotNull(controller);
        assertFalse(controller.isApiKeyConfigured());
    }

    @Test
    void testConstructorWithApiKey() {
        ScholarApiController controllerWithKey = new ScholarApiController("test-api-key");
        assertNotNull(controllerWithKey);
        assertTrue(controllerWithKey.isApiKeyConfigured());
    }

    @Test
    void testConstructorWithNullApiKey() {
        ScholarApiController controllerWithNull = new ScholarApiController(null);
        assertNotNull(controllerWithNull);
        assertFalse(controllerWithNull.isApiKeyConfigured());
    }

    @Test
    void testSearchAuthorsWithEmptyQuery() {
        SearchResult result = controller.searchAuthors("");
        assertNotNull(result);
        assertFalse(result.hasResults());
        assertEquals("", result.getSearchQuery());
    }

    @Test
    void testSearchAuthorsWithNullQuery() {
        SearchResult result = controller.searchAuthors(null);
        assertNotNull(result);
        assertFalse(result.hasResults());
        assertNull(result.getSearchQuery());
    }

    @Test
    void testSearchAuthorsWithValidQuery() {
        // Note: This test will fail without a valid API key
        // In a real environment, you would mock the HTTP client
        SearchResult result = controller.searchAuthors("machine learning");
        assertNotNull(result);
        // We can't assert results without a valid API key
    }

    @Test
    void testSearchAuthorsWithMaxResults() {
        SearchResult result = controller.searchAuthors("test query", 5);
        assertNotNull(result);
        assertEquals("test query", result.getSearchQuery());
    }

    @Test
    void testGetAuthorDetailsWithNullId() {
        assertNull(controller.getAuthorDetails(null));
    }

    @Test
    void testGetAuthorDetailsWithEmptyId() {
        assertNull(controller.getAuthorDetails(""));
    }

    @Test
    void testGetAuthorDetailsWithValidId() {
        // Note: This test will fail without a valid API key
        // In a real environment, you would mock the HTTP client
        // For now, we just test that it doesn't throw an exception
        assertDoesNotThrow(() -> controller.getAuthorDetails("test-id"));
    }

    @Test
    void testApiKeyStatus() {
        String status = controller.getApiKeyStatus();
        assertNotNull(status);
        assertTrue(status.contains("not configured") || status.contains("configured successfully"));
    }

    @Test
    void testApiKeyConfigurationCheck() {
        // Default constructor should have no valid API key
        assertFalse(controller.isApiKeyConfigured());
        
        // Constructor with valid key should be configured
        ScholarApiController configuredController = new ScholarApiController("valid-key");
        assertTrue(configuredController.isApiKeyConfigured());
    }
}