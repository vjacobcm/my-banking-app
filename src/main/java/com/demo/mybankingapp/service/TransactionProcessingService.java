package com.demo.mybankingapp.service;


import org.springframework.http.ResponseEntity;

public interface TransactionProcessingService {
    ResponseEntity processTransactions();
}
