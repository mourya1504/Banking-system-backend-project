package com.bank.transaction_service.Client;

import com.bank.transaction_service.dto.BalanceUpdateRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "account-service")
public interface AccountServiceClient {
    
    @PutMapping("/api/accounts/{accountNumber}/balance")
    void updateBalance(@PathVariable String accountNumber, @RequestBody BalanceUpdateRequest request);
}