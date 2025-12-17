package de.krec.krecsafe.core.cloud;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.time.LocalDateTime;

@Service
@ConditionalOnProperty(name = "de.krec.cloudprovider", havingValue = "scaleway")
public class ScalewayClient implements CloudClient {

    @Override
    public void backupFile(Path hostFile, Path cloudFile) {
        // TODO cloud magic
    }

    @Override
    public LocalDateTime getLastBackupTime(Path cloudFile) {
        // TODO cloud magic
        return null;
    }
}
