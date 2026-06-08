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

    @GetMapping("/{id}")
    public ResponseEntity<TransactionDto> getTransactionById(
            Authentication authentication,
            @PathVariable Long id
    ) {
        String username = authentication.getName();
        TransactionDto transaction = transactionService.getTransactionById(username, id);
        return ResponseEntity.ok(transaction);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransactionById(
            Authentication authentication,
            @PathVariable Long id
    ) {
        String username = authentication.getName();
        transactionService.deleteTransaction(username, id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionDto> updateTransactionById(
            Authentication authentication,
            @PathVariable Long id,
            @RequestBody TransactionDto dto
    ) {
        String username = authentication.getName();
        TransactionDto updatedTransaction = transactionService.updateTransaction(username, id, dto);
        return ResponseEntity.ok(updatedTransaction);
    }
}
