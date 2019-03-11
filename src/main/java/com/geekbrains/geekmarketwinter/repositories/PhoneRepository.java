package com.geekbrains.geekmarketwinter.repositories;

import com.geekbrains.geekmarketwinter.entites.Phone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Set;

/**
 * @author Alexandr Stegnin
 */

@Repository
public interface PhoneRepository extends JpaRepository<Phone, Long> {

    Set<Phone> findByUserId(Long userId);

}
