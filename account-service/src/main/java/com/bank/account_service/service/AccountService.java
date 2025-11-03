package com.bank.account_service.service;

import com.bank.account_service.dto.*;
import com.bank.account_service.entity.Account;
//import com.bank.account_service.entity.AccountStatus;
//import com.bank.account_service.exception.InsufficientBalanceException;
import com.bank.account_service.repository.AccountRepository;
import com.bank.account_service.event.AccountEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import com.bank.account_service.entity.AccountResponse;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {
    
    private final AccountRepository accountRepository;
    private final KafkaTemplate<String, AccountEvent> kafkaTemplate;
    
    @Transactional
    @CacheEvict(value = "accounts", allEntries = true)
    public AccountResponse createAccount(CreateAccountRequest request) {
        String accountNumber = generateAccountNumber();
        
        Account account = Account.builder()
                .accountNumber(accountNumber)
                .customerId(request.getCustomerId())
                .accountType(request.getAccountType())
                .balance(request.getInitialDeposit())
                .status(AccountStatus.ACTIVE)
                .currency(request.getCurrency())
                .build();
        
        account = accountRepository.save(account);
        
        publishEvent("ACCOUNT_CREATED", account);
        
        return toResponse(account);
    }
    
    @Cacheable(value = "accounts", key = "#accountNumber")
    public AccountResponse getAccount(String accountNumber) {
        Account account = accountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found: " + accountNumber));
        return toResponse(account);
    }
    
    @Transactional
    public void updateBalance(String accountNumber, BigDecimal amount, String operation) {
        Account account = accountRepository.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException("Account not found"));
        
        if ("DEBIT".equals(operation)) {
            if (account.getBalance().compareTo(amount) < 0) {
                throw new InsufficientBalanceException("Insufficient balance");
            }
            account.setBalance(account.getBalance().subtract(amount));
        } else {
            account.setBalance(account.getBalance().add(amount));
        }
        
        accountRepository.save(account);
        publishEvent("BALANCE_UPDATED", account);
    }
    
    public List<AccountResponse> getCustomerAccounts(Long customerId) {
        return accountRepository.findByCustomerId(customerId).stream()
                .map(this::toResponse)
                .toList();
    }
    
    private String generateAccountNumber() {
        return "ACC" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
    
    private void publishEvent(String eventType, Account account) {
        AccountEvent event = AccountEvent.builder()
                .eventType(eventType)
                .accountNumber(account.getAccountNumber())
                .customerId(account.getCustomerId())
                .balance(account.getBalance())
                .timestamp(System.currentTimeMillis())
                .build();
        
        kafkaTemplate.send("account-events", account.getAccountNumber(), event);
        log.info("Published event: {} for account: {}", eventType, account.getAccountNumber());
    }
    
    private AccountResponse toResponse(Account account) {
        return AccountResponse.builder()
                .id(account.getId())
                .accountNumber(account.getAccountNumber())
                .customerId(account.getCustomerId())
                .accountType(account.getAccountType())
                .balance(account.getBalance())
                .status(account.getStatus())
                .currency(account.getCurrency())
                .createdAt(account.getCreatedAt())
                .build();
    }
}