package com.bank.atm.simulator.service;

import com.bank.atm.simulator.entity.User;
import com.bank.atm.simulator.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserValidation {

    @Autowired
    private UserRepository userRepository;

    public boolean validateUser(String cardNumber, String atmPin) {
        Optional<User> user = userRepository.findByCardNumber(cardNumber);
        return user.isPresent() && user.get().getAtmPin().equals(atmPin);
    }
}
