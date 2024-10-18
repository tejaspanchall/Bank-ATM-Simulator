package com.bank.atm.simulator.repository;

import com.bank.atm.simulator.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByCardNumber(String cardNumber);
}
