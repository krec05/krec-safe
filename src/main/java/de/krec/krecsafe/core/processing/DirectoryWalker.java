package de.krec.krecsafe.core.processing;

import de.krec.krecsafe.config.BackupPathProperties;
import de.krec.krecsafe.core.events.UnencryptableFileEvent;
import de.krec.krecsafe.core.events.UnreadableFileEvent;
import de.krec.krecsafe.core.cloud.CloudService;
import de.krec.krecsafe.core.service.FileChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.crypto.NoSuchPaddingException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import java.util.stream.Stream;

@Service
public class DirectoryWalker {

    private static final Logger LOG = LoggerFactory.getLogger(DirectoryWalker.class);

    private final BackupPathProperties backupPathProperties;
    private final FileChecker fileChecker;
    private final CloudService cloudService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    public DirectoryWalker(BackupPathProperties backupPathProperties, FileChecker fileChecker, CloudService cloudService, ApplicationEventPublisher applicationEventPublisher) {
        this.backupPathProperties = backupPathProperties;
        this.fileChecker = fileChecker;
        this.cloudService = cloudService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void walkThrowPaths() {
        List<BackupPathProperties.PathEntry> backupPathsList = backupPathProperties.getPaths();
        for (BackupPathProperties.PathEntry pathEntry : backupPathsList) {
            try (Stream<Path> paths = Files.walk(pathEntry.getPath())) {
                paths
                        .filter(Files::isRegularFile)
                        .forEach(p -> {
                            try {
                                if (fileChecker.isBackupNeeded(pathEntry, p)) {
                                    LOG.info("Backup needed for {}", p);
                                    cloudService.backupFile(pathEntry, p);
                                } else {
                                    LOG.info("No backup needed for {}", p);
                                }
                            } catch (IOException e) {
                                LOG.error("The file {} could not be read", p.toAbsolutePath(), e);
                                // If a file cannot be read, this should be noted without stopping the program.
                                applicationEventPublisher.publishEvent(new UnreadableFileEvent(p, e));
                            } catch (InvalidAlgorithmParameterException |
									 NoSuchPaddingException | NoSuchAlgorithmException |
									 InvalidKeySpecException | InvalidKeyException e) {
								LOG.error("The file {} could not be encrypted", p.toAbsolutePath(), e);
								// If a file cannot be read, this should be noted without stopping the program.
								applicationEventPublisher.publishEvent(new UnencryptableFileEvent(p, e));
							}
						});
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }

    }

}
