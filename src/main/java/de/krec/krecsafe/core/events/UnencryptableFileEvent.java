package de.krec.krecsafe.core.events;

import java.nio.file.Path;

public record UnencryptableFileEvent(Path path, Exception exception) {
}
