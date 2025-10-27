package br.com.physioapp.api.physioapp;

import java.time.Clock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class PhysioappApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhysioappApplication.class, args);
	}

	@Bean
	public Clock clock() {
		return Clock.systemUTC();
	}
}
