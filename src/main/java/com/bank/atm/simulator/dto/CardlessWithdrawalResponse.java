package com.bank.atm.simulator.dto;

public class CardlessWithdrawalResponse {
    private String message;
    private String qrCodeUrl; // URL to the generated UPI QR code

    public CardlessWithdrawalResponse(String message, String qrCodeUrl) {
        this.message = message;
        this.qrCodeUrl = qrCodeUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }
}