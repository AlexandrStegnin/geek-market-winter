package com.geekbrains.geekmarketwinter.vaadin.ui.admin;

import com.geekbrains.geekmarketwinter.repositories.AuthRepository;
import com.geekbrains.geekmarketwinter.vaadin.custom.CustomAppLayout;
import com.geekbrains.geekmarketwinter.vaadin.ui.manager.OrderView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.BoxSizing;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;

import static com.geekbrains.geekmarketwinter.config.support.Constants.ADMIN_PAGE;

@Route(ADMIN_PAGE)
@Theme(value = Material.class, variant = Material.DARK)
public class AdminView extends VerticalLayout {

    // TODO: 2019-02-06 Сделать добавление категорий/статусов
    
    private AuthRepository auth;

    public AdminView(AuthRepository auth) {
        this.auth = auth;
        init();
    }

    private void init() {
        HorizontalLayout content = new HorizontalLayout();
        content.setAlignItems(Alignment.CENTER);
        content.setAlignSelf(Alignment.CENTER);
        content.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        content.setSpacing(true);
        content.setVerticalComponentAlignment(Alignment.CENTER);
        content.setBoxSizing(BoxSizing.BORDER_BOX);
        content.setJustifyContentMode(JustifyContentMode.CENTER);
        content.setSizeFull();

        Image categoriesImg = new Image("images/sklad2.png", "Manage categories");
        categoriesImg.setHeight("150px");
        categoriesImg.setWidth("150px");

        Image statusesImg = new Image("images/dump-truck.png", "Manage delivery");
        statusesImg.setHeight("150px");
        statusesImg.setWidth("150px");

        HorizontalLayout btnLayout = new HorizontalLayout();
        btnLayout.setSizeFull();
        btnLayout.setJustifyContentMode(JustifyContentMode.CENTER);
        btnLayout.setVerticalComponentAlignment(Alignment.CENTER);
        btnLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        btnLayout.setAlignSelf(Alignment.CENTER);
        btnLayout.setAlignItems(Alignment.CENTER);
        btnLayout.setSpacing(true);

        Button categoriesBtn = new Button("View categories", categoriesImg, e -> goToPage(CategoryView.class));
        Button orderStatusesBtn = new Button("View order statuses", statusesImg, e -> goToPage(OrderView.class));
        btnLayout.add(categoriesBtn, orderStatusesBtn);
        CustomAppLayout appLayout = new CustomAppLayout(auth, btnLayout);
        add(appLayout);
        setHeight("100vh");
    }

    private void goToPage(Class<? extends Component> clazz) {
        getUI().ifPresent(ui -> ui.navigate(clazz));
    }

}
