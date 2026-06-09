package com.zhalgas.personalfinanceapi.transaction;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class BalanceDto {
    private BigDecimal income;
    private BigDecimal expense;
    private BigDecimal balance;
}

