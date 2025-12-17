package de.krec.krecsafe.core.cloud;

import de.krec.krecsafe.config.BackupPathProperties;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@ExtendWith(MockitoExtension.class)
class CloudServiceTest {

    @InjectMocks
    private CloudService cloudService;

    @Mock
    private CloudClient cloudClientMock;

    @Test
    public void testdetermineCloudPath() {
        LocalDateTime expectedTime = LocalDateTime.now();

        String clouddir = "krecsafebackup";
        Path propertyPath = Paths.get("home", "user", "krecsafe");
        Path hostFile = Paths.get("home", "user", "krecsafe", "backup", "filename.txt");
        Path cloudPath = Paths.get("krecsafebackup", "backup", "filename.txt");

        BackupPathProperties.PathEntry pathEntry = new BackupPathProperties.PathEntry();
        pathEntry.setPath(propertyPath);
        pathEntry.setClouddir(clouddir);

        ArgumentMatcher<Path> cloudPathMatcher = path -> cloudPath.toAbsolutePath().normalize().equals(path.toAbsolutePath().normalize());

        Mockito.when(cloudClientMock.getLastBackupTime(Mockito.argThat(cloudPathMatcher))).thenReturn(expectedTime);

        LocalDateTime responseTime = cloudService.getUploadDate(pathEntry, hostFile);
        assertEquals(expectedTime, responseTime);
    }

}