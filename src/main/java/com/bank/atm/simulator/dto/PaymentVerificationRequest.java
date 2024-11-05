package com.bank.atm.simulator.dto;

public class PaymentVerificationRequest {
    private String paymentId;
    private double amount;

    // Constructors
    public PaymentVerificationRequest() {}

    public PaymentVerificationRequest(String paymentId, double amount) {
        this.paymentId = paymentId;
        this.amount = amount;
    }

    // Getters and Setters
    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}
