package com.geekbrains.geekmarketwinter.maintenance.actions;

import com.geekbrains.geekmarketwinter.services.FileAssetService;
import org.springframework.stereotype.Service;

/**
 * @author stegnin
 */

@Service
public class ExpiredFilesActuator implements MaintenanceAction {

    private final FileAssetService fileAssetService;

    public ExpiredFilesActuator(FileAssetService fileAssetService) {
        this.fileAssetService = fileAssetService;
    }

    @Override
    public void execute() {
        fileAssetService.findExpiredFiles()
                .forEach(fileAsset -> fileAssetService.deleteFileAsset(fileAsset.getId()));
    }

}
