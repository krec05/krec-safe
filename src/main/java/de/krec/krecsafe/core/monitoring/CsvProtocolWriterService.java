package de.krec.krecsafe.core.monitoring;

import de.krec.krecsafe.config.MonitoringProperties;
import de.krec.krecsafe.core.service.ActiveProfileProvider;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Mithilfe dieses Services wird die {@link CsvProtocolEntry} zunächst aus der
 * {@link CsvProtocolQueue} entfernt und anschließend in die CSV-Protokolldatei geschrieben.
 */
@Service
public class CsvProtocolWriterService {

	protected static final String           CSV_HEADER  =
			"Profile;Backup Time;Source Path;Cloud Path;File Size;Checksum;Backup Status";
	private static final CsvProtocolEntry POISON_PILL =
			new CsvProtocolEntry(null, null, null, 0, null, null);

	private final ActiveProfileProvider activeProfileProvider;
	private final CsvProtocolQueue      csvProtocolQueue;
	private final ExecutorService       executorService =
			Executors.newSingleThreadExecutor();

	// In Java, a variable can remain permanently in the cache, and in a multithreaded
	// environment (writer thread, shutdown thread), one thread continues to read an old
	// value while another thread has already changed the value. As a result, the threads
	// “see” different values. volatile forces changes to be visible to all threads.
	private volatile boolean   running = true;
	private          Path      csvProtocolPath;
	// This is the object that this writer-thread is about. It is needed to enable
	// controlled termination.
	private          Future<?> writerFuture;

	@Autowired
	public CsvProtocolWriterService(CsvProtocolQueue csvProtocolQueue,
									MonitoringProperties monitoringProperties,
									ActiveProfileProvider activeProfileProvider) {
		this.csvProtocolQueue = csvProtocolQueue;
		this.csvProtocolPath = Paths.get(monitoringProperties.getCsvProtocolPath());
		this.activeProfileProvider = activeProfileProvider;
	}

	@PostConstruct
	public void start() {
		writerFuture = executorService.submit(this::runWriter);
	}

	@PreDestroy
	public void shutdown() throws ExecutionException, InterruptedException {
		csvProtocolQueue.offer(POISON_PILL);
		writerFuture.get();
	}

	private void runWriter() {
		try {
			if (!Files.exists(csvProtocolPath)) {
				System.out.println("Create CSV Protocol");
				createCsvProtocolFile();
			}

			try (BufferedWriter bwiter = Files.newBufferedWriter(csvProtocolPath,
																 StandardOpenOption.CREATE,
																 StandardOpenOption.WRITE,
																 StandardOpenOption.APPEND)) {
				int count = 0;
				while (true) {
					count++;
					System.out.println("Lauf Nr: " + count);
					CsvProtocolEntry csvProtocolEntry =
							csvProtocolQueue.poll(500, TimeUnit.MILLISECONDS);
					if (csvProtocolEntry == POISON_PILL) {
						break;
					}
					if (csvProtocolEntry != null) {
						System.out.println(
								"CsvProtocolEntry: " + csvProtocolEntry.sourceFile());
						String csvLine = createCsvLine(csvProtocolEntry);
						if (!csvLine.isEmpty()) {
							bwiter.write(csvLine);
							bwiter.newLine();
						}
					}
				}
			} catch (InterruptedException e) {
				//				throw new RuntimeException(e);
			}
		} catch (IOException e) {
			// No exception is deliberately triggered to prevent logging from
			// terminating the entire program flow.
		}
	}

	private String createCsvLine(CsvProtocolEntry csvProtocolEntry) throws IOException {
		String profile = activeProfileProvider.getActiveProfile();
		String backupTime = csvProtocolEntry.backupTime().atZone(ZoneId.systemDefault()).format(DateTimeFormatter.ISO_DATE_TIME);
		String sourceFile = csvProtocolEntry.sourceFile().toString();
		String cloudFile = csvProtocolEntry.cloudFile().toString();
		String fileSize = humanReadableBytes(Files.size(csvProtocolEntry.sourceFile()));
		String checksum = csvProtocolEntry.checksum();
		String backupStatus = csvProtocolEntry.backupStatus().name();

		return joinCsvLine(profile, backupTime, sourceFile, cloudFile, fileSize, checksum,
						   backupStatus);
	}

	private void createCsvProtocolFile() throws IOException {
		Files.createDirectories(csvProtocolPath.getParent());
		try (BufferedWriter bwriter = Files.newBufferedWriter(csvProtocolPath,
															  StandardOpenOption.CREATE,
															  StandardOpenOption.WRITE)) {
			bwriter.write(CSV_HEADER);
			bwriter.newLine();
		}
	}

	private String joinCsvLine(String... fields) {
		return Arrays.stream(fields).map(this::escapeCsvElement)
					 .collect(Collectors.joining(";"));
	}

	private String escapeCsvElement(String csvElement) {
		if (csvElement == null) {
			return "";
		}
		csvElement.replace("\"", "\"\"'");
		if (csvElement.contains(";") || csvElement.contains("\"") || csvElement.contains(
				"\n") || csvElement.contains("\r")) {
			return "\"" + csvElement + "\"";
		}
		return csvElement;
	}

	protected static String humanReadableBytes(long bytesLength) {
		if (bytesLength < 1024) {
			return bytesLength + " B";
		}

		int exp = (int) (Math.log(bytesLength) / Math.log(1024));
		String unit = "KMGTPE".charAt(exp - 1) + "B";

		return String.format(Locale.ROOT, "%.2f %s", bytesLength / Math.pow(1024, exp),
							 unit);
	}
}
