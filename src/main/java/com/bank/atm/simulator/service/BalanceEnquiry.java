package com.bank.atm.simulator.service;

import com.bank.atm.simulator.entity.User;
import com.bank.atm.simulator.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class BalanceEnquiry {

    @Autowired
    private UserRepository userRepository;

    public Double getBalance(String cardNumber, String atmPin) {
        Optional<User> user = userRepository.findByCardNumber(cardNumber);
        if (user.isPresent() && user.get().getAtmPin().equals(atmPin)) {
            return user.get().getBalance();
        } else {
            return null;
        }
    }
}
