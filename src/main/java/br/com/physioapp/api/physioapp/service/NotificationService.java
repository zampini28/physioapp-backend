package br.com.physioapp.api.physioapp.service;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.physioapp.api.physioapp.dto.CreateNotificationRequest;
import br.com.physioapp.api.physioapp.dto.NotificationRequestDTO;
import br.com.physioapp.api.physioapp.dto.NotificationResponseDTO;
import br.com.physioapp.api.physioapp.dto.NotificationSummaryDTO;
import br.com.physioapp.api.physioapp.exception.ResourceNotFoundException;
import br.com.physioapp.api.physioapp.events.NotificationCreatedEvent;
import br.com.physioapp.api.physioapp.events.NotificationsMarkedReadEvent;
import br.com.physioapp.api.physioapp.mapper.NotificationMapper;
import br.com.physioapp.api.physioapp.model.Notification;
import br.com.physioapp.api.physioapp.model.User;
import br.com.physioapp.api.physioapp.repository.NotificationRepository;
import br.com.physioapp.api.physioapp.repository.UserRepository;

@Service
public class NotificationService {

  private final NotificationRepository notificationRepository;
  private final UserRepository userRepository;
  private final ApplicationEventPublisher eventPublisher;
  private final NotificationMapper mapper;

  public NotificationService(NotificationRepository notificationRepository,
      UserRepository userRepository,
      ApplicationEventPublisher eventPublisher,
      NotificationMapper mapper) {
    this.notificationRepository = notificationRepository;
    this.userRepository = userRepository;
    this.eventPublisher = eventPublisher;
    this.mapper = mapper;
  }

  @Transactional
  public NotificationResponseDTO createNotification(CreateNotificationRequest req) {
    UUID recipientId = req.recipientId();
    User recipient = userRepository.findById(recipientId)
        .orElseThrow(() -> new ResourceNotFoundException("Usuário destinatário do ID não encontrado: " + recipientId));

    Notification notification = Notification.builder()
        .recipient(recipient)
        .title(req.title())
        .message(req.message())
        .type(req.type())
        .read(false)
        .build();

    Notification saved = notificationRepository.save(notification);

    eventPublisher.publishEvent(new NotificationCreatedEvent(mapper.toResponse(saved)));

    return mapper.toResponse(saved);
  }

  @Transactional(readOnly = true)
  public Page<NotificationSummaryDTO> getNotificationsForUser(UUID userId, Pageable pageable) {
    if (!userRepository.existsById(userId)) {
      throw new ResourceNotFoundException("ID Usuário não encontrado: " + userId);
    }
    return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId, pageable)
        .map(mapper::toSummary);
  }

  @Transactional(readOnly = true)
  public Page<NotificationSummaryDTO> getUnreadNotificationsForUser(UUID userId, Pageable pageable) {
    if (!userRepository.existsById(userId)) {
      throw new ResourceNotFoundException("ID Usuário não encontrado: " + userId);
    }
    return notificationRepository.findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(userId, pageable)
        .map(mapper::toSummary);
  }

  @Transactional
  public int markAllAsRead(UUID userId) {
    if (!userRepository.existsById(userId)) {
      throw new ResourceNotFoundException("ID Usuário não encontrado: " + userId);
    }
    int updated = notificationRepository.markAllAsReadByRecipientId(userId);
    if (updated > 0) {
      eventPublisher.publishEvent(new NotificationsMarkedReadEvent(userId, updated));
    }
    return updated;
  }

  @Transactional
  public int markAsRead(UUID userId, Collection<UUID> ids) {
    if (!userRepository.existsById(userId)) {
      throw new ResourceNotFoundException("ID Usuário não encontrado: " + userId);
    }
    if (ids == null || ids.isEmpty())
      return 0;
    int updated = notificationRepository.markAsReadByIds(userId, ids);
    if (updated > 0) {
      eventPublisher.publishEvent(new NotificationsMarkedReadEvent(userId, updated));
    }
    return updated;
  }

  @Transactional
  public void deleteNotification(UUID userId, UUID notificationId) {
    Notification notification = notificationRepository.findById(notificationId)
        .orElseThrow(() -> new ResourceNotFoundException("ID Notificação não encontrado: " + notificationId));
    if (!notification.getRecipient().getId().equals(userId)) {
      throw new AccessDeniedException("Notificação não pertence ao usuário");
    }
    notificationRepository.deleteById(notificationId);
  }

  @Transactional(readOnly = true)
  public long getUnreadCount(UUID userId) {
    if (!userRepository.existsById(userId)) {
      throw new ResourceNotFoundException("ID Usuário não encontrado: " + userId);
    }
    return notificationRepository.countByRecipientIdAndIsReadFalse(userId);
  }
}
