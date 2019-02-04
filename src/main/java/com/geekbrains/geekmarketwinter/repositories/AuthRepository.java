package com.geekbrains.geekmarketwinter.repositories;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthRepository {

    Authentication authenticate(String login, String password);

    void logout();
}
