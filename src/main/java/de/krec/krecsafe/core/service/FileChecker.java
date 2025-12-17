package de.krec.krecsafe.core.service;

import de.krec.krecsafe.config.BackupPathProperties;
import de.krec.krecsafe.core.cloud.CloudService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.ZoneId;

@Service
public class FileChecker {

    private static final Logger LOG = LoggerFactory.getLogger(FileChecker.class);

    private final CloudService cloudService;

    @Autowired
    public FileChecker(CloudService cloudService) {
        this.cloudService = cloudService;
    }

    public boolean isBackupNeeded(BackupPathProperties.PathEntry pathEntry, Path file) throws IOException {
        LOG.info("Checking if backup needed for {}", file);

        FileTime hostFileTime = Files.getLastModifiedTime(file);
        LocalDateTime lastModifiedHostFile = LocalDateTime.ofInstant(hostFileTime.toInstant(), ZoneId.systemDefault());
        LOG.debug("Last modified of host file: {}", lastModifiedHostFile);

        LocalDateTime lastModifiedCloudFile = cloudService.getUploadDate(pathEntry, file);
        LOG.debug("Last modified cloud file: {}", lastModifiedCloudFile);

        return !lastModifiedCloudFile.isAfter(lastModifiedHostFile);
    }
}
