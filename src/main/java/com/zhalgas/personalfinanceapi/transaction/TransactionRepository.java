package com.zhalgas.personalfinanceapi.transaction;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByUserIdOrderByDateDesc(Long userId);

    Optional<Transaction> findByIdAndUserId(Long transactionId, Long userId);

    List<Transaction> findByUserIdAndTypeOrderByDateDesc(Long userId, TransactionType type);

    List<Transaction> findByUserIdAndDateBetweenOrderByDateDesc(Long userId, LocalDate startDate, LocalDate endDate);

    List<Transaction> findByUserIdAndTypeAndDateBetweenOrderByDateDesc(Long userId, TransactionType type, LocalDate startDate, LocalDate endDate);
}
