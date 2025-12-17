package de.krec.krecsafe.config;

import de.krec.krecsafe.KrecsafeApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.builder.SpringApplicationBuilder;

public class BackupPathPropertiesInvalidDirectoryTest {

    @Test
    void invalidDirectory() {
        // * SpringApplicationBuilder starts a completely separate Spring context.
        // * Since the properties are loaded under application-invalidDirectory.yml, your @ExistingDirectory validation is executed.
        // * This fails → Spring does not delay → the context start immediately aborts with an exception.
        // * assertThrows() confirms: The context MUST fail – which is exactly what we want to test.
        Assertions.assertThrows(Exception.class, () -> new SpringApplicationBuilder(KrecsafeApplication.class)
                .profiles("invalidDirectory")
                .properties("spring.config.name=application-invalid")
                .run());
    }
}
