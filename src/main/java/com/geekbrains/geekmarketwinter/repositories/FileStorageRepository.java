package com.geekbrains.geekmarketwinter.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

/**
 * @author stegnin
 */

@NoRepositoryBean
public interface FileStorageRepository<T> extends JpaRepository<T, Long> {
}
