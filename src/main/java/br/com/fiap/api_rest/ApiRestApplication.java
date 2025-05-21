package br.com.fiap.api_rest;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(info =
@Info(title = "API de estacionamento", description = "API RESTful com Swagger", version = "v.01"))
public class ApiRestApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiRestApplication.class, args);
	}

}