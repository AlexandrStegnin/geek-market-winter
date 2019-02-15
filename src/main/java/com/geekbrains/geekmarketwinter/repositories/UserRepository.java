package com.geekbrains.geekmarketwinter.repositories;

import com.geekbrains.geekmarketwinter.entites.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {

    User findOneByUserName(String userName);

    List<User> findAll();

    Optional<User> findById(Long id);

}
