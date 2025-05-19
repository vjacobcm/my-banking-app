package com.demo.mybankingapp.dto;

import lombok.Data;

@Data
public class BankTransferRequestDTO {
    private String debitAccountNumber;
    private String creditAccountNumber;
    private Float amount;
}
