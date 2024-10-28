package com.bank.atm.simulator.service;

import com.bank.atm.simulator.entity.CashInventory;
import com.bank.atm.simulator.repository.CashInventoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class Inventory {

    @Autowired
    private CashInventoryRepository cashInventoryRepository;

    public List<CashInventory> getCashInventory() {
        return cashInventoryRepository.findAllByOrderByDenominationDesc();
    }

    public void updateInventory(int denomination, int quantity) {
        cashInventoryRepository.findByDenomination(denomination).ifPresent(inventory -> {
            inventory.setQuantity(inventory.getQuantity() - quantity);
            cashInventoryRepository.save(inventory);
        });
    }

}
