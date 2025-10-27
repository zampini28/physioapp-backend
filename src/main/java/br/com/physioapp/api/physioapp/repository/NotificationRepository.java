package br.com.physioapp.api.physioapp.repository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import br.com.physioapp.api.physioapp.model.Notification;
import br.com.physioapp.api.physioapp.model.NotificationType;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

  Page<Notification> findByRecipientIdOrderByCreatedAtDesc(UUID recipientId, Pageable pageable);

  long countByRecipientIdAndIsReadFalse(UUID recipientId);

  Page<Notification> findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(UUID recipientId, Pageable pageable);

  @Modifying
  @Query("UPDATE Notification n SET n.isRead = true WHERE n.recipient.id = :recipientId AND n.isRead = false")
  int markAllAsReadByRecipientId(@Param("recipientId") UUID recipientId);

  @Modifying
  @Query("UPDATE Notification n SET n.isRead = true WHERE n.id IN :ids AND n.recipient.id = :recipientId")
  int markAsReadByIds(@Param("recipientId") UUID recipientId, @Param("ids") Collection<UUID> ids);

  interface Summary {
    UUID getId();

    String getTitle();

    Boolean getIsRead();

    LocalDateTime getCreatedAt();

    NotificationType getType();
  }

  Page<Summary> findByRecipientIdOrderByCreatedAtDesc(UUID recipientId, Pageable pageable, Class<Summary> projection);
}
