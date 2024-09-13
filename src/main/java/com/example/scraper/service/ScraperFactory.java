package com.example.scraper.service;


import org.springframework.stereotype.Service;

@Service
public class ScraperFactory {
    private final JsonScraper jsonScraper;
    private final HtmlScraper htmlScraper;

    public ScraperFactory(JsonScraper jsonScraper, HtmlScraper htmlScraper) {
        this.jsonScraper = jsonScraper;
        this.htmlScraper = htmlScraper;
    }

    public Scraper getScraper(String url) {
        if (url.endsWith(".json")) {
            return jsonScraper;
        } else if (url.endsWith(".html")) {
            return htmlScraper;
        }
        throw new IllegalArgumentException("Unsupported URL format: " + url);
    }
}
