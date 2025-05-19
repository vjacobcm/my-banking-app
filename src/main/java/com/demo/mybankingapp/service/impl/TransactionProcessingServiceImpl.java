package com.demo.mybankingapp.service.impl;

import com.demo.mybankingapp.repository.TransactionRepository;
import com.demo.mybankingapp.service.TransactionProcessingService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class TransactionProcessingServiceImpl implements TransactionProcessingService {
    
    private TransactionRepository transactionRepository;

    @Override
    public ResponseEntity processTransactions() {
        // fetch all transactions, filter out processed ones (unprocessed are left)
        // for each transaction left, create a thread, transfer funds
//        transactionRepository.findAll();
        return null;
    }
}
