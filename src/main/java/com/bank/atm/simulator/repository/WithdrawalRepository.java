package com.bank.atm.simulator.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WithdrawalRepository<Withdrawal> extends JpaRepository<Withdrawal, Long> {

    @Query("SELECT COALESCE(SUM(w.amount), 0) FROM Withdrawal w WHERE w.userId = :userId AND w.timestamp > CURRENT_TIMESTAMP - INTERVAL '24 hours'")
    Double getTotalWithdrawnInLast24Hours(@Param("userId") Long userId);
}

