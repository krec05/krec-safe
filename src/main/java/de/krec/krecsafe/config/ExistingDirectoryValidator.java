package de.krec.krecsafe.config;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.nio.file.Files;
import java.nio.file.Path;

public class ExistingDirectoryValidator implements ConstraintValidator<ExistingDirectory, Path> {

    @Override
    public boolean isValid(Path path, ConstraintValidatorContext constraintValidatorContext) {
        if (!Files.exists(path)) {
            return false;
        }
        return Files.isDirectory(path);
    }
}
