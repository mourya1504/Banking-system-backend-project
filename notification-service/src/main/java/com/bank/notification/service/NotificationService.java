package com.bank.notification.service;
import com.bank.notification.dto.NotificationRequest;
import com.bank.notification.dto.NotificationResponse;
import com.bank.notification.entity.*;
import com.bank.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {
    
    private final NotificationRepository notificationRepository;
    private final EmailService emailService;
    private final SmsService smsService;
    
    @Async
    @Transactional
    public void sendNotification(NotificationRequest request) {
        Notification notification = Notification.builder()
                .customerId(request.getCustomerId())
                .type(request.getType())
                .channel(request.getChannel())
                .recipient(request.getRecipient())
                .subject(request.getSubject())
                .message(request.getMessage())
                .status(NotificationStatus.PENDING)
                .referenceId(request.getReferenceId())
                .build();
        
        notification = notificationRepository.save(notification);
        
        try {
            switch (request.getChannel()) {
                case EMAIL -> emailService.sendEmail(
                    request.getRecipient(), request.getSubject(), request.getMessage());
                case SMS -> smsService.sendSms(
                    request.getRecipient(), request.getMessage());
                case PUSH -> sendPushNotification(notification);
                case IN_APP -> saveInAppNotification(notification);
            }
            
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            log.info("Notification sent successfully: {}", notification.getId());
            
        } catch (Exception e) {
            notification.setStatus(NotificationStatus.FAILED);
            notification.setErrorMessage(e.getMessage());
            log.error("Failed to send notification: {}", e.getMessage());
        } finally {
            notificationRepository.save(notification);
        }
    }
    
    public List<NotificationResponse> getCustomerNotifications(String customerId) {
        return notificationRepository.findByCustomerId(customerId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }
    
    public void retryFailedNotifications() {
        LocalDateTime threshold = LocalDateTime.now().minusHours(1);
        List<Notification> failedNotifications = notificationRepository
                .findByStatusAndCreatedAtBefore(NotificationStatus.FAILED, threshold);
        
        failedNotifications.forEach(notification -> {
            NotificationRequest request = NotificationRequest.builder()
                    .customerId(notification.getCustomerId())
                    .type(notification.getType())
                    .channel(notification.getChannel())
                    .recipient(notification.getRecipient())
                    .subject(notification.getSubject())
                    .message(notification.getMessage())
                    .referenceId(notification.getReferenceId())
                    .build();
            sendNotification(request);
        });
    }
    
    private void sendPushNotification(Notification notification) {
        // Implement push notification logic
        log.info("Sending push notification to: {}", notification.getRecipient());
    }
    
    private void saveInAppNotification(Notification notification) {
        // In-app notifications are just saved to database
        notification.setStatus(NotificationStatus.DELIVERED);
        log.info("Saved in-app notification for customer: {}", notification.getCustomerId());
    }
    
    private NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .customerId(notification.getCustomerId())
                .type(notification.getType())
                .channel(notification.getChannel())
                .subject(notification.getSubject())
                .message(notification.getMessage())
                .status(notification.getStatus())
                .sentAt(notification.getSentAt())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}
