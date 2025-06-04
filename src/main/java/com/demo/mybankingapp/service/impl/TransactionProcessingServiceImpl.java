package com.demo.mybankingapp.service.impl;

import com.demo.mybankingapp.dto.BankTransferRequestDTO;
import com.demo.mybankingapp.entity.BankTransaction;
import com.demo.mybankingapp.repository.AccountRepository;
import com.demo.mybankingapp.repository.TransactionRepository;
import com.demo.mybankingapp.service.TransactionProcessingService;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TransactionProcessingServiceImpl implements TransactionProcessingService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;
    private final ExecutorService executor = Executors.newFixedThreadPool(4);
    public ResponseEntity processTransactions() {
        log.info("Starting to process transactions");
        List<BankTransaction> transactions = transactionRepository.findByIsProcessed()
                .orElse(Collections.emptyList());

        if (transactions.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK).body(List.of("No unprocessed transactions found."));
        }

        List<CompletableFuture<String>> futures = transactions.stream()
                .map(ts -> CompletableFuture.supplyAsync(() -> processSingleTransaction(ts), executor))
                .toList();

        List<String> results = futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());

        return ResponseEntity.status(HttpStatus.OK).body(results);
    }
    @Transactional
    private String processSingleTransaction(BankTransaction transaction) {
        try {
            log.info("Thread {} processing transactionId: {}", Thread.currentThread().getName(), transaction.transactionID);
            var debitorOpt = accountRepository.findByAccountNumber(transaction.getDebitor());
            var creditorOpt = accountRepository.findByAccountNumber(transaction.getCreditor());

            if (debitorOpt.isEmpty() || creditorOpt.isEmpty()) {
                return "Transaction ID " + transaction.getTransactionID() + ": Failed - Missing account";
            }

            var debitor = debitorOpt.get();
            var creditor = creditorOpt.get();

            if (debitor.getBalance() < transaction.getAmount()) {
                return "Transaction ID " + transaction.getTransactionID() + ": Failed - Insufficient funds";
            }

            debitor.setBalance(debitor.getBalance() - transaction.getAmount());
            creditor.setBalance(creditor.getBalance() + transaction.getAmount());

            accountRepository.save(debitor);
            // or here
//            if (Math.random() > 0.7) { // Introduce failure randomly
//                throw new RuntimeException("Task failed on thread: " + Thread.currentThread().getName());
//            } 
            accountRepository.save(creditor);

            transaction.setProcessed(true);
            transactionRepository.save(transaction);

            return "Transaction ID " + transaction.getTransactionID() + ": Success";

        } catch (Exception e) {
            return "Transaction ID " + transaction.getTransactionID() + ": Error - " + e.getMessage();
        }
    }
    
    @Override
    public ResponseEntity addTransactions(List<BankTransferRequestDTO> bankTransferRequestDTOs){
        try{
            bankTransferRequestDTOs.forEach(bankTransferRequestDTO -> {
                BankTransaction newTransaction = new BankTransaction();
                newTransaction.setDebitor(bankTransferRequestDTO.getDebitAccountNumber());
                newTransaction.setCreditor(bankTransferRequestDTO.getCreditAccountNumber());
                newTransaction.setAmount(bankTransferRequestDTO.getAmount());
                newTransaction.setProcessed(false);
                transactionRepository.save(newTransaction);
            });
            return ResponseEntity.status(HttpStatus.CREATED).body("Transactions added");
        }
        catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: " + e.getMessage());
        }
    }
}
