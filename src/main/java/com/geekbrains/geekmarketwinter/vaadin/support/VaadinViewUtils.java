package com.geekbrains.geekmarketwinter.vaadin.support;

import com.geekbrains.geekmarketwinter.entites.Category;
import com.geekbrains.geekmarketwinter.entites.Product;
import com.geekbrains.geekmarketwinter.entites.Role;
import com.geekbrains.geekmarketwinter.entites.User;
import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.ListDataProvider;
import com.vaadin.flow.data.value.ValueChangeMode;

import java.util.HashSet;
import java.util.List;

public class VaadinViewUtils {

    public static Div makeEditorColumnActions(ComponentEventListener<ClickEvent<Button>> editListener,
                                              ComponentEventListener<ClickEvent<Button>> deleteListener) {
        Div actions = new Div();
        Button edit = new Button("", VaadinIcon.EDIT.create());
        edit.addClickListener(editListener);
        Button delete = new Button("", VaadinIcon.TRASH.create());
        delete.addClickListener(deleteListener);
        actions.add(edit, delete);
        return actions;
    }

    public static Dialog initDialog() {
        Dialog dialog = new Dialog();
        dialog.setCloseOnEsc(false);
        dialog.setCloseOnOutsideClick(false);
        return dialog;
    }

    public static Div makeUserRolesDiv(User user, List<Role> availableRoles) {
        Div checkBoxDiv = new Div();
        Div label = new Div();
        label.setText("Choose roles");
        label.getStyle().set("margin", "10px 0");
        checkBoxDiv.add(label);
        Div contentDiv = new Div();
        if (user.getRoles() == null) user.setRoles(new HashSet<>());
        availableRoles.forEach(role -> {
            Checkbox checkbox = new Checkbox(
                    role.getHumanized(),
                    user.getRoles().contains(role));
            checkbox.addValueChangeListener(e -> {
                String roleName = e.getSource().getElement().getText();
                Role userRole = availableRoles.stream()
                        .filter(r -> r.getHumanized().equalsIgnoreCase(roleName))
                        .findAny().orElse(null);
                if (e.getValue()) {
                    user.getRoles().add(userRole);
                } else {
                    user.getRoles().remove(userRole);
                }
            });
            contentDiv.add(checkbox);
        });
        checkBoxDiv.add(contentDiv);
        return checkBoxDiv;
    }

    public static HorizontalLayout getProductFilterForm(ListDataProvider<Product> dataProvider, List<Category> categories) {
        HorizontalLayout formLayout = new HorizontalLayout();
        TextField title = new TextField("Search by title");
        title.setValueChangeMode(ValueChangeMode.EAGER);
        title.setPlaceholder("Search by title");
        title.addValueChangeListener(e -> {
            dataProvider.setFilter(product -> product.getTitle().toLowerCase().contains(e.getValue().toLowerCase()));
            dataProvider.refreshAll();
        });
        ComboBox<Category> comboBox = new ComboBox<>("Search by category");
        comboBox.setPlaceholder("Search by category");
        comboBox.setItemLabelGenerator(Category::getTitle);
        comboBox.setItems(categories);
        comboBox.addValueChangeListener(event -> {
            if (event.getValue() != null) {
                dataProvider.setFilter(value -> value.getCategory().equals(event.getValue()));
                dataProvider.refreshAll();
            } else {
                dataProvider.clearFilters();
                dataProvider.refreshAll();
            }
        });
        formLayout.add(title, comboBox);
        formLayout.setPadding(true);
        formLayout.setSpacing(true);
        formLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        return formLayout;
    }

}
