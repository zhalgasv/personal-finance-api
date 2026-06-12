package com.zhalgas.personalfinanceapi.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zhalgas.personalfinanceapi.auth.JwUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransactionController.class)
@AutoConfigureMockMvc(addFilters = false)
class TransactionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransactionService transactionService;

    @MockitoBean
    private JwUtil jwUtil;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getBalance_shouldReturnBalance() throws Exception {
        BalanceDto balance = new BalanceDto(
                new BigDecimal("5000"),
                new BigDecimal("1200"),
                new BigDecimal("3800")
        );

        when(transactionService.getBalance("adil")).thenReturn(balance);

        mockMvc.perform(get("/api/transactions/balance")
                        .principal(new UsernamePasswordAuthenticationToken("adil", null)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.income").value(5000))
                .andExpect(jsonPath("$.expense").value(1200))
                .andExpect(jsonPath("$.balance").value(3800));
    }

    @Test
    void getMyTransactions_shouldReturnTransactions() throws Exception {
        TransactionDto dto = new TransactionDto();
        dto.setId(1L);
        dto.setAmount(new BigDecimal("5000"));
        dto.setDescription("Salary");
        dto.setType(TransactionType.INCOME);
        dto.setDate(LocalDate.now());

        when(transactionService.getTransactionsByUsername("adil", null, null, null))
                .thenReturn(List.of(dto));

        mockMvc.perform(get("/api/transactions")
                        .principal(new UsernamePasswordAuthenticationToken("adil", null)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].description").value("Salary"))
                .andExpect(jsonPath("$[0].type").value("INCOME"));
    }

    @Test
    void createTransaction_shouldReturnCreatedTransaction() throws Exception {
        TransactionDto dto = new TransactionDto();
        dto.setId(1L);
        dto.setAmount(new BigDecimal("5000"));
        dto.setDescription("Salary");
        dto.setType(TransactionType.INCOME);
        dto.setDate(LocalDate.now());

        when(transactionService.createTransaction(eq("adil"), any(TransactionDto.class)))
                .thenReturn(dto);

        mockMvc.perform(post("/api/transactions")
                        .principal(new UsernamePasswordAuthenticationToken("adil", null))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Salary"))
                .andExpect(jsonPath("$.type").value("INCOME"));
    }

    @Test
    void deleteTransaction_shouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/api/transactions/1")
                        .principal(new UsernamePasswordAuthenticationToken("adil", null)))
                .andExpect(status().isNoContent());

        verify(transactionService).deleteTransaction("adil", 1L);
    }

    @Test
    void getTransactionById_shouldReturnTransaction() throws Exception {
        TransactionDto dto = new TransactionDto();
        dto.setId(1L);
        dto.setAmount(new BigDecimal("5000"));
        dto.setDescription("Salary");
        dto.setType(TransactionType.INCOME);
        dto.setDate(LocalDate.now());

        when(transactionService.getTransactionById("adil", 1L))
                .thenReturn(dto);

        mockMvc.perform(get("/api/transactions/1")
                        .principal(new UsernamePasswordAuthenticationToken("adil", null)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.description").value("Salary"))
                .andExpect(jsonPath("$.type").value("INCOME"));
    }

    @Test
    void updateTransaction_shouldReturnUpdatedTransaction() throws Exception {
        TransactionDto dto = new TransactionDto();
        dto.setId(1L);
        dto.setAmount(new BigDecimal("1200"));
        dto.setDescription("Updated salary");
        dto.setType(TransactionType.INCOME);
        dto.setDate(LocalDate.now());

        when(transactionService.updateTransaction(eq("adil"), eq(1L), any(TransactionDto.class)))
                .thenReturn(dto);

        mockMvc.perform(put("/api/transactions/1")
                        .principal(new UsernamePasswordAuthenticationToken("adil", null))
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.amount").value(1200))
                .andExpect(jsonPath("$.description").value("Updated salary"))
                .andExpect(jsonPath("$.type").value("INCOME"));
    }
}
