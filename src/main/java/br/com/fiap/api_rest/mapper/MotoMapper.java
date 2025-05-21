package br.com.fiap.api_rest.mapper;

import br.com.fiap.api_rest.controller.MotoController;
import br.com.fiap.api_rest.dto.MotoRequest;
import br.com.fiap.api_rest.dto.MotoResponse;
import br.com.fiap.api_rest.dto.MotoResponseDTO;
import br.com.fiap.api_rest.model.MotoJava;
import org.springframework.hateoas.Link;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class MotoMapper {

    public MotoJava requestToMoto(MotoRequest motoRequest) {
        return new MotoJava(
                null,
                motoRequest.getPlaca(),
                motoRequest.getMarca(),
                motoRequest.getModelo(),
                motoRequest.getAno()
        );
    }

    public MotoResponse motoToResponse(MotoJava moto) {
        return new MotoResponse(
                moto.getId(),
                moto.getPlaca(),
                moto.getMarca(),
                moto.getModelo(),
                moto.getAno()
        );
    }

    public MotoResponseDTO motoToResponseDTO(MotoJava moto, boolean self) {
        Link link;
        if (self) {
            link = linkTo(methodOn(MotoController.class).readMoto(moto.getId())).withSelfRel();
        } else {
            link = linkTo(methodOn(MotoController.class).readMotos(0)).withRel("Lista de Motos");
        }
        return new MotoResponseDTO(
                moto.getId(),
                moto.getPlaca() + " - " + moto.getMarca() + " - " + moto.getModelo() + " - " + moto.getAno(),
                link
        );
    }
}
