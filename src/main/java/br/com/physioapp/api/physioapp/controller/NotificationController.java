package br.com.physioapp.api.physioapp.controller;

import br.com.physioapp.api.physioapp.dto.CreateNotificationRequest;
import br.com.physioapp.api.physioapp.dto.NotificationResponseDTO;
import br.com.physioapp.api.physioapp.dto.NotificationSummaryDTO;
import br.com.physioapp.api.physioapp.service.NotificationService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collection;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

  private final NotificationService notificationService;

  public NotificationController(NotificationService notificationService) {
    this.notificationService = notificationService;
  }

  @PostMapping
  public ResponseEntity<NotificationResponseDTO> createNotification(
      @Valid @RequestBody CreateNotificationRequest request) {
    NotificationResponseDTO created = notificationService.createNotification(request);
    URI location = URI.create("/notifications/" + created.id());
    return ResponseEntity.created(location).body(created);
  }

  @GetMapping
  public ResponseEntity<Page<NotificationSummaryDTO>> listNotifications(
      Authentication authentication,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size,
      @RequestParam(defaultValue = "false") boolean unreadOnly) {

    UUID userId = UUID.fromString(authentication.getName());
    Pageable pageable = PageRequest.of(page, Math.min(size, 100));
    Page<NotificationSummaryDTO> result;
    if (unreadOnly) {
      result = notificationService.getUnreadNotificationsForUser(userId, pageable);
    } else {
      result = notificationService.getNotificationsForUser(userId, pageable);
    }
    return ResponseEntity.ok(result);
  }

  @GetMapping("/count/unread")
  public ResponseEntity<Long> getUnreadCount(Authentication authentication) {
    UUID userId = UUID.fromString(authentication.getName());
    long count = notificationService.getUnreadCount(userId);
    return ResponseEntity.ok(count);
  }

  @PostMapping("/read")
  public ResponseEntity<Integer> markAsReadBulk(
      Authentication authentication,
      @RequestBody Collection<UUID> notificationIds) {

    UUID userId = UUID.fromString(authentication.getName());
    int updated = notificationService.markAsRead(userId, notificationIds);
    return ResponseEntity.ok(updated);
  }

  @PostMapping("/{id}/read")
  public ResponseEntity<Void> markAsRead(
      Authentication authentication,
      @PathVariable("id") UUID id) {

    UUID userId = UUID.fromString(authentication.getName());
    notificationService.markAsRead(userId, java.util.List.of(id));
    return ResponseEntity.ok().build();
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteNotification(
      Authentication authentication,
      @PathVariable("id") UUID id) {

    UUID userId = UUID.fromString(authentication.getName());
    notificationService.deleteNotification(userId, id);
    return ResponseEntity.noContent().build();
  }
}
