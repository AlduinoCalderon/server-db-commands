package com.innovationcenter.scholarapi.api;

import com.innovationcenter.scholarapi.model.Article;
import com.innovationcenter.scholarapi.repository.ArticleRepository;
import com.innovationcenter.scholarapi.service.ArticleIntegrationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST API Controller for Article Management.
 * Provides endpoints for CRUD operations and Google Scholar integration.
 * 
 * Base URL: /api/articles
 * 
 * Endpoints:
 * - GET /api/articles - Get all articles
 * - GET /api/articles/{id} - Get article by ID
 * - GET /api/articles/search - Search articles by criteria
 * - POST /api/articles/import - Import from Google Scholar
 * - DELETE /api/articles/{id} - Delete article
 * - GET /api/articles/stats - Get statistics
 */
@RestController
@RequestMapping("/api/articles")
@CrossOrigin(origins = "*") // Will be restricted in production
public class ArticleRestController {

    private static final Logger logger = LoggerFactory.getLogger(ArticleRestController.class);

    private final ArticleRepository articleRepository;
    private final ArticleIntegrationService integrationService;

    public ArticleRestController(ArticleRepository articleRepository,
                                 ArticleIntegrationService integrationService) {
        this.articleRepository = articleRepository;
        this.integrationService = integrationService;
    }

    /**
     * Get all articles from database.
     * GET /api/articles
     */
    @GetMapping
    public ResponseEntity<List<Article>> getAllArticles() {
        try {
            logger.info("📚 Fetching all articles from database...");
            List<Article> articles = articleRepository.findAll();
            logger.info("✅ Retrieved {} articles successfully", articles.size());
            return ResponseEntity.ok(articles);
        } catch (Exception e) {
            logger.error("❌ Error retrieving articles", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Get article by ID.
     * GET /api/articles/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Article> getArticleById(@PathVariable Long id) {
        try {
            logger.info("🔍 Fetching article with ID: {}", id);
            return articleRepository.findById(id)
                .map(article -> {
                    logger.info("✅ Found article: {}", article.getTitle());
                    return ResponseEntity.ok(article);
                })
                .orElseGet(() -> {
                    logger.warn("⚠️  Article not found with ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
        } catch (Exception e) {
            logger.error("❌ Error retrieving article {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Search articles by author name.
     * GET /api/articles/search?author=name
     */
    @GetMapping("/search")
    public ResponseEntity<List<Article>> searchArticles(
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer minCitations) {
        try {
            List<Article> articles = articleRepository.findAll();
            
            // Filter by author
            if (author != null && !author.trim().isEmpty()) {
                String lowerAuthor = author.toLowerCase();
                articles = articles.stream()
                    .filter(a -> a.getAuthors() != null && 
                                a.getAuthors().toLowerCase().contains(lowerAuthor))
                    .collect(java.util.stream.Collectors.toList());
            }
            
            // Filter by year
            if (year != null) {
                articles = articles.stream()
                    .filter(a -> a.getPublicationYear() != null && 
                                a.getPublicationYear() >= year)
                    .collect(java.util.stream.Collectors.toList());
            }
            
            // Filter by citations
            if (minCitations != null) {
                articles = articles.stream()
                    .filter(a -> a.getCitationCount() >= minCitations)
                    .collect(java.util.stream.Collectors.toList());
            }
            
            return ResponseEntity.ok(articles);
        } catch (Exception e) {
            logger.error("Error searching articles", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Import articles from Google Scholar.
     * POST /api/articles/import
     * Body: { "query": "researcher name", "maxResults": 10 }
     */
    @PostMapping("/import")
    public ResponseEntity<Map<String, Object>> importArticles(
            @RequestBody Map<String, Object> request) {
        try {
            String query = (String) request.get("query");
            Integer maxResults = request.get("maxResults") != null ? 
                ((Number) request.get("maxResults")).intValue() : 10;
            
            if (query == null || query.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Query parameter is required"));
            }
            
            logger.info("Importing articles for query: {}", query);
            List<Article> articles = integrationService.searchAndStoreArticles(query, maxResults);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", articles.size());
            response.put("articles", articles);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error importing articles", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    /**
     * Delete article by ID.
     * DELETE /api/articles/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteArticle(@PathVariable Long id) {
        try {
            boolean deleted = articleRepository.deleteById(id);
            Map<String, Object> response = new HashMap<>();
            response.put("success", deleted);
            response.put("id", id);
            
            if (deleted) {
                logger.info("Deleted article {}", id);
                return ResponseEntity.ok(response);
            } else {
                response.put("error", "Article not found");
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error deleting article {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * Delete multiple articles.
     * DELETE /api/articles/batch
     * Body: { "ids": [1, 2, 3] }
     */
    @DeleteMapping("/batch")
    public ResponseEntity<Map<String, Object>> deleteArticles(
            @RequestBody Map<String, List<Long>> request) {
        try {
            List<Long> ids = request.get("ids");
            if (ids == null || ids.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "IDs array is required"));
            }
            
            int deletedCount = 0;
            for (Long id : ids) {
                if (articleRepository.deleteById(id)) {
                    deletedCount++;
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("deletedCount", deletedCount);
            response.put("totalRequested", ids.size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error batch deleting articles", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("success", false, "error", e.getMessage()));
        }
    }

    /**
     * Get article statistics.
     * GET /api/articles/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        try {
            List<Article> articles = articleRepository.findAll();
            
            Map<String, Object> stats = new HashMap<>();
            stats.put("totalArticles", articles.size());
            stats.put("totalCitations", articles.stream()
                .mapToInt(Article::getCitationCount)
                .sum());
            stats.put("averageCitations", articles.isEmpty() ? 0 : 
                articles.stream()
                    .mapToInt(Article::getCitationCount)
                    .average()
                    .orElse(0.0));
            
            // Year distribution
            Map<Integer, Long> yearDist = articles.stream()
                .filter(a -> a.getPublicationYear() != null)
                .collect(java.util.stream.Collectors.groupingBy(
                    Article::getPublicationYear,
                    java.util.stream.Collectors.counting()));
            stats.put("yearDistribution", yearDist);
            
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            logger.error("Error calculating statistics", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Health check endpoint.
     * GET /api/articles/health
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
            "status", "UP",
            "service", "Scholar API",
            "version", "1.0.0"
        ));
    }
}
