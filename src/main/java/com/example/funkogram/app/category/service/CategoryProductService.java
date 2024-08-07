package com.example.funkogram.app.category.service;

import com.example.funkogram.app.category.domain.Category;
import com.example.funkogram.app.category.domain.CategoryProduct;
import com.example.funkogram.app.product.domain.Product;

public interface CategoryProductService {
    CategoryProduct add(Category category, Product product);
}
