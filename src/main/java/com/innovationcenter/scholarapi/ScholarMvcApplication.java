package com.innovationcenter.scholarapi;

import com.innovationcenter.scholarapi.controller.ScholarArticleController;
import com.innovationcenter.scholarapi.repository.ArticleRepository;
import com.innovationcenter.scholarapi.repository.SimpleAuthorRepository;
import com.innovationcenter.scholarapi.repository.impl.MySQLArticleRepository;
import com.innovationcenter.scholarapi.repository.impl.MySQLSimpleAuthorRepository;
import com.innovationcenter.scholarapi.service.ArticleService;
import com.innovationcenter.scholarapi.service.ConfigurationService;
import com.innovationcenter.scholarapi.service.DatabaseService;
import com.innovationcenter.scholarapi.service.ScholarSearchService;
import com.innovationcenter.scholarapi.service.impl.DotenvConfigurationService;
import com.innovationcenter.scholarapi.service.impl.MySQLDatabaseService;
import com.innovationcenter.scholarapi.service.impl.SerpApiScholarSearchService;
import com.innovationcenter.scholarapi.view.ArticleView;
import com.innovationcenter.scholarapi.view.impl.ConsoleArticleView;

import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Main application class implementing MVC architecture.
 * Demonstrates complete integration of Scholar API with database storage.
 * 
 * Architecture Overview:
 * - Model: Article entity with Technical Report field mapping
 * - View: Console-based display with ArticleView interface
 * - Controller: ScholarArticleController handling user interactions
 * - Service Layer: Business logic with clean interfaces
 * - Repository Layer: Data access abstraction
 */
public class ScholarMvcApplication {
    private static final Logger logger = Logger.getLogger(ScholarMvcApplication.class.getName());
    
    private ScholarArticleController controller;
    private Scanner scanner;
    
