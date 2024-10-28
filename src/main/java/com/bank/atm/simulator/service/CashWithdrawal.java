package com.bank.atm.simulator.service;

import com.bank.atm.simulator.dto.WithdrawalRequest;
import com.bank.atm.simulator.dto.WithdrawalResponse;
import com.bank.atm.simulator.entity.CashInventory;
import com.bank.atm.simulator.entity.User;
import com.bank.atm.simulator.entity.Withdrawal;
import com.bank.atm.simulator.repository.CashInventoryRepository;
import com.bank.atm.simulator.repository.UserRepository;
import com.bank.atm.simulator.repository.WithdrawalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CashWithdrawal {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CashInventoryRepository cashInventoryRepository;

    @Autowired
    private WithdrawalRepository withdrawalRepository;

    @Transactional
    public WithdrawalResponse withdraw(WithdrawalRequest request) {
        Optional<User> optionalUser = userRepository.findByCardNumber(request.getCardNumber());

        // Check if user exists
        if (optionalUser.isEmpty()) {
            return new WithdrawalResponse("Invalid card number", null);
        }

        User user = optionalUser.get();

        // Validate PIN
        if (!user.getAtmPin().equals(request.getAtmPin())) {
            return new WithdrawalResponse("Invalid PIN", null);
        }

        // Check for sufficient funds
        if (user.getBalance() < request.getAmount()) {
            return new WithdrawalResponse("Insufficient funds", null);
        }

        // Check and update cash inventory
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

        // If remaining amount is still greater than 0, ATM has insufficient cash
        if (remainingAmount > 0) {
            return new WithdrawalResponse("ATM does not have enough cash", null);
        }

        // Record the withdrawal transaction
        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setCardNumber(request.getCardNumber());
        withdrawal.setAmount(request.getAmount());
        withdrawal.setTimestamp(LocalDateTime.now());
        withdrawalRepository.save(withdrawal);

        // Deduct balance from user account and save
        user.setBalance(user.getBalance() - request.getAmount());
        userRepository.save(user);

        return new WithdrawalResponse("Withdrawal successful", user.getBalance());
    }
}
