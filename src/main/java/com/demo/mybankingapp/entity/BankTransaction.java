package com.demo.mybankingapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name="transactions")
public class BankTransaction {
    @Id
    @Column(name="transaction_id")
    public String transactionID;

    @Column(name="debitor_account")
    public String debitor;

    @Column(name="creditor_account")
    public String creditor;

    @Column(name="amount")
    public Float amount;

    @Column(name="is_processed")
    public boolean isProcessed;
}
