package de.krec.krecsafe.core.monitoring;

import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * The queue controls the write operations of {@link CsvProtocolEntry} to the CSV log file.
 * Since writing is not atomic, blocking behavior can occur in a multithreaded environment.
 * For this reason, the queue collects the entries to be written and has them processed
 * sequentially by {@link CsvProtocolWriterService}.
 */
@Component
public class CsvProtocolQueue {

	private final BlockingQueue<CsvProtocolEntry> queue = new LinkedBlockingQueue<>();

	public boolean offer(CsvProtocolEntry csvEntry) {
		return queue.offer(csvEntry);
	}

	public CsvProtocolEntry poll(long timeout, TimeUnit unit) throws InterruptedException {
		return queue.poll(timeout, unit);
	}

	public boolean isEmpty() {
		return  queue.isEmpty();
	}
}
