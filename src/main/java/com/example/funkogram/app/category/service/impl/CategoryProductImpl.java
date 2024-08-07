package com.example.funkogram.app.category.service.impl;

import com.example.funkogram.app.category.domain.Category;
import com.example.funkogram.app.category.domain.CategoryProduct;
import com.example.funkogram.app.category.domain.embeddable.CategoryProductKey;
import com.example.funkogram.app.category.repository.CategoryProductRepository;
import com.example.funkogram.app.category.service.CategoryProductService;

import com.example.funkogram.app.category.service.CategoryService;
import com.example.funkogram.app.product.domain.Product;
import com.example.funkogram.app.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryProductImpl implements CategoryProductService {

    private final CategoryProductRepository categoryProductRepository;

    @Override
    public CategoryProduct add(Category category, Product product) {
        CategoryProduct categoryProduct = new CategoryProduct();

        if(category != null && product != null) {
            CategoryProductKey categoryProductKey = new CategoryProductKey(category.getId(), product.getId());
            categoryProduct.setId(categoryProductKey);
            categoryProduct.setCategory(category);
            categoryProduct.setProduct(product);
        }

        return this.categoryProductRepository.save(categoryProduct);
    }
}
