package com.diegomartinez.taller.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/css/**", "/img/**").permitAll()
                // Solo el administrador puede eliminar trabajos
                .requestMatchers(HttpMethod.POST, "/trabajos/*/eliminar").hasRole("ADMINISTRADOR")
                .anyRequest().authenticated())
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/trabajos", true)
                .failureHandler(loginFailureHandler()))
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout"));
        return http.build();
    }

    /** Distingue cuenta inactiva de credenciales incorrectas. */
    private AuthenticationFailureHandler loginFailureHandler() {
        return (request, response, exception) -> {
            String destino = (exception instanceof DisabledException) ? "/login?inactivo" : "/login?error";
            response.sendRedirect(request.getContextPath() + destino);
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
