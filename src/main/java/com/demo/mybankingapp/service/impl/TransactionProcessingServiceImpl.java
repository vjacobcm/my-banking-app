package com.demo.mybankingapp.service.impl;

import com.demo.mybankingapp.dto.BankAccountRequestDTO;
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
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

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
        // fetch all transactions, filter out processed ones (unprocessed are left)
        // for each transaction left, create a thread, transfer funds

        // what's the industry practice when it comes to making queries in general
        // less queries better ??
        Optional<List<BankTransaction>> transactions = transactionRepository.findByIsProcessed();

        List<BankTransaction> unprocessedTransactions = transactions.get();

        List<Future<String>> futures = new ArrayList<>();

        for (BankTransaction transaction : unprocessedTransactions) {
            futures.add(executor.submit(() -> {
                try {
                    Optional<BankAccount> checkDebitor = accountRepository.findByAccountNumber(transaction.getDebitor());
                    Optional<BankAccount> checkCreditor = accountRepository.findByAccountNumber(transaction.getCreditor());

                    if (checkDebitor.isEmpty() || checkCreditor.isEmpty()) {
                        return "Missing account for transaction ID: " + transaction.getTransactionID();
                    }

                    BankAccount debitor = checkDebitor.get();
                    BankAccount creditor = checkCreditor.get();

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
            }));
        }

        List<String> results = new ArrayList<>();
        for (Future<String> future : futures) {
            try {
                results.add(future.get());
            } catch (Exception e) {
                results.add("Error retrieving result: " + e.getMessage());
            }
        }

        return ResponseEntity.ok(results);
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
