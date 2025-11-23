package com.bank.notification.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String customerId;
    
    @Enumerated(EnumType.STRING)
    private NotificationType type;
    
    @Enumerated(EnumType.STRING)
    private NotificationChannel channel;
    
    private String recipient;
    
    private String subject;
    
    @Column(length = 2000)
    private String message;
    
    @Enumerated(EnumType.STRING)
    private NotificationStatus status;
    
    private String referenceId;
    
    private LocalDateTime sentAt;
    
    private LocalDateTime createdAt;
    
    private String errorMessage;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
/* 
enum NotificationType {
    TRANSACTION_ALERT, ACCOUNT_UPDATE, SECURITY_ALERT, MARKETING, STATEMENT
}

enum NotificationChannel {
    EMAIL, SMS, PUSH, IN_APP
}

enum NotificationStatus {
    PENDING, SENT, FAILED, DELIVERED
}
*/