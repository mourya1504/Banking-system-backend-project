package com.bank.notification.service;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SmsService {
    
    public void sendSms(String phoneNumber, String message) {
        // Integration with SMS gateway (Twilio, AWS SNS, etc.)
        // This is a mock implementation
        log.info("Sending SMS to {}: {}", phoneNumber, message);
        
        // Simulate SMS sending
        try {
            Thread.sleep(500);
            log.info("SMS sent successfully to: {}", phoneNumber);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("SMS sending failed", e);
        }
    }
}