package com.example.funkogram.app.product.service.impl;

import com.example.funkogram.app.category.domain.Category;
import com.example.funkogram.app.category.repository.CategoryRepository;
import com.example.funkogram.app.category.service.CategoryProductService;
import com.example.funkogram.app.category.repository.CategoryProductRepository;

import com.example.funkogram.app.exceptions.BadRequestException;
import com.example.funkogram.app.exceptions.NotFoundEntityException;

import com.example.funkogram.app.product.domain.Product;
import com.example.funkogram.app.product.domain.ProductDto;
import com.example.funkogram.app.product.domain.enums.ProductType;
import com.example.funkogram.app.product.repository.ProductSpecification;
import com.example.funkogram.app.product.repository.ProductRepository;
import com.example.funkogram.app.product.service.ProductService;
import com.example.funkogram.app.product.domain.enums.ProductStatus;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.io.IOException;
import java.util.List;

import org.apache.tomcat.util.codec.binary.Base64;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final CategoryProductRepository categoryProductRepository;

    private final CategoryProductService categoryProductService;

    private final CategoryRepository categoryRepository;

    public Page<ProductDto> listAll(int page, int size, String searchFilter, String categoryFilter, String sortFilter, String productStatus, String productType) {
        Pageable pageable = PageRequest.of(page, size);
        Specification<Product> spec = Specification.where(ProductSpecification.hasCategory(categoryFilter))
                .and(ProductSpecification.hasNameContaining(searchFilter))
                .and(ProductSpecification.hasSort(sortFilter))
                .and(ProductSpecification.hasProductStatus(productStatus))
                .and(ProductSpecification.hasProductType(productType));
        Page<Product> productPage = productRepository.findAll(spec, pageable);

        return productPage.map(product -> {
            byte[] imageData = this.getPhoto(product);
            return this.createProductDto(product, imageData);
        });
    }

    @Override
    public Product add(String name, byte[] imageUrl, double price, double adminPrice,
                       int stock, ProductStatus status, ProductType productType) {
        Product product = new Product(name, imageUrl, price, adminPrice, stock, status, productType);
        this.productRepository.save(product);
        return product;
    }

    @Override
    public ProductDto findByIdDto(Long id) {
        Product product = this.productRepository.findById(id)
                .orElseThrow(() -> new NotFoundEntityException(Product.class, id));

        byte[] imageData = this.getPhoto(product);

        return this.createProductDto(product, imageData);
    }

    @Override
    public byte[] getPhoto(Product product) {
        if (product == null) {
            throw new NotFoundEntityException(Product.class);
        }
        return product.getImageUrl();
    }

    @Override
    public ProductDto createProductDto(Product p, byte[] imageData) {
        ProductDto productDto = null;
        List<Category> categories = this.categoryProductRepository.findCategoriesByProductId(p.getId());
        if (imageData != null && imageData.length > 0) {
            String photoBase64 = Base64.encodeBase64String(imageData);
            productDto = new ProductDto(p.getName(), photoBase64, p.getPrice(),
                    p.getStock(), p.getProductStatus(), categories);
        }
        return productDto;
    }

    @Override
    public Product findByName(String name) {
        return this.productRepository.findByName(name);
    }

    @Override
    public Product create(String name, MultipartFile image, double price, double adminPrice, int stock,
                          ProductStatus productStatus, List<Long> categoriesId, ProductType productType) {
        try {
            if (!List.of(MediaType.IMAGE_JPEG_VALUE, MediaType.IMAGE_PNG_VALUE).contains(image.getContentType())) {
                throw new BadRequestException("The file is not an image");
            }
            if (image.getBytes().length == 0) {
                throw new BadRequestException("The image has 0 length");
            }

            Product product = this.add(name, image.getBytes(), price, adminPrice, stock, productStatus, productType);
            List<Category> categories = this.categoryRepository.findAllById(categoriesId);
            for (Category category : categories) {
                this.categoryProductService.add(category, product);
            }

            return product;
        } catch (IOException e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    @Override
    public Product findByNameAndUpdate(String name, double price, double adminPrice, int stock, ProductStatus status, ProductType productType) {
        Product product = this.findByName(name);
        if (product == null) {
            return null;
        }
        product.setName(name);
        product.setPrice(price);
        product.setProductStatus(status);
        product.setStock(stock);
        product.setAdminPrice(adminPrice);
        product.setProductType(productType);

        return this.productRepository.save(product);
    }
}
