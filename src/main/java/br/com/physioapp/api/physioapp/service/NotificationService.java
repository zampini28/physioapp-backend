package br.com.physioapp.api.physioapp.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.physioapp.api.physioapp.dto.NotificationRequestDTO;
import br.com.physioapp.api.physioapp.exception.ResourceNotFoundException;
import br.com.physioapp.api.physioapp.model.Notification;
import br.com.physioapp.api.physioapp.model.User;
import br.com.physioapp.api.physioapp.repository.NotificationRepository;
import br.com.physioapp.api.physioapp.repository.UserRepository;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Notification createNotification(NotificationRequestDTO request) {
        User recipient = userRepository.findById(request.recipientId())
                .orElseThrow(() -> new ResourceNotFoundException("Usuário destinatário do ID não encontrado: " + request.recipientId()));

        Notification notification = Notification.builder()
                .recipient(recipient)
                .title(request.title())
                .message(request.message())
                .type(request.type())
                .read(false)
                .build();

        return notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<Notification> getNotificationsForUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("ID Usuário não encontrado: " + userId);
        }
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
    }
    
    @Transactional(readOnly = true)
    public List<Notification> getUnreadNotificationsForUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("ID Usuário não encontrado: " + userId);
        }
        return notificationRepository.findByRecipientIdAndReadFalse(userId);
    }

    @Transactional
    public Notification markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("ID Notificação não encontrado: " + notificationId));

        notification.setRead(true);
        return notificationRepository.save(notification);
    }

    @Transactional
    public void deleteNotification(Long notificationId) {
        if (!notificationRepository.existsById(notificationId)) {
            throw new ResourceNotFoundException("ID Notificação não encontrado: " + notificationId);
        }
        notificationRepository.deleteById(notificationId);
    }
}