package br.com.fiap.api_rest.service;

import br.com.fiap.api_rest.mapper.VagaMapper;
import br.com.fiap.api_rest.model.VagaJava;
import br.com.fiap.api_rest.repository.VagaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class VagaService {

    private final VagaRepository vagaRepository;
    private final VagaMapper vagaMapper = new VagaMapper();

    @Autowired
    public VagaService(VagaRepository vagaRepository) {
        this.vagaRepository = vagaRepository;
    }

    @Transactional
    @CachePut(value = "vagas", key = "#result.id")
    public VagaJava createVaga(VagaJava vaga) {
        return vagaRepository.save(vaga);
    }

    @Cacheable(value = "vagas", key = "#id")
    public VagaJava readVagaById(Long id) {
        return vagaRepository.findById(id).orElse(null);
    }

    @Cacheable(value = "vagas", key = "'todos'")
    public Page<VagaJava> readVagas(Pageable pageable) {
        return vagaRepository.findAll(pageable);
    }

    @Transactional
    @CachePut(value = "vagas", key = "#result.id")
    public VagaJava updateVaga(Long id, VagaJava vaga) {
        Optional<VagaJava> vagaOptional = vagaRepository.findById(id);
        if (vagaOptional.isEmpty()) {
            return null;
        }
        vaga.setId(id);
        return vagaRepository.save(vaga);
    }

    @Transactional
    @CacheEvict(value = "vagas", key = "#id")
    public void deleteVaga(Long id) {
        vagaRepository.deleteById(id);
        limparCacheTodosVagas();
    }

    @CacheEvict(value = "vagas", key = "'todos'")
    public void limparCacheTodosVagas() {}
}
