package de.krec.krecsafe.core.cloud;

import java.nio.file.Path;
import java.time.LocalDateTime;

public interface CloudClient {

    void backupFile(Path encryptedFile, Path cloudFile);

    LocalDateTime getLastBackupTime(Path cloudFile);
}
