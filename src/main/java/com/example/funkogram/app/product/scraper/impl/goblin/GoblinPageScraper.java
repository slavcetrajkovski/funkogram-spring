package com.example.funkogram.app.product.scraper.impl.goblin;

import com.example.funkogram.app.product.domain.enums.ProductStatus;
import com.example.funkogram.app.product.domain.Product;
import com.example.funkogram.app.product.domain.enums.ProductType;
import com.example.funkogram.app.product.service.ProductService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class GoblinPageScraper {

    private static final String GOBLIN_URL = "https://goblingames.mk/product-category/figuri/funko/";

    private final ProductService productService;

    private String extractPrice(String text) {
        Pattern pattern = Pattern.compile("\\b(\\d+(\\.\\d+)?)\\b");
        Matcher matcher = pattern.matcher(text);

        if (matcher.find()) {
            return matcher.group(1).replace(".", "");
        } else {
            return null;
        }
    }

    private byte[] convertImageUrlToByteArray(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            return url.openStream().readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }


    private int setPricePropertyGoblin(String price, String name) {
        int realPrice = Integer.parseInt(price);
        if ((name.contains("Bitty Pop") || name.contains("Trading") ||
                name.contains("Albums") || name.contains("Covers") ||
                name.contains("Jumbo") || name.contains("Protective") ||
                name.contains("(25cm)") || name.contains("Art Series") ||
                name.contains("Artist Series") || name.contains("Art-Series") ||
                name.contains("Tee")
        )) {
            return 0;
        } else if (name.contains("Pocket") && !name.contains("Tee")) {
            realPrice = 850;
        } else if (name.contains("Pocket") && name.contains("Valentine's")) {
            realPrice = 3000;
        } else if (realPrice == 1980 || realPrice == 1990) {
            realPrice = 3000;
        } else if (realPrice == 1590) {
            realPrice = 2150;
        } else if (realPrice == 2970 || realPrice == 2990 || realPrice == 2790 || realPrice == 3190) {
            realPrice = 4000;
        } else if (realPrice == 2490) {
            realPrice = 3500;
        } else if (realPrice == 3990 || realPrice == 3490) {
            realPrice = 5000;
        } else if (realPrice == 990 || realPrice == 890 || realPrice == 790 || realPrice == 690
                || realPrice == 650 || realPrice == 590 || realPrice == 490) {
            realPrice = 1500;
        } else if (realPrice == 1190 || realPrice == 1090) {
            realPrice = 1600;
        } else if (realPrice == 1290) {
            realPrice = 1800;
        } else if (name.contains("Super") && name.contains("Yu-Gi-Oh")) {
            realPrice = 2750;
        }

        return realPrice;
    }

    public void scrapeGoblinPage(int pageNum) {
        String url = GOBLIN_URL;
        url = url + "page/" + pageNum + "/";
        try {
            Document document = Jsoup.connect(url).get();
            Elements productElements = document.select(".rey-productInner");
            for (Element elem : productElements) {
                String name = elem.select(".woocommerce-loop-product__title").text().replace("Bobble-Head", "").trim();
                String price = extractPrice(elem.select(".price").text());
                String imageUrl = elem.select("img").attr("data-src");
                byte[] imageBytes = convertImageUrlToByteArray(imageUrl);
                int adminPrice = 0;
                if (price != null) {
                    adminPrice = Integer.parseInt(price);
                }
                int realPrice = setPricePropertyGoblin(price, name);
                if (realPrice == 0) continue;

                Product product = this.productService.findByNameAndUpdate(name, realPrice, adminPrice, 1,
                        ProductStatus.AVAILABLE_RIGHT_AWAY, ProductType.FUNKO_POP);

                if (product == null) {
                    this.productService.add(name, imageBytes, realPrice, adminPrice, 1, ProductStatus.AVAILABLE_RIGHT_AWAY, ProductType.FUNKO_POP);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
