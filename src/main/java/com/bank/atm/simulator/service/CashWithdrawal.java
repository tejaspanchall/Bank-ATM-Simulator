package com.bank.atm.simulator.service;

import com.bank.atm.simulator.entity.CashInventory;
import com.bank.atm.simulator.entity.User;
import com.bank.atm.simulator.entity.Withdrawal;
import com.bank.atm.simulator.repository.CashInventoryRepository;
import com.bank.atm.simulator.repository.UserRepository;
import com.bank.atm.simulator.repository.WithdrawalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

@Service
public class CashWithdrawal {

    private static final double DAILY_WITHDRAW_LIMIT = 30000;

    @Autowired
    private WithdrawalRepository withdrawalRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CashInventoryRepository cashInventoryRepository;

    public CashWithdrawal(WithdrawalRepository withdrawalRepository) {
        this.withdrawalRepository = withdrawalRepository;
    }

    public void withdraw(Long userId, Double amount) throws Exception {
        if (amount % 100 != 0) {
            throw new Exception("Amount must be a multiple of 100, 200, or 500.");
        }

        // Fetch user
        User user = userRepository.findById(userId).orElseThrow(() -> new Exception("User not found."));

        // Check user balance
        if (user.getBalance() < amount) {
            throw new Exception("Insufficient balance.");
        }

        // Fetch cash inventory
        CashInventory cashInventory = cashInventoryRepository.findTopByOrderByIdAsc();
        if (cashInventory == null) {
            throw new Exception("Cash inventory not found.");
        }

        // Calculate the required notes
        int originalAmount = amount.intValue();
        int remainingAmount = originalAmount;

        // Check if enough ₹500 notes are available
        int fiveHundredNotesNeeded = Math.min(remainingAmount / 500, cashInventory.getFiveHundredCount());
        remainingAmount -= fiveHundredNotesNeeded * 500;

        // Check if enough ₹200 notes are available
        int twoHundredNotesNeeded = Math.min(remainingAmount / 200, cashInventory.getTwoHundredCount());
        remainingAmount -= twoHundredNotesNeeded * 200;

        // Check if enough ₹100 notes are available
        int hundredNotesNeeded = Math.min(remainingAmount / 100, cashInventory.getHundredCount());
        remainingAmount -= hundredNotesNeeded * 100;

        // If remaining amount is not 0, then we don't have enough notes
        if (remainingAmount != 0) {
            throw new Exception("ATM doesn't have enough notes for this amount.");
        }

        // Update cash inventory
        cashInventory.setFiveHundredCount(cashInventory.getFiveHundredCount() - fiveHundredNotesNeeded);
        cashInventory.setTwoHundredCount(cashInventory.getTwoHundredCount() - twoHundredNotesNeeded);
        cashInventory.setHundredCount(cashInventory.getHundredCount() - hundredNotesNeeded);
        cashInventoryRepository.save(cashInventory);

        // Update user balance
        user.setBalance(user.getBalance() - originalAmount);
        userRepository.save(user);

        // Log the withdrawal
        withdrawalRepository.save(new Withdrawal(userId, BigDecimal.valueOf(originalAmount), new Date()));

    }

    public BigDecimal getTotalWithdrawnInLast24Hours(Long userId) {
        // Calculate the timestamp for 24 hours ago
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -24);
        Date twentyFourHoursAgo = calendar.getTime();

        // Use the repository method to get total withdrawals since the calculated time
        Double totalWithdrawn = withdrawalRepository.getTotalWithdrawnSince(userId, twentyFourHoursAgo);
        return BigDecimal.valueOf(totalWithdrawn);
    }
}
