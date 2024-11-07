package com.bank.atm.simulator.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "cardless_withdrawals")
public class CardlessWithdrawal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId; // Primary key for each transaction

    private double amount;

    @Column(name = "transaction_time")
    private LocalDateTime transactionTime;

    private String status; // "PENDING" or "COMPLETED"

    private String upiUrl; // URL for UPI QR code generation

    // Default constructor
    public CardlessWithdrawal() {}

    // Constructor with all fields
    public CardlessWithdrawal(double amount, LocalDateTime transactionTime, String status, String upiUrl) {
        this.amount = amount;
        this.transactionTime = transactionTime;
        this.status = status;
        this.upiUrl = upiUrl;
    }

    // Getters and Setters
    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(LocalDateTime transactionTime) {
        this.transactionTime = transactionTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUpiUrl() {
        return upiUrl;
    }

    public void setUpiUrl(String upiUrl) {
        this.upiUrl = upiUrl;
    }
}
