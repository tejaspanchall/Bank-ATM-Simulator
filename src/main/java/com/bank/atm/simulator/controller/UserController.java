package com.bank.atm.simulator.controller;

import com.bank.atm.simulator.dto.*;
import com.bank.atm.simulator.entity.CardlessWithdrawal;
import com.bank.atm.simulator.service.*;
import com.google.zxing.WriterException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Base64;

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
    private CardlessWithdrawal cardlessWithdrawal;

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

    @PostMapping("/generateUpiQr")
    public ResponseEntity<CardlessWithdrawalResponse> generateUpiQr(@RequestBody CardlessWithdrawalRequest requestDTO) {
        double amount = requestDTO.getAmount();
        try {
            BufferedImage qrCode = cardlessWithdrawal.generateUPIQRCode(amount);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qrCode, "png", baos);
            String base64QRCode = Base64.getEncoder().encodeToString(baos.toByteArray());
            return ResponseEntity.ok(new CardlessWithdrawalResponse("QR Code generated", base64QRCode));
        } catch (WriterException | IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new CardlessWithdrawalResponse("Failed to generate QR Code", null));
        }
    }

    @PostMapping("/cardlessWithdraw")
    public ResponseEntity<CardlessWithdrawalResponse> cardlessWithdraw(@RequestBody CardlessWithdrawalRequest requestDTO) {
        String result = cardlessWithdrawal.processCardlessWithdrawal(requestDTO.getAmount());
        if (result.equals("Withdrawal successful")) {
            return ResponseEntity.ok(new CardlessWithdrawalResponse(result, null));
        }
        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED)
                .body(new CardlessWithdrawalResponse(result, null));
    }

    @PostMapping("/exit")
    public ResponseEntity<String> exitTransaction() {
        return ResponseEntity.ok("Transaction exited successfully.");
    }
}
