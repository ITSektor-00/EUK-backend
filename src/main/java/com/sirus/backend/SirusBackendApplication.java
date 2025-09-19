package com.sirus.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SirusBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(SirusBackendApplication.class, args);
		System.out.println("=== SIRUS Backend Started ===");
		System.out.println("Active profiles: " + System.getProperty("spring.profiles.active"));
		System.out.println("Environment: " + System.getenv("SPRING_PROFILES_ACTIVE"));
		System.out.println("=============================");
	}

}