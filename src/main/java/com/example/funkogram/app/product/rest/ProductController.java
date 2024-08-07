package com.example.funkogram.app.product.rest;

import com.example.funkogram.app.product.domain.enums.ProductStatus;
import com.example.funkogram.app.product.domain.Product;
import com.example.funkogram.app.product.domain.ProductDto;
import com.example.funkogram.app.product.domain.enums.ProductType;
import com.example.funkogram.app.product.service.ProductService;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@RestController
@RequestMapping("/api/funkogram")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    /**
     * Create a product.
     *
     * @param name          Name of the product
     * @param image         Image for the product
     * @param price         Price for the product
     * @param adminPrice    Actual price for the product
     * @param stock         Quantity for the product
     * @param productStatus Status for the product
     * @param categoriesId  Ids of categories associated with the product
     * @return ResponseEntity containing the created Product object
     */
    @PostMapping(value = "/add", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<Product> createProduct(@RequestParam String name,
                                                 @RequestParam("imageUrl") MultipartFile image,
                                                 @RequestParam double price,
                                                 @RequestParam double adminPrice,
                                                 @RequestParam int stock,
                                                 @RequestParam ProductStatus productStatus,
                                                 @RequestParam List<Long> categoriesId,
                                                 @RequestParam ProductType productType) {
        return ResponseEntity.status(HttpStatus.CREATED).body(this.productService.create(name, image, price, adminPrice, stock,
                productStatus, categoriesId, productType));
    }

    /**
     * List all products.
     *
     * @return ResponseEntity containing a list of all the products
     */

    @GetMapping("/products")
    public ResponseEntity<Page<ProductDto>> listAllProducts(@RequestParam(defaultValue = "0", required = false) int page,
                                                            @RequestParam(defaultValue = "40", required = false) int size,
                                                            @RequestParam(required = false) String searchFilter,
                                                            @RequestParam(required = false) String categoryFilter,
                                                            @RequestParam(required = false) String sortFilter,
                                                            @RequestParam(required = false) String productStatus,
                                                            @RequestParam(required = false) String productType) {
        Page<ProductDto> products = this.productService.listAll(page, size, searchFilter, categoryFilter, sortFilter, productStatus, productType);
        return ResponseEntity.ok(products);
    }

    /**
     * Details of a product.
     *
     * @param id ID of the product
     * @return ResponseEntity containing the Product object with all its details
     */
    @GetMapping("/product/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Long id) {
        ProductDto productDto = this.productService.findByIdDto(id);
        return ResponseEntity.ok(productDto);
    }
}
