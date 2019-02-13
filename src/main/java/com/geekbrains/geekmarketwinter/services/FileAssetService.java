package com.geekbrains.geekmarketwinter.services;

import com.geekbrains.geekmarketwinter.entites.FileAsset;
import com.geekbrains.geekmarketwinter.exceptions.EntityNotFoundException;
import com.geekbrains.geekmarketwinter.exceptions.InvalidEntityException;
import com.geekbrains.geekmarketwinter.providers.ConfigurationProvider;
import com.geekbrains.geekmarketwinter.providers.FileStorageProvider;
import com.geekbrains.geekmarketwinter.repositories.FileAssetRepository;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static com.geekbrains.geekmarketwinter.utils.EncryptionUtils.getFileHash;

/**
 * @author stegnin
 */

@Service
public class FileAssetService implements FileStorageService<FileAsset> {

    private final FileAssetRepository fileAssetRepository;
    private final FileStorageProvider fileStorageProvider;
    private final ConfigurationProvider configurationProvider;

    public FileAssetService(FileAssetRepository fileAssetRepository,
                            FileStorageProvider fileStorageProvider,
                            ConfigurationProvider configurationProvider) {
        this.fileAssetRepository = fileAssetRepository;
        this.fileStorageProvider = fileStorageProvider;
        this.configurationProvider = configurationProvider;
    }

    public FileAsset createFileAsset(FileAsset fileAsset, File content) {

        return fileAssetRepository.findByHash(getFileHash(content)).orElseGet(() -> {
            validateFileAsset(fileAsset);
            validateFileContent(content);

            File savedContent = fileStorageProvider.uploadFile(content);

            try {
                return persist(fileAsset, savedContent);
            } catch (RuntimeException ex) {
                fileStorageProvider.deleteFile(savedContent);
                throw ex;
            }
        });

    }

    public List<FileAsset> getFileAssets() {
        return fileAssetRepository.findAll();
    }

    public FileAsset findByFileName(String fileName) {
        return fileAssetRepository
                .findByFileName(fileName)
                .orElse(null);
    }

    public List<FileAsset> findExpiredFiles() {
        return fileAssetRepository.findByExpiringDateTimeBefore(LocalDateTime.now());
    }

    public void deleteFileAssets(List<FileAsset> assets) {
        fileAssetRepository.deleteAll(assets);
    }

    public FileAsset getFileAsset(Long id) {
        return fileAssetRepository
                .findById(id)
                .filter(fileAsset -> fileAsset
                        .getHash()
                        .equalsIgnoreCase(getFileHash(fileStorageProvider.getFile(fileAsset.getFileName()))))
                .orElseThrow(() -> new EntityNotFoundException(FileAsset.class));
    }

    public FileAsset getFileAsset(String hash) {
        return fileAssetRepository
                .findByHash(hash)
                .filter(fileAsset -> fileAsset
                        .getHash()
                        .equalsIgnoreCase(getFileHash(fileStorageProvider.getFile(fileAsset.getFileName()))))
                .orElseThrow(() -> new EntityNotFoundException(FileAsset.class));
    }

    public void deleteFileAsset(Long id) {
        FileAsset fileAsset = getFileAsset(id);
        fileStorageProvider.deleteFile(fileStorageProvider.getFile(fileAsset.getFileName()));
        fileAssetRepository.delete(fileAsset);
    }

    private FileAsset persist(FileAsset entity, File savedFile) {

        entity = setCreatedDateTime(entity);
        entity = setExpirationDateTime(entity);
        entity = setHash(entity, savedFile);
        entity = setFileName(entity, savedFile);
        return fileAssetRepository.save(entity);
    }

    private FileAsset setFileName(FileAsset entity, File savedFile) {
        entity.setFileName(savedFile.getName());
        return entity;
    }

    private FileAsset setHash(FileAsset entity, File savedFile) {
        entity.setHash(getFileHash(savedFile));
        return entity;
    }

    private FileAsset setExpirationDateTime(FileAsset entity) {

        validateFileAsset(entity);

        if (null == entity.getExpiringDateTime()) {
            return setDefaultExpirationDate(entity);
        }

        if (entity.getExpiringDateTime().isBefore(LocalDateTime.now())) {
            return setDefaultExpirationDate(entity);
        }

        if (Duration.between(LocalDateTime.now(), entity.getExpiringDateTime()).toDays() > configurationProvider.getMaxExpirationDays()) {
            return setDefaultExpirationDate(entity);
        }

        return entity;
    }

    private FileAsset setCreatedDateTime(FileAsset entity) {

        validateFileAsset(entity);

        entity.setCreatedDateTime(LocalDateTime.now());
        return entity;
    }

    private FileAsset setDefaultExpirationDate(FileAsset entity) {
        entity.setExpiringDateTime(LocalDateTime.now().plusDays(configurationProvider.getExpirationDays()));
        return entity;
    }

    private void validateFileAsset(FileAsset asset) {
        if (null == asset) {
            throw new InvalidEntityException(FileAsset.class, "No file asset presented.");
        }

        if (null == asset.getName()) {
            throw new InvalidEntityException(FileAsset.class, "No file name presented.");
        }
    }

    private void validateFileContent(File content) {
        if (null == content) {
            throw new InvalidEntityException(FileAsset.class, "No file presented.");
        }

        if (0 == content.length()) {
            throw new InvalidEntityException(FileAsset.class, "File is empty.");
        }
    }

}
