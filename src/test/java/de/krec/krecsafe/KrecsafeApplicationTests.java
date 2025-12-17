package de.krec.krecsafe;

import de.krec.krecsafe.config.BackupPathProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;

// If REST is implemented, the WebEnvironment must be set from NONE to RANDOM_PORT for testing.
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class KrecsafeApplicationTests {

    @Autowired
    private Environment environment;

    @Autowired
    BackupPathProperties backupPathProperties;

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

    @SuppressWarnings("EmptyMethod")
    @Test
    void contextLoads() {
        /* The smoke test is used to indicate whether the application has started correctly. If the application throws
           an error, it is passed to the empty test method and the test fails. */
    }

    @Test
    void testProfileLoaded() {
        Assertions.assertTrue(Arrays.asList(environment.getActiveProfiles()).contains("test"));
        Assertions.assertFalse(backupPathProperties.getPaths().isEmpty());
    }

}
