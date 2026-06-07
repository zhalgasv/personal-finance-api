package com.zhalgas.personalfinanceapi.transaction;


import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping
    public ResponseEntity<List<TransactionDto>> getMyTransactions(Authentication authentication) {
        String username = authentication.getName();
        List<TransactionDto> transactions = transactionService.getTransactionsByUsername(username);
        return ResponseEntity.ok(transactions);
    }

    @PostMapping
    public ResponseEntity<TransactionDto> createTransaction(
            Authentication authentication,
            @RequestBody TransactionDto dto
    ) {
        String username = authentication.getName();
        TransactionDto createdTransaction = transactionService.createTransaction(username, dto);
        return ResponseEntity.ok(createdTransaction);
    }
}
