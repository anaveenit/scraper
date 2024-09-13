package com.example.scraper;

import com.example.scraper.service.JsonScraper;
import com.example.scraper.service.HtmlScraper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MockServerTest {

    private MockWebServer mockWebServer;
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        restTemplate = new RestTemplate();  // Initialize RestTemplate
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testJsonScraperWithValidJsonResponse() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"title\":\"My title\"}")
                .addHeader("Content-Type", "application/json"));

        JsonScraper jsonScraper = new JsonScraper(restTemplate);
        String baseUrl = mockWebServer.url("/entity-slug-uuid.json").toString();
        String result = jsonScraper.scrape(baseUrl);

        assertTrue(result.contains("My title"));
    }

    @Test
    void testHtmlScraperWithValidHtmlResponse() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setBody("<html><body><h1 class='product-title' data-id='1234'>Product Title</h1></body></html>")
                .addHeader("Content-Type", "text/html"));

        HtmlScraper htmlScraper = new HtmlScraper();
        String baseUrl = mockWebServer.url("/product-slug.html").toString();
        String result = htmlScraper.scrape(baseUrl);

        assertEquals("ProductData{id='1234', title='Product Title'}", result);
    }

    @Test
    void testJsonScraperWithInvalidJsonResponse() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{invalid-json}")
                .addHeader("Content-Type", "application/json"));

        JsonScraper jsonScraper = new JsonScraper(restTemplate);
        String baseUrl = mockWebServer.url("/entity-slug-uuid.json").toString();
        String result = jsonScraper.scrape(baseUrl);
        assertTrue(result.contains("Failed to parse JSON response"));
    }

    @Test
    void testHtmlScraperWithMissingTitleElement() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setBody("<html><body>No product title available</body></html>")
                .addHeader("Content-Type", "text/html"));

        HtmlScraper htmlScraper = new HtmlScraper();
        String baseUrl = mockWebServer.url("/product-slug.html").toString();
        String result = htmlScraper.scrape(baseUrl);

        assertEquals("Product title element not found in the HTML.", result);
    }

    @Test
    void testScraperWith404NotFound() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("Not Found"));

        JsonScraper jsonScraper = new JsonScraper(restTemplate);
        String baseUrl = mockWebServer.url("/entity-slug-uuid.json").toString();
        String result = jsonScraper.scrape(baseUrl);
        assertTrue(result.contains("Failed to parse JSON response"));
    }

    @Test
    void testScraperWith500ServerError() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error"));

        HtmlScraper htmlScraper = new HtmlScraper();
        String baseUrl = mockWebServer.url("/product-slug.html").toString();
        String result = htmlScraper.scrape(baseUrl);
        assertTrue(result.contains("Error while fetching HTML content from URL"));
    }

    @Test
    void testScraperWithTimeout() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"title\":\"Delayed Response\"}")
                .setHeader("Content-Type", "application/json")
                .setBodyDelay(3, java.util.concurrent.TimeUnit.SECONDS));  // Simulate a delay

        JsonScraper jsonScraper = new JsonScraper(restTemplate);
        String baseUrl = mockWebServer.url("/entity-slug-uuid.json").toString();
        String result = jsonScraper.scrape(baseUrl);
        assertTrue(result.contains("Delayed Response"));
    }

    @Test
    void testRateLimitingHandling() throws Exception {
        for (int i = 0; i < 15; i++) {
            mockWebServer.enqueue(new MockResponse()
                    .setBody("{\"title\":\"Response " + i + "\"}")
                    .addHeader("Content-Type", "application/json"));
        }

        JsonScraper jsonScraper = new JsonScraper(restTemplate);
        String baseUrl = mockWebServer.url("/entity-slug-uuid.json").toString();

        for (int i = 0; i < 15; i++) {
            String result = jsonScraper.scrape(baseUrl);
            assertTrue(result.contains("Response " + i));
        }
    }
}
