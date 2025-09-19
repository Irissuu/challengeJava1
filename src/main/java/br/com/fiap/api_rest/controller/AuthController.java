package br.com.fiap.api_rest.controller;

import br.com.fiap.api_rest.dto.Request.AuthRequest;
import br.com.fiap.api_rest.dto.Response.AuthResponse;
import br.com.fiap.api_rest.dto.Request.RegisterRequest;
import br.com.fiap.api_rest.model.UserRole;
import br.com.fiap.api_rest.model.UsuarioJava;
import br.com.fiap.api_rest.repository.UsuarioRepository;
import br.com.fiap.api_rest.security.TokenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticação")
public class AuthController {

    private final AuthenticationManager authManager;
    private final TokenService tokenService;
    private final UsuarioRepository repo;
    private final PasswordEncoder encoder;

    public AuthController(AuthenticationManager authManager, TokenService tokenService,
                          UsuarioRepository repo, PasswordEncoder encoder) {
        this.authManager = authManager;
        this.tokenService = tokenService;
        this.repo = repo;
        this.encoder = encoder;
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar usuário (BCrypt). Role padrão: USER")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        if (repo.existsByEmail(req.getEmail())) {
            return ResponseEntity.badRequest().body(Map.of("error","E-mail já cadastrado"));
        }
        UsuarioJava u = new UsuarioJava();
        u.setNome(req.getNome());
        u.setEmail(req.getEmail());
        u.setSenha(encoder.encode(req.getSenha()));      // BCrypt
        u.setRole(req.getRole() == null ? UserRole.USER : req.getRole());
        repo.save(u);

        return ResponseEntity.ok(Map.of(
                "id", u.getId(),
                "nome", u.getNome(),
                "email", u.getEmail(),
                "role", u.getRole()
        ));
    }

    @PostMapping("/login")
    @Operation(summary = "Login — retorna JWT Bearer")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest req) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getSenha())
        );
        String token = tokenService.generate((User) auth.getPrincipal());
        return ResponseEntity.ok(AuthResponse.bearer(token));
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout — revoga o token atual (Authorization: Bearer <token>)")
    public ResponseEntity<Void> logout(@RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            tokenService.revoke(authorization.substring(7));
        }
        return ResponseEntity.noContent().build();
    }
}
