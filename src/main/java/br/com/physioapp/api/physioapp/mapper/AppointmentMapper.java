package br.com.physioapp.api.physioapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import br.com.physioapp.api.physioapp.dto.AppointmentResponseDTO;
import br.com.physioapp.api.physioapp.model.Appointment;

@Mapper(componentModel = "spring")
public interface AppointmentMapper {

  @Mapping(target = "physiotherapistId", source = "physiotherapist.id")
  @Mapping(target = "physiotherapistName", source = "physiotherapist.fullname")
  @Mapping(target = "patientId", source = "patient.id")
  @Mapping(target = "patientName", source = "patient.fullname")
  @Mapping(target = "createdAt", source = "createdAt")
  @Mapping(target = "updatedAt", source = "updatedAt")
  AppointmentResponseDTO toResponse(Appointment appointment);
}
