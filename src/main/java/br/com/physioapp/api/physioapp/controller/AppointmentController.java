package br.com.physioapp.api.physioapp.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.physioapp.api.physioapp.dto.AppointmentRequestDTO;
import br.com.physioapp.api.physioapp.model.Appointment;
import br.com.physioapp.api.physioapp.service.AppointmentService;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

  @Autowired
  private  AppointmentService appointmentService;

  @PostMapping
  public ResponseEntity<Appointment> createAppointment(@RequestBody AppointmentRequestDTO request) {
    Appointment newAppointment = appointmentService.createAppointment(request);
    return new ResponseEntity<>(newAppointment, HttpStatus.CREATED);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Appointment> getAppointmentById(@PathVariable UUID id) {
    Appointment appointment = appointmentService.getAppointmentById(id);
    return ResponseEntity.ok(appointment);
  }

  @GetMapping
  public ResponseEntity<List<Appointment>> getAppointments(
      @RequestParam(required = false) UUID patientId,
      @RequestParam(required = false) UUID physiotherapistId) {

    List<Appointment> appointments = appointmentService.getAppointments(patientId, physiotherapistId);
    return ResponseEntity.ok(appointments);
  }

  @PostMapping("/{id}/cancel")
  public ResponseEntity<Appointment> cancelAppointment(@PathVariable UUID id) {
    Appointment cancelledAppointment = appointmentService.cancelAppointment(id);
    return ResponseEntity.ok(cancelledAppointment);
  }
}