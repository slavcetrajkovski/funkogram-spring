package com.example.funkogram.app.product.scraper.impl.efantasy;

import com.example.funkogram.app.category.domain.Category;
import com.example.funkogram.app.category.service.CategoryProductService;
import com.example.funkogram.app.product.domain.enums.ProductStatus;
import com.example.funkogram.app.exceptions.BadRequestException;
import com.example.funkogram.app.product.domain.Product;
import com.example.funkogram.app.product.domain.enums.ProductType;
import com.example.funkogram.app.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EfantasyPageScraperTemplate {
    private static final double DISCOUNT = 0.05;

    private final ProductService productService;

    private final CategoryProductService categoryProductService;

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

    public void templateScrape(String url, Category category) {
        try {
            Document document = Jsoup.connect(url).userAgent(getRandomUserAgent())
                    .timeout(10 * 10000)
                    .get();
            Elements elems = document.select(".product-box");
            for (Element elem : elems) {
                String status = elem.select(".product-label").text();
                String imageUrl = elem.select(".product-image img").first().attr("src");
                byte[] imageBytes = convertImageUrlToByteArray(imageUrl);
                String[] prices = elem.select(".product-price").text().replace(",", ".")
                        .split("€");
                String price = prices[0];
                String name = elem.select(".product-title").text().replace("Figures", "")
                .replace("Φιγούρα", "").replace("Figure", "").trim();
                String stock = elem.select(".product-stock").text().replaceAll("\\D+", "");

                int parsedStock = stock.isEmpty() ? 0 : Integer.parseInt(stock);
                ProductStatus productStatus = setProductStatus(status);
                if (parsedStock == 0 && !productStatus.equals(ProductStatus.PREORDER)) continue;

                ProductType productType = setProductType(url);

                double realPrice = Double.parseDouble(price);
                realPrice = (double) Math.round((realPrice - realPrice * DISCOUNT) * 10.0) / 10;

                double adminPrice = realPrice;
                realPrice = setPricePropertyEfantasy(realPrice);

                Product product = this.productService.findByNameAndUpdate(name, realPrice, adminPrice, parsedStock,
                        productStatus, productType);

                if (product == null) {
                    product = this.productService.add(name, imageBytes, realPrice, adminPrice, parsedStock, productStatus, productType);
                    if (category != null) {
                        this.categoryProductService.add(category, product);
                    }
                }
            }
        } catch (IOException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    private byte[] convertImageUrlToByteArray(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            return url.openStream().readAllBytes();
        } catch (IOException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    private double setPricePropertyEfantasy(double realPrice) {
        if (realPrice <= 1) {
            realPrice = 200;
        } else if (realPrice <= 3) {
            realPrice = 350;
        } else if (realPrice <= 6) {
            realPrice = 750;
        } else if (realPrice <= 8) {
            realPrice = 850;
        } else if (realPrice <= 10) {
            realPrice = 950;
        } else if (realPrice <= 15.9) {
            realPrice = 1500;
        } else if (realPrice <= 17.9) {
            realPrice = 1650;
        } else if (realPrice <= 22) {
            realPrice = 2000;
        } else if (realPrice <= 26.9) {
            realPrice = 2250;
        } else if (realPrice <= 30) {
            realPrice = 2650;
        } else if (realPrice <= 36) {
            realPrice = 3150;
        } else if (realPrice <= 44.9) {
            realPrice = 3650;
        } else if (realPrice <= 50) {
            realPrice = 4000;
        } else if (realPrice <= 55) {
            realPrice = 4500;
        } else if (realPrice <= 60) {
            realPrice = 5000;
        } else if (realPrice <= 70) {
            realPrice = 6000;
        } else if (realPrice <= 80) {
            realPrice = 6500;
        } else if (realPrice <= 90) {
            realPrice = 7250;
        } else if (realPrice <= 100) {
            realPrice = 8500;
        }
        return realPrice;
    }

    private ProductStatus setProductStatus(String status) {
        if (status.contains("NEW ARRIVAL")) {
            return ProductStatus.NEW_ARRIVAL;
        } else if (status.contains("PRE-ORDER")) {
            return ProductStatus.PREORDER;
        }
        return ProductStatus.IN_STOCK;
    }

    private ProductType setProductType(String url) {
        if(url.contains("funko-pop")) {
            return ProductType.FUNKO_POP;
        } else if(url.contains("bitty")) {
            return ProductType.BITTY_POP;
        } else if(url.contains("manga")) {
            return ProductType.MANGA;
        } else if(url.contains("comics")) {
            return ProductType.COMIC;
        }
        return ProductType.FUNKO_KEYCHAIN;
    }
}
