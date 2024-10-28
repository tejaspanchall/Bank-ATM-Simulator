package com.bank.atm.simulator.repository;

import com.bank.atm.simulator.entity.CashInventory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CashInventoryRepository extends JpaRepository<CashInventory, Long> {
    Optional<CashInventory> findByDenomination(int denomination);
    List<CashInventory> findAllByOrderByDenominationDesc();
}
