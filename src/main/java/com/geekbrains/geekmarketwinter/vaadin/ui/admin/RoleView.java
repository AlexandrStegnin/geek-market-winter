package com.geekbrains.geekmarketwinter.vaadin.ui.admin;

import com.geekbrains.geekmarketwinter.entites.Role;
import com.geekbrains.geekmarketwinter.entites.Role_;
import com.geekbrains.geekmarketwinter.services.AuthService;
import com.geekbrains.geekmarketwinter.services.RoleService;
import com.geekbrains.geekmarketwinter.vaadin.custom.CustomAppLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.material.Material;
import org.vaadin.crudui.crud.CrudOperation;
import org.vaadin.crudui.crud.impl.GridCrud;

import static com.geekbrains.geekmarketwinter.config.support.Constants.ADMIN_ROLES_PAGE;

@PageTitle("Roles")
@Route(ADMIN_ROLES_PAGE)
@Theme(value = Material.class, variant = Material.DARK)
public class RoleView extends VerticalLayout {

    private RoleService roleService;
    private GridCrud<Role> grid;
    private AuthService auth;

    public RoleView(RoleService roleService, AuthService auth) {
        this.roleService = roleService;
        this.grid = new GridCrud<>(Role.class);
        this.auth = auth;
        init();
    }

    private void init() {
        grid.getCrudFormFactory().setUseBeanValidation(true);

        grid.setFindAllOperation(() -> roleService.getAllRoles());
        grid.setAddOperation(roleService::add);
        grid.setUpdateOperation(roleService::update);
        grid.setDeleteOperation(roleService::delete);

        grid.getCrudFormFactory().setVisibleProperties(CrudOperation.ADD, Role_.NAME);
        grid.getCrudFormFactory().setVisibleProperties(CrudOperation.READ, Role_.NAME);
        grid.getCrudFormFactory().setVisibleProperties(CrudOperation.UPDATE, Role_.NAME);
        grid.getCrudFormFactory().setVisibleProperties(CrudOperation.DELETE, Role_.NAME);

        CustomAppLayout appLayout = new CustomAppLayout(auth, grid);
        add(appLayout);
    }

}
