package com.bank.atm.simulator.service;

import com.bank.atm.simulator.entity.User;
import com.bank.atm.simulator.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CashDeposit {

    @Autowired
    private UserRepository userRepository;

    public void deposit(String cardNumber, String atmPin, Double amount) {
        if (amount > 50000) {
            throw new IllegalArgumentException("Cannot deposit more than ₹50,000 in a single transaction.");
        }

        Optional<User> user = userRepository.findByCardNumber(cardNumber);
        if (user.isPresent() && user.get().getAtmPin().equals(atmPin)) {
            User existingUser = user.get();
            Double currentBalance = existingUser.getBalance();
            existingUser.setBalance(currentBalance + amount);

            userRepository.save(existingUser);
        } else {
            throw new IllegalArgumentException("Invalid card number or ATM PIN.");
        }
    }
}
