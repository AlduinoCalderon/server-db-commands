package com.innovationcenter.scholarapi;package com.innovationcenter.scholarapi;package com.innovationcenter.scholarapi;package com.innovationcenter.scholarapi;



import com.innovationcenter.scholarapi.controller.AuthorSearchController;

import com.innovationcenter.scholarapi.model.AuthorSearchResult;

import com.innovationcenter.scholarapi.service.ApiService;import com.innovationcenter.scholarapi.controller.AuthorSearchController;

import com.innovationcenter.scholarapi.service.ConfigurationService;

import com.innovationcenter.scholarapi.service.JsonParser;import com.innovationcenter.scholarapi.model.AuthorSearchResult;

import com.innovationcenter.scholarapi.service.impl.DotenvConfigurationService;

import com.innovationcenter.scholarapi.service.impl.GoogleScholarApiService;import com.innovationcenter.scholarapi.service.ApiService;import com.innovationcenter.scholarapi.controller.AuthorSearchController;import com.innovationcenter.scholarapi.controller.AuthorSearchController;

import com.innovationcenter.scholarapi.service.impl.GoogleScholarJsonParser;

import com.innovationcenter.scholarapi.view.ConsoleViewAdapter;import com.innovationcenter.scholarapi.service.ConfigurationService;

import com.innovationcenter.scholarapi.view.UserInterface;

import org.slf4j.Logger;import com.innovationcenter.scholarapi.service.JsonParser;import com.innovationcenter.scholarapi.model.AuthorSearchResult;import com.innovationcenter.scholarapi.model.AuthorSearchResult;

import org.slf4j.LoggerFactory;

import com.innovationcenter.scholarapi.service.impl.DotenvConfigurationService;

import java.io.IOException;

import java.util.List;import com.innovationcenter.scholarapi.service.impl.GoogleScholarApiService;import com.innovationcenter.scholarapi.service.ApiService;import com.innovationcenter.scholarapi.service.ApiService;

import java.util.Scanner;

import com.innovationcenter.scholarapi.service.impl.GoogleScholarJsonParser;

/**

 * Main Scholar Search Application following SOLID principles.import com.innovationcenter.scholarapi.view.AuthorDisplayService;

 * Demonstrates proper separation of concerns and dependency inversion.import com.innovationcenter.scholarapi.service.ConfigurationService;import com.innovationcenter.scholarapi.service.ConfigurationService;

 */

public class ScholarSearchApplication {import com.innovationcenter.scholarapi.view.ConsoleViewAdapter;

    

    private static final Logger logger = LoggerFactory.getLogger(ScholarSearchApplication.class);import org.slf4j.Logger;import com.innovationcenter.scholarapi.service.JsonParser;import com.innovationcenter.scholarapi.service.JsonParser;

    

    private final AuthorSearchController controller;import org.slf4j.LoggerFactory;

    private final UserInterface userInterface;

    private final Scanner scanner;import com.innovationcenter.scholarapi.service.impl.DotenvConfigurationService;import com.innovationcenter.scholarapi.service.impl.DotenvConfigurationService;

    private int searchCount;

    import java.io.IOException;

    /**

     * Constructor with dependency injection following SOLID principles.import java.util.List;import com.innovationcenter.scholarapi.service.impl.GoogleScholarApiService;import com.innovationcenter.scholarapi.service.impl.GoogleScholarApiService;

     */

