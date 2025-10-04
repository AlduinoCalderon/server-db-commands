package com.innovationcenter.scholarapi.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.time.Duration;
import java.time.Instant;

/**
 * Configuration for logging all incoming HTTP requests.
 * Provides clear visibility into API usage and performance.
 */
@Configuration
public class RequestLoggingConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(RequestLoggingConfig.class);

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RequestLoggingInterceptor());
    }

    /**
     * Interceptor that logs all incoming requests with timing information.
     */
    private static class RequestLoggingInterceptor implements HandlerInterceptor {

        @Override
        public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
            // Store start time for duration calculation
            request.setAttribute("startTime", Instant.now());
            
            // Log incoming request
            String method = request.getMethod();
            String uri = request.getRequestURI();
            String queryString = request.getQueryString();
            String remoteAddr = request.getRemoteAddr();
            
            String fullUrl = queryString != null ? uri + "?" + queryString : uri;
            
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            logger.info("🌐 INCOMING REQUEST");
            logger.info("   Method:  {}", method);
            logger.info("   URL:     {}", fullUrl);
            logger.info("   Client:  {}", remoteAddr);
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            
            return true;
        }

        @Override
        public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
                                   Object handler, Exception ex) {
            // Calculate request duration
            Instant startTime = (Instant) request.getAttribute("startTime");
            Duration duration = Duration.between(startTime, Instant.now());
            
            String method = request.getMethod();
            String uri = request.getRequestURI();
            int status = response.getStatus();
            
            // Choose emoji and log level based on status code
            String emoji;
            String statusText;
            
            if (status >= 200 && status < 300) {
                emoji = "✅";
                statusText = "SUCCESS";
            } else if (status >= 400 && status < 500) {
                emoji = "⚠️";
                statusText = "CLIENT ERROR";
            } else if (status >= 500) {
                emoji = "❌";
                statusText = "SERVER ERROR";
            } else {
                emoji = "ℹ️";
                statusText = "INFO";
            }
            
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            logger.info("{} RESPONSE - {}", emoji, statusText);
            logger.info("   Method:   {} {}", method, uri);
            logger.info("   Status:   {}", status);
            logger.info("   Duration: {} ms", duration.toMillis());
            
            if (ex != null) {
                logger.error("   Error:    {}", ex.getMessage());
            }
            
            logger.info("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
            logger.info(""); // Empty line for readability
        }
    }
}
