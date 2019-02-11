package com.geekbrains.geekmarketwinter.vaadin.ui.admin;

import com.geekbrains.geekmarketwinter.entites.Category;
import com.geekbrains.geekmarketwinter.services.AuthService;
import com.geekbrains.geekmarketwinter.services.CategoryService;
import com.geekbrains.geekmarketwinter.vaadin.custom.CustomAppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.ColumnTextAlign;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.editor.Editor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
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
    private Button addNewBtn;

    public CategoryView(CategoryService categoryService, AuthService auth) {
        this.categoryService = categoryService;
        this.dataProvider = new ListDataProvider<>(getAllCategories());
        this.grid = new Grid<>();
        this.auth = auth;
        this.addNewBtn = new Button("Add new category", e -> showDialog());
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
            Div actions = new Div();
            Button edit = new Button("", VaadinIcon.EDIT.create());
            edit.addClickListener(e -> editor.editItem(category));
            Button delete = new Button("", VaadinIcon.TRASH.create());
            delete.addClickListener(e -> showDeleteDialog(category));
            actions.add(edit, delete);
            return actions;
        });
        editorColumn.setHeader("Actions");
        editorColumn.setKey("actions");

        Button save = new Button("Save", e -> editor.save());
        save.addClassName("save");

        Button cancel = new Button("Cancel", e -> editor.cancel());
        cancel.addClassName("cancel");

        grid.getElement().addEventListener("keyup", event -> editor.cancel())
                .setFilter("event.key === 'Escape' || event.key === 'Esc'");

        Div buttons = new Div(save, cancel);
        editorColumn.setEditorComponent(buttons);

        Notification message = new Notification("", 3000, Notification.Position.TOP_END);

        VerticalLayout verticalLayout = new VerticalLayout();

        verticalLayout.add(addNewBtn, grid);
        verticalLayout.setAlignItems(Alignment.END);
        CustomAppLayout appLayout = new CustomAppLayout(auth, verticalLayout);
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

        grid.getColumnByKey("actions").setTextAlign(ColumnTextAlign.CENTER);

    }

    private void deleteCategory(Category category) {
        dataProvider.getItems().remove(category);
        categoryService.delete(category);
        dataProvider.refreshAll();
    }

    private void showDialog() {
        Dialog dialog = new Dialog();
        TextField title = new TextField("Title");
        title.setPlaceholder("Enter title");
        TextField description = new TextField("Description");
        description.setPlaceholder("Enter description");
        HorizontalLayout fields = new HorizontalLayout();
        fields.add(title, description);

        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);
        Button save = new Button("Save", e -> {
            Category category = new Category();
            category.setTitle(title.getValue());
            category.setDescription(description.getValue());
            addNewCategory(category);
            dialog.close();
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

}
