package com.geekbrains.geekmarketwinter.services.specifications;

import com.geekbrains.geekmarketwinter.entites.Category;
import com.geekbrains.geekmarketwinter.entites.Product;
import com.geekbrains.geekmarketwinter.entites.Product_;
import com.geekbrains.geekmarketwinter.utils.filters.ProductFilter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

import static org.springframework.data.jpa.domain.Specification.where;

@Component
public class ProductSpecification extends BaseSpecification<Product, ProductFilter> {
    @Override
    public Specification<Product> getFilter(ProductFilter filter) {
        return (root, query, cb) -> where(
                titleLike(filter.getTitle()))
                .and(categoryEquals(filter.getCategory()))
                .toPredicate(root, query, cb);
    }

    private static Specification<Product> titleLike(String title) {
        return ((root, criteriaQuery, criteriaBuilder) -> {
            if (Objects.equals(null, title) || StringUtils.isEmpty(title)) {
                return null;
            } else {
                return criteriaBuilder.like(root.get(Product_.title), title);
            }
        }
        );
    }

    private static Specification<Product> categoryEquals(Category category) {
        return ((root, criteriaQuery, criteriaBuilder) -> {
            if (Objects.equals(null, category)) {
                return null;
            } else {
                return criteriaBuilder.equal(root.get(Product_.category), category);
            }
        }
        );
    }

}
