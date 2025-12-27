package de.krec.krecsafe.core.cloud;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.time.LocalDateTime;

@Service
@ConditionalOnProperty(name = "de.krec.cloudprovider", havingValue = "mock")
public class CloudServiceMock implements CloudClient {

    @Override
    public void backupFile(Path encryptedFile, Path cloudFile) {

    }

    @Override
    public LocalDateTime getLastBackupTime(Path cloudFile) {
        return LocalDateTime.of(2018, 8, 1, 8, 17);
    }
}
