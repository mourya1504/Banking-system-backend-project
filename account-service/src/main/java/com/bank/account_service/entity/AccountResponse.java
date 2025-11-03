package com.bank.account_service.entity;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {
      private Long id;
    private String accountNumber;
    private Long customerId;
    private AccountType accountType;
    private BigDecimal balance;
    private AccountStatus status;
    private String currency;
    private LocalDateTime createdAt;
    
}
