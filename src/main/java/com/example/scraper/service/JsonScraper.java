package com.example.scraper.service;


import com.example.scraper.model.EntityData;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class JsonScraper implements Scraper {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public JsonScraper() {
        this.webClient = WebClient.create();
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void scrape(String url) {
        try {
            String jsonResponse = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            // Attempt to parse the JSON response
            EntityData entity = objectMapper.readValue(jsonResponse, EntityData.class);

            // Log the scraped data
            System.out.println("Scraped Entity Data: " + entity);

        } catch (JsonProcessingException e) {
            // Handle invalid JSON gracefully
            System.err.println("Failed to parse JSON response: " + e.getMessage());
        } catch (Exception e) {
            // Handle other exceptions (e.g., network issues)
            System.err.println("An error occurred during scraping: " + e.getMessage());
        }
    }
}