    public ScholarSearchApplication(AuthorSearchController controller, UserInterface userInterface) {import java.util.Scanner;

        this.controller = controller;

        this.userInterface = userInterface;import com.innovationcenter.scholarapi.service.impl.GoogleScholarJsonParser;import com.innovationcenter.scholarapi.service.impl.GoogleScholarJsonParser;

        this.scanner = new Scanner(System.in);

        this.searchCount = 0;/**

    }

     * Main Scholar Search Application following SOLID principles.import com.innovationcenter.scholarapi.view.AuthorDisplayService;import com.innovationcenter.scholarapi.view.AuthorDisplayService;

    /**

     * Main application entry point with dependency injection setup. * Demonstrates proper separation of concerns and dependency inversion.

     */

    public static void main(String[] args) { */import com.innovationcenter.scholarapi.view.ConsoleViewAdapter;import com.innovationcenter.scholarapi.view.ConsoleViewAdapter;

        logger.info("Starting Google Scholar Author Search System");

        public class ScholarSearchApplication {

        try {

            // Initialize dependencies following SOLID principles    import org.slf4j.Logger;import org.slf4j.Logger;

            ConfigurationService configService = new DotenvConfigurationService();

            JsonParser jsonParser = new GoogleScholarJsonParser();    private static final Logger logger = LoggerFactory.getLogger(ScholarSearchApplication.class);

            ApiService apiService = new GoogleScholarApiService(configService, jsonParser);

            AuthorSearchController controller = new AuthorSearchController(apiService);    import org.slf4j.LoggerFactory;import org.slf4j.LoggerFactory;

            UserInterface userInterface = new ConsoleViewAdapter();

                private final AuthorSearchController controller;

            // Create and run application

            ScholarSearchApplication app = new ScholarSearchApplication(controller, userInterface);    private final AuthorDisplayService displayService;

            app.run();

                private final Scanner scanner;

        } catch (Exception e) {

            logger.error("Application startup failed: {}", e.getMessage());    private int searchCount;import java.io.IOException;import java.io.IOException;

            System.err.println("Application error: " + e.getMessage());

        }    

    }

        /**import java.util.List;import java.util.List;

    /**

     * Main application loop following Single Responsibility Principle.     * Constructor with dependency injection following SOLID principles.

     */

    public void run() {     */import java.util.Scanner;import java.util.Scanner;

        try {

            logger.info("Scholar Search Application initialized");    public ScholarSearchApplication(AuthorSearchController controller, AuthorDisplayService displayService) {

            

            userInterface.showWelcomeMessage();        this.controller = controller;

            checkApiConfiguration();

                    this.displayService = displayService;

            boolean running = true;

            while (running) {        this.scanner = new Scanner(System.in);/**/**

                running = handleMainMenu();

            }        this.searchCount = 0;

            

        } catch (Exception e) {    } * Main Scholar Search Application following SOLID principles. * Refactored main application following SOLID principles.

            logger.error("Application error: {}", e.getMessage());

            userInterface.showErrorMessage("Application error: " + e.getMessage());    

        } finally {

            cleanup();    /** * Demonstrates proper separation of concerns and dependency inversion. * Uses Dependency Injection and follows Single Responsibility Principle.

        }

    }     * Main application entry point with dependency injection setup.

    

    /**     */ */ */

     * Check API configuration and display status.

     */    public static void main(String[] args) {

    private void checkApiConfiguration() {

        if (controller.isApiConfigured()) {        logger.info("Starting Google Scholar Author Search System");public class ScholarSearchApplication {public class ScholarSearchApplication {

            userInterface.showSuccessMessage("API key configured successfully!");

        } else {        

            userInterface.showWarningMessage("Using demo mode - configure API key for real data");

        }        // Initialize dependencies following SOLID principles        

    }

            ConfigurationService configService = new DotenvConfigurationService();

    /**

     * Handle main menu interactions following Single Responsibility Principle.        JsonParser jsonParser = new GoogleScholarJsonParser();    private static final Logger logger = LoggerFactory.getLogger(ScholarSearchApplication.class);    private static final Logger logger = LoggerFactory.getLogger(ScholarSearchApplication.class);

     */

    private boolean handleMainMenu() {        ApiService apiService = new GoogleScholarApiService(configService, jsonParser);

        try {

            int choice = userInterface.showMainMenu(scanner);        AuthorSearchController controller = new AuthorSearchController(apiService);        

            

            switch (choice) {        AuthorDisplayService displayService = new ConsoleViewAdapter();

                case 1:

                    performAuthorSearch();            private final AuthorSearchController controller;    private final AuthorSearchController controller;

                    return true;

                case 2:        // Create and run application

                    userInterface.showSearchHistory(searchCount);

                    return true;        ScholarSearchApplication app = new ScholarSearchApplication(controller, displayService);    private final AuthorDisplayService displayService;    private final AuthorDisplayService displayService;

                case 3:

                    userInterface.showGoodbyeMessage();        

                    return false;

                default:        try {    private final Scanner scanner;    private final Scanner scanner;

                    userInterface.showErrorMessage("Invalid option. Please try again.");

                    return true;            app.run();

            }

        } catch (Exception e) {        } catch (Exception e) {    private int searchCount;    private int searchCount;

            logger.error("Menu handling error: {}", e.getMessage());

            userInterface.showErrorMessage("Menu error: " + e.getMessage());            logger.error("Application startup failed: {}", e.getMessage());

            return true;

        }            System.err.println("Application error: " + e.getMessage());        

    }

            }

    /**

     * Perform author search following Single Responsibility Principle.    }    /**    /**

     */

    private void performAuthorSearch() {    

        try {

            String query = userInterface.promptForAuthorName(scanner);    /**     * Constructor with dependency injection following SOLID principles.     * Constructor with dependency injection.

            

            if (query == null || query.trim().isEmpty()) {     * Main application loop following Single Responsibility Principle.

                userInterface.showErrorMessage("Search query cannot be empty");

                return;     */     */     */

            }

                public void run() {

            userInterface.showSearchingMessage(query);

                    try {    public ScholarSearchApplication(AuthorSearchController controller, AuthorDisplayService displayService) {    public ScholarSearchApplication(AuthorSearchController controller, AuthorDisplayService displayService) {

            List<AuthorSearchResult> results = controller.searchAuthors(query);

            searchCount++;            logger.info("Scholar Search Application initialized");

            

            userInterface.showSearchResults(results, query);                    this.controller = controller;        this.controller = controller;

            

        } catch (IOException e) {            displayService.showWelcomeMessage();

            logger.error("Search failed: {}", e.getMessage());

            userInterface.showErrorMessage("Search failed: " + e.getMessage());            checkApiConfiguration();        this.displayService = displayService;        this.displayService = displayService;

        } catch (Exception e) {

            logger.error("Unexpected error during search: {}", e.getMessage());            

            userInterface.showErrorMessage("An unexpected error occurred: " + e.getMessage());

        }            boolean running = true;        this.scanner = new Scanner(System.in);        this.scanner = new Scanner(System.in);

    }

                while (running) {

    /**

     * Clean up resources following proper resource management principles.                running = handleMainMenu();        this.searchCount = 0;        this.searchCount = 0;

     */

    private void cleanup() {            }

        if (scanner != null) {

            scanner.close();                }    }

        }

                } catch (Exception e) {

        logger.info("Application session completed. Total searches: {}", searchCount);

        logger.info("Application shutdown complete");            logger.error("Application error: {}", e.getMessage());        

    }

}            displayService.showErrorMessage("Application error: " + e.getMessage());

        } finally {    /**    /**

            cleanup();

        }     * Main application entry point with dependency injection setup.     * Main application entry point with dependency injection setup.

    }

         */     */

    /**

     * Check API configuration and display status.    public static void main(String[] args) {    public static void main(String[] args) {

     */

    private void checkApiConfiguration() {        logger.info("Starting Google Scholar Author Search System");        logger.info("Starting Google Scholar Author Search System (SOLID Version)");

        if (controller.isApiConfigured()) {

            displayService.showSuccessMessage("API key configured successfully!");                

        } else {

            displayService.showWarningMessage("Using demo mode - configure API key for real data");        // Initialize dependencies following SOLID principles        // Dependency Injection Setup

        }

    }        ConfigurationService configService = new DotenvConfigurationService();        ConfigurationService configService = new DotenvConfigurationService();

    

    /**        JsonParser jsonParser = new GoogleScholarJsonParser();        JsonParser jsonParser = new GoogleScholarJsonParser();

     * Handle main menu interactions following Single Responsibility Principle.

     */        ApiService apiService = new GoogleScholarApiService(configService, jsonParser);        ApiService apiService = new GoogleScholarApiService(configService, jsonParser);

    private boolean handleMainMenu() {

        try {        AuthorSearchController controller = new AuthorSearchController(apiService);        ScholarApiControllerSOLID controller = new ScholarApiControllerSOLID(apiService);

            int choice = displayService.showMainMenu(scanner);

                    AuthorDisplayService displayService = new ConsoleViewAdapter();        UserInterface view = new ConsoleViewAdapter();

            switch (choice) {

                case 1:                

                    performAuthorSearch();

                    return true;        // Create and run application        // Create and run application

                case 2:

                    displayService.showSearchHistory(searchCount);        ScholarSearchApplication app = new ScholarSearchApplication(controller, displayService);        ScholarApiApplicationSOLID app = new ScholarApiApplicationSOLID(controller, view);

                    return true;

                case 3:                app.run(args);

                    displayService.showGoodbyeMessage();

                    return false;        try {    }

                default:

                    displayService.showErrorMessage("Invalid option. Please try again.");            app.run();    

                    return true;

            }        } catch (Exception e) {    /**

        } catch (Exception e) {

            logger.error("Menu handling error: {}", e.getMessage());            logger.error("Application startup failed: {}", e.getMessage());     * Run the application following Single Responsibility Principle.

            displayService.showErrorMessage("Menu error: " + e.getMessage());

            return true;            System.err.println("Application error: " + e.getMessage());     */

        }

    }        }    public void run(String[] args) {

    

    /**    }        try {

     * Perform author search following Single Responsibility Principle.

     */                logger.info("Scholar API Application (SOLID) initialized");

    private void performAuthorSearch() {

        try {    /**            

            String query = displayService.getSearchQuery(scanner);

                 * Main application loop following Single Responsibility Principle.            view.showWelcomeMessage();

            if (query == null || query.trim().isEmpty()) {

                displayService.showErrorMessage("Search query cannot be empty");     */            checkApiConfiguration();

                return;

            }    public void run() {            

            

            displayService.showSearchMessage("Searching for authors matching: " + query);        try {            boolean running = true;

            

            List<AuthorSearchResult> results = controller.searchAuthors(query);            logger.info("Scholar Search Application initialized");            while (running) {

            searchCount++;

                                        running = handleMainMenu();

            displayService.displaySearchResults(results, query);

                        displayService.showWelcomeMessage();            }

        } catch (IOException e) {

            logger.error("Search failed: {}", e.getMessage());            checkApiConfiguration();            

            displayService.showErrorMessage("Search failed: " + e.getMessage());

        } catch (Exception e) {                    } catch (Exception e) {

            logger.error("Unexpected error during search: {}", e.getMessage());

            displayService.showErrorMessage("An unexpected error occurred: " + e.getMessage());            boolean running = true;            logger.error("Application error: {}", e.getMessage());

        }

    }            while (running) {            view.showErrorMessage("Application error: " + e.getMessage());

    

    /**                running = handleMainMenu();        } finally {

     * Clean up resources following proper resource management principles.

     */            }            cleanup();

    private void cleanup() {

        if (scanner != null) {                    }

            scanner.close();

        }        } catch (Exception e) {    }

        

        logger.info("Application session completed. Total searches: {}", searchCount);            logger.error("Application error: {}", e.getMessage());    

        logger.info("Application shutdown complete");

    }            displayService.showErrorMessage("Application error: " + e.getMessage());    /**

}
        } finally {     * Check and display API configuration status.

            cleanup();     */

        }    private void checkApiConfiguration() {

    }        if (controller.isApiConfigured()) {

                view.showSuccessMessage("API key configured successfully!");

    /**        } else {

     * Check API configuration and display status.            view.showWarningMessage("Using demo mode - configure API key for real data");

     */        }

    private void checkApiConfiguration() {    }

        if (controller.isApiConfigured()) {    

            displayService.showSuccessMessage("API key configured successfully!");    /**

        } else {     * Handle main menu interactions.

            displayService.showWarningMessage("Using demo mode - configure API key for real data");     * @return true to continue, false to exit

        }     */

    }    private boolean handleMainMenu() {

            int choice = view.showMainMenu(scanner);

    /**        

     * Handle main menu interactions following Single Responsibility Principle.        switch (choice) {

     */            case 1:

    private boolean handleMainMenu() {                handleAuthorSearch();

        try {                return true;

            int choice = displayService.showMainMenu(scanner);            case 2:

                            view.showSearchHistory(totalSearches);

            switch (choice) {                return true;

                case 1:            case 3:

                    performAuthorSearch();                view.showGoodbyeMessage();

                    return true;                return false;

                case 2:            default:

                    displayService.showSearchHistory(searchCount);                view.showErrorMessage("Invalid option. Please try again.");

                    return true;                return true;

                case 3:        }

                    displayService.showGoodbyeMessage();    }

                    return false;    

                default:    /**

                    displayService.showErrorMessage("Invalid option. Please try again.");     * Handle author search functionality.

                    return true;     */

            }    private void handleAuthorSearch() {

        } catch (Exception e) {        try {

            logger.error("Menu handling error: {}", e.getMessage());            String query = view.promptForAuthorName(scanner);

            displayService.showErrorMessage("Menu error: " + e.getMessage());            

            return true;            if (query == null || query.trim().isEmpty()) {

        }                view.showErrorMessage("Search query cannot be empty");

    }                return;

                }

    /**            

     * Perform author search following Single Responsibility Principle.            view.showSearchingMessage(query);

     */            

    private void performAuthorSearch() {            List<AuthorSearchResult> results = controller.searchAuthors(query.trim());

        try {            totalSearches++;

            String query = displayService.getSearchQuery(scanner);            

                        if (results.isEmpty()) {

            if (query == null || query.trim().isEmpty()) {                view.showNoResultsMessage(query);

                displayService.showErrorMessage("Search query cannot be empty");            } else {

                return;                displaySearchResults(results, query);

            }            }

                        

            displayService.showSearchMessage("Searching for authors matching: " + query);        } catch (IllegalArgumentException e) {

                        view.showErrorMessage("Invalid search: " + e.getMessage());

            List<AuthorSearchResult> results = controller.searchAuthors(query);        } catch (IOException e) {

            searchCount++;            view.showErrorMessage("Search failed: " + e.getMessage());

                        logger.error("Search error: {}", e.getMessage());

            displayService.displaySearchResults(results, query);        } catch (Exception e) {

                        view.showErrorMessage("Unexpected error: " + e.getMessage());

        } catch (IOException e) {            logger.error("Unexpected error during search: {}", e.getMessage());

            logger.error("Search failed: {}", e.getMessage());        }

            displayService.showErrorMessage("Search failed: " + e.getMessage());    }

        } catch (Exception e) {    

            logger.error("Unexpected error during search: {}", e.getMessage());    /**

            displayService.showErrorMessage("An unexpected error occurred: " + e.getMessage());     * Display search results and handle author selection.

        }     */

    }    private void displaySearchResults(List<AuthorSearchResult> results, String query) {

            view.showSearchResults(results, query);

    /**        

     * Clean up resources following proper resource management principles.        int selection = view.promptForAuthorSelection(scanner, results.size());

     */        

    private void cleanup() {        if (selection > 0 && selection <= results.size()) {

        if (scanner != null) {            AuthorSearchResult selectedResult = results.get(selection - 1);

            scanner.close();            view.showAuthorDetails(selectedResult.getAuthor());

        }        }

            }

        logger.info("Application session completed. Total searches: {}", searchCount);    

        logger.info("Application shutdown complete");    /**

    }     * Clean up resources.

}     */
    private void cleanup() {
        logger.info("Shutting down application...");
        
        if (scanner != null) {
            scanner.close();
        }
        
        logger.info("Application session completed. Total searches: {}", totalSearches);
        logger.info("Application shutdown complete");
    }
}