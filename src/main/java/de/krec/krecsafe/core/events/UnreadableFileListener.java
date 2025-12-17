package de.krec.krecsafe.core.events;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class UnreadableFileListener {

    private final UnreadableFileRegistry unreadableFileRegistry;

    @Autowired
    public UnreadableFileListener(UnreadableFileRegistry unreadableFileRegistry) {
        this.unreadableFileRegistry = unreadableFileRegistry;
    }

    @EventListener
    public void onFileUnreadableEvent(UnreadableFileEvent event) {
        unreadableFileRegistry.addUnreadableFileEvent(event);
    }
}
