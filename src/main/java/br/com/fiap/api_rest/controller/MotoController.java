package br.com.fiap.api_rest.controller;

import br.com.fiap.api_rest.dto.Request.MotoRequest;
import br.com.fiap.api_rest.dto.Response.MotoResponse;
import br.com.fiap.api_rest.mapper.MotoMapper;
import br.com.fiap.api_rest.model.MotoJava;
import br.com.fiap.api_rest.service.MotoService;
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


@RestController
@RequestMapping(value = "/motos")
@Tag(name = "api-motos-java")
public class MotoController {

    private final MotoService motoService;
    private final MotoMapper motoMapper = new MotoMapper();

    @Autowired
    public MotoController(MotoService motoService) {
        this.motoService = motoService;
    }

    @Operation(summary = "Cadastra uma moto")
    @PostMapping
    public ResponseEntity<MotoResponse> createMoto(@Valid @RequestBody MotoRequest motoRequest) {
        MotoJava moto = motoMapper.requestToMoto(motoRequest);
        MotoJava savedMoto = motoService.createMoto(moto);
        return new ResponseEntity<>(motoMapper.motoToResponse(savedMoto), HttpStatus.CREATED);
    }

    @Operation(summary = "Lista todas as motos")
    @GetMapping
    public ResponseEntity<Page<MotoResponse>> readMotos(@RequestParam(defaultValue = "0") Integer pageNumber) {
        Pageable pageable = PageRequest.of(pageNumber, 10, Sort.by("placa").ascending());
        Page<MotoJava> motosPage = motoService.readMotos(pageable);
        Page<MotoResponse> motoResponses = motosPage.map(motoMapper::motoToResponse);
        return new ResponseEntity<>(motoResponses, HttpStatus.OK);
    }


    @Operation(summary = "Retorna uma moto por ID")
    @GetMapping("/{id}")
    public ResponseEntity<MotoResponse> readMoto(@PathVariable Long id) {
        MotoJava moto = motoService.readMotoById(id);
        return moto != null ?
                new ResponseEntity<>(motoMapper.motoToResponse(moto), HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @Operation(summary = "Atualiza uma moto existente")
    @PutMapping("/{id}")
    public ResponseEntity<MotoResponse> updateMoto(@PathVariable Long id, @Valid @RequestBody MotoRequest motoRequest) {
        MotoJava motoAtualizada = motoMapper.requestToMoto(motoRequest);
        MotoJava updatedMoto = motoService.updateMoto(id, motoAtualizada);
        return updatedMoto != null ?
                new ResponseEntity<>(motoMapper.motoToResponse(updatedMoto), HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @Operation(summary = "Exclui uma moto por ID")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMoto(@PathVariable Long id) {
        MotoJava existingMoto = motoService.readMotoById(id);
        if (existingMoto != null) {
            motoService.deleteMoto(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
