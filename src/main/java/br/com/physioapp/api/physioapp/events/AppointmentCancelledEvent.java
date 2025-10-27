package br.com.physioapp.api.physioapp.events;

import java.util.Objects;

import br.com.physioapp.api.physioapp.dto.AppointmentResponseDTO;

public final class AppointmentCancelledEvent {

  private final AppointmentResponseDTO appointment;

  public AppointmentCancelledEvent(AppointmentResponseDTO appointment) {
    this.appointment = Objects.requireNonNull(appointment, "appointment");
  }

  public AppointmentResponseDTO appointment() {
    return appointment;
  }
}
