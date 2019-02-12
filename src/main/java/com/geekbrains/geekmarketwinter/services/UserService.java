package com.geekbrains.geekmarketwinter.services;

import com.geekbrains.geekmarketwinter.entites.SystemUser;
import com.geekbrains.geekmarketwinter.entites.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    User findByUserName(String userName);

    void save(SystemUser systemUser);

    User create(User user);

    List<User> findAll();

    User update(User user);

    void delete(User user);
}
