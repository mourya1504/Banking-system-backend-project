// File: notification-service/src/main/java/com/bank/notification/Repository/NotificationRepository.java

package com.bank.notification.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bank.notification.entity.Notification;
import com.bank.notification.entity.NotificationStatus;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    /**
     * Find all notifications for a specific customer
     * Used in: getCustomerNotifications()
     */
    List<Notification> findByCustomerId(String customerId);
    
    /**
     * Find notifications by status that were created before a certain time
     * Used in: retryFailedNotifications()
     */
    List<Notification> findByStatusAndCreatedAtBefore(
        NotificationStatus status, 
        LocalDateTime createdAt
    );
    
    /**
     * Find notifications by reference ID (optional - for future use)
     */
    List<Notification> findByReferenceId(String referenceId);
    
    /**
     * Count notifications by status (optional - for monitoring)
     */
    long countByStatus(NotificationStatus status);
}