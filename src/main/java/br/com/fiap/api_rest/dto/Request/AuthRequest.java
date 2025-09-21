package br.com.fiap.api_rest.dto.Request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AuthRequest(
        @Email String email,
        @NotBlank String senha
) {}
