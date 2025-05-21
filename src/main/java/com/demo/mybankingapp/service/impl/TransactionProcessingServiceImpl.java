package com.demo.mybankingapp.service.impl;

import com.demo.mybankingapp.dto.BankTransferRequestDTO;
import com.demo.mybankingapp.entity.BankAccount;
import com.demo.mybankingapp.entity.BankTransaction;
import com.demo.mybankingapp.repository.AccountRepository;
import com.demo.mybankingapp.repository.TransactionRepository;
import com.demo.mybankingapp.service.TransactionProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
@Slf4j
public class TransactionProcessingServiceImpl implements TransactionProcessingService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    @Override
    public ResponseEntity processTransactions() {
        Optional<List<BankTransaction>> transactions = transactionRepository.findByIsNotProcessed();
        if (transactions.isEmpty()) {
            return ResponseEntity.ok(Collections.emptyList());
        }

        List<Future<String>> futures = submitTransactionTasks(transactions.get());
        List<String> results = collectTaskResults(futures);

        return ResponseEntity.ok(results);
    }

    private List<Future<String>> submitTransactionTasks(List<BankTransaction> transactions) {
        List<Future<String>> futures = new ArrayList<>();

        for (BankTransaction transaction : transactions) {
            futures.add(executor.submit(() -> processSingleTransaction(transaction)));
        }

        return futures;
    }
    private String processSingleTransaction(BankTransaction transaction) {
        try {
            BankAccount debitor = accountRepository.findByAccountNumber(transaction.getDebitor())
                    .orElse(null);
            BankAccount creditor = accountRepository.findByAccountNumber(transaction.getCreditor())
                    .orElse(null);

            if (debitor == null || creditor == null) {
                return "Missing account for transaction ID: " + transaction.getTransactionID();
            }

            if (debitor.getBalance() < transaction.getAmount()) {
                return "Insufficient funds for transaction ID: " + transaction.getTransactionID();
            }

            debitor.setBalance(debitor.getBalance() - transaction.getAmount());
            creditor.setBalance(creditor.getBalance() + transaction.getAmount());

            accountRepository.save(debitor);
            accountRepository.save(creditor);

            transaction.setProcessed(true);
            transactionRepository.save(transaction);

            return "Transaction ID " + transaction.getTransactionID() + " processed successfully.";
        } catch (Exception e) {
            return "Error processing transaction ID: " + transaction.getTransactionID() + " - " + e.getMessage();
        }
    }

    private List<String> collectTaskResults(List<Future<String>> futures) {
        List<String> results = new ArrayList<>();
        for (Future<String> future : futures) {
            try {
                results.add(future.get());
            } catch (Exception e) {
                results.add("Error retrieving result: " + e.getMessage());
            }
        }
        return results;
    }

    @Override
    public ResponseEntity addTransaction(BankTransferRequestDTO bankTransferRequestDTO){
        try{
            BankTransaction newTransaction = new BankTransaction();
            newTransaction.setDebitor(bankTransferRequestDTO.getDebitAccountNumber());
            newTransaction.setCreditor(bankTransferRequestDTO.getCreditAccountNumber());
            newTransaction.setAmount(bankTransferRequestDTO.getAmount());
            newTransaction.setProcessed(false);
            
            transactionRepository.save(newTransaction);
            return ResponseEntity.status(HttpStatus.CREATED).body("Transaction added");
        }
        catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }
}
