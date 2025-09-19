package br.com.fiap.api_rest.dto.Response;

public record UsuarioResponse(
        Long id,
        String nome,
        String email
) {}
