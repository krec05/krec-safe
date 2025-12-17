package de.krec.krecsafe.core.cloud;

import java.nio.file.Path;
import java.time.LocalDateTime;

public interface CloudClient {

    void backupFile(Path hostFile, Path cloudFile);

    LocalDateTime getLastBackupTime(Path cloudFile);
}
