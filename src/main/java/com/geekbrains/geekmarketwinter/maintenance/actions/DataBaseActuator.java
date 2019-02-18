package com.geekbrains.geekmarketwinter.maintenance.actions;

import com.geekbrains.geekmarketwinter.providers.FileStorageProvider;
import com.geekbrains.geekmarketwinter.services.FileAssetService;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

/**
 * @author stegnin
 */

@Service
public class DataBaseActuator implements MaintenanceAction {

    private final FileAssetService fileAssetService;
    private final FileStorageProvider fileStorageProvider;

    public DataBaseActuator(FileAssetService fileAssetService, FileStorageProvider fileStorageProvider) {
        this.fileAssetService = fileAssetService;
        this.fileStorageProvider = fileStorageProvider;
    }

    @Override
    public void execute() {
        removeAssetsWithoutFiles();
    }

    private void removeAssetsWithoutFiles() {
        fileAssetService.deleteFileAssets(fileAssetService
                .getFileAssets()
                .stream()
                .filter(fileAsset -> !fileStorageProvider.getFile(fileAsset.getFileName()).exists())
                .collect(Collectors.toList()));
    }

}
