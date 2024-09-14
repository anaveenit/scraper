package com.example.scraper.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class WebScraperService {
    private final ScraperFactory scraperFactory;

    @Autowired
    public WebScraperService(ScraperFactory scraperFactory) {
        this.scraperFactory = scraperFactory;
    }

    public String scrape(String url) {
        Scraper scraper = scraperFactory.getScraper(url);  // Use ScraperFactory to get the appropriate scraper
        return scraper.scrape(url);
    }
}
