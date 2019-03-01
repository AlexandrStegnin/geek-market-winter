package com.geekbrains.geekmarketwinter.vaadin.ui.admin;

import com.geekbrains.geekmarketwinter.config.support.OperationEnum;
import com.geekbrains.geekmarketwinter.entites.Category;
import com.geekbrains.geekmarketwinter.entites.Category_;
import com.geekbrains.geekmarketwinter.services.AuthService;
import com.geekbrains.geekmarketwinter.services.CategoryService;
import com.geekbrains.geekmarketwinter.vaadin.custom.CustomAppLayout;
import com.geekbrains.geekmarketwinter.vaadin.support.VaadinViewUtils;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.BeanValidationBinder;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

import java.util.List;

import static com.geekbrains.geekmarketwinter.config.support.Constants.ADMIN_CATEGORIES_PAGE;

@PageTitle("Categories")
@Route(ADMIN_CATEGORIES_PAGE)
@Theme(value = Material.class, variant = Material.LIGHT)
public class CategoryView extends VerticalLayout {

    private final CategoryService categoryService;
    private Grid<Category> grid;
    private ListDataProvider<Category> dataProvider;
    private final Button addNewBtn;
    private Binder<Category> binder;
    private CustomAppLayout appLayout;

    public CategoryView(CategoryService categoryService, AuthService auth) {
        this.addNewBtn = new Button("Add new category", e -> showDialog(new Category(), OperationEnum.CREATE));
        this.binder = new BeanValidationBinder<>(Category.class);
        this.categoryService = categoryService;
        this.dataProvider = new ListDataProvider<>(getAllCategories());
        this.grid = new Grid<>();
        this.appLayout = new CustomAppLayout(auth);
        init();
    }

    private void init() {
        addNewBtn.setIcon(VaadinIcon.PLUS.create());
        addNewBtn.setIconAfterText(true);

        grid.setDataProvider(dataProvider);

        grid.addColumn(Category::getTitle)
                .setHeader("Title")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(Category::getDescription)
                .setHeader("Description")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addComponentColumn(category -> VaadinViewUtils.makeEditorColumnActions(
                e -> showDialog(category, OperationEnum.UPDATE), e -> showDialog(category, OperationEnum.DELETE)))
                .setTextAlign(ColumnTextAlign.CENTER)
                .setEditorComponent(new Div())
                .setHeader("Actions")
                .setFlexGrow(2);

        VerticalLayout verticalLayout = new VerticalLayout();

        verticalLayout.add(addNewBtn, grid);
        verticalLayout.setAlignItems(Alignment.END);
        appLayout.setContent(verticalLayout);
        add(appLayout);
        setHeight("100vh");
    }

    private List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    private void saveCategory(Category category) {
        categoryService.save(category);
        dataProvider.refreshAll();
    }

    private void deleteCategory(Category category) {
        dataProvider.getItems().remove(category);
        categoryService.delete(category);
        dataProvider.refreshAll();
    }

    private void showDialog(Category category, OperationEnum operation) {
        FormLayout formLayout = new FormLayout();
        TextField title = new TextField("Title");
        title.setValue(category.getTitle() == null ? "" : category.getTitle());
        binder.forField(title)
                .bind(Category_.TITLE);

        TextField description = new TextField("Description");
        description.setValue(category.getDescription() == null ? "" : category.getDescription());
        binder.forField(description)
                .bind(Category_.DESCRIPTION);

        formLayout.add(title, description);

        Dialog dialog = VaadinViewUtils.initDialog();
        Button save = new Button("Save");

        Button cancel = new Button("Cancel", e -> dialog.close());
        HorizontalLayout actions = new HorizontalLayout();
        actions.add(save, cancel);

        VerticalLayout content = new VerticalLayout();

        switch (operation) {
            case UPDATE:
                content.add(formLayout, actions);
                save.addClickListener(e -> {
                    if (binder.writeBeanIfValid(category)) {
                        saveCategory(category);
                        dialog.close();
                    }
                });
                break;
            case CREATE:
                content.add(formLayout, actions);
                save.addClickListener(e -> {
                    if (binder.writeBeanIfValid(category)) {
                        dataProvider.getItems().add(category);
                        saveCategory(category);
                        dialog.close();
                    }
                });
                break;
            case DELETE:
                Div contentText = new Div();
                contentText.setText("Confirm delete category: " + category.getTitle() + "?");
                content.add(contentText, actions);
                save.setText("Yes");
                save.addClickListener(e -> {
                    deleteCategory(category);
                    dialog.close();
                });
                break;
        }

        dialog.add(content);
        dialog.open();
        title.getElement().callFunction("focus");

    }
}
