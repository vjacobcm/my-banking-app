package com.demo.mybankingapp.repository;

import com.demo.mybankingapp.entity.BankAccount;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<BankAccount, String> {
    @Query(nativeQuery = true, value = "SELECT * FROM accounts a "
    + "WHERE a.account_number = :account_number")
    Optional<BankAccount> findByAccountNumber(@Param("account_number") String accountNumber);
    
    List<BankAccount> findAll();
}
