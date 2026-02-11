package de.krec.krecsafe.core.monitoring;

import de.krec.krecsafe.core.processing.BackupStatus;

import java.nio.file.Path;
import java.time.Instant;

/**
 * This record corresponds to a line in the CSV log. This log contains the most important
 * information about the files uploaded to the cloud. This makes it possible to determine
 * retrospectively when which file was uploaded.
 *
 * @param backupTime   This is the time at which the backup was performed.
 * @param sourceFile   This is the directory path to the local source file that was uploaded.
 * @param cloudFile    This is the directory path in the cloud to the uploaded file.
 * @param fileSize     This is the size of the file.
 * @param checksum     This is the directory path in the cloud to the uploaded file.
 * @param backupStatus This is the status that indicates whether the upload to the cloud was successful.
 */
public record CsvProtocolEntry(Instant backupTime, Path sourceFile, Path cloudFile,
							   long fileSize, String checksum,
							   BackupStatus backupStatus) {
}
