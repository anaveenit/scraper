package com.example.scraper.service;

import com.example.scraper.model.ProductData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class HtmlScraper implements Scraper {

    @Override
    public void scrape(String url) {
        try {
            Document doc = Jsoup.connect(url).get();  // Fetch and parse HTML document
            Element productTitle = doc.selectFirst(".product-title");

            if (productTitle != null) {  // Check if the element exists
                String title = productTitle.text();
                String id = productTitle.attr("data-id");

                ProductData product = new ProductData(id, title);
                System.out.println("Scraped Product Data: " + product);
            } else {
                System.err.println("Error: Product title element not found in the HTML.");
            }
        } catch (IOException e) {
            System.err.println("Error while fetching HTML content from URL: " + url + " - " + e.getMessage());
        }
    }
}
