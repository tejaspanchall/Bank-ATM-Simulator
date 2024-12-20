package com.bank.atm.simulator.service;

import com.bank.atm.simulator.dto.UserDTO;
import com.bank.atm.simulator.entity.User;
import com.bank.atm.simulator.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserCreation {

    @Autowired
    private UserRepository userRepository;

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
        return "123456" + String.valueOf((int) (Math.random() * 1000000000));
    }

    private String generateAtmPin() {
        return String.format("%04d", (int) (Math.random() * 10000));
    }
}
