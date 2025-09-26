package br.com.fiap.api_rest.mapper;

import br.com.fiap.api_rest.dto.Request.UsuarioRequest;
import br.com.fiap.api_rest.dto.Response.UsuarioResponse;
import br.com.fiap.api_rest.model.UsuarioJava;

public class UsuarioMapper {


    public UsuarioJava requestToUsuario(UsuarioRequest request) {
        UsuarioJava u = new UsuarioJava();
        u.setNome(request.nome());
        u.setEmail(request.email());
        u.setSenha(request.senha());
        return u;
    }

    public UsuarioResponse usuarioToResponse(UsuarioJava usuario) {
        return new UsuarioResponse(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail()
        );
    }
}
