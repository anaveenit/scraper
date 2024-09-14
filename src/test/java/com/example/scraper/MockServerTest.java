package com.example.scraper;

import com.example.scraper.config.RateLimiterConfig;
import com.example.scraper.service.JsonScraper;
import com.example.scraper.service.HtmlScraper;
import com.example.scraper.service.ScraperFactory;
import com.example.scraper.service.WebScraperService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class MockServerTest {

    // Constants for URLs
    private static final String JSON_URL = "/entity-slug-uuid.json";
    private static final String HTML_URL = "/product-slug.html";

    // Constants for Responses
    private static final String VALID_JSON_RESPONSE = "{\"title\":\"My title\"}";
    private static final String VALID_HTML_RESPONSE = "<html><body><h1 class='product-title' data-id='1234'>Product Title</h1></body></html>";
    private static final String INVALID_JSON_RESPONSE = "{invalid-json}";
    private static final String MISSING_TITLE_HTML_RESPONSE = "<html><body>No product title available</body></html>";
    private static final String NOT_FOUND_RESPONSE = "Not Found";
    private static final String INTERNAL_SERVER_ERROR_RESPONSE = "Internal Server Error";
    private static final String DELAYED_JSON_RESPONSE = "{\"title\":\"Delayed Response\"}";

    // Constants for Expected Results
    private static final String EXPECTED_VALID_JSON_RESULT = "My title";
    private static final String EXPECTED_VALID_HTML_RESULT = "Product Title";
    private static final String EXPECTED_INVALID_JSON_RESULT = "Failed to parse JSON response";
    private static final String EXPECTED_MISSING_TITLE_RESULT = "Product title element not found in the HTML.";
    private static final String EXPECTED_NOT_FOUND_RESULT = "Failed to parse JSON response";
    private static final String EXPECTED_SERVER_ERROR_RESULT = "Error while fetching HTML content from URL";
    private static final String EXPECTED_TIMEOUT_RESULT = "Failed to parse JSON response";
    private static final String EXPECTED_RATE_LIMIT_EXCEEDED_RESULT = "Rate limit exceeded. Try again later.";

    // Constants for Test Setup
    private static final int RATE_LIMIT_THRESHOLD = 10; // Threshold for rate limiting
    private static final int TOTAL_REQUESTS = 15; // Total requests to simulate in rate limiting test
    private static final int TIMEOUT_MS = 1000; // Timeout for RestTemplate in milliseconds
    private static final int DELAY_SECONDS = 3; // Delay in seconds for timeout simulation

    private static final Logger log = LogManager.getLogger(MockServerTest.class);

    private MockWebServer mockWebServer;
    private WebScraperService webScraperService;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        // Reset rate limiter bucket before each test
        RateLimiterConfig.resetBucket();

        // Initialize WebScraperService with JsonScraper and HtmlScraper
        JsonScraper jsonScraper = new JsonScraper(new RestTemplate());
        HtmlScraper htmlScraper = new HtmlScraper();

        ScraperFactory scraperFactory = new ScraperFactory(jsonScraper, htmlScraper);

        // Initialize WebScraperService with ScraperFactory
        webScraperService = new WebScraperService(scraperFactory);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void testScraperWithValidJsonResponse() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setBody(VALID_JSON_RESPONSE)
                .addHeader("Content-Type", "application/json"));

        String baseUrl = mockWebServer.url(JSON_URL).toString();
        String result = webScraperService.scrape(baseUrl);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertTrue(result.contains(EXPECTED_VALID_JSON_RESULT), "Result should contain '" + EXPECTED_VALID_JSON_RESULT + "'");
    }

    @Test
    void testScraperWithValidHtmlResponse() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setBody(VALID_HTML_RESPONSE)
                .addHeader("Content-Type", "text/html"));

        String baseUrl = mockWebServer.url(HTML_URL).toString();
        String result = webScraperService.scrape(baseUrl);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertTrue(result.contains(EXPECTED_VALID_HTML_RESULT), "Result should contain '" + EXPECTED_VALID_HTML_RESULT + "'");
    }

    @Test
    void testScraperWithInvalidJsonResponse() {
        mockWebServer.enqueue(new MockResponse()
                .setBody(INVALID_JSON_RESPONSE)
                .addHeader("Content-Type", "application/json"));

        String baseUrl = mockWebServer.url(JSON_URL).toString();

        // Act
        String result = webScraperService.scrape(baseUrl);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(EXPECTED_INVALID_JSON_RESULT, result, "Result should indicate JSON parsing failure.");
    }

    @Test
    void testScraperWithMissingTitleElement() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setBody(MISSING_TITLE_HTML_RESPONSE)
                .addHeader("Content-Type", "text/html"));

        String baseUrl = mockWebServer.url(HTML_URL).toString();
        String result = webScraperService.scrape(baseUrl);

        // Assert
        assertEquals(EXPECTED_MISSING_TITLE_RESULT, result, "Result should indicate missing elements.");
    }

    @Test
    void testScraperWith404NotFound() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody(NOT_FOUND_RESPONSE));

        String baseUrl = mockWebServer.url(JSON_URL).toString();

        // Act
        String result = webScraperService.scrape(baseUrl);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(EXPECTED_NOT_FOUND_RESULT, result, "Result should handle 404 error gracefully.");
    }

    @Test
    void testScraperWith500ServerError() {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody(INTERNAL_SERVER_ERROR_RESPONSE));

        String baseUrl = mockWebServer.url(HTML_URL).toString();

        // Act
        String result = webScraperService.scrape(baseUrl);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(EXPECTED_SERVER_ERROR_RESULT, result, "Result should handle 500 error gracefully.");
    }

    @Test
    void testScraperWithTimeout() {
        mockWebServer.enqueue(new MockResponse()
                .setBody(DELAYED_JSON_RESPONSE)
                .setHeader("Content-Type", "application/json")
                .setBodyDelay(DELAY_SECONDS, java.util.concurrent.TimeUnit.SECONDS));  // Simulate a delay

        String baseUrl = mockWebServer.url(JSON_URL).toString();

        // Create a new JsonScraper instance with a short timeout for this test
        JsonScraper jsonScraperWithTimeout = new JsonScraper(createRestTemplateWithTimeout()); // 1-second timeout
        ScraperFactory scraperFactory = new ScraperFactory(jsonScraperWithTimeout, new HtmlScraper());
        WebScraperService webScraperServiceWithTimeout = new WebScraperService(scraperFactory);

        // Act
        String result = webScraperServiceWithTimeout.scrape(baseUrl);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(EXPECTED_TIMEOUT_RESULT, result, "Result should indicate timeout handling.");
    }

    // Helper method to create RestTemplate with a specified timeout using SimpleClientHttpRequestFactory
    private RestTemplate createRestTemplateWithTimeout() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(TIMEOUT_MS);  // Set connection timeout
        requestFactory.setReadTimeout(TIMEOUT_MS);     // Set read timeout
        return new RestTemplate(requestFactory);
    }

    @Test
    void testRateLimitingHandling() throws Exception {
        // Define a valid JSON and HTML response
        String validJsonResponse = "{\"title\":\"Valid Response\"}";
        String validHtmlResponse = "<html><body><h1 class='product-title' data-id='1234'>Valid HTML Title</h1></body></html>";

        // Enqueue valid mock responses for JSON and HTML requests
        for (int i = 0; i < TOTAL_REQUESTS; i++) {
            if (i % 2 == 0) {  // Even index: JSON response
                mockWebServer.enqueue(new MockResponse()
                        .setBody(validJsonResponse)
                        .addHeader("Content-Type", "application/json"));
            } else {  // Odd index: HTML response
                mockWebServer.enqueue(new MockResponse()
                        .setBody(validHtmlResponse)
                        .addHeader("Content-Type", "text/html"));
            }
        }

        String jsonBaseUrl = mockWebServer.url("/entity-slug-uuid.json").toString();
        String htmlBaseUrl = mockWebServer.url("/product-slug.html").toString();

        for (int i = 0; i < TOTAL_REQUESTS; i++) {
            // Use JSON URL for even requests and HTML URL for odd requests
            String baseUrl = (i % 2 == 0) ? jsonBaseUrl : htmlBaseUrl;

            System.out.println("Current request: " + i);

            if (i >= RATE_LIMIT_THRESHOLD) {  // After 10 requests, rate limiting should kick in
                System.out.println("Expecting rate limit exception for request: " + i);

                RuntimeException exception = assertThrows(RuntimeException.class, () -> {
                    webScraperService.scrape(baseUrl);  // This call should trigger rate limit exception
                });

                assertTrue(exception.getMessage().contains(EXPECTED_RATE_LIMIT_EXCEEDED_RESULT), "Exception should indicate rate limit exceeded.");
            } else {
                System.out.println("Request should succeed for: " + i);
                String result = webScraperService.scrape(baseUrl);
                assertNotNull(result, "Result should not be null for request " + i);
                if (i % 2 == 0) {
                    assertTrue(result.contains("Valid Response"), "Result should contain 'Valid Response' for JSON response.");
                } else {
                    assertTrue(result.contains("Valid HTML Title"), "Result should contain 'Valid HTML Title' for HTML response.");
                }
            }
        }
    }



}
