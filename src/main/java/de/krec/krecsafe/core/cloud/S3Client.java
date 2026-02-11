package de.krec.krecsafe.core.cloud;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.time.LocalDateTime;

@Service
@ConditionalOnProperty(name = "de.krec.cloudprovider", havingValue = "s3")
@ConditionalOnMissingBean(CloudClient.class)
public class S3Client implements CloudClient {

    @Override
    public String backupFile(Path encryptedFile, Path cloudFile) {
        // TODO cloud magic
		return null;
    }

    @Override
    public LocalDateTime getLastBackupTime(Path cloudFile) {
        // TODO cloud magic
        return null;
    }
}
