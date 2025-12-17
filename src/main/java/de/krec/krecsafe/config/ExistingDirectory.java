package de.krec.krecsafe.config;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import java.lang.annotation.Target;

@Documented         // is displayed in JavaDoc
@Constraint(validatedBy = ExistingDirectoryValidator.class)         // marks your annotation as a Bean Validation Constraint
@Target({ElementType.FIELD, ElementType.PARAMETER, ElementType.TYPE_USE})
// Where can it be used? (e.g., fields, parameters, or elements of lists→TYPE_USE)
@Retention(RUNTIME) // must be available at runtime, otherwise validation will not work
public @interface ExistingDirectory {

    String message() default "Path is not a valid directory";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
