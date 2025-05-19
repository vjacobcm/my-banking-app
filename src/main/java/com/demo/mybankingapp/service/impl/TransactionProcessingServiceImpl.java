package com.demo.mybankingapp.service.impl;

import com.demo.mybankingapp.entity.BankTransaction;
import com.demo.mybankingapp.repository.TransactionRepository;
import com.demo.mybankingapp.service.TransactionProcessingService;
import jakarta.transaction.Transaction;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class TransactionProcessingServiceImpl implements TransactionProcessingService {
    
    private TransactionRepository transactionRepository;
    private int THREADS = 4;
    @Override
    public ResponseEntity processTransactions() {
        // fetch all transactions, filter out processed ones (unprocessed are left)
        // for each transaction left, create a thread, transfer funds

        // what's the industry practice when it comes to making queries in general
        // less queries better ??

//        Optional<List<BankTransaction>> transactions = transactionRepository.findByIsProcessed();
//        if (transactions.isPresent()) {
//            transactions.get().forEach(transaction -> {
//                transaction.getDebitor()
//            })
//        }
        return null;
    }
}
