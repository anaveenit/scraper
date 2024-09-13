package com.example.scraper.service;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WebScraperService {
    private final ScraperFactory scraperFactory;

    public WebScraperService(ScraperFactory scraperFactory) {
        this.scraperFactory = scraperFactory;
    }

    public void scrapeUrls(List<String> urls) {
        urls.forEach(url -> {
            Scraper scraper = scraperFactory.getScraper(url);
            scraper.scrape(url);
        });
    }
}
