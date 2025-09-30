package br.com.fiap.api_rest.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "VAGA_JAVA")
public class VagaJava {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String status;

    @NotNull(message = "O número da vaga é obrigatório")
    private int numero;

    @NotBlank(message = "O pátio da vaga é obrigatório")
    private String patio;

    @ManyToMany
    @JoinTable(
            name = "vaga_moto",
            joinColumns = @JoinColumn(name = "vaga_id"),
            inverseJoinColumns = @JoinColumn(name = "moto_id")
    )
    private List<MotoJava> motos;

    public VagaJava(Long id, String status, int numero, String patio) {
        this.id = id;
        this.status = status;
        this.numero = numero;
        this.patio = patio;
        this.motos = new ArrayList<>();
    }

    public VagaJava() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getNumero() {
        return numero;
    }

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public String getPatio() {
        return patio;
    }

    public void setPatio(String patio) {
        this.patio = patio;
    }

    public List<MotoJava> getMotos() {
        return motos;
    }

    public void setMotos(List<MotoJava> motos) {
        this.motos = motos;
    }
}
