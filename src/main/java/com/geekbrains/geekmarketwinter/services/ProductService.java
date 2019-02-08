package com.geekbrains.geekmarketwinter.services;

import com.geekbrains.geekmarketwinter.entites.Product;
import com.geekbrains.geekmarketwinter.repositories.ProductRepository;
import com.geekbrains.geekmarketwinter.services.specifications.ProductSpecification;
import com.geekbrains.geekmarketwinter.utils.filters.ProductFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductSpecification specification;

    @Autowired
    public ProductService(ProductSpecification specification, ProductRepository productRepository) {
        this.specification = specification;
        this.productRepository = productRepository;
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).get();
    }

    public Page<Product> findAll(ProductFilter filters, Pageable pageable) {
        return productRepository.findAll(
                specification.getFilter(filters),
                pageable
        );
    }

    public int countByFilter(ProductFilter filter, Pageable pageable) {
        return findAll(filter, pageable).getContent().size();
    }
}