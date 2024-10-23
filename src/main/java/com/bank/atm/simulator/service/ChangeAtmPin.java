package com.bank.atm.simulator.service;

import com.bank.atm.simulator.entity.User;
import com.bank.atm.simulator.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ChangeAtmPin {

    @Autowired
    private UserRepository userRepository;

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
}
