package com.bank.atm.simulator.service;

import com.bank.atm.simulator.repository.CardlessWithdrawalRepository;
import com.bank.atm.simulator.repository.CashInventoryRepository;
import com.razorpay.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.json.JSONObject;

import java.util.Optional;

@Service
public class CardlessWithdrawal {

    @Autowired
    private CardlessWithdrawalRepository cardlessWithdrawalRepository;

    @Autowired
    private CashInventoryRepository cashInventoryRepository;

    public JSONObject createUpiQrCode(double amount) throws RazorpayException {
        RazorpayClient razorpay = new RazorpayClient("rzp_test_dIWHnYQuhhWmk8", "gJ435bPAQ2usrxWvcBey1Acm");
        JSONObject request = new JSONObject();
        request.put("amount", amount * 100); // amount in paise
        request.put("currency", "INR");

        return razorpay.paymentLink.create(request).toJson();
    }

    public boolean verifyPaymentAndWithdraw(String paymentId, double amount) {
        // Verify payment status
        try {
            RazorpayClient razorpay = new RazorpayClient("rzp_test_dIWHnYQuhhWmk8", "gJ435bPAQ2usrxWvcBey1Acm");
            Payment payment = razorpay.payments.fetch(paymentId);
            if (payment.get("status").equals("captured")) {
                // Update Cash Inventory
                updateInventory(amount);
                // Record the transaction
                com.bank.atm.simulator.entity.CardlessWithdrawal withdrawal = new com.bank.atm.simulator.entity.CardlessWithdrawal(amount);
                cardlessWithdrawalRepository.save(withdrawal);
                return true;
            }
        } catch (RazorpayException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void updateInventory(double amount) {
        int[] denominations = {500, 200, 100};
        for (int denomination : denominations) {
            Optional<com.bank.atm.simulator.entity.CashInventory> inventoryOpt = cashInventoryRepository.findByDenomination(denomination);
            if (inventoryOpt.isPresent()) {
                com.bank.atm.simulator.entity.CashInventory inventory = inventoryOpt.get();
                int count = (int) Math.min(inventory.getQuantity(), amount / denomination);
                inventory.setQuantity(inventory.getQuantity() - count);
                cashInventoryRepository.save(inventory);
                amount -= count * denomination;
                if (amount <= 0) break;
            }
        }
    }
}
