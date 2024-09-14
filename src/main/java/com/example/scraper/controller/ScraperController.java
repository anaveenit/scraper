package com.example.scraper.controller;

import com.example.scraper.service.WebScraperService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class ScraperController {

    private final WebScraperService webScraperService;

    public ScraperController(WebScraperService webScraperService) {
        this.webScraperService = webScraperService;
    }

    @GetMapping("/scrape")
    public String scrape(@RequestParam String url) {
        return webScraperService.scrape(url);
    }
}
