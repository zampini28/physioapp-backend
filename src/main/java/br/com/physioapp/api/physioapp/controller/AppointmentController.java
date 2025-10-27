package br.com.physioapp.api.physioapp.controller;

import br.com.physioapp.api.physioapp.dto.AppointmentRequestDTO;
import br.com.physioapp.api.physioapp.dto.AppointmentResponseDTO;
import br.com.physioapp.api.physioapp.service.AppointmentService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

  private final AppointmentService appointmentService;
  private final int MAX_PAGE_SIZE = 100;

  public AppointmentController(AppointmentService appointmentService) {
    this.appointmentService = appointmentService;
  }

  @PostMapping
  public ResponseEntity<AppointmentResponseDTO> createAppointment(
      @Valid @RequestBody AppointmentRequestDTO request) {

    AppointmentResponseDTO created = appointmentService.createAppointment(request);
    URI location = URI.create("/appointments/" + created.id());
    return ResponseEntity.created(location).body(created);
  }

  @GetMapping("/{id}")
  public ResponseEntity<AppointmentResponseDTO> getAppointmentById(@PathVariable UUID id) {
    AppointmentResponseDTO dto = appointmentService.getAppointmentById(id);
    return ResponseEntity.ok(dto);
  }

  @GetMapping
  public ResponseEntity<Page<AppointmentResponseDTO>> listAppointments(
      @RequestParam(required = false) UUID patientId,
      @RequestParam(required = false) UUID physiotherapistId,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {

    int safeSize = Math.min(size, MAX_PAGE_SIZE);
    Pageable pageable = PageRequest.of(Math.max(0, page), safeSize);
    Page<AppointmentResponseDTO> result = appointmentService.getAppointments(patientId, physiotherapistId, pageable);
    return ResponseEntity.ok(result);
  }

  @PostMapping("/{id}/cancel")
  public ResponseEntity<AppointmentResponseDTO> cancelAppointment(@PathVariable UUID id) {
    AppointmentResponseDTO cancelled = appointmentService.cancelAppointment(id);
    return ResponseEntity.ok(cancelled);
  }
}
