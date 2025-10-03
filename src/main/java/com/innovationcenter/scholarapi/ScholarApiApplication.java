package com.innovationcenter.scholarapi;

import com.innovationcenter.scholarapi.controller.ScholarApiController;
import com.innovationcenter.scholarapi.model.Author;
import com.innovationcenter.scholarapi.model.SearchResult;
import com.innovationcenter.scholarapi.view.ConsoleView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Main application class that integrates the MVC components.
 * Provides the entry point for the Google Scholar Author Search System.
 */
public class ScholarApiApplication {
    private static final Logger logger = LoggerFactory.getLogger(ScholarApiApplication.class);
    
    private final ConsoleView view;
    private final ScholarApiController controller;
    private final List<SearchResult> searchHistory;
    private boolean running;

    /**
     * Constructor initializes MVC components.
     */
    public ScholarApiApplication() {
        this.view = new ConsoleView();
        this.controller = new ScholarApiController();
        this.searchHistory = new ArrayList<>();
        this.running = true;
        
        logger.info("Scholar API Application initialized");
    }

    /**
     * Constructor with custom API key.
     * @param apiKey The SerpAPI key for Google Scholar access
     */
    public ScholarApiApplication(String apiKey) {
        this.view = new ConsoleView();
        this.controller = new ScholarApiController(apiKey);
        this.searchHistory = new ArrayList<>();
        this.running = true;
        
        logger.info("Scholar API Application initialized with custom API key");
    }

    /**
     * Main application entry point.
     * @param args Command line arguments (optional: API key)
     */
    public static void main(String[] args) {
        logger.info("Starting Google Scholar Author Search System");
        
        try {
            ScholarApiApplication app;
            
            // Check if API key provided via command line
            if (args.length > 0 && args[0] != null && !args[0].trim().isEmpty()) {
                app = new ScholarApiApplication(args[0]);
                System.out.println("Using API key from command line arguments.");
            } else {
                app = new ScholarApiApplication();
                System.out.println("Using default API key configuration.");
                System.out.println("To use your own API key, run: java -jar app.jar YOUR_API_KEY");
            }
            
            // Start the application
            app.run();
            
        } catch (Exception e) {
            logger.error("Fatal error in main application: {}", e.getMessage(), e);
            System.err.println("Application failed to start: " + e.getMessage());
            System.exit(1);
        }
        
        logger.info("Application shutdown complete");
    }

    /**
     * Main application loop.
     */
    public void run() {
        try {
            // Display welcome message
            view.displayWelcome();
            
            // Check API key configuration
            if (!controller.isApiKeyConfigured()) {
                view.displayError("API key not configured properly!");
                view.displayError("Please set your SerpAPI key to use this application.");
                view.displayError("Get your free API key at: https://serpapi.com/");
                System.out.println("\nDemo mode: Using sample data for demonstration purposes.");
            } else {
                view.displaySuccess("API key configured successfully!");
            }

            // Main application loop
            while (running) {
                try {
                    view.displayMenu();
                    int choice = view.getMenuChoice();
                    
                    handleMenuChoice(choice);
                    
                } catch (Exception e) {
                    logger.error("Error in main loop: {}", e.getMessage(), e);
                    view.displayError("An unexpected error occurred: " + e.getMessage());
                    view.waitForUser();
                }
            }

        } finally {
            shutdown();
        }
    }

    /**
     * Handle user menu selection.
     * @param choice The menu choice selected by user
     */
    private void handleMenuChoice(int choice) {
        switch (choice) {
            case 1:
                handleAuthorSearch();
                break;
            case 2:
                handleSearchHistory();
                break;
            case 3:
                handleExit();
                break;
            default:
                view.displayError("Invalid option. Please select 1-3.");
                break;
        }
    }

    /**
     * Handle author search functionality.
     */
    private void handleAuthorSearch() {
        try {
            String query = view.getSearchQuery();
            
            if (query == null || query.trim().isEmpty()) {
                view.displayError("Please enter a valid search query.");
                return;
            }

            // Display loading message
            view.displayLoading("Searching for authors matching: " + query);

            SearchResult result;
            
            if (!controller.isApiKeyConfigured()) {
                // Demo mode - return sample data
                result = createSampleSearchResult(query);
                view.displaySuccess("Demo data loaded (API key required for real data)");
            } else {
                // Real API call
                result = controller.searchAuthors(query);
            }

            // Add to search history
            if (result != null) {
                searchHistory.add(result);
                
                // Display results
                view.displaySearchResults(result);
                
                // Ask if user wants to see details of any author
                if (result.hasResults()) {
                    handleAuthorSelection(result);
                }
            } else {
                view.displayError("Failed to retrieve search results. Please try again.");
            }

        } catch (Exception e) {
            logger.error("Error during author search: {}", e.getMessage(), e);
            view.displayError("Search failed: " + e.getMessage());
        }
    }

