package com.geekbrains.geekmarketwinter.config.security;

import com.geekbrains.geekmarketwinter.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SecurityUserHelper {

    private final UserRepository userRepo;

    @Autowired
    public SecurityUserHelper(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    public String getUserSyncFolder() {
        return "../temp/";
//        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        if (principal instanceof SecurityUser) {
//            return ((SecurityUser) principal).getSettings().getSyncFolder();
//        } else {
//            return userRepo.findByLogin(principal.toString()).getSettings().getSyncFolder();
//        }
    }
}
