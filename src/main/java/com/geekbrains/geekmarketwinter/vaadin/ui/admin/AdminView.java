package com.geekbrains.geekmarketwinter.vaadin.ui.admin;

import com.geekbrains.geekmarketwinter.repositories.AuthRepository;
import com.geekbrains.geekmarketwinter.vaadin.custom.CustomAppLayout;
import com.geekbrains.geekmarketwinter.vaadin.ui.manager.OrderView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

import static com.geekbrains.geekmarketwinter.config.support.Constants.ADMIN_PAGE;

@Route(ADMIN_PAGE)
@PageTitle("Administration")
@Theme(value = Material.class, variant = Material.LIGHT)
public class AdminView extends VerticalLayout {

    private CustomAppLayout appLayout;

    public AdminView(AuthRepository auth) {
        this.appLayout = new CustomAppLayout(auth);
        init();
    }

    private void init() {
        HorizontalLayout content = new HorizontalLayout();
        content.setAlignItems(Alignment.CENTER);
        content.setSizeFull();

        Image categoriesImg = createImage("images/sklad2.png", "Manage categories");
        Image statusesImg = createImage("images/dump-truck.png", "Manage statuses");
        Image usersImg = createImage("images/users-png.png", "Manage users");
        Image rolesImg = createImage("images/manage-roles.png", "Manage roles");
        Image productsImg = createImage("images/products.png", "Manage products");

        HorizontalLayout btnLayout = new HorizontalLayout();
        btnLayout.setSizeFull();
        btnLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        btnLayout.setAlignItems(Alignment.CENTER);
        btnLayout.setSpacing(true);

        Button categoriesBtn = new Button("Categories", categoriesImg, e -> goToPage(CategoryView.class));
        Button orderStatusesBtn = new Button("Statuses", statusesImg, e -> goToPage(OrderView.class));
        Button usersBtn = new Button(" Users", usersImg, e -> goToPage(UserView.class));
        Button rolesBtn = new Button("Roles", rolesImg, e -> goToPage(RoleView.class));
        Button productsBtn = new Button("Products", productsImg, e -> goToPage(ProductView.class));
        productsBtn.setId("products_btn");

        btnLayout.add(categoriesBtn, orderStatusesBtn, usersBtn, rolesBtn, productsBtn);
        appLayout.setContent(btnLayout);
        add(appLayout);
        setHeight("100vh");
    }

    private void goToPage(Class<? extends Component> clazz) {
        getUI().ifPresent(ui -> ui.navigate(clazz));
    }

    private Image createImage(String src, String alt) {
        Image image = new Image(src, alt);
        image.setHeight("150px");
        image.setWidth("150px");
        return image;
    }

}
