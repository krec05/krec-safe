package de.krec.krecsafe.core;

import de.krec.krecsafe.TestUtils;
import de.krec.krecsafe.config.BackupPathProperties;
import de.krec.krecsafe.core.cloud.CloudService;
import de.krec.krecsafe.core.processing.DirectoryWalker;
import de.krec.krecsafe.core.service.FileChecker;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class DirectoryWalkerExceptionTest {

    @InjectMocks
    DirectoryWalker directoryWalker;

    @Mock
    BackupPathProperties backupPathPropertiesMock;

    @Mock
    FileChecker fileCheckerMock;

    @SuppressWarnings("unused")
    @Mock
    CloudService cloudServiceMock;

    @BeforeAll
    static void createFiles() throws IOException {
        TestUtils.deleteNonEmptyDir(TestUtils.BACKUP_PATH);

        Files.createDirectories(TestUtils.BACKUP_PATH);

        Files.createTempFile(TestUtils.BACKUP_PATH, "testException", "txt");
        Files.createTempFile(TestUtils.BACKUP_PATH, "testNoException", "txt");
    }

    @Test
    void testDirectoryWalkerException() throws IOException {
        Mockito.when(fileCheckerMock.isBackupNeeded(Mockito.any(BackupPathProperties.PathEntry.class), Mockito.any(Path.class))).thenAnswer(i -> {
            Path p = i.getArgument(1);
            if ("testException.txt".equals(p.getFileName().toString())) {
                throw new IOException("Mockito throw this exception");
            }
            return true;
        });

        BackupPathProperties.PathEntry pathEntry = new BackupPathProperties.PathEntry();
        pathEntry.setPath(TestUtils.BACKUP_PATH);
        pathEntry.setClouddir("clouddir");
        Mockito.when(backupPathPropertiesMock.getPaths())
                .thenReturn(List.of(pathEntry));

        directoryWalker.walkThrowPaths();

        Mockito.verify(fileCheckerMock, Mockito.times(2)).isBackupNeeded(Mockito.any(BackupPathProperties.PathEntry.class), Mockito.any(Path.class));
    }

}