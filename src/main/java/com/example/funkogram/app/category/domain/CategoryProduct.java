package com.example.funkogram.app.category.domain;

import com.example.funkogram.app.category.domain.embeddable.CategoryProductKey;
import com.example.funkogram.app.product.domain.Product;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.MapsId;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;

import lombok.Data;

@Entity
@Data
@Table(name = "category_product")
public class CategoryProduct {

    @EmbeddedId
    private CategoryProductKey id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("categoryId")
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

}