    public static void main(String[] args) {
        try {
            ScholarMvcApplication app = new ScholarMvcApplication();
            app.initialize();
            app.runInteractiveMode();
        } catch (Exception e) {
            System.err.println("Application failed to start: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Initialize application with dependency injection following clean architecture.
     */
    public void initialize() throws Exception {
        logger.info("Initializing Scholar MVC Application...");
        
        // Configuration layer
        ConfigurationService configService = new DotenvConfigurationService();
        
        // Database layer  
        DatabaseService databaseService = new MySQLDatabaseService(configService);
        
        // Repository layer
        ArticleRepository articleRepository = new MySQLArticleRepository(databaseService);
        SimpleAuthorRepository authorRepository = new MySQLSimpleAuthorRepository(databaseService);
        
        // External service layer
        ScholarSearchService searchService = new SerpApiScholarSearchService(configService);
        
        // Business service layer
        ArticleService articleService = new ArticleService(articleRepository, authorRepository);
        
        // View layer
        ArticleView articleView = new ConsoleArticleView();
        
        // Controller layer (orchestrates all layers)
        controller = new ScholarArticleController(searchService, articleService, articleView);
        
        // Initialize scanner for user input
        scanner = new Scanner(System.in);
        
        logger.info("Application initialized successfully");
    }
    
    /**
     * Run interactive console mode for testing all functionality.
     */
    public void runInteractiveMode() {
        System.out.println("🎓 Scholar Article Management System");
        System.out.println("   Enhanced MVC Architecture with Technical Report Integration");
        System.out.println();
        
        // Test system connectivity first
        controller.testSystemConnectivity();
        
        boolean running = true;
        while (running) {
            displayMenu();
            
            try {
                int choice = Integer.parseInt(scanner.nextLine().trim());
                running = handleMenuChoice(choice);
            } catch (NumberFormatException e) {
                System.err.println("❌ Please enter a valid number");
            } catch (Exception e) {
                System.err.println("❌ Error: " + e.getMessage());
                logger.log(Level.WARNING, "Menu handling error", e);
            }
        }
        
        scanner.close();
        System.out.println("👋 Thank you for using Scholar Article Management System!");
    }
    
    private void displayMenu() {
        System.out.println("\n📋 MAIN MENU");
        System.out.println("=".repeat(50));
        System.out.println("1. 🔍 Search articles by researcher name");
        System.out.println("2. 🔎 Search articles by query");
        System.out.println("3. � Search articles by title");
        System.out.println("4. �🚀 Process multiple researchers");
        System.out.println("5. 👥 Display articles by author");
        System.out.println("6. 📅 Display articles by year");
        System.out.println("7. ⭐ Display highly cited articles");
        System.out.println("8. 📊 Show database statistics");
        System.out.println("9. 🔧 Test system connectivity");
        System.out.println("0. 🚪 Exit");
        System.out.println("=".repeat(50));
        System.out.print("Enter your choice: ");
    }
    
    private boolean handleMenuChoice(int choice) {
        switch (choice) {
            case 1:
                handleSearchByResearcher();
                break;
            case 2:
                handleSearchByQuery();
                break;
            case 3:
                handleSearchByTitle();
                break;
            case 4:
                handleMultipleResearchers();
                break;
            case 5:
                handleDisplayByAuthor();
                break;
            case 6:
                handleDisplayByYear();
                break;
            case 7:
                handleDisplayHighlyCited();
                break;
            case 8:
                controller.displayDatabaseStatistics();
                break;
            case 9:
                controller.testSystemConnectivity();
                break;
            case 0:
                return false;
            default:
                System.err.println("❌ Invalid choice. Please try again.");
        }
        return true;
    }
    
    private void handleSearchByResearcher() {
        System.out.print("Enter researcher name: ");
        String researcherName = scanner.nextLine().trim();
        
        System.out.print("Max articles to retrieve (1-20): ");
        try {
            int maxArticles = Integer.parseInt(scanner.nextLine().trim());
            controller.searchArticlesByResearcher(researcherName, maxArticles);
        } catch (NumberFormatException e) {
            System.err.println("❌ Invalid number format");
        }
    }
    
    private void handleSearchByQuery() {
        System.out.print("Enter search query: ");
        String query = scanner.nextLine().trim();
        
        System.out.print("Max results (1-20): ");
        try {
            int maxResults = Integer.parseInt(scanner.nextLine().trim());
            controller.searchArticlesByQuery(query, maxResults);
        } catch (NumberFormatException e) {
            System.err.println("❌ Invalid number format");
        }
    }
    
    private void handleMultipleResearchers() {
        System.out.print("Enter researcher names (comma-separated): ");
        String input = scanner.nextLine().trim();
        List<String> researchers = Arrays.asList(input.split(","));
        
        // Clean researcher names
        researchers = researchers.stream()
            .map(String::trim)
            .filter(name -> !name.isEmpty())
            .collect(java.util.stream.Collectors.toList());
        
        System.out.print("Articles per researcher (1-10): ");
        try {
            int articlesPerResearcher = Integer.parseInt(scanner.nextLine().trim());
            controller.processMultipleResearchers(researchers, articlesPerResearcher);
        } catch (NumberFormatException e) {
            System.err.println("❌ Invalid number format");
        }
    }
    
    private void handleDisplayByAuthor() {
        System.out.print("Enter author name: ");
        String authorName = scanner.nextLine().trim();
        controller.displayArticlesByAuthor(authorName);
    }
    
    private void handleDisplayByYear() {
        System.out.print("Enter publication year: ");
        try {
            int year = Integer.parseInt(scanner.nextLine().trim());
            controller.displayArticlesByYear(year);
        } catch (NumberFormatException e) {
            System.err.println("❌ Invalid year format");
        }
    }
    
    private void handleDisplayHighlyCited() {
        System.out.print("Minimum citations: ");
        try {
            int minCitations = Integer.parseInt(scanner.nextLine().trim());
            controller.displayHighlyCitedArticles(minCitations);
        } catch (NumberFormatException e) {
            System.err.println("❌ Invalid number format");
        }
    }
    
    /**
     * Handles searching articles by title keyword.
     */
    private void handleSearchByTitle() {
        System.out.print("Enter title keyword to search: ");
        String keyword = scanner.nextLine();
        
        if (keyword.trim().isEmpty()) {
            System.err.println("❌ Title keyword cannot be empty");
            return;
        }
        
        controller.searchArticlesByTitle(keyword);
    }
}