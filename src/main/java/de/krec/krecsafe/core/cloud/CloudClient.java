package de.krec.krecsafe.core.cloud;

import java.nio.file.Path;
import java.time.LocalDateTime;

public interface CloudClient {

	/**
	 * @param encryptedFile the file to be backed up.
	 * @param cloudFile     the path in the cloud where to upload.
	 * @return the checksum of the uploaded file
	 */
	String backupFile(Path encryptedFile, Path cloudFile);

	LocalDateTime getLastBackupTime(Path cloudFile);
}
