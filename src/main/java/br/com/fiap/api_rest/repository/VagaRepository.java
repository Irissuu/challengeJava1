package br.com.fiap.api_rest.repository;

import br.com.fiap.api_rest.model.VagaJava;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VagaRepository extends JpaRepository<VagaJava, Long> {
}
