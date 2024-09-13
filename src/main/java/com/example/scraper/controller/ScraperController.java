package com.example.scraper.controller;

import com.example.scraper.service.WebScraperService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ScraperController {

    @Autowired
    private WebScraperService webScraperService;

    @GetMapping("/scrape")
    public String scrape(@RequestParam List<String> urls) {
        webScraperService.scrapeUrls(urls);
        return "Scrapping Done!";
    }
}
