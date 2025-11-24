package com.bank.account_service.dto;



import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.bank.account_service.entity.Account;
import com.bank.account_service.entity.AccountStatus;
import com.bank.account_service.entity.AccountType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;  // ← Import enum
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data  // ← This generates ALL getters and setters
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateAccountRequest {
    @NotNull
    private Long customerId;
    
    @NotNull
    private AccountType accountType; // Use String to represent account type
    
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal initialDeposit;
    
    @NotBlank
    @Size(min=3, max=3, message="Currency must be 3 characters")
    private String currency;

}

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class AccountResponse {
    private Long id;
    private String accountNumber;
    private Long customerId;
    private Account accountType; // Use String to represent account type
    private BigDecimal balance;
    private String currency;
    private LocalDateTime createdAt;
    private AccountStatus status;
}

@Data
class BalanceUpdateRequest {
    @NotNull
    @DecimalMin(value="0.01", message="Amount musr be greater than 0")
    private BigDecimal amount;
    
    @NotBlank
    private String operation; // CREDIT or DEBIT
}
