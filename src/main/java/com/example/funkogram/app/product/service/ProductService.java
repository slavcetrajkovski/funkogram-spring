package com.example.funkogram.app.product.service;

import com.example.funkogram.app.product.domain.Product;
import com.example.funkogram.app.product.domain.ProductDto;
import com.example.funkogram.app.product.domain.enums.ProductStatus;
import com.example.funkogram.app.product.domain.enums.ProductType;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    Page<ProductDto> listAll(int page,
                             int size,
                             String searchFilter,
                             String categoryFilter,
                             String sortFilter,
                             String productStatus,
                             String productType);

    Product add(String name,
                byte [] imageUrl,
                double price,
                double adminPrice,
                int stock,
                ProductStatus status,
                ProductType productType);
    Product findByNameAndUpdate(String name,
                                double price,
                                double adminPrice,
                                int stock,
                                ProductStatus status,
                                ProductType productType);

    ProductDto findByIdDto(Long id);

    byte [] getPhoto(Product product);

    ProductDto createProductDto(Product p, byte [] imageData);

    Product findByName(String name);

    Product create(String name,
                   MultipartFile image,
                   double price,
                   double adminPrice,
                   int stock,
                   ProductStatus productStatus,
                   List<Long> categoriesId,
                   ProductType productType);
}
