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

    @Transactional
    public UsuarioJava create(String nome, String email, String senha, UserRole role) {
        if (role == null) {
            role = UserRole.NONE;
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

    @Transactional
    @CachePut(value = "usuarios", key = "#result.id")
    public UsuarioJava createUsuario(UsuarioJava usuario) {
        if (usuario == null) {
            throw new IllegalArgumentException("Payload do usuário é obrigatório");
        }
        if (usuario.getEmail() == null || usuario.getEmail().trim().isEmpty()) {
            throw new IllegalArgumentException("Email é obrigatório");
        }
        if (usuario.getSenha() == null || usuario.getSenha().trim().isEmpty()) {
            throw new IllegalArgumentException("Senha é obrigatória");
        }
        final String emailNormalizado = usuario.getEmail().trim().toLowerCase(java.util.Locale.ROOT);
        usuario.setEmail(emailNormalizado);
        if (usuario.getRole() == null) {
            usuario.setRole(UserRole.NONE);
        }
        if (existsByEmail(emailNormalizado)) {
            throw new IllegalStateException("Email já cadastrado");
        }
        usuario.setSenha(encodeIfNeeded(usuario.getSenha().trim()));
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
