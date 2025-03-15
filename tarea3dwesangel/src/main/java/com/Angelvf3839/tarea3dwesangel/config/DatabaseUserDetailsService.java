package com.Angelvf3839.tarea3dwesangel.config;

import com.Angelvf3839.tarea3dwesangel.modelo.Credenciales;
import com.Angelvf3839.tarea3dwesangel.repositorios.CredencialesRepository;
import com.Angelvf3839.tarea3dwesangel.servicios.ServiciosCredenciales;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpServletRequest;

@Service
public class DatabaseUserDetailsService implements UserDetailsService {

    private final CredencialesRepository credencialesRepository;
    private final ServiciosCredenciales serviciosCredenciales;
    private final HttpServletRequest request;

    public DatabaseUserDetailsService(CredencialesRepository credencialesRepository, ServiciosCredenciales serviciosCredenciales, HttpServletRequest request) {
        this.credencialesRepository = credencialesRepository;
        this.serviciosCredenciales = serviciosCredenciales;
        this.request = request;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Credenciales credencial = credencialesRepository.findByUsuario(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        HttpSession session = request.getSession();
        boolean autenticado = serviciosCredenciales.autenticar(username, credencial.getPassword(), session);

        if (!autenticado) {
            throw new UsernameNotFoundException("Autenticación fallida para usuario: " + username);
        }

        Long idPersona = credencial.getPersona().getId();
        String role = serviciosCredenciales.determinarPerfil(idPersona);

        return User.builder()
                .username(credencial.getUsuario())
                .password(credencial.getPassword())
                .roles(role)
                .build();
    }
}
