package com.bank.atm.simulator.service;

import com.bank.atm.simulator.dto.UserDTO;
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
public class UserService {

    @Autowired
    private UserRepository userRepository;
    private WithdrawalRepository withdrawalRepository;

    public UserDTO createUser(String name) {
        String cardNumber = generateCardNumber();
        String atmPin = generateAtmPin();

        User user = new User();
        user.setName(name);
        user.setCardNumber(cardNumber);
        user.setAtmPin(atmPin);
        user.setBalance(10000.0);

        userRepository.save(user);

        return new UserDTO(user.getName(), user.getCardNumber(), user.getAtmPin(), user.getBalance());
    }

    private String generateCardNumber() {
        return "123456" + String.valueOf((int)(Math.random() * 1000000000));
    }

    private String generateAtmPin() {
        return String.format("%04d", (int)(Math.random() * 10000));
    }

    public boolean validateUser(String cardNumber, String atmPin) {
        Optional<User> user = userRepository.findByCardNumber(cardNumber);
        return user.isPresent() && user.get().getAtmPin().equals(atmPin);
    }

    public Double getBalance(String cardNumber, String atmPin) {
        Optional<User> user = userRepository.findByCardNumber(cardNumber);
        if (user.isPresent() && user.get().getAtmPin().equals(atmPin)) {
            return user.get().getBalance();
        } else {
            return null;
        }
    }

    public boolean changePin(String cardNumber, String oldPin, String newPin) {
        Optional<User> user = userRepository.findByCardNumber(cardNumber);
        if (user.isPresent() && user.get().getAtmPin().equals(oldPin)) {
            User updatedUser = user.get();
            updatedUser.setAtmPin(newPin);
            userRepository.save(updatedUser);
            return true;
        } else {
            return false;
        }
    }

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

    public String withdraw(String cardNumber, String atmPin, Double amount, boolean otherAmount) {
        Optional<User> user = userRepository.findByCardNumber(cardNumber);

        if (user.isPresent() && user.get().getAtmPin().equals(atmPin)) {
            User currentUser = user.get();

            Double totalWithdrawnIn24Hours = withdrawalRepository.getTotalWithdrawnInLast24Hours(currentUser.getId());
            if (totalWithdrawnIn24Hours + amount > 30000) {
                throw new IllegalArgumentException("Daily withdrawal limit of ₹30,000 exceeded.");
            }

            if (otherAmount && !isValidAmountForNotes(amount)) {
                throw new IllegalArgumentException("Invalid amount. ATM only contains ₹100, ₹200, and ₹500 notes.");
            }

            if (currentUser.getBalance() < amount) {
                throw new IllegalArgumentException("Insufficient balance.");
            }

            currentUser.setBalance(currentUser.getBalance() - amount);
            userRepository.save(currentUser);

            Withdrawal withdrawal = new Withdrawal();
            withdrawal.setUserId(currentUser.getId());
            withdrawal.setAmount(amount);
            withdrawal.setTimestamp(new Date());

            withdrawalRepository.save(withdrawal);

            return "Withdrawal successful!";
        } else {
            throw new IllegalArgumentException("Invalid card number or ATM PIN.");
        }
    }

    private boolean isValidAmountForNotes(Double amount) {
        double remaining = amount;
        int[] notes = {500, 200, 100};
        for (int note : notes) {
            int count = (int) (remaining / note);
            remaining -= count * note;
        }
        return remaining == 0;
    }

    public Double getTotalWithdrawnInLast24Hours(Long userId) {
        // Calculate the time limit for the past 24 hours
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR_OF_DAY, -24);
        Date timeLimit = calendar.getTime();

        // Call the repository method to get the total amount withdrawn in the last 24 hours
        return withdrawalRepository.getTotalWithdrawnInLast24Hours(userId);
    }
}
