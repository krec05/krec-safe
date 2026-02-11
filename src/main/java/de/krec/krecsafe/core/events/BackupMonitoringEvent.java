package de.krec.krecsafe.core.events;

import de.krec.krecsafe.core.processing.BackupStatus;

import java.nio.file.Path;
import java.time.Instant;

/**
 * This event should be triggered when a file is uploaded to the cloud. In other words, the event is triggered during a backup process.
 *
 * @param backupTime This is the time at which the backup was performed.
 * @param sourceFile This is the directory path to the local source file that was uploaded.
 * @param targetFile This is the directory path in the cloud to the uploaded file.
 * @param checksum   This is the checksum of the uploaded file.
 * @param backupStatus This is the status that indicates whether the upload to the cloud was successful.
 */
public record BackupMonitoringEvent(Instant backupTime, Path sourceFile, Path targetFile,
									String checksum, BackupStatus backupStatus) {
}
