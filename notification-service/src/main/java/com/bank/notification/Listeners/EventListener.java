package com.bank.notification.Listeners;

import com.bank.notification.dto.NotificationRequest;
import com.bank.notification.entity.NotificationChannel;
import com.bank.notification.entity.NotificationType;
import com.bank.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventListener {
    
    private final NotificationService notificationService;
    private final ObjectMapper objectMapper;
    
    @KafkaListener(topics = "account-events", groupId = "notification-service-group")
    public void handleAccountEvent(String message) {
        try {
            JsonNode event = objectMapper.readTree(message);
            String eventType = event.get("eventType").asText();
            
            if ("ACCOUNT_CREATED".equals(eventType)) {
                String accountNumber = event.get("accountNumber").asText();
                Long customerId = event.get("customerId").asLong();
                
                NotificationRequest request = NotificationRequest.builder()
                        .customerId(String.valueOf(customerId))
                        .type(NotificationType.ACCOUNT_UPDATE)
                        .channel(NotificationChannel.EMAIL)
                        .subject("Account Created Successfully")
                        .message("Your account " + accountNumber + " has been created successfully.")
                        .referenceId(accountNumber)
                        .build();
                
                notificationService.sendNotification(request);
            }
            
        } catch (Exception e) {
            log.error("Error processing account event", e);
        }
    }
    
    @KafkaListener(topics = "transaction-events", groupId = "notification-service-group")
    public void handleTransactionEvent(String message) {
        try {
            JsonNode event = objectMapper.readTree(message);
            String eventType = event.get("eventType").asText();
            
            if ("TRANSACTION_COMPLETED".equals(eventType)) {
                String transactionId = event.get("transactionId").asText();
                String accountNumber = event.get("accountNumber").asText();
                String amount = event.get("amount").asText();
                String type = event.get("type").asText();
                
                NotificationRequest request = NotificationRequest.builder()
                        .customerId(accountNumber) // In real scenario, fetch customer ID
                        .type(NotificationType.TRANSACTION_ALERT)
                        .channel(NotificationChannel.SMS)
                        .subject("Transaction Alert")
                        .message(String.format("%s of $%s completed on account %s. Transaction ID: %s",
                                type, amount, accountNumber, transactionId))
                        .referenceId(transactionId)
                        .build();
                
                notificationService.sendNotification(request);
            }
            
        } catch (Exception e) {
            log.error("Error processing transaction event", e);
        }
    }
}