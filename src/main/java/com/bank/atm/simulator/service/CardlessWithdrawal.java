package com.bank.atm.simulator.service;

import com.bank.atm.simulator.entity.CashInventory;
import com.bank.atm.simulator.repository.CardlessWithdrawalRepository;
import com.bank.atm.simulator.repository.CashInventoryRepository;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class CardlessWithdrawal {

    @Autowired
    private CashInventoryRepository cashInventoryRepository;

    @Autowired
    private CardlessWithdrawalRepository cardlessWithdrawalRepository;

    @Value("${upi.defaultId}")
    private String defaultUpiId;

    public BufferedImage generateUPIQRCode(double amount) throws WriterException {
        String upiUri = "upi://pay?pa=" + defaultUpiId + "&pn=ATM%20Withdrawal&am=" + amount + "&cu=INR";
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, StandardCharsets.UTF_8.name());

        return MatrixToImageWriter.toBufferedImage(
                qrCodeWriter.encode(upiUri, BarcodeFormat.QR_CODE, 250, 250, hints)
        );
    }

    public String processCardlessWithdrawal(double amount) {
        if (!isCashAvailable(amount)) {
            return "Insufficient cash in the ATM.";
        }

        // Update cash inventory based on available denominations
        updateCashInventory(amount);

        // Record the cardless withdrawal transaction
        CardlessWithdrawal transaction = new CardlessWithdrawal();
        transaction.setAmount(amount);
        transaction.setTimestamp(LocalDateTime.now());
        cardlessWithdrawalRepository.save(transaction);

        return "Withdrawal successful";
    }

    /**
     * Confirms that the payment was made by the user.
     *
     * @param transactionId The transaction ID to confirm payment.
     * @return A message indicating payment confirmation status.
     */
    public String confirmPayment(Long transactionId) {
        Optional<CardlessWithdrawal> transaction = cardlessWithdrawalRepository.findById(transactionId);
        if (transaction.isPresent()) {
            CardlessWithdrawal cardlessWithdrawal = transaction.get();
            cardlessWithdrawal.setConfirmed(true);
            cardlessWithdrawalRepository.save(cardlessWithdrawal);
            return "Payment confirmed and withdrawal completed.";
        }
        return "Transaction not found.";
    }

    /**
     * Checks if the requested amount can be dispensed from the available cash inventory.
     *
     * @param amount The requested withdrawal amount.
     * @return True if the ATM has sufficient cash, false otherwise.
     */
    private boolean isCashAvailable(double amount) {
        List<CashInventory> cashInventory = cashInventoryRepository.findAllByOrderByDenominationDesc();
        double totalCash = cashInventory.stream()
                .mapToDouble(inventory -> inventory.getDenomination() * inventory.getQuantity())
                .sum();
        return totalCash >= amount;
    }

    /**
     * Updates the cash inventory by deducting the necessary quantity of notes for a given amount.
     *
     * @param amount The amount to be withdrawn.
     */
    private void updateCashInventory(double amount) {
        List<CashInventory> cashInventory = cashInventoryRepository.findAllByOrderByDenominationDesc();
        double remainingAmount = amount;

        for (CashInventory inventory : cashInventory) {
            int denomination = inventory.getDenomination();
            int notesRequired = (int) (remainingAmount / denomination);
            int notesAvailable = inventory.getQuantity();
            int notesToDeduct = Math.min(notesRequired, notesAvailable);

            if (notesToDeduct > 0) {
                inventory.setQuantity(notesAvailable - notesToDeduct);
                cashInventoryRepository.save(inventory);
                remainingAmount -= notesToDeduct * denomination;
            }

            if (remainingAmount <= 0) {
                break;
            }
        }
    }
}
