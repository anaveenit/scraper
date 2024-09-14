package com.example.scraper.service;

import com.example.scraper.config.RateLimiterConfig;
import com.example.scraper.mapper.HtmlUtil;
import com.example.scraper.model.ProductData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;


@Service
public class HtmlScraper implements Scraper {

    private static final Logger log = LogManager.getLogger(HtmlScraper.class);

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
            Document doc = Jsoup.connect(url).get();  // Fetch and parse HTML document
            Element productTitle = doc.selectFirst(".product-title");

            if (productTitle != null) {  // Check if the element exists
                String title = HtmlUtil.extractText(productTitle);
                String id = HtmlUtil.extractAttribute(productTitle, "data-id");

                ProductData product = new ProductData(id, title);
                String result = product.toString();
                HtmlScraper.log.error("Scraped Product Data: {}", product);
                return result;

            } else {
                log.error("Error: Product title element not found in the HTML.");
                return "Product title element not found in the HTML.";
            }
        } catch (IOException e) {
            log.error("Error while fetching HTML content from URL: {} - {}", url, e.getMessage());
            return "Error while fetching HTML content from URL";
        } catch (Exception e) {
            log.error("Exception in HtmlScraper{}", e.getMessage());
            return "Exception in HtmlScraper";
        }
    }
}
