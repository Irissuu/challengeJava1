package br.com.fiap.api_rest.repository;

import br.com.fiap.api_rest.model.UsuarioJava;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepository extends JpaRepository<UsuarioJava, Long> {
    boolean existsByEmail(String email);
    UsuarioJava findByEmail(String email);
}
