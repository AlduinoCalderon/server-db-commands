package com.innovationcenter.scholarapi;

import com.innovationcenter.scholarapi.repository.ArticleRepository;
import com.innovationcenter.scholarapi.repository.impl.MySQLArticleRepository;
import com.innovationcenter.scholarapi.service.ApiService;
import com.innovationcenter.scholarapi.service.ArticleIntegrationService;
import com.innovationcenter.scholarapi.service.ConfigurationService;
import com.innovationcenter.scholarapi.service.DatabaseService;
import com.innovationcenter.scholarapi.service.JsonParser;
import com.innovationcenter.scholarapi.service.ScholarSearchService;
import com.innovationcenter.scholarapi.service.impl.DotenvConfigurationService;
import com.innovationcenter.scholarapi.service.impl.GoogleScholarApiService;
import com.innovationcenter.scholarapi.service.impl.GoogleScholarJsonParser;
import com.innovationcenter.scholarapi.service.impl.MySQLDatabaseService;
import com.innovationcenter.scholarapi.service.impl.SerpApiScholarSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Bean;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Spring Boot REST API Application for Scholar Article Management.
 * Provides REST endpoints for frontend consumption (GitHub Pages static site).
 * 
 * Architecture:
 * - Backend: Spring Boot REST API (deployed on Render)
 * - Frontend: Static HTML/CSS/JS (deployed on GitHub Pages)
 * - Database: MySQL (Clever Cloud)
 * 
 * This separation allows:
 * - Single backend instance (no connection pool issues)
 * - Free static hosting on GitHub Pages
 * - Better scalability and caching
 */
@SpringBootApplication
public class ScholarRestApiApplication {

    private static final Logger logger = LoggerFactory.getLogger(ScholarRestApiApplication.class);

    @Autowired
    private Environment env;

    public static void main(String[] args) {
        logger.info("========================================");
        logger.info("🚀 Starting Scholar REST API Application");
        logger.info("========================================");
        SpringApplication.run(ScholarRestApiApplication.class, args);
    }

    /**
     * Logs application startup information after Spring Boot is ready.
     */
    @EventListener(ApplicationReadyEvent.class)
    public void logApplicationStartup() {
        String port = env.getProperty("server.port", "8080");
        String appName = env.getProperty("spring.application.name", "Scholar REST API");
        
        logger.info("========================================");
        logger.info("✅ {} is now running!", appName);
        logger.info("🌐 Server running on port: {}", port);
        logger.info("🏥 Health check: http://localhost:{}/actuator/health", port);
        logger.info("📚 API Base URL: http://localhost:{}/api", port);
        logger.info("========================================");
        logger.info("📋 Available Endpoints:");
        logger.info("  Database Operations:");
        logger.info("    GET    /api/articles - Get all articles");
        logger.info("    GET    /api/articles/{id} - Get article by ID");
        logger.info("    GET    /api/articles/search - Search articles");
        logger.info("    POST   /api/articles/import - Import from Google Scholar");
        logger.info("    DELETE /api/articles/{id} - Delete article");
        logger.info("    GET    /api/articles/stats - Get statistics");
        logger.info("  Live Search (SerpAPI):");
        logger.info("    GET    /api/search/articles - Search articles");
        logger.info("    GET    /api/search/author - Search by author");
        logger.info("    GET    /api/search/citations - Find citing articles");
        logger.info("    GET    /api/search/paginated - Paginated search");
        logger.info("    GET    /api/search/health - Search service health");
        logger.info("========================================");
        
        // Log environment status
        String dbHost = env.getProperty("DB_HOST");
        String serpApiKey = env.getProperty("SERP_API_KEY");
        
        if (dbHost != null && !dbHost.isEmpty()) {
            logger.info("✅ Database configured: {}", dbHost);
        } else {
            logger.warn("⚠️  Database host not configured!");
        }
        
        if (serpApiKey != null && !serpApiKey.isEmpty()) {
            logger.info("✅ SerpAPI key configured ({}...)", serpApiKey.substring(0, Math.min(8, serpApiKey.length())));
        } else {
            logger.warn("⚠️  SerpAPI key not configured!");
        }
        
        logger.info("========================================");
    }

    /**
     * Configure CORS to allow requests from GitHub Pages and testing tools.
     * Using allowedOriginPatterns for wildcards since allowCredentials is disabled.
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                    .allowedOriginPatterns("*")  // Allow all origins for REST API
                    .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                    .allowedHeaders("*")
                    .allowCredentials(false)  // Must be false when using "*"
                    .maxAge(3600);
            }
        };
    }

    @Bean
    public ConfigurationService configurationService() {
        DotenvConfigurationService service = new DotenvConfigurationService();
        service.loadConfiguration();
        return service;
    }

    @Bean
    public DatabaseService databaseService(ConfigurationService configService) {
        return new MySQLDatabaseService(configService);
    }

    @Bean
    public ArticleRepository articleRepository(DatabaseService dbService) {
        return new MySQLArticleRepository(dbService);
    }

    @Bean
    public JsonParser jsonParser() {
        return new GoogleScholarJsonParser();
    }

    @Bean
    public ApiService apiService(ConfigurationService configService, JsonParser jsonParser) {
        return new GoogleScholarApiService(configService, jsonParser);
    }

    @Bean
    public ScholarSearchService scholarSearchService(ConfigurationService configService) {
        return new SerpApiScholarSearchService(configService);
    }

    @Bean
    public ArticleIntegrationService articleIntegrationService(
            ApiService apiService,
            ArticleRepository articleRepository) {
        return new ArticleIntegrationService(apiService, articleRepository);
    }
}
