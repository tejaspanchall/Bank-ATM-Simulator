package com.bank.atm.simulator.controller;

import com.bank.atm.simulator.dto.*;
import com.bank.atm.simulator.service.UserService;
import com.bank.atm.simulator.service.WithdrawalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signup(@RequestBody SignupRequest signupRequest) {
        UserDTO newUser = userService.createUser(signupRequest.getName());
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        boolean isValid = userService.validateUser(loginRequest.getCardNumber(), loginRequest.getAtmPin());

        if (isValid) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid card number or PIN");
        }
    }

    @PostMapping("/balance")
    public ResponseEntity<Double> balanceInquiry(@RequestBody BalanceRequest balanceRequest) {
        Double balance = userService.getBalance(balanceRequest.getCardNumber(), balanceRequest.getAtmPin());

        if (balance == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        return ResponseEntity.ok(balance);
    }

    @PostMapping("/changePin")
    public ResponseEntity<String> changePin(@RequestBody PinChangeRequest pinChangeRequest) {
        boolean isPinChanged = userService.changePin(pinChangeRequest.getCardNumber(), pinChangeRequest.getOldPin(), pinChangeRequest.getNewPin());

        if (isPinChanged) {
            return ResponseEntity.ok("PIN changed successfully");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid card number or PIN");
        }
    }

    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(@RequestBody DepositRequest depositRequest) {
        try {
            userService.deposit(depositRequest.getCardNumber(), depositRequest.getAtmPin(), depositRequest.getAmount());
            return ResponseEntity.ok("Deposit successful!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @RequestMapping("/withdrawal")
    public class WithdrawalController {

        @Autowired
        private WithdrawalService withdrawalService;

        @PostMapping
        public ResponseEntity<String> withdrawAmount(@RequestBody WithdrawalRequest withdrawalRequest) {
            try {
                withdrawalService.withdraw(
                        withdrawalRequest.getCardNumber(),
                        withdrawalRequest.getAtmPin(),
                        withdrawalRequest.getAmount(),
                        withdrawalRequest.isOtherAmount()
                );
                return ResponseEntity.ok("Withdrawal successful.");
            } catch (Exception e) {
                return ResponseEntity.badRequest().body(e.getMessage());
            }
        }
    }

    @PostMapping("/exit")
    public ResponseEntity<String> exitTransaction() {
        return ResponseEntity.ok("Transaction exited successfully.");
    }
}
