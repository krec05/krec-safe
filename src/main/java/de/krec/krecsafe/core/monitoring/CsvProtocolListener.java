package de.krec.krecsafe.core.monitoring;

import de.krec.krecsafe.core.events.BackupMonitoringEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This listener responds to the {@link BackupMonitoringEvent} event. The information from the
 * event is transferred to a {@link CsvProtocolEntry} and written to the {@link CsvProtocolQueue}. This
 * queue controls writing to the CSV log file.
 */
@Component
public class CsvProtocolListener {

	private final CsvProtocolQueue     csvProtocolQueue;

	@Autowired
	public CsvProtocolListener(CsvProtocolQueue csvProtocolQueue) {
		this.csvProtocolQueue = csvProtocolQueue;
	}

	@EventListener
	public void onBackupMonitoringEvent(BackupMonitoringEvent backupRecordEvent)
	throws IOException {
		Path sourceFile = backupRecordEvent.sourceFile();
		CsvProtocolEntry csvProtocolEntry =
				new CsvProtocolEntry(backupRecordEvent.backupTime(), sourceFile, backupRecordEvent.targetFile(),
									 Files.size(sourceFile),
									 backupRecordEvent.checksum(), backupRecordEvent.backupStatus());

		csvProtocolQueue.offer(csvProtocolEntry);
	}
}
