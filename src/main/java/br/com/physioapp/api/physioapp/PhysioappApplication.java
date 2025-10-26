package br.com.physioapp.api.physioapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@SpringBootApplication
@RestController
public class PhysioappApplication {

	public static void main(String[] args) {
		SpringApplication.run(PhysioappApplication.class, args);
	}

	@GetMapping("/")
	public String sayHello() {
		return "hello world";
	}

}
