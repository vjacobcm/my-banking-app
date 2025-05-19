package com.demo.mybankingapp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Data
@Entity
@AllArgsConstructor
@Getter
@Setter
@Table(name="accounts")
@NoArgsConstructor
public class BankAccount {
    @Id
    @Column(name="account_id")
    public UUID accountId;
    @Column(name="account_holder")
    public String ownerName;
    @Column(name="account_number")
    public String accountNumber;
    @Column(name="balance")
    public Float balance;
    @Column(name="is_active")
    public boolean isActive;
}
