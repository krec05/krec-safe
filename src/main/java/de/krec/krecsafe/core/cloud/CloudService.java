package de.krec.krecsafe.core.cloud;

import de.krec.krecsafe.config.BackupPathProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.time.LocalDateTime;

@Service
public class CloudService {

    private final CloudClient cloudClient;

    @Autowired
    public CloudService(CloudClient cloudClient) {
        this.cloudClient = cloudClient;
    }

    public void backupFile(BackupPathProperties.PathEntry pathEntry, Path hostFile) {
        Path cloudFile = determineCloudPath(pathEntry, hostFile);
        cloudClient.backupFile(hostFile, cloudFile);
    }

    public LocalDateTime getUploadDate(BackupPathProperties.PathEntry pathEntry, Path hostFile) {
        Path cloudFile = determineCloudPath(pathEntry, hostFile);
        return cloudClient.getLastBackupTime(cloudFile);
    }

    private Path determineCloudPath(BackupPathProperties.PathEntry pathEntry, Path hostFile) {
        return Path.of(pathEntry.getClouddir()).resolve(pathEntry.getPath().relativize(hostFile));
    }
}
