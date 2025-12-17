package de.krec.krecsafe.config;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@ConfigurationProperties(prefix = "de.krec")
@Validated
public class BackupPathProperties {

    private List<PathEntry> paths;

    public static class PathEntry {

        @NotBlank(message = "The value of path must not be empty.")
        @ExistingDirectory
        private Path path;

        @NotBlank(message = "The value of clouddir must not be empty.")
        private String clouddir;

        public Path getPath() {
            return path;
        }

        public void setPath(Path path) {
            this.path = path;
        }

        public String getClouddir() {
            return clouddir;
        }

        public void setClouddir(String clouddir) {
            this.clouddir = clouddir;
        }
    }

    @AssertTrue(message = "The name of the cloud directory is only allowed to appear once in the list.")
    public boolean isCloudDirUnique() {
        Set<String> set =  new HashSet<>();
        return paths.stream().allMatch(p -> set.add(p.getClouddir()));
    }

    public List<PathEntry> getPaths() {
        return paths;
    }

    public void setPaths(List<PathEntry> paths) {
        this.paths = paths;
    }
}
