package br.com.physioapp.api.physioapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import java.security.Principal;

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

	@GetMapping("/hello")
	public String specialHello(Principal principal) {
		if (principal == null) {
			return "You must be authenticated to see this message.";
		}

		String fullname = principal.getName();
		return "Hello " + fullname + " from PhysioApp API!";
	}

}
