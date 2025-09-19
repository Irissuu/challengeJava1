package br.com.fiap.api_rest.dto.Response;

import org.springframework.hateoas.Link;

public record MotoResponseDTO(Long id, String infoMoto, Link link) {
}
