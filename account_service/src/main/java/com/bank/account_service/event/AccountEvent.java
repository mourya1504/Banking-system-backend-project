package com.bank.account_service.event;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountEvent {
    private String eventType;
    private String accountNumber;
    private Long customerId;
    private BigDecimal balance;
    private Long timestamp;
}