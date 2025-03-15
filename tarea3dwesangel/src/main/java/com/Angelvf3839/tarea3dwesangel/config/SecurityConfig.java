package com.Angelvf3839.tarea3dwesangel.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import com.Angelvf3839.tarea3dwesangel.modelo.Credenciales;
import com.Angelvf3839.tarea3dwesangel.modelo.Perfil;
import com.Angelvf3839.tarea3dwesangel.modelo.Sesion;
import com.Angelvf3839.tarea3dwesangel.repositorios.CredencialesRepository;
import com.Angelvf3839.tarea3dwesangel.servicios.ServiciosCredenciales;

import jakarta.servlet.http.HttpSession;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CredencialesRepository credencialesRepository;
    
    @Autowired
    private ServiciosCredenciales serviciosCredenciales;
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/index", "/registroCliente").permitAll()
                .requestMatchers("/menuCliente", "/realizarPedido", "/carrito").hasRole("CLIENTE")
                .requestMatchers("/menuAdmin", "/gestionUsuarios").hasRole("ADMIN")
                .requestMatchers("/menuPersonal", "/gestionarEjemplares", "/gestionarMensajes").hasRole("PERSONAL")
                .anyRequest().authenticated()
            )
            .formLogin(login -> login
                .loginPage("/index")
                .loginProcessingUrl("/login")
                .successHandler((request, response, authentication) -> {
                    String username = authentication.getName();
                    
                    // Buscar el usuario en la base de datos
                    Credenciales credenciales = credencialesRepository.findByUsuario(username).orElse(null);
                    if (credenciales == null) {
                        response.sendRedirect("/index?error=UsuarioNoEncontrado");
                        return;
                    }
                    
                    // Determinar el perfil desde ServiciosCredenciales
                    Long idPersona = credenciales.getPersona().getId();
                    String role = serviciosCredenciales.determinarPerfil(idPersona);
                    
                    Perfil perfil = Perfil.valueOf(role);
                    
                    // Guardar usuario en sesión
                    HttpSession session = request.getSession();
                    session.setAttribute("usuario", new Sesion(idPersona, username, perfil));
                    session.setAttribute("tiempoInicio", System.currentTimeMillis());
                    
                    // Redireccionar según el rol
                    switch (perfil) {
                        case ADMIN:
                            response.sendRedirect("/menuAdmin");
                            break;
                        case PERSONAL:
                            response.sendRedirect("/menuPersonal");
                            break;
                        case CLIENTE:
                            response.sendRedirect("/menuCliente");
                            break;
                        default:
                            response.sendRedirect("/index?error=PerfilNoReconocido");
                    }
                })
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/index?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .sessionManagement(session -> session
                .maximumSessions(1)
                .expiredUrl("/index?expired=true")
            );
        
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }
}
