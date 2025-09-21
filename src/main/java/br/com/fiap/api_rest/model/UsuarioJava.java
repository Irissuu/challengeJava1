package br.com.fiap.api_rest.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class UsuarioJava {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")               // armazena "GERENCIA_VAGA" ou "GERENCIA_MOTO"
    private UserRole role;               // <-- sem default!

    // getters e setters
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
