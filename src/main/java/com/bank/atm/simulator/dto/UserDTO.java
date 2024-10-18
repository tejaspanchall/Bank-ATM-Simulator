package com.bank.atm.simulator.dto;

public class UserDTO {
    private String name;
    private String cardNumber;
    private String atmPin;

    public UserDTO(String name, String cardNumber, String atmPin){
        this.name = name;
        this.cardNumber = cardNumber;
        this.atmPin = atmPin;
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
}