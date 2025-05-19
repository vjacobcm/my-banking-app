package com.demo.mybankingapp.repository;

import com.demo.mybankingapp.entity.BankTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<BankTransaction, String> {
}
