package com.bank.transaction_service.dto;

// File: transaction-service/src/main/java/com/bank/transaction/dto/TransactionStatementResponse.java

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionStatementResponse {
    
    /**
     * Account number for which the statement is generated
     */
    private String accountNumber;
    
    /**
     * Statement period start date
     */
    private LocalDateTime startDate;
    
    /**
     * Statement period end date
     */
    private LocalDateTime endDate;
    
    /**
     * List of transactions in the statement period
     */
    private List<TransactionResponse> transactions;
    
    /**
     * Total amount deposited in the period
     */
    private BigDecimal totalDeposits;
    
    /**
     * Total amount withdrawn in the period
     */
    private BigDecimal totalWithdrawals;
    
    /**
     * Total number of transactions in the period
     */
    private int transactionCount;
    
    /**
     * Opening balance at the start of the period (optional)
     */
    private BigDecimal openingBalance;
    
    /**
     * Closing balance at the end of the period (optional)
     */
    private BigDecimal closingBalance;
    
    /**
     * Net change in balance (deposits - withdrawals)
     */
    private BigDecimal netChange;
}
