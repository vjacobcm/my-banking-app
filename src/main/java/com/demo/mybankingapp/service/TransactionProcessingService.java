package com.demo.mybankingapp.service;


import com.demo.mybankingapp.dto.BankAccountRequestDTO;
import com.demo.mybankingapp.dto.BankTransferRequestDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TransactionProcessingService {
    ResponseEntity processTransactions();
    ResponseEntity addTransactions(List<BankTransferRequestDTO> bankTransferRequestDTOs);
}
