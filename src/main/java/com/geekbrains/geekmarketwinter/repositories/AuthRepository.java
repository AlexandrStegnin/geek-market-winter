package com.geekbrains.geekmarketwinter.repositories;

import org.springframework.security.core.Authentication;

public interface AuthRepository {

    Authentication authenticate(String login, String password);

    void logout();
}