    /**
     * Handle author selection for detailed view.
     * @param searchResult The search results to select from
     */
    private void handleAuthorSelection(SearchResult searchResult) {
        System.out.print("\nEnter author number for details (or 0 to continue): ");
        
        try {
            int selection = view.getMenuChoice();
            
            if (selection > 0 && selection <= searchResult.getAuthors().size()) {
                Author selectedAuthor = searchResult.getAuthors().get(selection - 1);
                
                if (controller.isApiKeyConfigured()) {
                    view.displayLoading("Loading detailed information for " + selectedAuthor.getName());
                    Author detailedAuthor = controller.getAuthorDetails(selectedAuthor.getAuthorId());
                    
                    if (detailedAuthor != null) {
                        view.displayAuthorDetails(detailedAuthor);
                    } else {
                        view.displayAuthorDetails(selectedAuthor);
                        view.displayError("Could not load additional details from API");
                    }
                } else {
                    // Demo mode - show basic details
                    view.displayAuthorDetails(selectedAuthor);
                }
                
                view.waitForUser();
            } else if (selection != 0) {
                view.displayError("Invalid selection. Please enter a number between 1 and " + 
                    searchResult.getAuthors().size());
            }
            
        } catch (Exception e) {
            logger.error("Error handling author selection: {}", e.getMessage());
            view.displayError("Invalid selection format.");
        }
    }

    /**
     * Handle search history display.
     */
    private void handleSearchHistory() {
        if (searchHistory.isEmpty()) {
            System.out.println("\nNo search history available.");
            return;
        }

        System.out.println("\n" + "=".repeat(40));
        System.out.println("SEARCH HISTORY");
        System.out.println("=".repeat(40));

        for (int i = 0; i < searchHistory.size(); i++) {
            SearchResult result = searchHistory.get(i);
            System.out.printf("%d. Query: '%s' - Found %d authors\n", 
                i + 1, result.getSearchQuery(), result.getResultCount());
        }

        view.waitForUser();
    }

    /**
     * Handle application exit.
     */
    private void handleExit() {
        view.displayGoodbye();
        running = false;
    }

    /**
     * Create sample search result for demo purposes.
     * @param query The search query
     * @return Sample SearchResult with demo data
     */
    private SearchResult createSampleSearchResult(String query) {
        SearchResult result = new SearchResult(query);
        
        // Create sample authors
        Author author1 = new Author("demo1", "Dr. Maria Rodriguez", "University of Northern Mexico");
        author1.setEmail("maria.rodriguez@university.mx");
        author1.setInterests("Machine Learning, Data Science, AI");
        author1.setCitedBy(1543);
        author1.setHIndex(18);
        author1.setI10Index(25);

        Author author2 = new Author("demo2", "Prof. Carlos Mendez", "Technology Institute");
        author2.setEmail("carlos.mendez@tech.edu");
        author2.setInterests("Software Engineering, Database Systems");
        author2.setCitedBy(892);
        author2.setHIndex(12);
        author2.setI10Index(15);

        Author author3 = new Author("demo3", "Dr. Ana Gutierrez", "Research Center");
        author3.setEmail("ana.gutierrez@research.org");
        author3.setInterests("Computer Networks, Cybersecurity");
        author3.setCitedBy(2156);
        author3.setHIndex(22);
        author3.setI10Index(34);

        result.addAuthor(author1);
        result.addAuthor(author2);
        result.addAuthor(author3);
        result.setTotalResults(3);

        return result;
    }

    /**
     * Shutdown the application and clean up resources.
     */
    private void shutdown() {
        try {
            logger.info("Shutting down application...");
            
            if (view != null) {
                view.close();
            }
            
            // Log search statistics
            logger.info("Application session completed. Total searches: {}", searchHistory.size());
            
        } catch (Exception e) {
            logger.error("Error during shutdown: {}", e.getMessage());
        }
    }

    /**
     * Get the current search history (for testing purposes).
     * @return List of search results
     */
    public List<SearchResult> getSearchHistory() {
        return new ArrayList<>(searchHistory);
    }

    /**
     * Check if the application is currently running.
     * @return true if application is running
     */
    public boolean isRunning() {
        return running;
    }

    /**
     * Stop the application (for testing purposes).
     */
    public void stop() {
        running = false;
    }
}