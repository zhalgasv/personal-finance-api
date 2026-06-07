package com.zhalgas.personalfinanceapi.transaction;

import com.zhalgas.personalfinanceapi.user.User;
import com.zhalgas.personalfinanceapi.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public List<TransactionDto> getTransactionsByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

       List<Transaction> transactions = transactionRepository.findByUserId(user.getId());

       return transactions.stream()
               .map(this::toDto)
               .toList();
    }

    private TransactionDto toDto(Transaction transaction) {
        TransactionDto dto = new TransactionDto();
        dto.setId(transaction.getId());
        dto.setAmount(transaction.getAmount());
        dto.setDate(transaction.getDate());
        dto.setDescription(transaction.getDescription());
        dto.setType(transaction.getType());
        return dto;
    }

    public TransactionDto createTransaction(String username, TransactionDto dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Transaction transaction = Transaction.builder()
                .amount(dto.getAmount())
                .description(dto.getDescription())
                .type(dto.getType())
                .date(dto.getDate())
                .user(user)
                .build();

        Transaction savedTransaction = transactionRepository.save(transaction);

        return toDto(savedTransaction);
    }
}
