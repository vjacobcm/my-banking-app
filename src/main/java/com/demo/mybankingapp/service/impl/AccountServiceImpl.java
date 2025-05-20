package com.demo.mybankingapp.service.impl;

import com.demo.mybankingapp.dto.BankAccountRequestDTO;
import com.demo.mybankingapp.entity.BankAccount;
import com.demo.mybankingapp.repository.AccountRepository;
import com.demo.mybankingapp.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class AccountServiceImpl implements AccountService {

    @Autowired
    AccountRepository accountRepository;

    @Override
    public ResponseEntity addAccount(BankAccountRequestDTO dto) {
        try {
            Optional<BankAccount> existing = accountRepository.findByAccountNumber(dto.getAccountNumber());
            if (existing.isPresent()) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body("Account already exists");
            }

            BankAccount account = new BankAccount();
            account.setAccountId(UUID.randomUUID());
            account.setOwnerName(dto.getOwnerName());
            account.setAccountNumber(dto.getAccountNumber());
            account.setBalance(dto.getBalance());
            account.setActive(dto.isActive());

            accountRepository.save(account);
            return ResponseEntity.status(HttpStatus.CREATED).body("Account created successfully");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
    @Override
    public ResponseEntity getAllAccounts() {
        List<BankAccount> accounts = accountRepository.findAll();
        if (accounts.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(accounts);
    }

}
