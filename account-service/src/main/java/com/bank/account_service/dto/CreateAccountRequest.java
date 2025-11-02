package com.bank.account_service.dto;



// import com.bank.account_service.entity.AccountType;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.bank.account_service.entity.AccountStatus;

@Data
public class CreateAccountRequest {
    @NotNull
    private Long customerId;
    
    @NotNull
    private String accountType; // Use String to represent account type
    
    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal initialDeposit;
    
    @NotBlank
    private String currency = "USD";
}

@Data
class AccountResponse {
    private Long id;
    private String accountNumber;
    private Long customerId;
    private String accountType; // Use String to represent account type
    private BigDecimal balance;
    public  AccountStatus status;
    private String currency;
    private LocalDateTime createdAt;
}

@Data
class BalanceUpdateRequest {
    @NotNull
    private BigDecimal amount;
    
    @NotBlank
    private String operation; // CREDIT or DEBIT
}
