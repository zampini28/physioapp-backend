package br.com.physioapp.api.physioapp.mapper;

import org.springframework.stereotype.Component;

import br.com.physioapp.api.physioapp.dto.NotificationResponseDTO;
import br.com.physioapp.api.physioapp.dto.NotificationSummaryDTO;
import br.com.physioapp.api.physioapp.model.Notification;

@Component
public class NotificationManualMapper {

    public NotificationResponseDTO toResponse(Notification n) {
        return new NotificationResponseDTO(
                n.getId(),
                n.getRecipient() != null ? n.getRecipient().getId() : null,
                n.getTitle(),
                n.getMessage(),
                n.getType(),
                n.getRead(),
                n.getCreatedAt());
    }

    public NotificationSummaryDTO toSummary(Notification n) {
        return new NotificationSummaryDTO(
                n.getId(),
                n.getTitle(),
                n.getType(),
                n.getRead(),
                n.getCreatedAt(),
                n.getRecipient() != null ? n.getRecipient().getId() : null);
    }
}