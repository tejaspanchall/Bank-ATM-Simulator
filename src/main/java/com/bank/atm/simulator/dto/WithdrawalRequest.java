package com.bank.atm.simulator.dto;

public class WithdrawalRequest {
    private Long userId;
    private Double amount;

    // Default constructor
    public WithdrawalRequest() {}

    // Constructor
    public WithdrawalRequest(Long userId, Double amount) {
        this.userId = userId;
        this.amount = amount;
    }

    // Getters and setters
    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }
}
