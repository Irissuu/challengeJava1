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
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Duration;
import java.util.Map;

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

    @Operation(summary = "Realiza login e grava cookie HttpOnly com o JWT")
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody @Valid AuthRequest body) {
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(body.email(), body.senha())
        );

        String jwt = tokenService.generate((UserDetails) auth.getPrincipal());

        ResponseCookie cookie = ResponseCookie.from("jwt", jwt)
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofHours(8))
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwt)
                .body(Map.of("token", jwt));
    }

    @Operation(summary = "Realiza logout (apaga o cookie)")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@CookieValue(value = "jwt", required = false) String token) {
        // se quiser, pode revogar o token recebido
        ResponseCookie clean = ResponseCookie.from("jwt", "")
                .httpOnly(true).secure(false).sameSite("Lax").path("/").maxAge(0)
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clean.toString())
                .body(Map.of("message", "Logout realizado com sucesso."));
    }

    @Operation(summary = "Meus dados")
    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication auth) {
        if (auth == null) return ResponseEntity.status(401).build();
        var roles = auth.getAuthorities().stream().map(a -> a.getAuthority()).toList();
        return ResponseEntity.ok(Map.of(
                "email", auth.getName(),
                "roles", roles
        ));
    }
}
