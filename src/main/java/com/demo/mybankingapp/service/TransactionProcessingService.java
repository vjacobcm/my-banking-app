package com.demo.mybankingapp.service;


import com.demo.mybankingapp.dto.BankAccountRequestDTO;
import com.demo.mybankingapp.dto.BankTransferRequestDTO;
import org.springframework.http.ResponseEntity;

public interface TransactionProcessingService {
    ResponseEntity processTransactions();
    ResponseEntity addTransaction(BankTransferRequestDTO bankTransferRequestDTO);
}
