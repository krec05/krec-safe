package de.krec.krecsafe.core.events;

import java.nio.file.Path;

public record UnreadableFileEvent(Path path, Exception exception) {}
