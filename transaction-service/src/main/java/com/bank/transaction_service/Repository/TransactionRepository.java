package com.bank.transaction_service.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bank.transaction_service.Entity.Transaction;
import com.bank.transaction_service.Entity.TransactionStatus;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Optional<Transaction> findByTransactionId(String transactionId);
    
    Page<Transaction> findByAccountNumber(String accountNumber, Pageable pageable);
    
    List<Transaction> findByAccountNumberAndTransactionDateBetween(
        String accountNumber, LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT t FROM Transaction t WHERE t.accountNumber = :accountNumber " +
           "AND t.status = :status ORDER BY t.transactionDate DESC")
    List<Transaction> findRecentTransactionsByStatus(
        String accountNumber, TransactionStatus status, Pageable pageable);
}