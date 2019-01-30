package com.geekbrains.geekmarketwinter.utils.filters;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ProductFilter extends AbstractFilter {

    private String title;

    private double price;
}
