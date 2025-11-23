package com.bank.notification.Repository;

// File: notification-service/src/main/java/com/bank/notification/repository/NotificationTemplateRepository.java

import com.bank.notification.entity.NotificationChannel;
import com.bank.notification.entity.NotificationTemplate;
import com.bank.notification.entity.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, Long> {
    
    /**
     * Find template by unique template code
     */
    Optional<NotificationTemplate> findByTemplateCode(String templateCode);
    
    /**
     * Check if template exists by code
     */
    boolean existsByTemplateCode(String templateCode);
    
    /**
     * Find templates by type
     */
    List<NotificationTemplate> findByType(NotificationType type);
    
    /**
     * Find templates by channel
     */
    List<NotificationTemplate> findByChannel(NotificationChannel channel);
    
    /**
     * Find templates by type and channel
     */
    Optional<NotificationTemplate> findByTypeAndChannel(
        NotificationType type, 
        NotificationChannel channel
    );
    
    /**
     * Find all active templates
     */
    List<NotificationTemplate> findByActiveTrue();
    
    /**
     * Find active templates by type
     */
    List<NotificationTemplate> findByTypeAndActiveTrue(NotificationType type);
    
    /**
     * Find active templates by channel
     */
    List<NotificationTemplate> findByChannelAndActiveTrue(NotificationChannel channel);
    
    /**
     * Find active template by type and channel
     */
    Optional<NotificationTemplate> findByTypeAndChannelAndActiveTrue(
        NotificationType type, 
        NotificationChannel channel
    );
}