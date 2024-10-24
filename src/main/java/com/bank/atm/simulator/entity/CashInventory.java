package com.bank.atm.simulator.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cash_inventory")
public class CashInventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "hundred_count", nullable = false)
    private int hundredCount;

    @Column(name = "two_hundred_count", nullable = false)
    private int twoHundredCount;

    @Column(name = "five_hundred_count", nullable = false)
    private int fiveHundredCount;

    // Default constructor
    public CashInventory() {

    }

    // Constructor
    public CashInventory(int hundredCount, int twoHundredCount, int fiveHundredCount) {
        this.hundredCount = hundredCount;
        this.twoHundredCount = twoHundredCount;
        this.fiveHundredCount = fiveHundredCount;
    }

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
