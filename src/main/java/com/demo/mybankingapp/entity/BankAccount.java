package com.demo.mybankingapp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BankAccount {
    public String ownerName;
    public String accountNumber;
    public Float balance;
    public boolean isActive;
}
