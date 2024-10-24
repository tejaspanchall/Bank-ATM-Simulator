package com.bank.atm.simulator.repository;

import com.bank.atm.simulator.entity.CashInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CashInventoryRepository extends JpaRepository<CashInventory, Long> {
    // This will help to find the single inventory row (assuming one ATM inventory)
    CashInventory findTopByOrderByIdAsc();
}
