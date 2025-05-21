package br.com.fiap.api_rest.service;

import br.com.fiap.api_rest.model.MotoJava;
import br.com.fiap.api_rest.repository.MotoRepository;
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
public class MotoService {

    private final MotoRepository motoRepository;

    @Autowired
    public MotoService(MotoRepository motoRepository) {
        this.motoRepository = motoRepository;
    }

    @Transactional
    @CachePut(value = "motos", key = "#result.id")
    public MotoJava createMoto(MotoJava moto) {
        return motoRepository.save(moto);
    }

    @Cacheable(value = "motos", key = "#id")
    public MotoJava readMotoById(Long id) {
        return motoRepository.findById(id).orElse(null);
    }

    @Cacheable(value = "motos", key = "'todos'")
    public Page<MotoJava> readMotos(Pageable pageable) {
        return motoRepository.findAll(pageable);
    }

    @Transactional
    @CachePut(value = "motos", key = "#result.id")
    public MotoJava updateMoto(Long id, MotoJava moto) {
        Optional<MotoJava> motoOptional = motoRepository.findById(id);
        if (motoOptional.isEmpty()) {
            return null;
        }
        moto.setId(id);
        return motoRepository.save(moto);
    }

    @Transactional
    @CacheEvict(value = "motos", key = "#id")
    public void deleteMoto(Long id) {
        motoRepository.deleteById(id);
        limparCacheTodosMotos();
    }

    @CacheEvict(value = "motos", key = "'todos'")
    public void limparCacheTodosMotos() {}
}
