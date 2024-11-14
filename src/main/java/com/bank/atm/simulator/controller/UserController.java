package com.bank.atm.simulator.controller;

import com.bank.atm.simulator.dto.*;
import com.bank.atm.simulator.entity.CardlessWithdrawal;
import com.bank.atm.simulator.service.*;
import com.google.zxing.WriterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserCreation userCreation;

    @Autowired
    private UserValidation userValidation;

    @Autowired
    private BalanceEnquiry balanceEnquiry;

    @Autowired
    private ChangeAtmPin changeAtmPin;

    @Autowired
    private CashDeposit cashDeposit;

    @Autowired
    private CashWithdrawal cashWithdrawal;

    @Autowired
    private CardlessWithdraw cardlessWithdraw;

    @PostMapping("/signup")
    public ResponseEntity<UserDTO> signup(@RequestBody SignupRequest signupRequest) {
        UserDTO newUser = userCreation.createUser(signupRequest.getName());
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest loginRequest) {
        boolean isValid = userValidation.validateUser(loginRequest.getCardNumber(), loginRequest.getAtmPin());

        if (isValid) {
            return ResponseEntity.ok("Login successful");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid card number or PIN");
        }
    }

    @PostMapping("/balance")
    public ResponseEntity<Double> balanceInquiry(@RequestBody BalanceRequest balanceRequest) {
        Double balance = balanceEnquiry.getBalance(balanceRequest.getCardNumber(), balanceRequest.getAtmPin());

        if (balance == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        return ResponseEntity.ok(balance);
    }

    @PostMapping("/changePin")
    public ResponseEntity<String> changePin(@RequestBody PinChangeRequest pinChangeRequest) {
        boolean isPinChanged = changeAtmPin.changePin(pinChangeRequest.getCardNumber(), pinChangeRequest.getOldPin(), pinChangeRequest.getNewPin());

        if (isPinChanged) {
            return ResponseEntity.ok("PIN changed successfully");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid card number or PIN");
        }
    }

    @PostMapping("/deposit")
    public ResponseEntity<String> deposit(@RequestBody DepositRequest depositRequest) {
        try {
            cashDeposit.deposit(depositRequest.getCardNumber(), depositRequest.getAtmPin(), depositRequest.getAmount());
            return ResponseEntity.ok("Deposit successful!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping("/withdraw")
    public ResponseEntity<WithdrawalResponse> withdraw(@RequestBody WithdrawalRequest withdrawalRequest) {
        try {
            WithdrawalResponse response = cashWithdrawal.withdraw(withdrawalRequest);
            if (response.getMessage().equals("Withdrawal successful")) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new WithdrawalResponse(e.getMessage(), null));
        }
    }

    @GetMapping("/generateQrCode")
    public ResponseEntity<String> generateQrCode(@RequestParam double amount) {
        try {
            // Generate the QR code as a Base64-encoded string
            String qrCodeBase64 = cardlessWithdraw.generateUpiQrCode(amount);
            return ResponseEntity.ok(qrCodeBase64);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to generate QR code");
        }
    }

    @PostMapping("/cardlessWithdraw")
    public ResponseEntity<?> initiateCardlessWithdrawal(@RequestParam double amount) {
        try {
            CardlessWithdrawal withdrawal = cardlessWithdraw.initiateCardlessWithdrawal(amount);
            return ResponseEntity.ok(withdrawal);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to initiate withdrawal");
        }
    }

    @PostMapping("/confirmCardlessWithdraw")
    public ResponseEntity<String> confirmCardlessWithdrawal(@RequestParam Long transactionId) {
        try {
            String result = cardlessWithdraw.confirmCardlessWithdrawal(transactionId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to confirm withdrawal");
        }
    }

    @PostMapping("/exit")
    public ResponseEntity<String> exitTransaction() {
        return ResponseEntity.ok("Transaction exited successfully.");
    }
}
