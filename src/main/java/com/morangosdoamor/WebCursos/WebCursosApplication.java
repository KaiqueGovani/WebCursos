package com.morangosdoamor.WebCursos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Classe principal da aplicação Spring Boot WebCursos.
 * 
 * Responsabilidades:
 * - Inicializar o contexto Spring Boot
 * - Configurar auto-configuração do Spring Boot
 * - Expor endpoint básico de health check
 */
@SpringBootApplication
@RestController
public class WebCursosApplication {

	/**
	 * Método principal que inicia a aplicação Spring Boot.
	 * 
	 * @param args Argumentos da linha de comando passados para a aplicação
	 */
	public static void main(String[] args) {
		SpringApplication.run(WebCursosApplication.class, args);
	}

	/**
	 * Endpoint básico de health check.
	 * Retorna mensagem simples indicando que a aplicação está em execução.
	 * 
	 * @return Mensagem de saudação indicando que a API está funcionando
	 */
	@GetMapping("/")
	public String hello() {
		return "Hello World!";
	}

}
