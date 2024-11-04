package com.bank.atm.simulator.service;

import com.bank.atm.simulator.dto.CardlessWithdrawalResponse;
import com.bank.atm.simulator.dto.CardlessWithdrawalRequest;
import com.bank.atm.simulator.entity.CashInventory;
import com.bank.atm.simulator.entity.CardlessWithdrawal;
import com.bank.atm.simulator.repository.CardlessWithdrawalRepository;
import com.bank.atm.simulator.repository.CashInventoryRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
public class CardlessWithdraw {

    @Autowired
    private RazorpayClient razorpayClient;

    @Autowired
    private CardlessWithdrawalRepository cardlessWithdrawalRepository;

    @Autowired
    private CashInventoryRepository cashInventoryRepository;

    @Transactional
    public CardlessWithdrawalResponse cardlessWithdraw(CardlessWithdrawalRequest request) {
        Double amount = request.getAmount();

        Map<String, Object> options = new HashMap<>();
        options.put("amount", amount * 100); // Convert amount to paise
        options.put("currency", "INR");
        options.put("receipt", "receipt#1");

        try {
            Order order = razorpayClient.orders.create((JSONObject) options);
            String qrCodeUrl = generateQRCode(order.get("id").toString());

            return new CardlessWithdrawalResponse("QR code generated", qrCodeUrl);
        } catch (RazorpayException e) {
            throw new RuntimeException("Error generating payment order: " + e.getMessage());
        }
    }

    @Transactional
    public String completeWithdrawal(String orderId, String paymentId, Double amount) {
        // Check if payment is successful (for the sake of this example, let's assume it is)

        CashInventory cashInventory = cashInventoryRepository.findByDenomination(200) // Check for the appropriate denomination
                .orElseThrow(() -> new RuntimeException("Cash inventory not sufficient"));

        if (cashInventory.getQuantity() > 0) {
            cashInventory.setQuantity(cashInventory.getQuantity() - 1);
            cashInventoryRepository.save(cashInventory);

            CardlessWithdrawal withdrawal = new CardlessWithdrawal();
            withdrawal.setAmount(amount);
            withdrawal.setTimestamp(LocalDateTime.now());
            cardlessWithdrawalRepository.save(withdrawal);

            return "Withdrawal successful";
        } else {
            throw new RuntimeException("Insufficient cash in the ATM");
        }
    }

    private String generateQRCode(String orderId) {
        // Logic to generate QR code (can use ZXing or similar library)
        return "https://example.com/qrcode/" + orderId; // Placeholder for the QR code URL
    }
}
