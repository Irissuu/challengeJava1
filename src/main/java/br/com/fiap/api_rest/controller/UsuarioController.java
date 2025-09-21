package br.com.fiap.api_rest.controller;

import br.com.fiap.api_rest.dto.Request.UsuarioRequest;
import br.com.fiap.api_rest.dto.Response.UsuarioResponse;
import br.com.fiap.api_rest.mapper.UsuarioMapper;
import br.com.fiap.api_rest.model.UsuarioJava;
import br.com.fiap.api_rest.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/usuarios")
@Tag(name = "api-usuarios-java")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final UsuarioMapper usuarioMapper = new UsuarioMapper();

    @Autowired
    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }



    @Operation(summary = "Lista todos os usuários")
    @GetMapping
    public ResponseEntity<Page<UsuarioResponse>> readUsuarios(
            @RequestParam(defaultValue = "0") Integer pageNumber) {

        Pageable pageable = PageRequest.of(pageNumber, 10, Sort.by("nome").ascending());
        Page<UsuarioJava> usuariosPage = usuarioService.readUsuarios(pageable);
        Page<UsuarioResponse> responses = usuariosPage.map(usuarioMapper::usuarioToResponse);
        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @Operation(summary = "Retorna um usuário por ID")
    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> readUsuario(@PathVariable Long id) {
        UsuarioJava usuario = usuarioService.readUsuarioById(id);
        return (usuario != null)
                ? new ResponseEntity<>(usuarioMapper.usuarioToResponse(usuario), HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Atualiza um usuário existente")
    @PutMapping("/{id}")
    public ResponseEntity<UsuarioResponse> updateUsuario(
            @PathVariable Long id, @Valid @RequestBody UsuarioRequest request) {

        UsuarioJava atualizado = usuarioMapper.requestToUsuario(request);
        UsuarioJava salvo = usuarioService.updateUsuario(id, atualizado);
        return (salvo != null)
                ? new ResponseEntity<>(usuarioMapper.usuarioToResponse(salvo), HttpStatus.OK)
                : new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Operation(summary = "Exclui um usuário por ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUsuario(@PathVariable Long id) {
        UsuarioJava existente = usuarioService.readUsuarioById(id);
        if (existente != null) {
            usuarioService.deleteUsuario(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
