// File: notification-service/src/main/java/com/bank/notification/entity/NotificationStatus.java

package com.bank.notification.entity;

public enum NotificationStatus {
    PENDING,    // Notification created but not sent
    SENT,       // Notification sent successfully
    DELIVERED,  // Notification delivered to recipient
    READ,       // Notification read by recipient
    FAILED,     // Notification failed to send
    RETRYING    // Attempting to resend
}