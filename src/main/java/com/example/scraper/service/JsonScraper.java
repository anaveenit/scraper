package com.example.scraper.service;


import com.example.scraper.config.RateLimiterConfig;
import com.example.scraper.mapper.JsonUtil;
import com.example.scraper.model.EntityData;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class JsonScraper implements Scraper {


    private static final Logger log = LogManager.getLogger(JsonScraper.class);
    private final RestTemplate restTemplate;

    // Constructor injection
    public JsonScraper(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }
    @Override
    public String scrape(String url) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("URL must not be null or empty");
        }
        // Rate limiting: Check if a token is available
        if (!RateLimiterConfig.tryConsumeToken()) {
            throw new RuntimeException("Rate limit exceeded. Try again later.");
        }
        try {
            // Perform an HTTP GET request to the provided URL
            String jsonResponse = restTemplate.getForObject(url, String.class);

            // Parse JSON response to EntityData object
            EntityData data = JsonUtil.parseJson(jsonResponse, EntityData.class);
            return data.toString();

        } catch (JsonProcessingException e) {
            // Handle invalid JSON gracefully
            log.error("Failed to parse JSON response: {}", e.getMessage());
            return "Failed to parse JSON response";
        } catch (Exception e) {
            // Handle other exceptions (e.g., network issues)
            log.error("An error occurred during scraping: {},{}", e.getMessage(),url);
            return "Failed to parse JSON response";
        }
    }
}
