package br.com.fiap.api_rest.controller;

import br.com.fiap.api_rest.model.UsuarioJava;
import br.com.fiap.api_rest.repository.UsuarioRepository;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/perfil")
public class PerfilController {

    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

    public PerfilController(UsuarioRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    private UsuarioJava me(Authentication auth) {
        if (auth == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Não autenticado");
        UsuarioJava u = repo.findByEmail(auth.getName());
        if (u == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuário não encontrado");
        return u;
    }

    @GetMapping("/me")
    public Map<String,Object> getMe(Authentication auth) {
        var u = me(auth);
        return Map.of("id", u.getId(), "nome", u.getNome(), "email", u.getEmail());
    }

    public record UpdateReq(String nome, String email, String senha, String confirmaSenha) {}

    @PutMapping("/me")
    public Map<String,String> updateMe(Authentication auth, @RequestBody UpdateReq body) {
        var u = me(auth);

        if (StringUtils.hasText(body.nome()))  u.setNome(body.nome().trim());

        if (StringUtils.hasText(body.email())) {
            String novo = body.email().trim().toLowerCase();
            if (!Objects.equals(novo, u.getEmail()) && repo.existsByEmail(novo)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "E-mail já cadastrado");
            }
            u.setEmail(novo);
        }

        if (StringUtils.hasText(body.senha())) {
            if (!Objects.equals(body.senha(), body.confirmaSenha()))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "As senhas não conferem");
            u.setSenha(encoder.encode(body.senha()));
        }

        repo.save(u);
        return Map.of("message", "Perfil atualizado com sucesso");
    }

    @DeleteMapping("/me")
    public Map<String, String> deleteMe(Authentication auth) {
        UsuarioJava u = me(auth);
        repo.delete(u);
        return Map.of("message", "Conta excluída");
    }
}

