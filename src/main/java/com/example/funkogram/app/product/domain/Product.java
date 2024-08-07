package com.example.funkogram.app.product.domain;

import com.example.funkogram.app.category.domain.CategoryProduct;
import com.example.funkogram.app.product.domain.enums.ProductStatus;
import com.example.funkogram.app.product.domain.enums.ProductType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte [] imageUrl;

    private double price;

    private double adminPrice;

    private int stock;

    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;

    @OneToMany(mappedBy = "id.productId")
    @JsonIgnore
    private List<CategoryProduct> categoryProducts;

    private boolean deleted;

    @Enumerated(EnumType.STRING)
    private ProductType productType;

    public Product(String name, byte [] imageUrl, double price, double adminPrice, int stock,
                   ProductStatus productStatus, ProductType productType) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.stock = stock;
        this.price = price;
        this.adminPrice = adminPrice;
        this.productStatus = productStatus;
        this.categoryProducts = new ArrayList<>();
        this.productType = productType;
        this.deleted = false;
    }
}
