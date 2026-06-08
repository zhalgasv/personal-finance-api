package com.zhalgas.personalfinanceapi.transaction;


import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Data
public class TransactionDto {

    private Long id;
    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    private String description;

    @NotNull(message = "Type is required")
    private TransactionType type;

    @NotNull(message = "Date is required")
    private LocalDate date;
}
