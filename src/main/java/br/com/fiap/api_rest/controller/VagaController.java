package br.com.fiap.api_rest.controller;

import br.com.fiap.api_rest.dto.Request.VagaRequest;
import br.com.fiap.api_rest.dto.Response.MotoResponse;
import br.com.fiap.api_rest.dto.Response.VagaResponse;
import br.com.fiap.api_rest.mapper.VagaMapper;
import br.com.fiap.api_rest.mapper.MotoMapper;
import br.com.fiap.api_rest.service.MotoService;
import br.com.fiap.api_rest.model.VagaJava;
import br.com.fiap.api_rest.service.VagaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/vagas")
@Tag(name = "api-vagas-java")
public class VagaController {

    private final VagaService vagaService;
    private final MotoService motoService;
    private final VagaMapper vagaMapper = new VagaMapper();
    private final MotoMapper motoMapper = new MotoMapper();

    @Autowired
    public VagaController(VagaService vagaService, MotoService motoService) {
        this.vagaService = vagaService;
        this.motoService = motoService;
    }

    @Operation(summary = "Cadastra uma vaga")
    @PostMapping
    public ResponseEntity<VagaResponse> createVaga(@Valid @RequestBody VagaRequest vagaRequest) {
        VagaJava vaga = vagaMapper.requestToVaga(vagaRequest);
        VagaJava savedVaga = vagaService.createVaga(vaga);
        return new ResponseEntity<>(vagaMapper.vagaToResponse(savedVaga), HttpStatus.CREATED);
    }

    @Operation(summary = "Lista todas as vagas")
    @GetMapping
    public ResponseEntity<Page<VagaResponse>> readVagas(@RequestParam(defaultValue = "0") Integer pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 10, Sort.by("numero").ascending());
        Page<VagaJava> vagasPage = vagaService.readVagas(pageable);
        Page<VagaResponse> vagaResponses = vagasPage.map(vagaMapper::vagaToResponse);
        return new ResponseEntity<>(vagaResponses, HttpStatus.OK);
    }

    @Operation(summary = "Retorna uma vaga por ID")
    @GetMapping("/{id}")
    public ResponseEntity<VagaResponse> readVaga(@PathVariable Long id) {
        VagaJava vaga = vagaService.readVagaById(id);
        return vaga != null ?
                new ResponseEntity<>(vagaMapper.vagaToResponse(vaga), HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Atualiza uma vaga existente")
    @PutMapping("/{id}")
    public ResponseEntity<VagaResponse> updateVaga(@PathVariable Long id, @Valid @RequestBody VagaRequest vagaRequest) {
        VagaJava vagaAtualizada = vagaMapper.requestToVaga(vagaRequest);
        VagaJava updatedVaga = vagaService.updateVaga(id, vagaAtualizada);
        return updatedVaga != null ?
                new ResponseEntity<>(vagaMapper.vagaToResponse(updatedVaga), HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Operation(summary = "Exclui uma vaga por ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVaga(@PathVariable Long id) {
        VagaJava existingVaga = vagaService.readVagaById(id);
        if (existingVaga != null) {
            vagaService.deleteVaga(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Operation(summary = "Lista as motos relacionadas a uma vaga")
    @GetMapping("/{vagaId}/motos")
    public ResponseEntity<List<MotoResponse>> listarMotosDaVaga(@PathVariable Long vagaId) {
        VagaJava vaga = vagaService.readVagaById(vagaId);
        if (vaga == null) return ResponseEntity.notFound().build();
        List<MotoResponse> motos = vaga.getMotos().stream()
                .map(motoMapper::motoToResponse)
                .toList();
        return ResponseEntity.ok(motos);
    }

    @Operation(summary = "Relaciona uma moto a uma vaga")
    @PutMapping("/{vagaId}/motos/{motoId}")
    public ResponseEntity<VagaResponse> adicionarMotoNaVaga(@PathVariable Long vagaId, @PathVariable Long motoId) {
        VagaJava vaga = vagaService.readVagaById(vagaId);
        var moto = motoService.readMotoById(motoId);
        if (vaga == null || moto == null) return ResponseEntity.notFound().build();

        boolean jaExiste = vaga.getMotos().stream().anyMatch(m -> m.getId().equals(motoId));
        if (!jaExiste) {
            vaga.getMotos().add(moto);
            vagaService.updateVaga(vagaId, vaga);
        }
        return ResponseEntity.ok(vagaMapper.vagaToResponse(vaga));
    }

    @Operation(summary = "Remove a relação de uma moto com a vaga")
    @DeleteMapping("/{vagaId}/motos/{motoId}")
    public ResponseEntity<VagaResponse> removerMotoDaVaga(@PathVariable Long vagaId, @PathVariable Long motoId) {
        VagaJava vaga = vagaService.readVagaById(vagaId);
        if (vaga == null) return ResponseEntity.notFound().build();

        boolean removed = vaga.getMotos().removeIf(m -> m.getId().equals(motoId));
        if (removed) {
            vagaService.updateVaga(vagaId, vaga);
        }
        return ResponseEntity.ok(vagaMapper.vagaToResponse(vaga));
    }
}
