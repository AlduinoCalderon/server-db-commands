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
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
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

    public static void main(String[] args) {
        SpringApplication.run(ScholarRestApiApplication.class, args);
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
