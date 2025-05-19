package com.demo.mybankingapp.controller;

import com.demo.mybankingapp.dto.BankTransferRequestDTO;
import com.demo.mybankingapp.service.TransactionProcessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionController {
    
    @Autowired
    private TransactionProcessingService transactionProcessingService;
    
    @PostMapping(value="/api/add-transaction")
    ResponseEntity addTransaction(@RequestBody BankTransferRequestDTO bankTransferRequestDTO){
        return this.transactionProcessingService.addTransaction(bankTransferRequestDTO);
    }
    
}
