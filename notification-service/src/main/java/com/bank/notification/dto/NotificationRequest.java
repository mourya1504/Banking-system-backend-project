
package com.bank.notification.dto;
import com.bank.notification.entity.NotificationChannel;
import com.bank.notification.entity.NotificationType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationRequest {
    private String customerId;
    private NotificationType type;
    private NotificationChannel channel;
    private String recipient;
    private String subject;
    private String message;
    private String referenceId;
}
