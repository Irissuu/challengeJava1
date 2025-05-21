package br.com.fiap.api_rest.mapper;

import br.com.fiap.api_rest.dto.VagaRequest;
import br.com.fiap.api_rest.dto.VagaResponse;
import br.com.fiap.api_rest.model.VagaJava;

public class VagaMapper {

    public VagaJava requestToVaga(VagaRequest vagaRequest) {
        return new VagaJava(
                null,
                vagaRequest.status(),
                vagaRequest.numero(),
                vagaRequest.patio()
        );
    }

    public VagaResponse vagaToResponse(VagaJava vaga) {
        return new VagaResponse(
                vaga.getId(),
                vaga.getStatus(),
                vaga.getNumero(),
                vaga.getPatio()
        );
    }
}
