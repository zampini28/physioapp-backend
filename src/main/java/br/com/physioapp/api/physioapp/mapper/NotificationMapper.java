package br.com.physioapp.api.physioapp.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import br.com.physioapp.api.physioapp.dto.CreateNotificationRequest;
import br.com.physioapp.api.physioapp.dto.NotificationResponseDTO;
import br.com.physioapp.api.physioapp.dto.NotificationSummaryDTO;
import br.com.physioapp.api.physioapp.model.Notification;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "recipientId", source = "recipient.id")
    NotificationResponseDTO toResponse(Notification entity);

    @Mapping(target = "recipientId", source = "recipient.id")
    NotificationSummaryDTO toSummary(Notification entity);

    @Mapping(target = "recipient", ignore = true)
    @Mapping(target = "isRead", constant = "false")
    Notification toEntity(CreateNotificationRequest dto);
}
