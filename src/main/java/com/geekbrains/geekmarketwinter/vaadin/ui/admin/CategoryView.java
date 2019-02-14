package com.geekbrains.geekmarketwinter.vaadin.ui.admin;

import com.geekbrains.geekmarketwinter.entites.Category;
import com.geekbrains.geekmarketwinter.entites.Category_;
import com.geekbrains.geekmarketwinter.services.AuthService;
import com.geekbrains.geekmarketwinter.services.CategoryService;
import com.geekbrains.geekmarketwinter.vaadin.custom.CustomAppLayout;
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
@Theme(value = Material.class, variant = Material.DARK)
public class CategoryView extends VerticalLayout {

    private final CategoryService categoryService;
    private Grid<Category> grid;
    private final AuthService auth;
    private ListDataProvider<Category> dataProvider;
    private final Button addNewBtn;
    private Binder<Category> binder;

    public CategoryView(CategoryService categoryService, AuthService auth) {
        this.categoryService = categoryService;
        this.dataProvider = new ListDataProvider<>(getAllCategories());
        this.grid = new Grid<>();
        this.auth = auth;
        this.addNewBtn = new Button("Add new category", e -> showAddDialog(new Category()));
        this.binder = new BeanValidationBinder<>(Category.class);
        init();
    }

    private void init() {
        addNewBtn.setIcon(VaadinIcon.PLUS.create());
        addNewBtn.setIconAfterText(true);

        grid.setDataProvider(dataProvider);

        grid.addColumn(Category::getId)
                .setHeader("ID")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setWidth("10px")
                .setFlexGrow(0);

        grid.addColumn(Category::getTitle)
                .setHeader("Title")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addColumn(Category::getDescription)
                .setHeader("Description")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        grid.addComponentColumn(category -> {
            Div actions = new Div();
            Button edit = new Button("", VaadinIcon.EDIT.create());
            edit.addClickListener(e -> showEditDialog(category));
            Button delete = new Button("", VaadinIcon.TRASH.create());
            delete.addClickListener(e -> showDeleteDialog(category));
            actions.add(edit, delete);
            return actions;
        })
                .setTextAlign(ColumnTextAlign.CENTER)
                .setEditorComponent(new Div())
                .setHeader("Actions")
                .setKey("actions");

        VerticalLayout verticalLayout = new VerticalLayout();

        verticalLayout.add(addNewBtn, grid);
        verticalLayout.setAlignItems(Alignment.END);
        CustomAppLayout appLayout = new CustomAppLayout(auth, verticalLayout);
        add(appLayout);
        setHeight("100vh");
    }

    private void deleteCategory(Category category) {
        dataProvider.getItems().remove(category);
        categoryService.delete(category);
        dataProvider.refreshAll();
    }

    private void showAddDialog(Category category) {
        Dialog dialog = new Dialog();

        TextField title = new TextField("Title");
        title.setPlaceholder("Enter title");
        binder.forField(title)
                .bind(Category_.TITLE);

        TextField description = new TextField("Description");
        description.setPlaceholder("Enter description");
        HorizontalLayout fields = new HorizontalLayout();
        fields.add(title, description);

        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);
        Button save = new Button("Save", e -> {
            category.setTitle(title.getValue());
            category.setDescription(description.getValue());
            if (binder.writeBeanIfValid(category)) {
                addNewCategory(category);
                dialog.close();
            }
        });
        Button cancel = new Button("Cancel", e -> dialog.close());
        HorizontalLayout actions = new HorizontalLayout();
        actions.add(save, cancel);

        VerticalLayout content = new VerticalLayout();
        content.add(fields, actions);
        dialog.add(content);
        dialog.open();
        title.getElement().callFunction("focus");
    }

    private void showDeleteDialog(Category category) {
        Dialog dialog = new Dialog();
        Div contentText = new Div();
        contentText.setText("Are you sure, you want to delete category: \n" + category.getTitle() + "?");

        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);
        Button yes = new Button("Yes", e -> {
            deleteCategory(category);
            dialog.close();
        });
        Button no = new Button("No", e -> dialog.close());
        HorizontalLayout actions = new HorizontalLayout();
        actions.add(yes, no);

        VerticalLayout content = new VerticalLayout();
        content.add(contentText, actions);
        dialog.add(content);
        dialog.open();
    }

    private List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

    private void addNewCategory(Category category) {
        category = categoryService.create(category);
        dataProvider.getItems().add(category);
        dataProvider.refreshAll();
    }

    private void showEditDialog(Category category) {
        FormLayout formLayout = new FormLayout();

        Dialog dialog = new Dialog();
        TextField title = new TextField("Title");
        title.setValue(category.getTitle());
        binder.forField(title)
                .bind(Category_.TITLE);

        TextField description = new TextField("Description");
        description.setValue(category.getDescription());

        formLayout.add(title, description);

        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);
        Button save = new Button("Save", e -> {
            category.setTitle(title.getValue());
            category.setDescription(description.getValue());
            updateCategory(category);
            dialog.close();
        });
        Button cancel = new Button("Cancel", e -> dialog.close());
        HorizontalLayout actions = new HorizontalLayout();
        actions.add(save, cancel);

        VerticalLayout content = new VerticalLayout();
        content.add(formLayout, actions);
        dialog.add(content);
        dialog.open();
        title.getElement().callFunction("focus");
    }

    private void updateCategory(Category category) {
        categoryService.update(category);
        dataProvider.refreshAll();
    }

}
