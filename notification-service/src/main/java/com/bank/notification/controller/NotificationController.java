package com.bank.notification.controller;
import com.bank.notification.dto.*;
import com.bank.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {
    
    private final NotificationService notificationService;
    
    @PostMapping
    public ResponseEntity<Void> sendNotification(@RequestBody NotificationRequest request) {
        notificationService.sendNotification(request);
        return ResponseEntity.accepted().build();
    }
    
    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<NotificationResponse>> getCustomerNotifications(
            @PathVariable String customerId) {
        return ResponseEntity.ok(notificationService.getCustomerNotifications(customerId));
    }
    
    @PostMapping("/retry-failed")
    public ResponseEntity<Void> retryFailedNotifications() {
        notificationService.retryFailedNotifications();
        return ResponseEntity.ok().build();
    }
}
