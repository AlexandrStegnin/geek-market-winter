package com.geekbrains.geekmarketwinter.vaadin.ui;

import com.geekbrains.geekmarketwinter.repositories.AuthRepository;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.spring.annotation.SpringComponent;
import org.springframework.beans.factory.annotation.Autowired;

@SpringComponent
@Route("")
public class MainView extends VerticalLayout {

    private final AuthRepository authRepository;

    @Autowired
    public MainView(AuthRepository authRepository) {
        Button button = new Button("Click me",
                event -> {
                    Notification.show("Clicked!");
                    logout();
                });
        add(button);
        this.authRepository = authRepository;
    }

    private void logout() {
        authRepository.logout();
    }
}
