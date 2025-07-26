package com.example.UberBookingService;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EntityScan("com.example.UberEntityService.models")
@EnableJpaAuditing
@EnableDiscoveryClient
public class UberBookingServiceApplication {

	static {
		Dotenv dotenv = Dotenv.configure()
				.directory(".")
				.filename(".env")
				.load();
		dotenv.entries().forEach(entry ->
				System.setProperty(entry.getKey(), entry.getValue())
		);
	}

	public static void main(String[] args) {
		SpringApplication.run(UberBookingServiceApplication.class, args);
	}

}
