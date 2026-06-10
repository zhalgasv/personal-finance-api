package com.zhalgas.personalfinanceapi.transaction;

import com.zhalgas.personalfinanceapi.exception.TransactionNotFoundException;
import com.zhalgas.personalfinanceapi.exception.UserNotFoundException;
import com.zhalgas.personalfinanceapi.user.User;
import com.zhalgas.personalfinanceapi.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public List<TransactionDto> getTransactionsByUsername(String username, TransactionType type, LocalDate from, LocalDate to) {
        User user = getUserByUsername(username);
        validateDateRange(from, to);
        List<Transaction> transactions = findTransactions(user, type, from, to);
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
        User user = getUserByUsername(username);
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
        User user = getUserByUsername(username);
        Transaction transaction = getTransactionByIdAndUserId(transactionId, user.getId());
        return toDto(transaction);
    }

    public void deleteTransaction(String username, Long transactionId) {
        User user = getUserByUsername(username);
        Transaction transaction = getTransactionByIdAndUserId(transactionId, user.getId());
        transactionRepository.delete(transaction);
    }

    public TransactionDto updateTransaction(String username, Long transactionId, TransactionDto dto) {
        User user = getUserByUsername(username);
        Transaction transaction = getTransactionByIdAndUserId(transactionId, user.getId());

        transaction.setAmount(dto.getAmount());
        transaction.setDate(dto.getDate());
        transaction.setDescription(dto.getDescription());
        transaction.setType(dto.getType());

        Transaction updatedTransaction = transactionRepository.save(transaction);

        return toDto(updatedTransaction);
    }

    public BalanceDto getBalance(String username) {
        User user = getUserByUsername(username);
        List<Transaction> transactions = transactionRepository.findByUserIdOrderByDateDesc(user.getId());
        BigDecimal income = calculateIncome(transactions);
        BigDecimal expense = calculateExpense(transactions);
        return new BalanceDto(income, expense, income.subtract(expense));
    }

    private void validateDateRange(LocalDate from, LocalDate to) {
        if ((from == null && to != null) || (from != null && to == null)) {
            throw new IllegalArgumentException("Both from and to dates must be provided");
        }
        if (from != null && to != null && from.isAfter(to)) {
            throw new IllegalArgumentException("From date cannot be after to date");
        }
    }

    private User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    private Transaction getTransactionByIdAndUserId(Long transactionId, Long userId) {
        return transactionRepository.findByIdAndUserId(transactionId, userId)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found"));
    }

    private List<Transaction> findTransactions(User user, TransactionType type, LocalDate from, LocalDate to) {
        List<Transaction> transactions;
        if (type != null && from != null && to != null) {
            transactions = transactionRepository.findByUserIdAndTypeAndDateBetweenOrderByDateDesc(user.getId(), type, from, to);
        } else if (from != null && to != null) {
            transactions = transactionRepository.findByUserIdAndDateBetweenOrderByDateDesc(user.getId(), from, to);
        } else if (type != null) {
            transactions = transactionRepository.findByUserIdAndTypeOrderByDateDesc(user.getId(), type);
        } else {
            transactions = transactionRepository.findByUserIdOrderByDateDesc(user.getId());
        }
        return transactions;
    }

    private BigDecimal calculateIncome(List<Transaction> transactions) {
        BigDecimal income = BigDecimal.ZERO;
        for (Transaction transaction : transactions) {
            if (transaction.getType() == TransactionType.INCOME) {
                income = income.add(transaction.getAmount());
            }
        }
        return income;
    }

    private BigDecimal calculateExpense(List<Transaction> transactions) {
        BigDecimal expense = BigDecimal.ZERO;
        for (Transaction transaction : transactions) {
            if (transaction.getType() == TransactionType.EXPENSE) {
                expense = expense.add(transaction.getAmount());
            }
        }
        return expense;
    }
}
