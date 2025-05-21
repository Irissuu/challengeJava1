package br.com.fiap.api_rest.repository;

import br.com.fiap.api_rest.model.MotoJava;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MotoRepository extends JpaRepository<MotoJava, Long> {
}
