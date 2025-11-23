package com.bank.transaction_service.Entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "transactions", indexes = {
    @Index(name = "idx_account_number", columnList = "accountNumber"),
    @Index(name = "idx_transaction_date", columnList = "transactionDate")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String transactionId;
    
    @Column(nullable = false)
    private String accountNumber;
    
    private String toAccountNumber;
    
    @Enumerated(EnumType.STRING)
    private TransactionType type;
    
    @Column(nullable = false)
    private BigDecimal amount;
    
    private BigDecimal balanceAfter;
    
    @Enumerated(EnumType.STRING)
    private TransactionStatus status;
    
    private String description;
    
    private String referenceNumber;
    
    @Column(name = "transaction_date")
    private LocalDateTime transactionDate;
    
    private String failureReason;
    
    @PrePersist
    protected void onCreate() {
        transactionDate = LocalDateTime.now();
    }
}

