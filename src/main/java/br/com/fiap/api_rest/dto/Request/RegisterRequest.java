package br.com.fiap.api_rest.dto.Request;

import br.com.fiap.api_rest.model.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class RegisterRequest {
    @NotBlank
    private String nome;

    @Email @NotBlank
    private String email;

    @NotBlank @Size(min = 8)
    private String senha;

    private UserRole role;

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
}
