package com.example.funkogram;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class FunkogramApplication {

	public static void main(String[] args) {
		SpringApplication.run(FunkogramApplication.class, args);
	}
}
