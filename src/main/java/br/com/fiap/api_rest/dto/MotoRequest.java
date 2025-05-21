package br.com.fiap.api_rest.dto;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;

public class MotoRequest {

    @Size(min = 7, max = 7, message = "A placa deve ter 7 caracteres")
    private String placa;

    @NotBlank(message = "A marca do veículo é obrigatório")
    private String marca;

    @NotBlank(message = "O modelo do veículo é obrigatório")
    private String modelo;

    @NotNull(message = "o ano do veículo é obrigatório")
    private int ano;

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getAno() {
        return ano;
    }

    public void setAno(int ano) {
        this.ano = ano;
    }
}
