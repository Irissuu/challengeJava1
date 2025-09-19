package br.com.fiap.api_rest.mapper;

import br.com.fiap.api_rest.dto.UsuarioRequest;
import br.com.fiap.api_rest.dto.UsuarioResponse;
import br.com.fiap.api_rest.model.UsuarioJava;

public class UsuarioMapper {

    public UsuarioJava requestToUsuario(UsuarioRequest request) {
        return new UsuarioJava(
                null,
                request.nome(),
                request.email(),
                request.senha()
        );
    }

    public UsuarioResponse usuarioToResponse(UsuarioJava usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail()
        );
    }
}
