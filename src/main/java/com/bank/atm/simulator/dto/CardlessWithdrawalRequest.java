package com.bank.atm.simulator.dto;

public class CardlessWithdrawalRequest {

    private double amount;

    public CardlessWithdrawalRequest() {}

    public CardlessWithdrawalRequest(double amount) {
        this.amount = amount;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
