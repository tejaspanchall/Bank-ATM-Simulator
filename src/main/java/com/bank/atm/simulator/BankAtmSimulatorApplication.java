package com.bank.atm.simulator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.bank.atm.simulator.repository")
@EntityScan(basePackages = "com.bank.atm.simulator.entity")
public class BankAtmSimulatorApplication {

	public static void main(String[] args) {
		SpringApplication.run(BankAtmSimulatorApplication.class, args);
	}
}
