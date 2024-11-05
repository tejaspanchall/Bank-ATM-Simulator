package com.bank.atm.simulator.repository;

import com.bank.atm.simulator.entity.CardlessWithdrawal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardlessWithdrawalRepository extends JpaRepository<CardlessWithdrawal, Long> {
}
