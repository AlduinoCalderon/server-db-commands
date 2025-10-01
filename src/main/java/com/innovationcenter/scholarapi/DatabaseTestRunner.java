package com.innovationcenter.scholarapi;

import com.innovationcenter.scholarapi.controller.ScholarArticleController;
import com.innovationcenter.scholarapi.repository.ArticleRepository;
import com.innovationcenter.scholarapi.repository.impl.MySQLArticleRepository;
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
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Test application to demonstrate automatic database saving functionality.
 * This application will search for researchers and automatically save articles to the database.
 */
public class DatabaseTestRunner {
    private static final Logger logger = Logger.getLogger(DatabaseTestRunner.class.getName());
    
    public static void main(String[] args) {
        try {
            DatabaseTestRunner runner = new DatabaseTestRunner();
            runner.runDatabaseTest();
        } catch (Exception e) {
            System.err.println("❌ Test failed: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void runDatabaseTest() throws Exception {
        System.out.println("🚀 STARTING DATABASE INTEGRATION TEST");
        System.out.println("   This will automatically search and save articles to the database");
        System.out.println("=" .repeat(80));
        
        // Initialize application components
        ScholarArticleController controller = initializeApplication();
        
        // Test 1: System connectivity
        System.out.println("\n🔧 STEP 1: Testing system connectivity...");
        controller.testSystemConnectivity();
        
        // Test 2: Single researcher search and auto-save
        System.out.println("\n🔍 STEP 2: Single researcher search with auto-save...");
        controller.searchArticlesByResearcher("John Smith machine learning", 3);
        
        // Test 3: Multiple researchers (Sprint 3 requirement)
        System.out.println("\n🚀 STEP 3: Multiple researchers batch processing...");
        List<String> researchers = Arrays.asList(
            "Maria Garcia artificial intelligence",
            "David Johnson computer science"
        );
        controller.processMultipleResearchers(researchers, 3);
        
        // Test 4: Display saved results
        System.out.println("\n📊 STEP 4: Displaying saved articles...");
        controller.displayDatabaseStatistics();
        
        // Test 5: Query saved articles by author
        System.out.println("\n👥 STEP 5: Querying articles by author...");
        controller.displayArticlesByAuthor("John Smith");
        
        // Test 6: Display highly cited articles
        System.out.println("\n⭐ STEP 6: Highly cited articles...");
        controller.displayHighlyCitedArticles(10);
        
        System.out.println("\n🎉 DATABASE TEST COMPLETED SUCCESSFULLY!");
        System.out.println("   All searches were automatically saved to the database");
        System.out.println("=" .repeat(80));
    }
    
    private ScholarArticleController initializeApplication() throws Exception {
        logger.info("Initializing application components...");
        
        // Configuration
        ConfigurationService configService = new DotenvConfigurationService();
        
        // Database
        DatabaseService databaseService = new MySQLDatabaseService(configService);
        databaseService.initializeSchema(); // Ensure database is ready
        
        // Repository
        ArticleRepository articleRepository = new MySQLArticleRepository(databaseService);
        
        // External API service
        ScholarSearchService searchService = new SerpApiScholarSearchService(configService);
        
        // Business service
        ArticleService articleService = new ArticleService(articleRepository);
        
        // View
        ArticleView articleView = new ConsoleArticleView();
        
        // Controller
        ScholarArticleController controller = new ScholarArticleController(
            searchService, articleService, articleView);
        
        logger.info("Application initialized successfully");
        return controller;
    }
}