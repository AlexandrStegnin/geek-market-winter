package com.geekbrains.geekmarketwinter.services;

import com.geekbrains.geekmarketwinter.entites.Product;
import com.geekbrains.geekmarketwinter.repositories.ProductRepository;
import com.geekbrains.geekmarketwinter.services.specifications.ProductSpecification;
import com.geekbrains.geekmarketwinter.utils.filters.ProductFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

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

    public Product create(Product product) {
        return productRepository.save(product);
    }

    public List<Product> fetchAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> fetchAll(ProductFilter filter) {
        return productRepository.findAll(specification.getFilter(filter));
    }

    public Product update(Product product) {
        return create(product);
    }

    public void delete(Product product) {
        productRepository.delete(product);
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }
}