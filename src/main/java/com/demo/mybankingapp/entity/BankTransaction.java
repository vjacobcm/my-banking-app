package com.demo.mybankingapp.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;
@Data
@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name="transactions")
public class BankTransaction {
    @Id
    @Column(name="transaction_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    public int transactionID;

    @Column(name="debitor_account")
    public String debitor;

    @Column(name="creditor_account")
    public String creditor;

    @Column(name="amount")
    public Float amount;

    @Column(name="is_processed")
    public boolean isProcessed;
}
