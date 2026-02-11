package de.krec.krecsafe.core.monitoring;

import de.krec.krecsafe.config.MonitoringProperties;
import de.krec.krecsafe.core.events.BackupMonitoringEvent;
import de.krec.krecsafe.core.processing.BackupStatus;
import de.krec.krecsafe.core.service.ActiveProfileProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mockito;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

class CsvProtocolTest {

	@TempDir private Path tempDir;

	@Test
	public void testHumanReadableBytes() {
		Map<Long, String> inOutMap = new HashMap<>();
		inOutMap.put(965L, "965 B");
		inOutMap.put(1_024L, "1.00 KB");
		inOutMap.put(1_573_000L, "1.50 MB");
		inOutMap.put(2_523_300_000L, "2.35 GB");
		inOutMap.put(3_925_300_000_000L, "3.57 TB");
		inOutMap.put(5_134_100_000_100_000L, "4.56 PB");

		for (Long size : inOutMap.keySet()) {
			String result = CsvProtocolWriterService.humanReadableBytes(size);
			Assertions.assertEquals(inOutMap.get(size), result);
		}
	}

	@Test
	public void testWriteCsvProtocolEntries()
	throws InterruptedException, IOException, ExecutionException {
		String profile = "CsvProtocolTest";
		Path protocolPath = tempDir.resolve("protocol.csv");

		MonitoringProperties monitoringProperties =
				Mockito.mock(MonitoringProperties.class);
		Mockito.when(monitoringProperties.getCsvProtocolPath())
			   .thenReturn(protocolPath.toString());

		ActiveProfileProvider activeProfileProvider =
				Mockito.mock(ActiveProfileProvider.class);
		Mockito.when(activeProfileProvider.getActiveProfile()).thenReturn(profile);

		CsvProtocolQueue queue = new CsvProtocolQueue();

		CsvProtocolListener csvProtocolListener = new CsvProtocolListener(queue);

		CsvProtocolWriterService service =
				new CsvProtocolWriterService(queue, monitoringProperties,
											 activeProfileProvider);
		service.start();

		int maxCount = 7;

		for (int i = 1; i < maxCount + 1; i++) {
			Path sourceFile =
					Files.createFile(tempDir.resolve("sourceFile-" + i + ".tmp"));
			csvProtocolListener.onBackupMonitoringEvent(new BackupMonitoringEvent(
					LocalDateTime.of(2026, 6, 6, i, i).atZone(ZoneId.systemDefault())
								 .toInstant(), sourceFile, Paths.get("cloudFile-" + i),
					"checksum-" + i, BackupStatus.SUCCESS));
		}

		service.shutdown();

		List<String> lines = Files.readAllLines(protocolPath);
		Assertions.assertEquals(maxCount + 1, lines.size(),
								maxCount + " lines should be written.");
		Assertions.assertEquals(CsvProtocolWriterService.CSV_HEADER, lines.get(0));
		for (int i = 1; i < maxCount + 1; i++) {
			String expected = String.format(
					"%s;2026-06-06T0%d:0%d:00+02:00[Europe/Berlin];%s\\sourceFile-%d.tmp;cloudFile-%d;0 B;checksum-%d;SUCCESS",
					profile, i, i, tempDir.toAbsolutePath(), i, i, i);
			Assertions.assertEquals(expected, lines.get(i));
		}
	}
}