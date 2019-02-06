package com.geekbrains.geekmarketwinter.vaadin.ui.admin;

import com.geekbrains.geekmarketwinter.entites.Category;
import com.geekbrains.geekmarketwinter.services.AuthService;
import com.geekbrains.geekmarketwinter.services.CategoryService;
import com.geekbrains.geekmarketwinter.vaadin.custom.CustomAppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
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

    private CategoryService categoryService;
    private Grid<Category> grid;
    private AuthService auth;
    private ListDataProvider<Category> dataProvider;

    public CategoryView(CategoryService categoryService, AuthService auth) {
        this.categoryService = categoryService;
        this.dataProvider = new ListDataProvider<>(getAllCategories());
        this.grid = new Grid<>();
        this.auth = auth;
        init();
    }

    private void init() {

        grid.setDataProvider(dataProvider);

        grid.addColumn(Category::getId)
                .setHeader("ID")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setWidth("10px")
                .setFlexGrow(0);

        Grid.Column<Category> titleColumn = grid.addColumn(Category::getTitle)
                .setHeader("Title")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        Grid.Column<Category> descriptionColumn = grid.addColumn(Category::getDescription)
                .setHeader("Description")
                .setTextAlign(ColumnTextAlign.CENTER)
                .setFlexGrow(1);

        Binder<Category> binder = new Binder<>(Category.class);
        Editor<Category> editor = grid.getEditor();
        editor.setBinder(binder);
        editor.setBuffered(true);

        TextField titleField = new TextField();
        binder.bind(titleField, "title");
        titleColumn.setEditorComponent(titleField);
        titleColumn.setTextAlign(ColumnTextAlign.CENTER);
        titleField.setSizeFull();

        TextField descriptionField = new TextField();
        binder.bind(descriptionField, "description");
        descriptionColumn.setEditorComponent(descriptionField);
        descriptionColumn.setTextAlign(ColumnTextAlign.CENTER);
        descriptionField.setSizeFull();

        Grid.Column<Category> editorColumn = grid.addComponentColumn(category -> {
            Button edit = new Button("Edit");
            edit.addClassName("edit");
            edit.addClickListener(e -> editor.editItem(category));
            return edit;
        });

        Button save = new Button("Save", e -> editor.save());
        save.addClassName("save");

        Button cancel = new Button("Cancel", e -> editor.cancel());
        cancel.addClassName("cancel");

        grid.getElement().addEventListener("keyup", event -> editor.cancel())
                .setFilter("event.key === 'Escape' || event.key === 'Esc'");

        Div buttons = new Div(save, cancel);
        editorColumn.setEditorComponent(buttons);

        Notification message = new Notification("", 3000, Notification.Position.TOP_END);

        CustomAppLayout appLayout = new CustomAppLayout(auth, grid);
        add(appLayout);
        setHeight("100vh");

        editor.addSaveListener(event -> {
            Category updatedCategory = event.getItem();
            binder.writeBeanIfValid(updatedCategory);
            grid.getDataProvider().refreshItem(updatedCategory); // для refreshItem необходимо переопределить equal & hashCode
            categoryService.update(updatedCategory);
            message.setText("Category successful updated: " +
                    "Title = " + updatedCategory.getTitle() + ", " +
                    "Description = " + updatedCategory.getDescription());
            message.open();
        });
    }

    private List<Category> getAllCategories() {
        return categoryService.getAllCategories();
    }

}
