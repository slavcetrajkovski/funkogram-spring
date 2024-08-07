package com.example.funkogram.app.product.scraper.impl;

import com.example.funkogram.app.category.domain.Category;
import com.example.funkogram.app.exceptions.BadRequestException;
import com.example.funkogram.app.product.scraper.impl.efantasy.EfantasyPageScraperTemplate;
import com.example.funkogram.helpers.UrlPattern;
import com.example.funkogram.app.product.scraper.ProductServiceScraper;
import com.example.funkogram.app.product.repository.UrlPatternRepository;
import com.example.funkogram.app.category.service.CategoryService;
import com.example.funkogram.app.product.scraper.impl.goblin.GoblinPageScraper;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class ProductServiceScraperImpl implements ProductServiceScraper {
    private static final String GOBLIN_URL = "https://goblingames.mk/product-category/figuri/funko/";
    private static final String EFANTASY_FUNKO_URL = "https://www.efantasy.gr/en/products/funko-pop/";

    private static final String EFANTASY_BITTY_URL = "https://www.efantasy.gr/en/products/search=Funko%20bitty%20POP!/";

    private static final String EFANTASY_COMICS_URL = "https://www.efantasy.gr/en/products/comics/p-28-201-language=english";

    private static final String EFANTASY_MANGA_URL = "https://www.efantasy.gr/en/products/comics/p-69-559-cover=manga/p-28-201-language=english";

    private final UrlPatternRepository urlPatternRepository;

    private final CategoryService categoryService;

    private final EfantasyPageScraperTemplate efantasyPageScraperTemplate;

    private final GoblinPageScraper goblinPageScraper;

    private static final String[] USER_AGENTS = {
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.114 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:89.0) Gecko/20100101 Firefox/89.0",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/605.1.15 (KHTML, like Gecko) Version/14.1.1 Safari/605.1.15",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/90.0.4430.93 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 11.2; rv:86.0) Gecko/20100101 Firefox/86.0"
    };

    private String getRandomUserAgent() {
        Random random = new Random();
        return USER_AGENTS[random.nextInt(USER_AGENTS.length)];
    }

    @Override
    public void scrapeGoblin() {
        try {
            Document document = Jsoup.connect(GOBLIN_URL).get();
            int totalPages = Integer.parseInt(document
                    .select("#main > div > nav > a:nth-child(6)")
                    .text());

            ExecutorService executorService = Executors.newFixedThreadPool(5);
            List<Future<?>> futures = new ArrayList<>();

            for (int i = 1; i <= totalPages; i++) {
                int pageNum = i;
                Future<?> future = executorService.submit(() -> this.goblinPageScraper.scrapeGoblinPage(pageNum));
                futures.add(future);
            }

            for (Future<?> future : futures) {
                try {
                    future.get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new BadRequestException(e.getMessage());
                }
            }

            executorService.shutdown();

        } catch (IOException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public void scrapeEfantasyFunko() {
        List<UrlPattern> urlPatterns = this.urlPatternRepository.findAll();

        for (UrlPattern pattern : urlPatterns) {
            try {
                Category category = this.categoryService.createCategory(pattern.getPatternName(), pattern.getId());
                StringBuilder urlBuilder = new StringBuilder(EFANTASY_FUNKO_URL + pattern.getPatternName() + "/");
                Document document = Jsoup.connect(urlBuilder.toString())
                        .userAgent(getRandomUserAgent())
                        .timeout(10 * 1000)
                        .get();
                int totalPages = resolvePages(document.select(".pagination").text());

                System.out.println(pattern.getPatternName());
                for (int i = 0; i < totalPages; i++) {
                    String finalUrl = urlBuilder + "start=" + i * 48;
                    this.efantasyPageScraperTemplate.templateScrape(finalUrl, category);
                }

            } catch (IOException e) {
                throw new BadRequestException(e.getMessage());
            }
        }
    }

    @Override
    public void scrapeEfantasyBitty() {
        try {
            Document document = Jsoup.connect(EFANTASY_BITTY_URL)
                    .timeout(10 * 1000)
                    .get();
            int totalPages = resolvePages(document.select(".pagination").text());

            for(int i = 0; i < totalPages; i++) {
                String url = EFANTASY_BITTY_URL + "start=" + i * 48;
                this.efantasyPageScraperTemplate.templateScrape(url, null);
            }
        } catch (IOException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public void scrapeEfantasyComics() {
        try {
            Document document = Jsoup.connect(EFANTASY_COMICS_URL)
                    .timeout(10 * 1000)
                    .get();
            int totalPages = resolvePages(document.select(".pagination").text());

            for(int i = 0; i < totalPages; i++) {
                String url = EFANTASY_COMICS_URL + "start=" + i * 48;
                this.efantasyPageScraperTemplate.templateScrape(url, null);
            }
        } catch (IOException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    private int resolvePages(String text) {
        Pattern pattern = Pattern.compile("(?:\\.\\.\\.)(\\d+)$|(\\d)$");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            String matched = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);

            return Integer.parseInt(matched);
        }

        return 0;
    }

}

