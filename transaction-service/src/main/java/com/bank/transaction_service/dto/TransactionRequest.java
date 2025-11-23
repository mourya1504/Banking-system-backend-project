package com.bank.transaction_service.dto;
import java.math.BigDecimal;

import com.bank.transaction_service.Entity.TransactionType;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TransactionRequest {
    @NotBlank
    private String accountNumber;
    
    private String toAccountNumber;
    
    @NotNull
    private TransactionType type;
    
    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;
    
    private String description;
    private String referenceNumber;
}
