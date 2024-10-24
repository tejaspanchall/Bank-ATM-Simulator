package com.bank.atm.simulator.dto;

public class CashInventory {
    private int hundredCount;
    private int twoHundredCount;
    private int fiveHundredCount;

    // Default constructor
    public CashInventory() {}

    // Constructor
    public CashInventory(int hundredCount, int twoHundredCount, int fiveHundredCount) {
        this.hundredCount = hundredCount;
        this.twoHundredCount = twoHundredCount;
        this.fiveHundredCount = fiveHundredCount;
    }

    // Getters and setters
    public int getHundredCount() {
        return hundredCount;
    }

    public void setHundredCount(int hundredCount) {
        this.hundredCount = hundredCount;
    }

    public int getTwoHundredCount() {
        return twoHundredCount;
    }

    public void setTwoHundredCount(int twoHundredCount) {
        this.twoHundredCount = twoHundredCount;
    }

    public int getFiveHundredCount() {
        return fiveHundredCount;
    }

    public void setFiveHundredCount(int fiveHundredCount) {
        this.fiveHundredCount = fiveHundredCount;
    }
}
