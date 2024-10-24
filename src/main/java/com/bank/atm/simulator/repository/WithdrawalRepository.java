package com.bank.atm.simulator.repository;

import com.bank.atm.simulator.entity.Withdrawal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface WithdrawalRepository extends JpaRepository<Withdrawal, Long> {

    // Modify the query to compare the timestamp with a provided date
    @Query("SELECT COALESCE(SUM(w.amount), 0) FROM Withdrawal w WHERE w.userId = :userId AND w.timestamp > :since")
    Double getTotalWithdrawnSince(@Param("userId") Long userId, @Param("since") Date since);
}
