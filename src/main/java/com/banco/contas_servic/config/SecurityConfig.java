package com.banco.contas_servic.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());
        http.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/transferencias").hasRole("CLIENTE")
                .requestMatchers("/contas/*/validar-pin").hasRole("CLIENTE")
                .requestMatchers("/contas/*/pin").hasRole("CLIENTE")
                .requestMatchers("/contas/*/movimentos").hasAnyRole("CLIENTE", "FUNCIONARIO")
                .requestMatchers("/contas/*/bloquear", "/contas/*/desbloquear").hasRole("FUNCIONARIO")
                .requestMatchers("/contas/*/depositos").hasRole("FUNCIONARIO")
                .requestMatchers("/clientes/**").hasRole("FUNCIONARIO")
                .requestMatchers("/auditoria").hasRole("FUNCIONARIO")
                .requestMatchers("/contas", "/contas/*").hasAnyRole("CLIENTE", "FUNCIONARIO")
                .anyRequest().authenticated());
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}