package br.com.physioapp.api.physioapp.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.com.physioapp.api.physioapp.model.Notification;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientIdOrderByCreatedAtDesc(Long recipientId);
    List<Notification> findByRecipientIdAndReadFalse(Long recipientId);
}
