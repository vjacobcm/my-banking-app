package com.demo.mybankingapp.controller;

import com.demo.mybankingapp.dto.BankAccountRequestDTO;
import com.demo.mybankingapp.dto.BankTransferRequestDTO;
import com.demo.mybankingapp.entity.BankAccount;
import com.demo.mybankingapp.service.AccountService;
import com.demo.mybankingapp.service.TransactionProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class Controller {
    
    @Autowired
    private TransactionProcessingService transactionProcessingService;

    @Autowired
    private AccountService accountService;

    @PostMapping(value="/api/add-transaction")
    ResponseEntity addTransaction(@RequestBody BankTransferRequestDTO bankTransferRequestDTO){
        return this.transactionProcessingService.addTransaction(bankTransferRequestDTO);
    }
    @PostMapping(value="/api/add-transactions")
    ResponseEntity addTransactions(@RequestBody List<BankTransferRequestDTO> bankTransferRequestDTOs){
        return this.transactionProcessingService.addTransactions(bankTransferRequestDTOs);
    }

    @PostMapping("/api/add-account")
    ResponseEntity addAccount(@RequestBody BankAccountRequestDTO accountDTO) {
        return accountService.addAccount(accountDTO);
    }
    @GetMapping("/api/accounts")
    public ResponseEntity getAllAccounts() {
        return accountService.getAllAccounts();
    }
}
