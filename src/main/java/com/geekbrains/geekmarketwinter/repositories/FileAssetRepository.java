package com.geekbrains.geekmarketwinter.repositories;

import com.geekbrains.geekmarketwinter.entites.FileAsset;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * @author stegnin
 */

@Repository
public interface FileAssetRepository extends FileStorageRepository<FileAsset> {

    Optional<FileAsset> findByHash(String hash);

    @NotNull Optional<FileAsset> findById(@NotNull Long id);

    Optional<FileAsset> findByFileName(String fileName);

    List<FileAsset> findByExpiringDateTimeBefore(LocalDateTime time);

}
