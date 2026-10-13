package com.eduardo.pacientes;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.eduardo.pacientes", "com.eduardo.commons"})
public class PacientesApplication {

	public static void main(String[] args) {
		SpringApplication.run(PacientesApplication.class, args);
	}

}
