package br.com.fiap.api_rest.dto.Request;

import br.com.fiap.api_rest.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank String nome,
        @Email String email,
        @Size(min = 8) String senha,
        UserRole role
) {}
