package br.com.physioapp.api.physioapp.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import br.com.physioapp.api.physioapp.dto.NotificationRequestDTO;
import br.com.physioapp.api.physioapp.model.Notification;
import br.com.physioapp.api.physioapp.service.NotificationService;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

  @Autowired
  private NotificationService notificationService;

  @PostMapping
  public ResponseEntity<Notification> createNotification(@RequestBody NotificationRequestDTO request) {
    Notification createdNotification = notificationService.createNotification(request);
    return new ResponseEntity<>(createdNotification, HttpStatus.CREATED);
  }

  @GetMapping
  public ResponseEntity<List<Notification>> getNotificationsForUser(
      @RequestParam UUID userId,
      @RequestParam(defaultValue = "false") boolean unreadOnly) {

    List<Notification> notifications;
    if (unreadOnly) {
      notifications = notificationService.getUnreadNotificationsForUser(userId);
    } else {
      notifications = notificationService.getNotificationsForUser(userId);
    }
    return ResponseEntity.ok(notifications);
  }

  @PostMapping("/{id}/read")
  public ResponseEntity<Notification> markNotificationAsRead(@PathVariable("id") UUID id) {
    Notification notification = notificationService.markAsRead(id);
    return ResponseEntity.ok(notification);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteNotification(@PathVariable UUID id) {
    notificationService.deleteNotification(id);
    return ResponseEntity.noContent().build();
  }
}