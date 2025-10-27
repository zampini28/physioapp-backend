package br.com.physioapp.api.physioapp.model;

import java.util.Locale;

public enum NotificationType {
    REMINDER(false, Priority.NORMAL, true),
    ALERT(true, Priority.HIGH, true),
    INFO(false, Priority.LOW, false),
    WARNING(true, Priority.HIGH, false);

    private final boolean urgent;
    private final Priority priority;
    private final boolean persist;

    NotificationType(boolean urgent, Priority priority, boolean persist) {
        this.urgent = urgent;
        this.priority = priority;
        this.persist = persist;
    }

    public boolean isUrgent() {
        return urgent;
    }

    public Priority getPriority() {
        return priority;
    }

    public boolean shouldPersist() {
        return persist;
    }

    public static NotificationType fromString(String value) {
        try {
            return NotificationType.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid notification type: " + value);
        }
    }

    public enum Priority {
        LOW, NORMAL, HIGH
    }
}
