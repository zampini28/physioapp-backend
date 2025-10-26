package br.com.physioapp.api.physioapp.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.physioapp.api.physioapp.model.Notification;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {
  
  List<Notification> findByRecipientIdOrderByCreatedAtDesc(UUID recipientId);
  
  List<Notification> findByRecipientIdAndReadFalse(UUID recipientId);
}
