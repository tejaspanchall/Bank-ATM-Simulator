package com.bank.atm.simulator.dto;

public class CardlessWithdrawalResponse {
    private String qrCodeUrl;
    private String message;
    private boolean success;

    // Constructors
    public CardlessWithdrawalResponse() {}

    public CardlessWithdrawalResponse(String qrCodeUrl, String message, boolean success) {
        this.qrCodeUrl = qrCodeUrl;
        this.message = message;
        this.success = success;
    }

    // Getters and Setters
    public String getQrCodeUrl() {
        return qrCodeUrl;
    }

    public void setQrCodeUrl(String qrCodeUrl) {
        this.qrCodeUrl = qrCodeUrl;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
