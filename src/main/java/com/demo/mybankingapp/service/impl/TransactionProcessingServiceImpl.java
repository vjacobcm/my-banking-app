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
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@Slf4j
public class TransactionProcessingServiceImpl implements TransactionProcessingService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private AccountRepository accountRepository;

    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    private final Map<String, Object> accountLocks = new ConcurrentHashMap<>();

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
    public String processSingleTransaction(BankTransaction transaction) {
        String debitorAccNum = transaction.getDebitor();
        String creditorAccNum = transaction.getCreditor();
        // sort first
        // ensures that threads acquire locks in the same order
        List<String> lockOrder = Stream.of(debitorAccNum, creditorAccNum)
                .sorted()
                .toList();
        // account number : java object
        Object lock1 = getLockForAccount(lockOrder.get(0));
        Object lock2 = getLockForAccount(lockOrder.get(1));

        // only one thread can read/write the same account pair at a time
        // other threads involving different accounts can proceed concurrently
        synchronized (lock1) {
            synchronized (lock2) {
                try {
                    log.info("Thread {} processing transactionId: {}",
                            Thread.currentThread().getName(),
                            transaction.getTransactionID());

                    var debitorOpt = accountRepository.findByAccountNumber(debitorAccNum);
                    var creditorOpt = accountRepository.findByAccountNumber(creditorAccNum);

                    log.info("debitorOpt is {}", debitorOpt.isPresent());
                    log.info("creditorOpt is {}", creditorOpt.isPresent());

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
                    accountRepository.save(creditor);

                    transaction.setProcessed(true);
                    transactionRepository.save(transaction);

                    return "Transaction ID " + transaction.getTransactionID() + ": Success";

                } catch (Exception e) {
                    log.error("Error processing transaction {}", transaction.getTransactionID(), e);
                    return "Transaction ID " + transaction.getTransactionID() + ": Error - " + e.getMessage();
                }
            }
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

    private Object getLockForAccount(String accountNumber){
        return accountLocks.computeIfAbsent(accountNumber, k -> new Object());
    }

    @Transactional
    public String processSingleTransactionBugged(BankTransaction transaction) {
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

            accountRepository.save(creditor);

            transaction.setProcessed(true);
            transactionRepository.save(transaction);

            return "Transaction ID " + transaction.getTransactionID() + ": Success";

        } catch (Exception e) {
            return "Transaction ID " + transaction.getTransactionID() + ": Error - " + e.getMessage();
        }
    }
}
