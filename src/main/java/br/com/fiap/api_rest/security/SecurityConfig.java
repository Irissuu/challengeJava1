package br.com.fiap.api_rest.security;

import br.com.fiap.api_rest.repository.UsuarioRepository;
import br.com.fiap.api_rest.model.UsuarioJava;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService(UsuarioRepository repo) {
        return username -> {
            UsuarioJava u = repo.findByEmail(username);
            if (u == null) throw new UsernameNotFoundException("Usuário não encontrado: " + username);
            return User.withUsername(u.getEmail())
                    .password(u.getSenha())
                    .roles(u.getRole().name())
                    .build();
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @Bean
    public DaoAuthenticationProvider authProvider(UserDetailsService uds, PasswordEncoder enc) {
        var p = new DaoAuthenticationProvider();
        p.setUserDetailsService(uds);
        p.setPasswordEncoder(enc);
        return p;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration cfg) throws Exception {
        return cfg.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthFilter jwtFilter,
                                           DaoAuthenticationProvider daoAuthProvider) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(daoAuthProvider)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**","/js/**","/images/**","/webjars/**").permitAll()
                        .requestMatchers("/v3/api-docs/**","/swagger-ui/**","/swagger-ui.html","/docs/**").permitAll()

                        .requestMatchers("/", "/home", "/login", "/cadastro", "/negado").permitAll()
                        .requestMatchers("/auth/**").permitAll()

                        .requestMatchers(HttpMethod.GET,
                                "/motos/list", "/motos/form", "/motos/form/**", "/motos/edit/**", "/motos/delete/**"
                        ).hasRole("GERENCIA_MOTO")

                        .requestMatchers(HttpMethod.GET,
                                "/vagas/list", "/vagas/form", "/vagas/form/**", "/vagas/edit/**", "/vagas/delete/**"
                        ).hasRole("GERENCIA_VAGA")

                        .requestMatchers(HttpMethod.GET, "/motos/*/*").hasRole("GERENCIA_MOTO")
                        .requestMatchers(HttpMethod.GET, "/vagas/*/*").hasRole("GERENCIA_VAGA")

                        .requestMatchers("/ver/**").authenticated()
                        .requestMatchers("/negado/motos", "/negado/vagas").permitAll()

                        .requestMatchers(HttpMethod.GET, "/motos", "/motos/*", "/vagas", "/vagas/*")
                        .hasAnyRole("NONE","GERENCIA_MOTO","GERENCIA_VAGA")

                        .requestMatchers(HttpMethod.GET, "/usuarios/me").authenticated()
                        .requestMatchers(HttpMethod.PUT, "/usuarios/me").authenticated()
                        .requestMatchers(HttpMethod.DELETE, "/usuarios/me").authenticated()
                        .requestMatchers("/perfil").authenticated()
                        .requestMatchers("/perfil/**").authenticated()

                        .requestMatchers(HttpMethod.POST,   "/motos/**").hasRole("GERENCIA_MOTO")
                        .requestMatchers(HttpMethod.PUT,    "/motos/**").hasRole("GERENCIA_MOTO")
                        .requestMatchers(HttpMethod.DELETE, "/motos/**").hasRole("GERENCIA_MOTO")

                        .requestMatchers(HttpMethod.POST,   "/vagas/**").hasRole("GERENCIA_VAGA")
                        .requestMatchers(HttpMethod.PUT,    "/vagas/**").hasRole("GERENCIA_VAGA")
                        .requestMatchers(HttpMethod.DELETE, "/vagas/**").hasRole("GERENCIA_VAGA")


                        .requestMatchers("/motos/**").hasRole("GERENCIA_MOTO")
                        .requestMatchers("/vagas/**").hasRole("GERENCIA_VAGA")
                        .requestMatchers("/usuarios/**").hasAnyRole("GERENCIA_VAGA","GERENCIA_MOTO")

                        .requestMatchers("/favicon.ico", "/error").permitAll()

                        .anyRequest().authenticated()
                )

                .exceptionHandling(e -> e
                        .authenticationEntryPoint((req, res, ex) -> res.sendRedirect("/login"))
                        .accessDeniedHandler((req, res, ex) -> {
                            String uri = req.getRequestURI();
                            if (uri.startsWith("/motos")) res.sendRedirect("/negado/motos");
                            else if (uri.startsWith("/vagas")) res.sendRedirect("/negado/vagas");
                            else res.sendRedirect("/negado");
                        })
                )

                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
