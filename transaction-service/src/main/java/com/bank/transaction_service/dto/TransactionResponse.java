package com.bank.transaction_service.dto;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.bank.transaction_service.Entity.TransactionStatus;
import com.bank.transaction_service.Entity.TransactionType;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionResponse {
    private String transactionId;
    private String accountNumber;
    private String toAccountNumber;
    private TransactionType type;
    private BigDecimal amount;
    private TransactionStatus status;
    private String description;
    private LocalDateTime transactionDate;
    private String failureReason;
}
