package com.zhalgas.personalfinanceapi.transaction;

import com.zhalgas.personalfinanceapi.exception.TransactionNotFoundException;
import com.zhalgas.personalfinanceapi.exception.UserNotFoundException;
import com.zhalgas.personalfinanceapi.user.User;
import com.zhalgas.personalfinanceapi.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public List<TransactionDto> getTransactionsByUsername(String username, TransactionType type) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        List<Transaction> transactions;
        if(type == null) {
            transactions = transactionRepository.findByUserIdOrderByDateDesc(user.getId());
        } else {
            transactions = transactionRepository.findByUserIdAndTypeOrderByDateDesc(user.getId(), type);
        }

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
                .orElseThrow(() -> new UserNotFoundException("User not found"));

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

    public TransactionDto getTransactionById(String username, Long transactionId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Transaction transaction = transactionRepository.findByIdAndUserId(transactionId, user.getId())
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));

        return toDto(transaction);
    }

    public void deleteTransaction(String username, Long transactionId) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Transaction transaction = transactionRepository.findByIdAndUserId(transactionId, user.getId())
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));

        transactionRepository.delete(transaction);
    }

    public TransactionDto updateTransaction(String username, Long transactionId, TransactionDto dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Transaction transaction = transactionRepository.findByIdAndUserId(transactionId, user.getId())
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));

        transaction.setAmount(dto.getAmount());
        transaction.setDate(dto.getDate());
        transaction.setDescription(dto.getDescription());
        transaction.setType(dto.getType());

        Transaction updatedTransaction = transactionRepository.save(transaction);

        return toDto(updatedTransaction);
    }

    public BalanceDto getBalance(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        List<Transaction> transactions = transactionRepository.findByUserIdOrderByDateDesc(user.getId());

        BigDecimal income = BigDecimal.ZERO;
        BigDecimal expense = BigDecimal.ZERO;

        for(Transaction transaction : transactions) {
            if(transaction.getType() == TransactionType.INCOME) {
                income = income.add(transaction.getAmount());
            } else if(transaction.getType() == TransactionType.EXPENSE) {
                expense = expense.add(transaction.getAmount());
            }
        }

        BigDecimal balance = income.subtract(expense);

        return new BalanceDto(income, expense, balance);
    }
}
