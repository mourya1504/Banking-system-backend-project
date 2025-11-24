package com.bank.transaction_service.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bank.transaction_service.Client.AccountServiceClient;
import com.bank.transaction_service.Entity.Transaction;
import com.bank.transaction_service.Entity.TransactionStatus;
import com.bank.transaction_service.Entity.TransactionType;
import com.bank.transaction_service.Event.TransactionEvent;
import com.bank.transaction_service.Exception.TransactionFailedException;
import com.bank.transaction_service.Repository.TransactionRepository;
import com.bank.transaction_service.dto.BalanceUpdateRequest;
import com.bank.transaction_service.dto.TransactionRequest;
import com.bank.transaction_service.dto.TransactionResponse;
import com.bank.transaction_service.dto.TransactionStatementResponse;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionService {
    
    private final TransactionRepository transactionRepository;
    private final AccountServiceClient accountServiceClient;
    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;
    
    @Transactional
    @CircuitBreaker(name = "transactionService", fallbackMethod = "transactionFallback")
    public TransactionResponse processTransaction(TransactionRequest request) {
        String transactionId = generateTransactionId();
        
        Transaction transaction = Transaction.builder()
                .transactionId(transactionId)
                .accountNumber(request.getAccountNumber())
                .toAccountNumber(request.getToAccountNumber())
                .type(request.getType())
                .amount(request.getAmount())
                .status(TransactionStatus.PENDING)
                .description(request.getDescription())
                .referenceNumber(request.getReferenceNumber())
                .build();
        
        transaction = transactionRepository.save(transaction);
        
        try {
            switch (request.getType()) {
                case DEPOSIT -> processDeposit(transaction);
                case WITHDRAWAL -> processWithdrawal(transaction);
                case TRANSFER -> processTransfer(transaction);
                default -> throw new IllegalArgumentException("Unsupported transaction type");
            }
            
            transaction.setStatus(TransactionStatus.COMPLETED);
            publishEvent("TRANSACTION_COMPLETED", transaction);
            
        } catch (IllegalArgumentException e) {
            log.error("Transaction failed: {}", e.getMessage());
            transaction.setStatus(TransactionStatus.FAILED);
            transaction.setFailureReason(e.getMessage());
            publishEvent("TRANSACTION_FAILED", transaction);
            throw new TransactionFailedException("Transaction failed: " + e.getMessage());
        } finally {
            transaction = transactionRepository.save(transaction);
        }
        
        return toResponse(transaction);
    }
    
    private void processDeposit(Transaction transaction) {
        BalanceUpdateRequest request = new BalanceUpdateRequest();
        request.setAmount(transaction.getAmount());
        request.setOperation("CREDIT");
        
        accountServiceClient.updateBalance(transaction.getAccountNumber(), request);
        log.info("Deposit processed for account: {}", transaction.getAccountNumber());
    }
    
    private void processWithdrawal(Transaction transaction) {
        BalanceUpdateRequest request = new BalanceUpdateRequest();
        request.setAmount(transaction.getAmount());
        request.setOperation("DEBIT");
        
        accountServiceClient.updateBalance(transaction.getAccountNumber(), request);
        log.info("Withdrawal processed for account: {}", transaction.getAccountNumber());
    }
    
    private void processTransfer(Transaction transaction) {
        // Debit from source account
        BalanceUpdateRequest debitRequest = new BalanceUpdateRequest();
        debitRequest.setAmount(transaction.getAmount());
        debitRequest.setOperation("DEBIT");
        accountServiceClient.updateBalance(transaction.getAccountNumber(), debitRequest);
        
        // Credit to destination account
        BalanceUpdateRequest creditRequest = new BalanceUpdateRequest();
        creditRequest.setAmount(transaction.getAmount());
        creditRequest.setOperation("CREDIT");
        accountServiceClient.updateBalance(transaction.getToAccountNumber(), creditRequest);
        
        log.info("Transfer processed from {} to {}", 
                 transaction.getAccountNumber(), transaction.getToAccountNumber());
    }
    
    public TransactionResponse getTransaction(String transactionId) {
        Transaction transaction = transactionRepository.findByTransactionId(transactionId)
                .orElseThrow(() -> new RuntimeException("Transaction not found"));
        return toResponse(transaction);
    }
    
    public Page<TransactionResponse> getAccountTransactions(String accountNumber, Pageable pageable) {
        return transactionRepository.findByAccountNumber(accountNumber, pageable)
                .map(this::toResponse);
    }
    
    public TransactionStatementResponse getStatement(String accountNumber, 
                                                      LocalDateTime startDate, 
                                                      LocalDateTime endDate) {
        var transactions = transactionRepository
                .findByAccountNumberAndTransactionDateBetween(accountNumber, startDate, endDate);
        
        BigDecimal totalDeposits = transactions.stream()
                .filter(t -> t.getType() == TransactionType.DEPOSIT && 
                            t.getStatus() == TransactionStatus.COMPLETED)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalWithdrawals = transactions.stream()
                .filter(t -> (t.getType() == TransactionType.WITHDRAWAL || 
                             t.getType() == TransactionType.TRANSFER) && 
                            t.getStatus() == TransactionStatus.COMPLETED)
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return TransactionStatementResponse.builder()
                .accountNumber(accountNumber)
                .startDate(startDate)
                .endDate(endDate)
                .transactions(transactions.stream().map(this::toResponse).toList())
                .totalDeposits(totalDeposits)
                .totalWithdrawals(totalWithdrawals)
                .transactionCount(transactions.size())
                .build();
    }
    
    private String generateTransactionId() {
        return "TXN" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
    
    private void publishEvent(String eventType, Transaction transaction) {
        TransactionEvent event = TransactionEvent.builder()
                .eventType(eventType)
                .transactionId(transaction.getTransactionId())
                .accountNumber(transaction.getAccountNumber())
                .amount(transaction.getAmount())
                .type(transaction.getType().name())
                .status(transaction.getStatus().name())
                .timestamp(System.currentTimeMillis())
                .build();
        
        kafkaTemplate.send("transaction-events", transaction.getTransactionId(), event);
        log.info("Published event: {} for transaction: {}", eventType, transaction.getTransactionId());
    }
    
    private TransactionResponse toResponse(Transaction transaction) {
        return TransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .accountNumber(transaction.getAccountNumber())
                .toAccountNumber(transaction.getToAccountNumber())
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .status(transaction.getStatus())
                .description(transaction.getDescription())
                .transactionDate(transaction.getTransactionDate())
                .failureReason(transaction.getFailureReason())
                .build();
    }
    
    private TransactionResponse transactionFallback(TransactionRequest request, Exception e) {
        log.error("Circuit breaker activated for transaction", e);
        throw new TransactionFailedException("Service temporarily unavailable. Please try again later.");
    }
}