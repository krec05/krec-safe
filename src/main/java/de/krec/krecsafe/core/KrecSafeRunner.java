package de.krec.krecsafe.core;

import de.krec.krecsafe.core.events.UnreadableFileRegistry;
import de.krec.krecsafe.core.processing.DirectoryWalker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class KrecSafeRunner implements ApplicationRunner {

    private static final Logger LOG = LoggerFactory.getLogger(KrecSafeRunner.class);

    private final DirectoryWalker directoryWalker;
    private final UnreadableFileRegistry unreadableFileRegistry;

    @Autowired
    public KrecSafeRunner(DirectoryWalker directoryWalker, UnreadableFileRegistry unreadableFileRegistry) {
        this.directoryWalker = directoryWalker;
        this.unreadableFileRegistry = unreadableFileRegistry;
    }

    @Override
    public void run(ApplicationArguments args) {
        LOG.info("KrecSafeRunner started");
        directoryWalker.walkThrowPaths();
        LOG.info("KrecSafeRunner finished");

        LOG.info("Check if files could not be read");
        if (unreadableFileRegistry.hasErrors()) {
            LOG.error("The following files could not be read:");
            unreadableFileRegistry.getFileUnreadableEvents().forEach(fileUnreadableEvent -> LOG.error("{} based on {}", fileUnreadableEvent.path(), fileUnreadableEvent.exception().getLocalizedMessage()));
        } else {
            LOG.info("All files could be read");
        }
    }
}
