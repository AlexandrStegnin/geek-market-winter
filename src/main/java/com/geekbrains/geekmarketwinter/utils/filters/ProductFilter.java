package com.geekbrains.geekmarketwinter.utils.filters;

import com.geekbrains.geekmarketwinter.entites.Category;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ProductFilter extends AbstractFilter {

    private String title;

    private Category category;

    private double price;

    public ProductFilter(String title) {
        this.title = title;
    }

    public ProductFilter(Category category) {
        this.category = category;
    }

    public ProductFilter(String title, Category category) {
        this.title = title;
        this.category = category;
    }
}
