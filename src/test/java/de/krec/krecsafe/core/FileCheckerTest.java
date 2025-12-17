package de.krec.krecsafe.core;

import de.krec.krecsafe.TestUtils;
import de.krec.krecsafe.config.BackupPathProperties;
import de.krec.krecsafe.core.cloud.CloudService;
import de.krec.krecsafe.core.service.FileChecker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneOffset;

@ExtendWith(MockitoExtension.class)
class FileCheckerTest {

    private static final LocalDateTime FIRST_DATE = LocalDateTime.of(2020, Month.JANUARY, 1, 12, 21, 42);
    private static final LocalDateTime LAST_DATE = LocalDateTime.of(2020, Month.JANUARY, 2, 12, 21, 43);

    @InjectMocks
    FileChecker fileChecker;

    @Mock
    CloudService cloudServiceMock;

    private BackupPathProperties.PathEntry pathEntry;
    private Path file;

    @BeforeEach
    void setUp() throws IOException {
        pathEntry = new BackupPathProperties.PathEntry();
        pathEntry.setClouddir("clouddir");
        pathEntry.setPath(TestUtils.BACKUP_PATH);

        file = Files.createTempFile("lastModifiedTestFile", "txt");
    }

    @Test
    void hostIsBeforeCloud() throws IOException {
        Files.setLastModifiedTime(file, FileTime.from(FIRST_DATE.toInstant(ZoneOffset.UTC)));
        Mockito.when(cloudServiceMock.getUploadDate(Mockito.any(BackupPathProperties.PathEntry.class), Mockito.any(Path.class))).thenReturn(LAST_DATE);

        Assertions.assertFalse(fileChecker.isBackupNeeded(pathEntry, file), "The file in the cloud is newer, so no upload is necessary.");
    }

    @Test
    void hostIsAfterCloud() throws IOException {
        Files.setLastModifiedTime(file, FileTime.from(LAST_DATE.toInstant(ZoneOffset.UTC)));
        Mockito.when(cloudServiceMock.getUploadDate(Mockito.any(BackupPathProperties.PathEntry.class), Mockito.any(Path.class))).thenReturn(FIRST_DATE);

        Assertions.assertTrue(fileChecker.isBackupNeeded(pathEntry, file), "The file in the cloud is older, so an upload is necessary.");
    }

    @Test
    void hostIsEqualCloud() throws IOException {
        Files.setLastModifiedTime(file, FileTime.from(FIRST_DATE.toInstant(ZoneOffset.UTC)));
        Mockito.when(cloudServiceMock.getUploadDate(Mockito.any(BackupPathProperties.PathEntry.class), Mockito.any(Path.class))).thenReturn(FIRST_DATE);

        Assertions.assertTrue(fileChecker.isBackupNeeded(pathEntry, file), "The file in the cloud is the same age, so uploading it is safer.");
    }

}