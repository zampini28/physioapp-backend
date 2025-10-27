package br.com.physioapp.api.physioapp.model;

import java.util.Locale;

public enum AppointmentStatus {
    SCHEDULED,
    COMPLETED,
    CANCELLED;

    public boolean isFinal() {
        return this == COMPLETED || this == CANCELLED;
    }

    public boolean canTransitionTo(AppointmentStatus target) {
        if (this == target)
            return true;
        return switch (this) {
            case SCHEDULED -> target == COMPLETED || target == CANCELLED;
            case COMPLETED -> false;
            case CANCELLED -> false;
        };
    }

    public static AppointmentStatus fromString(String value) {
        try {
            return AppointmentStatus.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (Exception ex) {
            throw new IllegalArgumentException("Invalid appointment status: " + value);
        }
    }
}
