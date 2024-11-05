package com.bank.atm.simulator.dto;

public class CardlessWithdrawalResponse {

    private String message;
    private String qrCodeBase64;

    public CardlessWithdrawalResponse() {}

    public CardlessWithdrawalResponse(String message, String qrCodeBase64) {
        this.message = message;
        this.qrCodeBase64 = qrCodeBase64;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getQrCodeBase64() {
        return qrCodeBase64;
    }

    public void setQrCodeBase64(String qrCodeBase64) {
        this.qrCodeBase64 = qrCodeBase64;
    }
}
