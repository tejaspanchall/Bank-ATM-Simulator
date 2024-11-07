package com.bank.atm.simulator.dto;

public class CardlessWithdrawalResponse {
    private String message;
    private String qrCodeImageUrl;

    public CardlessWithdrawalResponse(String message, String qrCodeImageUrl) {
        this.message = message;
        this.qrCodeImageUrl = qrCodeImageUrl;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getQrCodeImageUrl() {
        return qrCodeImageUrl;
    }

    public void setQrCodeImageUrl(String qrCodeImageUrl) {
        this.qrCodeImageUrl = qrCodeImageUrl;
    }
}