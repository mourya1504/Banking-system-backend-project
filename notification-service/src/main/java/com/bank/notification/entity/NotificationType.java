package com.bank.notification.entity;

public enum NotificationType {
    TRANSACTION_ALERT,      // Sent when transaction occurs
    ACCOUNT_UPDATE,         // Account information changed
    SECURITY_ALERT,         // Security-related notifications
    MARKETING,              // Promotional messages
    STATEMENT,              // Account statements
    PASSWORD_RESET,         // Password reset notifications
    WELCOME,                // Welcome message for new users
    PROMOTIONAL,            // Special offers
    PAYMENT_REMINDER,       // Payment due reminders
    LOW_BALANCE_ALERT,      // Low balance warnings
    ACCOUNT_LOCKED,         // Account locked notification
    LOGIN_ALERT,            // Unusual login detected
    CARD_EXPIRY,            // Card expiring soon
    ACCOUNT_CLOSED          // Account closure confirmation
}