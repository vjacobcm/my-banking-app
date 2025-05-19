package com.demo.mybankingapp.repository;

import com.demo.mybankingapp.entity.BankAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<BankAccount, String> {
}
