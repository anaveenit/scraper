package com.example.scraper;

import com.example.scraper.service.JsonScraper;
import com.example.scraper.service.HtmlScraper;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class MockServerTest {

    private MockWebServer mockWebServer;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
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

        JsonScraper jsonScraper = new JsonScraper();
        String baseUrl = mockWebServer.url("/entity-slug-uuid.json").toString();
        jsonScraper.scrape(baseUrl);
    }

    @Test
    void testHtmlScraperWithValidHtmlResponse() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setBody("<html><body><h1 class='product-title' data-id='1234'>Product Title</h1></body></html>")
                .addHeader("Content-Type", "text/html"));

        HtmlScraper htmlScraper = new HtmlScraper();
        String baseUrl = mockWebServer.url("/product-slug.html").toString();
        htmlScraper.scrape(baseUrl);
    }

    @Test
    void testJsonScraperWithInvalidJsonResponse() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{invalid-json}")
                .addHeader("Content-Type", "application/json"));

        JsonScraper jsonScraper = new JsonScraper();
        String baseUrl = mockWebServer.url("/entity-slug-uuid.json").toString();
        jsonScraper.scrape(baseUrl);  // This should now handle or log the error gracefully.
    }


    @Test
    void testHtmlScraperWithMissingTitleElement() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setBody("<html><body>No product title available</body></html>")
                .addHeader("Content-Type", "text/html"));

        HtmlScraper htmlScraper = new HtmlScraper();
        String baseUrl = mockWebServer.url("/product-slug.html").toString();
        htmlScraper.scrape(baseUrl);  // Should handle missing elements gracefully.
    }

    @Test
    void testScraperWith404NotFound() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(404)
                .setBody("Not Found"));

        JsonScraper jsonScraper = new JsonScraper();
        String baseUrl = mockWebServer.url("/entity-slug-uuid.json").toString();
        jsonScraper.scrape(baseUrl);  // Should handle 404 error gracefully.
    }

    @Test
    void testScraperWith500ServerError() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setResponseCode(500)
                .setBody("Internal Server Error"));

        HtmlScraper htmlScraper = new HtmlScraper();
        String baseUrl = mockWebServer.url("/product-slug.html").toString();
        htmlScraper.scrape(baseUrl);  // Should handle 500 error gracefully.
    }

    @Test
    void testScraperWithTimeout() throws Exception {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"title\":\"Delayed Response\"}")
                .setHeader("Content-Type", "application/json")
                .setBodyDelay(3, java.util.concurrent.TimeUnit.SECONDS));  // Simulate a delay

        JsonScraper jsonScraper = new JsonScraper();
        String baseUrl = mockWebServer.url("/entity-slug-uuid.json").toString();
        jsonScraper.scrape(baseUrl);  // Should handle timeout if configured.
    }

    @Test
    void testRateLimitingHandling() throws Exception {
        for (int i = 0; i < 15; i++) {
            mockWebServer.enqueue(new MockResponse()
                    .setBody("{\"title\":\"Response " + i + "\"}")
                    .addHeader("Content-Type", "application/json"));
        }

        JsonScraper jsonScraper = new JsonScraper();
        String baseUrl = mockWebServer.url("/entity-slug-uuid.json").toString();

        for (int i = 0; i < 15; i++) {
            jsonScraper.scrape(baseUrl);  // Should respect rate-limiting settings.
        }
    }
}
