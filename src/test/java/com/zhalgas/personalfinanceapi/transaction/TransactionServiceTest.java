package com.zhalgas.personalfinanceapi.transaction;

import com.zhalgas.personalfinanceapi.exception.TransactionNotFoundException;
import com.zhalgas.personalfinanceapi.exception.UserNotFoundException;
import com.zhalgas.personalfinanceapi.user.User;
import com.zhalgas.personalfinanceapi.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private TransactionService transactionService;

    @Test
    void getBalance_shouldCalculateIncomeExpenseAndBalance() {
        User user = User.builder()
                .id(1L)
                .username("adil")
                .build();

        Transaction income = Transaction.builder()
                .amount(new BigDecimal("5000"))
                .type(TransactionType.INCOME)
                .user(user)
                .build();

        Transaction expense = Transaction.builder()
                .amount(new BigDecimal("1200"))
                .type(TransactionType.EXPENSE)
                .user(user)
                .build();

        when(userRepository.findByUsername("adil"))
                .thenReturn(Optional.of(user));

        when(transactionRepository.findByUserIdOrderByDateDesc(1L))
                .thenReturn(List.of(income, expense));

        BalanceDto result = transactionService.getBalance("adil");

        assertEquals(new BigDecimal("5000"), result.getIncome());
        assertEquals(new BigDecimal("1200"), result.getExpense());
        assertEquals(new BigDecimal("3800"), result.getBalance());

        verify(userRepository).findByUsername("adil");
        verify(transactionRepository).findByUserIdOrderByDateDesc(1L);
    }

    @Test
    void getBalance_shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByUsername("adil"))
                .thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> transactionService.getBalance("adil"));
        verify(transactionRepository, never()).findByUserIdOrderByDateDesc(anyLong());
    }

    @Test
    void getTransactionById_shouldReturnTransactionWhenExists() {
        when(userRepository.findByUsername("adil"))
                .thenReturn(Optional.of(User.builder().id(1L).username("adil").build()));

        when(transactionRepository.findByIdAndUserId(10L, 1L))
                .thenReturn(Optional.of(Transaction.builder().id(10L).description("Salary").amount(new BigDecimal("5000")).type(TransactionType.INCOME).build()));

        TransactionDto result = transactionService.getTransactionById("adil", 10L);

        assertEquals(new BigDecimal("5000"), result.getAmount());
        assertEquals(TransactionType.INCOME, result.getType());
        assertEquals("Salary", result.getDescription());
        verify(userRepository).findByUsername("adil");
        verify(transactionRepository).findByIdAndUserId(10L, 1L);
    }

    @Test
    void getTransactionById_shouldThrowExceptionWhenTransactionNotFound() {
        when(userRepository.findByUsername("adil"))
                .thenReturn(Optional.of(User.builder().id(1L).username("adil").build()));

        when(transactionRepository.findByIdAndUserId(10L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () ->
                transactionService.getTransactionById("adil", 10L));

        verify(userRepository).findByUsername("adil");
        verify(transactionRepository).findByIdAndUserId(10L, 1L);
    }

    @Test
    void deleteTransaction_shouldDeleteTransactionWhenExists() {
        User user = User.builder()
                .id(1L)
                .username("adil")
                .build();

        Transaction transaction = Transaction.builder()
                .id(1L)
                .amount(new BigDecimal("150"))
                .description("Groceries")
                .type(TransactionType.EXPENSE)
                .user(user)
                .build();

        when(userRepository.findByUsername("adil"))
                .thenReturn(Optional.of(user));

        when(transactionRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.of(transaction));

        transactionService.deleteTransaction("adil", 1L);

        verify(userRepository).findByUsername("adil");
        verify(transactionRepository).findByIdAndUserId(1L, 1L);
        verify(transactionRepository).delete(transaction);
    }

    @Test
    void deleteTransaction_shouldThrowExceptionWhenTransactionNotFound() {
        User user = User.builder()
                .id(1L)
                .username("adil")
                .build();

        when(userRepository.findByUsername("adil"))
                .thenReturn(Optional.of(user));

        when(transactionRepository.findByIdAndUserId(1L, 1L))
                .thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class, () ->
                transactionService.deleteTransaction("adil", 1L));

       verify(userRepository).findByUsername("adil");
       verify(transactionRepository).findByIdAndUserId(1L, 1L);
       verify(transactionRepository, never()).delete(any(Transaction.class));
    }
}