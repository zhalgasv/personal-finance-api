package com.zhalgas.personalfinanceapi.transaction;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionDto {

    private Long id;
    private BigDecimal amount;
    private String description;
    private TransactionType type;
    private LocalDate date;
}
