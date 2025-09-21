package br.com.fiap.api_rest.service;

import br.com.fiap.api_rest.model.UserRole;
import br.com.fiap.api_rest.model.UsuarioJava;
import br.com.fiap.api_rest.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    /** Usado pelo /auth/register */
    @Transactional
    public UsuarioJava create(String nome, String email, String senha, UserRole role) {
        if (role == null) {
            throw new IllegalArgumentException("Informe a função: GERENCIA_VAGA ou GERENCIA_MOTO.");
        }
        if (existsByEmail(email)) {
            throw new IllegalStateException("Email já cadastrado");
        }
        UsuarioJava u = new UsuarioJava();
        u.setNome(nome);
        u.setEmail(email);
        u.setSenha(encodeIfNeeded(senha));
        u.setRole(role);
        return usuarioRepository.save(u);
    }

    /** Mantido para compatibilidade com código legado; sem default de role. */
    @Transactional
    @CachePut(value = "usuarios", key = "#result.id")
    public UsuarioJava createUsuario(UsuarioJava usuario) {
        if (usuario.getEmail() == null || usuario.getSenha() == null) {
            throw new IllegalArgumentException("Email e senha são obrigatórios");
        }
        if (usuario.getRole() == null) {
            throw new IllegalArgumentException("Informe a função: GERENCIA_VAGA ou GERENCIA_MOTO.");
        }
        if (existsByEmail(usuario.getEmail())) {
            throw new IllegalStateException("Email já cadastrado");
        }
        usuario.setSenha(encodeIfNeeded(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    @Cacheable(value = "usuarios", key = "#id")
    public UsuarioJava readUsuarioById(Long id) {
        return usuarioRepository.findById(id).orElse(null);
    }

    @Cacheable(value = "usuarios", key = "'todos'")
    public Page<UsuarioJava> readUsuarios(Pageable pageable) {
        return usuarioRepository.findAll(pageable);
    }

    @Transactional
    @CachePut(value = "usuarios", key = "#result.id")
    public UsuarioJava updateUsuario(Long id, UsuarioJava usuario) {
        Optional<UsuarioJava> existente = usuarioRepository.findById(id);
        if (existente.isEmpty()) return null;

        usuario.setId(id);

        if (usuario.getSenha() != null && !usuario.getSenha().isBlank()) {
            usuario.setSenha(encodeIfNeeded(usuario.getSenha()));
        } else {
            usuario.setSenha(existente.get().getSenha());
        }

        // Se não enviar role no update, preserva o atual (evita quebrar o login)
        if (usuario.getRole() == null) {
            usuario.setRole(existente.get().getRole());
        }

        return usuarioRepository.save(usuario);
    }

    @Transactional
    @CacheEvict(value = "usuarios", key = "#id")
    public void deleteUsuario(Long id) {
        usuarioRepository.deleteById(id);
        limparCacheTodosUsuarios();
    }

    @CacheEvict(value = "usuarios", key = "'todos'")
    public void limparCacheTodosUsuarios() {}

    private String encodeIfNeeded(String rawOrEncoded) {
        if (rawOrEncoded == null) return null;
        String s = rawOrEncoded.trim();
        if (s.startsWith("$2a$") || s.startsWith("$2b$") || s.startsWith("$2y$")) return s;
        return passwordEncoder.encode(s);
    }
}
