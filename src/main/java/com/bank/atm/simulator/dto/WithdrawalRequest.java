package com.bank.atm.simulator.dto;

public class WithdrawalRequest {
    private String cardNumber;
    private String atmPin;
    private Double amount;
    private boolean otherAmount;

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getAtmPin() {
        return atmPin;
    }

    public void setAtmPin(String atmPin) {
        this.atmPin = atmPin;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public boolean isOtherAmount() {
        return otherAmount;
    }

    public void setOtherAmount(boolean otherAmount) {
        this.otherAmount = otherAmount;
    }
}
