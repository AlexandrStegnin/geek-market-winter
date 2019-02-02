package com.geekbrains.geekmarketwinter.vaadin.ui.admin;

import com.geekbrains.geekmarketwinter.entites.Category;
import com.geekbrains.geekmarketwinter.services.AuthService;
import com.geekbrains.geekmarketwinter.services.CategoryService;
import com.geekbrains.geekmarketwinter.vaadin.custom.CustomAppLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

import java.util.List;

@Route("admin/categories")
@Theme(value = Material.class, variant = Material.DARK)
public class CategoryView extends VerticalLayout {

    private CategoryService categoryService;
    private Grid<Category> grid;
    private ListDataProvider<Category> categoryProvider;
    private AuthService auth;

    public CategoryView(CategoryService categoryService, AuthService auth) {
        this.categoryService = categoryService;
        this.grid = new Grid<>();
        this.categoryProvider = new ListDataProvider(getAllCategories());
        this.auth = auth;
        init();
    }

    private void init() {

        grid.setDataProvider(categoryProvider);

        grid.addColumn(Category::getId)
                .setHeader("ID")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setWidth("10px")
                .setFlexGrow(1);

        grid.addColumn(Category::getTitle)
                .setHeader("Title")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(Category::getDescription)
                .setHeader("Description")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        CustomAppLayout appLayout = new CustomAppLayout(auth, grid);
        add(appLayout);
        setHeight("100vh");
    }

    private List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

}
