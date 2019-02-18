package com.geekbrains.geekmarketwinter.maintenance.actions;

import com.geekbrains.geekmarketwinter.providers.FileStorageProvider;
import com.geekbrains.geekmarketwinter.services.FileAssetService;
import org.springframework.stereotype.Service;

/**
 * @author stegnin
 */

@Service
public class FileStorageActuator implements MaintenanceAction {

    private final FileAssetService fileAssetService;
    private final FileStorageProvider fileStorageProvider;

    public FileStorageActuator(FileAssetService fileAssetService, FileStorageProvider fileStorageProvider) {
        this.fileAssetService = fileAssetService;
        this.fileStorageProvider = fileStorageProvider;
    }

    @Override
    public void execute() {
        fileStorageProvider
                .getFiles()
                .stream()
                .filter(file -> fileAssetService.findByFileName(file.getName()) == null)
                .forEach(fileStorageProvider::deleteFile);
    }

}
