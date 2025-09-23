package com.morangosdoamor.WebCursos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class WebCursosApplication {

	public static void main(String[] args) {
		SpringApplication.run(WebCursosApplication.class, args);
	}

	@GetMapping("/")
	public String hello() {
		return "Hello World!";
	}

}
