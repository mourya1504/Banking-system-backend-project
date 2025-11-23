package com.bank.transaction_service.Event;
import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionEvent {
    private String eventType;
    private String transactionId;
    private String accountNumber;
    private BigDecimal amount;
    private String type;
    private String status;
    private Long timestamp;
}