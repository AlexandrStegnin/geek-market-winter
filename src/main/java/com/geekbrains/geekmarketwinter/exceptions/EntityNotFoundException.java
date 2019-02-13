package com.geekbrains.geekmarketwinter.exceptions;

import com.geekbrains.geekmarketwinter.entites.FileStorageEntity;

/**
 * @author stegnin
 */
public class EntityNotFoundException extends FileStorageException {

    public EntityNotFoundException(Class<? extends FileStorageEntity> entity) {
        super("Entity not found: " + entity.getSimpleName());
    }
}
