package br.com.fiap.api_rest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class NavegacaoController {

    @GetMapping("/")
    public String root() {
        return "redirect:/login";
    }

    @GetMapping("/home")
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/cadastro")
    public String cadastro() {
        return "cadastro";
    }

    @GetMapping("/motos/list")
    public String motosList() {
        return "motos/list";
    }

    @GetMapping("/motos/form")
    public String motosForm() {
        return "motos/form";
    }

    @GetMapping("/vagas/list")
    public String vagasList() {
        return "vagas/list";
    }

    @GetMapping("/vagas/form")
    public String vagasForm() {
        return "vagas/form";
    }

    @GetMapping("/perfil")
    public String perfil() {
        return "perfil";
    }

    @GetMapping("/ver/motos")
    public String verMotos() { return "motos/verMotos"; }

    @GetMapping("/ver/vagas")
    public String verVagas() { return "vagas/verVagas"; }

    @GetMapping("/negado/motos")
    public String negadoMotos() {
        return "negadoMoto";
    }

    @GetMapping("/negado/vagas")
    public String negadoVagas() {
        return "negadoVaga";
    }

    @GetMapping("/negado")
    public String negado() {
        return "negadoMoto";
    }
}


