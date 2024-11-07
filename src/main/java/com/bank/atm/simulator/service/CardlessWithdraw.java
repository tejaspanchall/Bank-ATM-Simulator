package com.bank.atm.simulator.service;

import com.bank.atm.simulator.entity.CardlessWithdrawal;
import com.bank.atm.simulator.repository.CardlessWithdrawalRepository;
import com.bank.atm.simulator.repository.CashInventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class CardlessWithdraw {

    @Autowired
    private CardlessWithdrawalRepository cardlessWithdrawalRepository;

    @Autowired
    private CashInventoryRepository cashInventoryRepository;

    @Value("${upi.id}")
    private String upiId;

    // Initiates a cardless withdrawal by creating a UPI URL and saving the withdrawal transaction
    public CardlessWithdrawal initiateCardlessWithdrawal(double amount) {
        // Generate UPI URL
        String upiUrl = generateUpiUrl(amount);

        // Create a new cardless withdrawal transaction with status "PENDING"
        CardlessWithdrawal withdrawal = new CardlessWithdrawal(amount, LocalDateTime.now(), "PENDING", upiUrl);

        // Save the withdrawal transaction in the database
        return cardlessWithdrawalRepository.save(withdrawal);
    }

    // Confirms the cardless withdrawal by transaction ID, updates status to COMPLETED, and adjusts cash inventory
    @Transactional
    public String confirmCardlessWithdrawal(Long transactionId) {
        // Retrieve the withdrawal transaction by transactionId
        Optional<CardlessWithdrawal> optionalWithdrawal = cardlessWithdrawalRepository.findById(transactionId);

        if (optionalWithdrawal.isPresent()) {
            CardlessWithdrawal withdrawal = optionalWithdrawal.get();

            // Check if the status is still "PENDING"
            if (!"PENDING".equals(withdrawal.getStatus())) {
                return "Withdrawal has already been confirmed or cancelled.";
            }

            // Update the status to "COMPLETED"
            withdrawal.setStatus("COMPLETED");
            cardlessWithdrawalRepository.save(withdrawal);

            // Update cash inventory based on the withdrawn amount
            updateCashInventory(withdrawal.getAmount());

            return "Withdrawal successful";
        } else {
            return "Transaction not found";
        }
    }

    // Generates a UPI URL for the given amount
    private String generateUpiUrl(double amount) {
        return "upi://pay?pa=" + upiId + "&pn=Bank-ATM-Simulator&am=" + amount + "&cu=INR";
    }

    // Updates cash inventory by deducting the withdrawn amount from the denominations
    private void updateCashInventory(double amount) {
        // Deduct from inventory starting from highest denomination (500), then 200, then 100
        int[] denominations = {500, 200, 100};

        for (int denomination : denominations) {
            int quantityRequired = (int) (amount / denomination);
            if (quantityRequired > 0) {
                // Find inventory entry for the denomination
                var cashInventoryOpt = cashInventoryRepository.findByDenomination(denomination);
                if (cashInventoryOpt.isPresent()) {
                    var cashInventory = cashInventoryOpt.get();
                    int availableQuantity = cashInventory.getQuantity();
                    int quantityToDeduct = Math.min(availableQuantity, quantityRequired);

                    // Update inventory if there's enough cash
                    if (quantityToDeduct > 0) {
                        cashInventory.setQuantity(availableQuantity - quantityToDeduct);
                        cashInventoryRepository.save(cashInventory);
                        amount -= quantityToDeduct * denomination;
                    }
                }
            }
        }
    }
}
