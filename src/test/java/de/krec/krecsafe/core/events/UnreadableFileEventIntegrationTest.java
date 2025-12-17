package de.krec.krecsafe.core.events;

import de.krec.krecsafe.TestUtils;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
class UnreadableFileEventIntegrationTest {

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    @Autowired
    private UnreadableFileRegistry unreadableFileRegistry;

    @BeforeAll
    static void createFiles() throws IOException {
        TestUtils.deleteNonEmptyDir(TestUtils.BACKUP_PATH);

        Instant oldTime = LocalDateTime.of(2017, 8, 1, 8, 17).atZone(ZoneId.systemDefault()).toInstant();

        Path dir11 = Files.createDirectories(TestUtils.BACKUP_PATH.resolve("dir1").resolve("dir11"));
        Path dir2 = Files.createDirectories(TestUtils.BACKUP_PATH.resolve("dir2"));
        Path dir3 = Files.createDirectories(TestUtils.BACKUP_PATH.resolve("dir3"));

        Files.createFile(dir11.resolve("testFileNew.txt"));
        Path oldFile = Files.createFile(dir2.resolve("testFileOld.txt"));
        Files.setLastModifiedTime(oldFile, FileTime.from(oldTime));
        Files.createFile(dir2.resolve("testFileUpdate.txt"));
        Files.createFile(dir3.resolve("testFileNotFound.txt"));
    }

    @Test
    void testUnreadableFileEventHandling() {
        Path path = Path.of("backup-test.txt");
        String errorMsg = "FAILURE";
        UnreadableFileEvent unreadableFileEvent = new UnreadableFileEvent(path, new IOException(errorMsg));

        applicationEventPublisher.publishEvent(unreadableFileEvent);

        assertTrue(unreadableFileRegistry.hasErrors());
        List<UnreadableFileEvent> fileUnreadableEvents = unreadableFileRegistry.getFileUnreadableEvents();
        assertEquals(1, fileUnreadableEvents.size());
        assertEquals(unreadableFileEvent, fileUnreadableEvents.get(0));

    }

}