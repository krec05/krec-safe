package de.krec.krecsafe.core.events;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UnreadableFileRegistry {

    private final List<UnreadableFileEvent> unreadableFileEvents = new ArrayList<>();

    public boolean hasErrors() {
        return !unreadableFileEvents.isEmpty();
    }

    public List<UnreadableFileEvent> getFileUnreadableEvents() {
        // To prevent the list from being changed accidentally or intentionally, a copy of it is created.
        // To ensure data consistency, the list must be encapsulated.
        return List.copyOf(unreadableFileEvents);
    }

    public void addUnreadableFileEvent(UnreadableFileEvent unreadableFileEvent) {
        unreadableFileEvents.add(unreadableFileEvent);
    }
}
