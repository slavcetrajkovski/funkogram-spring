package com.example.funkogram.app.product.domain;

import com.example.funkogram.app.category.domain.Category;
import com.example.funkogram.app.product.domain.enums.ProductStatus;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class ProductDto {
    private String name;

    private String imageUrl;

    private double price;

    private int stock;

    private ProductStatus productStatus;

    private List<Category> categories;

    public ProductDto(String name, String imageUrl, double price, int stock,
                      ProductStatus productStatus, List<Category> categories) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.price = price;
        this.stock = stock;
        this.productStatus = productStatus;
        this.categories = categories;
    }
}
