package br.com.fiap.api_rest.controller;

import br.com.fiap.api_rest.dto.Request.AuthRequest;
import br.com.fiap.api_rest.dto.Request.RegisterRequest;
import br.com.fiap.api_rest.dto.Response.UsuarioResponse;
import br.com.fiap.api_rest.model.UsuarioJava;
import br.com.fiap.api_rest.service.TokenService;
import br.com.fiap.api_rest.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationManager authManager;
    private final TokenService tokenService;
    private final UsuarioService usuarioService;

    public AuthController(AuthenticationManager authManager,
                          TokenService tokenService,
                          UsuarioService usuarioService) {
        this.authManager = authManager;
        this.tokenService = tokenService;
        this.usuarioService = usuarioService;
    }

    @Operation(summary = "Cadastra um usuário com a função GERENCIA_VAGA ou GERENCIA_MOTO")
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        if (req.role() == null) {
            return ResponseEntity.badRequest().body("Informe o role: GERENCIA_VAGA ou GERENCIA_MOTO.");
        }
        try {
            UsuarioJava saved = usuarioService.create(req.nome(), req.email(), req.senha(), req.role());
            UsuarioResponse body = new UsuarioResponse(saved.getId(), saved.getNome(), saved.getEmail());
            return ResponseEntity.created(URI.create("/usuarios/" + saved.getId())).body(body);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    @Operation(summary = "Realiza login")
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody AuthRequest body) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(body.email(), body.senha()));
        String jwt = tokenService.generate((UserDetails) auth.getPrincipal());
        return ResponseEntity.ok()
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                .body(jwt);
    }

    @Operation(summary = "Realiza logout")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(
            @RequestHeader(value = HttpHeaders.AUTHORIZATION, required = false) String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", "Authorization header ausente ou inválido (use: Bearer <token>)."));
        }

        String token = authorization.substring(7).trim();
        if (token.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(java.util.Map.of("error", "Token não informado."));
        }

        try {
            tokenService.getSubject(token);

            tokenService.revoke(token);

            return ResponseEntity.ok(java.util.Map.of("message", "Logout realizado com sucesso."));
        } catch (Exception ex) {
            return ResponseEntity.status(org.springframework.http.HttpStatus.UNAUTHORIZED)
                    .body(java.util.Map.of("error", "Token inválido ou expirado."));
        }
    }

@GetMapping("/me")
public ResponseEntity<?> me(Authentication auth) {
    if (auth == null) return ResponseEntity.status(401).build();
    var user = auth.getName();
    var roles = auth.getAuthorities().stream().map(a -> a.getAuthority()).toList();
    return ResponseEntity.ok(new java.util.HashMap<>() {{
        put("email", user);
        put("roles", roles);
    }});
}
}
