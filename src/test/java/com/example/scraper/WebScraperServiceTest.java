package com.example.scraper;



import com.example.scraper.service.JsonScraper;
import com.example.scraper.service.WebScraperService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class WebScraperServiceTest {

    @Mock
    private JsonScraper jsonScraper;

    @InjectMocks
    private WebScraperService webScraperService;

    @Test
    void testJsonScraper() {
        MockitoAnnotations.initMocks(this);
        webScraperService.scrapeUrls(List.of("https://example.com/entity-{slug}-{uuid}.json"));
    }
}
