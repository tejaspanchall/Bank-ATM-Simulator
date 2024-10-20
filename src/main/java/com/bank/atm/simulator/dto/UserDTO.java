package com.bank.atm.simulator.dto;

public class UserDTO {
    private String name;
    private String cardNumber;
    private String atmPin;
    private Double balance;

    public UserDTO(String name, String cardNumber, String atmPin, Double balance){
        this.name = name;
        this.cardNumber = cardNumber;
        this.atmPin = atmPin;
        this.balance = balance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
}
