package com.geekbrains.geekmarketwinter.services;

import com.geekbrains.geekmarketwinter.entites.Role;
import com.geekbrains.geekmarketwinter.repositories.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.geekbrains.geekmarketwinter.config.support.Constants.ROLE_PREFIX;

@Service
public class RoleService {

    private final RoleRepository roleRepo;

    @Autowired
    public RoleService(RoleRepository roleRepo) {
        this.roleRepo = roleRepo;
    }

    public List<Role> getAllRoles() {
        return roleRepo.findAll();
    }

    public Role getRoleByName(String roleName) {
        return roleRepo.findOneByName(roleName);
    }

    public Role save(Role role) {
        return roleRepo.save(role);
    }

    public Role update(Role role) {
        return save(role);
    }

    public void delete(Role role) {
        roleRepo.delete(role);
    }

    public Role findById(Long id) {
        return roleRepo.getOne(id);
    }

    public Role create(Role role) {
        if (!role.getName().startsWith(ROLE_PREFIX))
            role.setName(ROLE_PREFIX + role.getName().toUpperCase());
        return save(role);
    }
}
