package com.bank.atm.simulator.service;

import com.bank.atm.simulator.entity.Deposit;
import com.bank.atm.simulator.entity.User;
import com.bank.atm.simulator.repository.DepositRepository;
import com.bank.atm.simulator.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CashDeposit {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DepositRepository depositRepository;

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

            // Save deposit record
            Deposit deposit = new Deposit();
            deposit.setCardNumber(cardNumber);
            deposit.setAmount(amount);
            deposit.setTimestamp(LocalDateTime.now());

            depositRepository.save(deposit);

        } else {
            throw new IllegalArgumentException("Invalid card number or ATM PIN.");
        }
    }
}
