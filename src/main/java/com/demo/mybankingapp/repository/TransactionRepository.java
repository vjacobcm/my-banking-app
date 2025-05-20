package com.demo.mybankingapp.repository;

import com.demo.mybankingapp.entity.BankTransaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends CrudRepository<BankTransaction, String> {
    @Query(nativeQuery = true, value = "SELECT * FROM transactions t WHERE t.is_processed = false")
    Optional<List<BankTransaction>> findByIsProcessed();
}
