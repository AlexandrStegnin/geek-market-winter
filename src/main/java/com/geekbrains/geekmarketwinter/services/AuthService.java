package com.geekbrains.geekmarketwinter.services;

import com.geekbrains.geekmarketwinter.repositories.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuthService extends AbstractRepository implements AuthRepository {

    private DaoAuthenticationProvider daoAuthenticationProvider;

    @Autowired
    public AuthService(DaoAuthenticationProvider daoAuthenticationProvider) {
        this.daoAuthenticationProvider = daoAuthenticationProvider;
    }

    @Override
    public Authentication authenticate(String login, String password) {
        Authentication auth = new UsernamePasswordAuthenticationToken(login, password);
        Authentication authentication = daoAuthenticationProvider.authenticate(auth);
        if (authentication.isAuthenticated()) {
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } else {
            SecurityContextHolder.clearContext();
        }
        return authentication;
    }

    @Override
    public void logout() {
        SecurityContextHolder.clearContext();
    }
}
