package de.krec.krecsafe.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class ExistingDirectoryValidatorTest {

    private final ExistingDirectoryValidator validator = new ExistingDirectoryValidator();

    @Test
    void validDirectory() throws IOException {
        Path directory = Files.createTempDirectory("validDirectory");
        Assertions.assertTrue(validator.isValid(directory, null));
    }

    @Test
    void nonExistingDirectory() {
        Path directory = Path.of("this/path/is/not/existing");
        Assertions.assertFalse(validator.isValid(directory, null));
    }

    @Test
    void fileInsteadofDirectory() throws IOException {
        Path file = Files.createTempFile("fileInsteadOfDirectory", ".tmp");
        Assertions.assertFalse(validator.isValid(file, null));
    }

}