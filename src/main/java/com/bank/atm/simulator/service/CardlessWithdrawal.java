package com.bank.atm.simulator.service;

import com.bank.atm.simulator.dto.WithdrawalRequest;
import com.bank.atm.simulator.dto.WithdrawalResponse;
import com.bank.atm.simulator.entity.CashInventory;
import com.bank.atm.simulator.entity.User;
import com.bank.atm.simulator.entity.Withdrawal;
import com.bank.atm.simulator.repository.CashInventoryRepository;
import com.bank.atm.simulator.repository.UserRepository;
import com.bank.atm.simulator.repository.WithdrawalRepository;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.json.JSONObject;

@Service
public class CardlessWithdrawal {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CashInventoryRepository cashInventoryRepository;

    @Autowired
    private WithdrawalRepository withdrawalRepository;

    private final RazorpayClient razorpayClient;

    public CardlessWithdrawal() throws Exception {
        this.razorpayClient = new RazorpayClient("YOUR_KEY_ID", "YOUR_KEY_SECRET");
    }

    public String generateUPIQRCode(Double amount) {
        return "https://example.com/qrcode/upi_" + amount;
    }

    public boolean verifyPayment(Double amount) {
        try {
            JSONObject params = new JSONObject();
            params.put("amount", amount * 100);
            params.put("currency", "INR");
            params.put("receipt", "receipt#1");

            Order order = razorpayClient.orders.create(params);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Transactional
    public WithdrawalResponse completeCardlessWithdrawal(WithdrawalRequest request) {
        Optional<User> optionalUser = userRepository.findByCardNumber(request.getCardNumber());

        if (optionalUser.isEmpty()) {
            return new WithdrawalResponse("Invalid card number", null);
        }

        User user = optionalUser.get();

        if (user.getBalance() < request.getAmount()) {
            return new WithdrawalResponse("Insufficient funds", null);
        }

        List<CashInventory> inventory = cashInventoryRepository.findAllByOrderByDenominationDesc();
        double remainingAmount = request.getAmount();

        for (CashInventory cash : inventory) {
            int neededNotes = (int) (remainingAmount / cash.getDenomination());
            if (neededNotes > 0) {
                if (cash.getQuantity() >= neededNotes) {
                    cash.setQuantity(cash.getQuantity() - neededNotes);
                    remainingAmount -= neededNotes * cash.getDenomination();
                } else {
                    int availableNotes = cash.getQuantity();
                    cash.setQuantity(0);
                    remainingAmount -= availableNotes * cash.getDenomination();
                }
                cashInventoryRepository.save(cash);
            }
        }

        if (remainingAmount > 0) {
            return new WithdrawalResponse("ATM does not have enough cash", null);
        }

        String qrCodeUrl = generateUPIQRCode(request.getAmount());
        boolean paymentSuccess = verifyPayment(request.getAmount());

        if (!paymentSuccess) {
            return new WithdrawalResponse("Payment failed. Please try again.", null);
        }

        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setCardNumber(request.getCardNumber());
        withdrawal.setAmount(request.getAmount());
        withdrawal.setTimestamp(LocalDateTime.now());
        withdrawalRepository.save(withdrawal);

        user.setBalance(user.getBalance() - request.getAmount());
        userRepository.save(user);

        return new WithdrawalResponse("Withdrawal successful", user.getBalance());
    }
}
