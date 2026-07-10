package com.backend_sistema_clinico;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class BackendSistemaClinicoApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendSistemaClinicoApplication.class, args);
	}

}
