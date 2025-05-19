package com.demo.mybankingapp.service.impl;

import com.demo.mybankingapp.dto.BankTransferRequestDTO;
import com.demo.mybankingapp.entity.BankTransaction;
import com.demo.mybankingapp.repository.TransactionRepository;
import com.demo.mybankingapp.service.TransactionProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class TransactionProcessingServiceImpl implements TransactionProcessingService {
    @Autowired
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
    @Override
    public ResponseEntity addTransaction(BankTransferRequestDTO bankTransferRequestDTO){
        try{
            BankTransaction newTransaction = new BankTransaction();
            newTransaction.setDebitor(bankTransferRequestDTO.getDebitAccountNumber());
            newTransaction.setCreditor(bankTransferRequestDTO.getCreditAccountNumber());
            newTransaction.setAmount(bankTransferRequestDTO.getAmount());
            newTransaction.setProcessed(false);
            
            transactionRepository.save(newTransaction);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
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
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        catch (Exception e){
            log.error(e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
}
