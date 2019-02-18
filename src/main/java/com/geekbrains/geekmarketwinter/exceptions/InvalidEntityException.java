package com.geekbrains.geekmarketwinter.exceptions;

import com.geekbrains.geekmarketwinter.entites.FileStorageEntity;

/**
 * @author stegnin
 */
public class InvalidEntityException extends FileStorageException {

    public InvalidEntityException(Class<? extends FileStorageEntity> entity) {
        this(entity, "");
    }

    public InvalidEntityException(Class<? extends FileStorageEntity> entity, String message) {
        super("Invalid entity: " + entity.getSimpleName() + " caused by: " + message);
    }
}
