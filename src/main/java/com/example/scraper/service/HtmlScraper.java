package com.example.scraper.service;

import com.example.scraper.mapper.HtmlUtil;
import com.example.scraper.model.ProductData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class HtmlScraper implements Scraper {

    @Override
    public String scrape(String url) {
        if (url == null || url.isEmpty()) {
            throw new IllegalArgumentException("URL must not be null or empty");
        }

        try {
            Document doc = Jsoup.connect(url).get();  // Fetch and parse HTML document
            Element productTitle = doc.selectFirst(".product-title");

            if (productTitle != null) {  // Check if the element exists
                String title = HtmlUtil.extractText(productTitle);
                String id = HtmlUtil.extractAttribute(productTitle, "data-id");

                ProductData product = new ProductData(id, title);
                String result = product.toString();
                System.out.println("Scraped Product Data: " + product);
                return result;

            } else {
                System.err.println("Error: Product title element not found in the HTML.");
                return "Product title element not found in the HTML.";
            }
        } catch (IOException e) {
            System.err.println("Error while fetching HTML content from URL: " + url + " - " + e.getMessage());
            return "Error while fetching HTML content from URL";
        } catch (Exception e) {
            return "Error while fetching HTML content from URL..";
        }
    }
}
