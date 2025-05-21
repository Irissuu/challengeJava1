package br.com.fiap.api_rest.dto;

import jakarta.validation.constraints.*;


public record VagaRequest(
        String status,

        @NotNull(message = "O numero da vaga é obrigatório")
        int numero,

        @NotBlank(message = "O pátio da vaga é obrigatório")
        String patio
) {
}
