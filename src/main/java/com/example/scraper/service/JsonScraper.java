package com.example.scraper.service;


import com.example.scraper.mapper.JsonUtil;
import com.example.scraper.model.EntityData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class JsonScraper implements Scraper {


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
        try {
            // Perform an HTTP GET request to the provided URL
            String jsonResponse = restTemplate.getForObject(url, String.class);

            // Parse JSON response to EntityData object
            EntityData data = JsonUtil.parseJson(jsonResponse, EntityData.class);
            return data.toString();

        } catch (JsonProcessingException e) {
            // Handle invalid JSON gracefully
            System.err.println("Failed to parse JSON response: " + e.getMessage());
            return "Failed to parse JSON response";
        } catch (Exception e) {
            // Handle other exceptions (e.g., network issues)
            System.err.println("An error occurred during scraping: " + e.getMessage());
            return "Failed to parse JSON response";
        }
    }
}
