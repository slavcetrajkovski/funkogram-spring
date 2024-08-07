package com.example.funkogram.app.category.domain.embeddable;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class CategoryProductKey implements Serializable {

    @Column(name = "category_id")
    private Long categoryId;

    @Column(name = "product_id")
    private Long productId;

    public CategoryProductKey(Long categoryId, Long productId) {
        this.categoryId = categoryId;
        this.productId = productId;
    }

    public CategoryProductKey() {

    }
}
