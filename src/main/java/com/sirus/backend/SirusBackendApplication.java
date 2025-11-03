package com.sirus.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SirusBackendApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(SirusBackendApplication.class);
		var context = app.run(args);
		
		System.out.println("=== EUK Backend Started ===");
		System.out.println("Active profiles: " + String.join(",", context.getEnvironment().getActiveProfiles()));
		System.out.println("Default profiles: " + String.join(",", context.getEnvironment().getDefaultProfiles()));
		System.out.println("=============================");
	}
}