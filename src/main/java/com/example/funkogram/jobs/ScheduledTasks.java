package com.example.funkogram.jobs;

import com.example.funkogram.app.product.scraper.ProductServiceScraper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTasks {
    private final ProductServiceScraper productServiceScraper;

    public ScheduledTasks(ProductServiceScraper productServiceScraper) {
        this.productServiceScraper = productServiceScraper;
    }

//    @Scheduled(cron = "0 0 5 * * ?")
//    @Scheduled(fixedDelay = 5000000)
    public void scheduledScraperEfantasyFunko() {
        this.productServiceScraper.scrapeEfantasyFunko();
    }

//    @Scheduled(fixedRate = 5000000)
    public void scheduledScraperGoblin() {
        this.productServiceScraper.scrapeGoblin();
    }

//    @Scheduled(fixedRate = 5000000)
    public void scheduledScraperEfantasyBitty() {
        this.productServiceScraper.scrapeEfantasyBitty();
    }
}
