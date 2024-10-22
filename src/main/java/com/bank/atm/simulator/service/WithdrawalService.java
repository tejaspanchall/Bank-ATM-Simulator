package com.bank.atm.simulator.service;

import com.bank.atm.simulator.entity.User;
import com.bank.atm.simulator.entity.Withdrawal;
import com.bank.atm.simulator.repository.UserRepository;
import com.bank.atm.simulator.repository.WithdrawalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.Date;
import java.util.Optional;

@Service
public class WithdrawalService {

    private static final double DAILY_WITHDRAW_LIMIT = 30000;

    @Autowired
    private WithdrawalRepository withdrawalRepository;

    @Autowired
    private UserRepository userRepository;

    public void withdraw(String cardNumber, String atmPin, Double amount, boolean otherAmount) throws Exception {
        Optional<User> userOptional = userRepository.findByCardNumber(cardNumber);
        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // Check if ATM PIN is correct
            if (!user.getAtmPin().equals(atmPin)) {
                throw new Exception("Invalid ATM PIN.");
            }

            // Get total withdrawn amount in the last 24 hours
            Double totalWithdrawnInLast24Hours = withdrawalRepository.getTotalWithdrawnInLast24Hours(user.getId());

            // Check if withdrawal limit is exceeded
            if (totalWithdrawnInLast24Hours + amount > DAILY_WITHDRAW_LIMIT) {
                throw new Exception("Daily withdrawal limit exceeded.");
            }

            // Check if sufficient balance is available
            if (user.getBalance() < amount) {
                throw new Exception("Insufficient balance.");
            }

            // Deduct the amount from user's balance
            user.setBalance(user.getBalance() - amount);
            userRepository.save(user);

            // Create a new withdrawal record
            Withdrawal withdrawal = new Withdrawal(user.getId(), amount, new Date());
            withdrawalRepository.save(withdrawal);
        } else {
            throw new Exception("User not found.");
        }
    }

}
