package br.com.fiap.api_rest.repository;

import br.com.fiap.api_rest.model.UsuarioJava;
import br.com.fiap.api_rest.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<UsuarioJava, Long> {
    boolean existsByEmail(String email);
    UsuarioJava findByEmail(String email);
    long countByRole(UserRole role); // opcional, útil se precisar checar existência de ADMIN
}
