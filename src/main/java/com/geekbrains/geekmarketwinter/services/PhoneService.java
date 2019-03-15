package com.geekbrains.geekmarketwinter.services;

import com.geekbrains.geekmarketwinter.entites.Phone;
import com.geekbrains.geekmarketwinter.entites.User;
import com.geekbrains.geekmarketwinter.repositories.PhoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author Alexandr Stegnin
 */

@Service
public class PhoneService {

    private final PhoneRepository phoneRepository;

    @Autowired
    public PhoneService(PhoneRepository phoneRepository) {
        this.phoneRepository = phoneRepository;
    }

    public Set<Phone> getUserPhones(User user) {
        return phoneRepository.findByUserId(user.getId());
    }

    public Phone save(Phone phone) {
        return phoneRepository.save(phone);
    }

    public void delete(Phone phone) {
        phoneRepository.delete(phone);
    }

}
