

package com.bank.notification.dto;
import com.bank.notification.entity.NotificationChannel;
import com.bank.notification.entity.NotificationStatus;
import com.bank.notification.entity.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    
    private Long id;
    
    private String customerId;
    
    private NotificationType type;
    
    private NotificationChannel channel;
    
    private String recipient;
    
    private String subject;
    
    private String message;
    
    private NotificationStatus status;
    
    private String referenceId;
    
    private LocalDateTime sentAt;
    
    private LocalDateTime createdAt;
    
    private String errorMessage;
}