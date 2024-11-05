package com.bank.atm.simulator.dto;

public class CardlessWithdrawalRequest {
    private double amount;

    // Constructors
    public CardlessWithdrawalRequest() {}

    public CardlessWithdrawalRequest(double amount) {
        this.amount = amount;
    }

    // Getter and Setter
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
