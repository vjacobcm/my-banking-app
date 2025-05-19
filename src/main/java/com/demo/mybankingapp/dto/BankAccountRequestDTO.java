package com.demo.mybankingapp.dto;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class BankAccountRequestDTO {
    private String ownerName;
    private String accountNumber;
    private Float balance;
    private boolean isActive;
}
