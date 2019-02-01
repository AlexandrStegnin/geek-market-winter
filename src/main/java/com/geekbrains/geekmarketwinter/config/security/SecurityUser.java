package com.geekbrains.geekmarketwinter.config.security;

import com.geekbrains.geekmarketwinter.entites.Role;
import com.geekbrains.geekmarketwinter.entites.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;

public class SecurityUser extends User implements UserDetails {

    public SecurityUser(User user) {
        if (user != null) {
            this.setId(user.getId());
            this.setFirstName(user.getFirstName());
            this.setLastName(user.getLastName());
            this.setUserName(user.getUserName());
            this.setPassword(user.getPassword());
            this.setEmail(user.getEmail());
            this.setRoles(user.getRoles());
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        Collection<Role> userRoles = this.getRoles();
        userRoles.forEach(userRole -> {
            if (userRole != null) {
                SimpleGrantedAuthority authority = new SimpleGrantedAuthority(userRole.getName());
                authorities.add(authority);
            }
        });
        return authorities;
    }

    @Override
    public String getUsername() {
        return super.getUserName();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
