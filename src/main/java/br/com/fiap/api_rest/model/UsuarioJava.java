package br.com.fiap.api_rest.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "usuarios", uniqueConstraints = @UniqueConstraint(name = "uk_usuario_email", columnNames = "email"))
public class UsuarioJava {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank private String nome;

    @Email @NotBlank
    private String email;

    @NotBlank @Size(min = 8)
    private String senha;

    @Enumerated(EnumType.STRING) @Column(nullable = false)
    private UserRole role = UserRole.USER;

    public UsuarioJava(Object o, @NotBlank(message = "O nome é obrigatório") String nome, @NotBlank(message = "O e-mail é obrigatório") @Email(message = "E-mail inválido") String email, @NotBlank(message = "A senha é obrigatória") @Size(min = 8, message = "A senha deve ter no mínimo 8 caracteres") String senha) {
    }

    public UsuarioJava() {

    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }
    public UserRole getRole() { return role; }
    public void setRole(UserRole role) { this.role = role; }
}
