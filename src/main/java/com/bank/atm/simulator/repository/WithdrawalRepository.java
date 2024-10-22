package com.bank.atm.simulator.repository;

import com.bank.atm.simulator.entity.Withdrawal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WithdrawalRepository extends JpaRepository<Withdrawal, Long> {
    @Query("SELECT COALESCE(SUM(w.amount), 0) FROM Withdrawal w WHERE w.userId = :userId AND w.timestamp > :timeLimit")
    Double getTotalWithdrawnInLast24Hours(@Param("userId") Long userId);
}
