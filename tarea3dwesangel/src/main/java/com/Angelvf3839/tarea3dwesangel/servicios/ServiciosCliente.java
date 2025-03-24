package com.Angelvf3839.tarea3dwesangel.servicios;

import com.Angelvf3839.tarea3dwesangel.modelo.Cliente;
import com.Angelvf3839.tarea3dwesangel.repositorios.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
public class ServiciosCliente {

    @Autowired
    private ClienteRepository clienteRepository;

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    private static final Pattern NIF_PATTERN = Pattern.compile("^[0-9]{8}[A-Za-z]$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[6-9][0-9]{8}$");

    public Cliente guardarCliente(Cliente cliente) {
        if (!validarCliente(cliente)) {
            throw new IllegalArgumentException("Datos del cliente no válidos");
        }
        return clienteRepository.save(cliente);
    }

    public Optional<Cliente> obtenerClientePorUsuario(String nombre) {
        return Optional.empty();
    }

    public Optional<Cliente> obtenerClientePorId(Long id) {
        return clienteRepository.findById(id);
    }

    public boolean existeNif(String nif) {
        return clienteRepository.existsByNif(nif);
    }
    
    public Optional<Cliente> buscarClientePorNif(String nif) {
        return clienteRepository.findByNif(nif);
    }

    
    public boolean esCliente(Long idUsuario) {
        Optional<Cliente> cliente = clienteRepository.findByIdPersona(idUsuario);
        return cliente.isPresent();
    }
    
    public Optional<Cliente> obtenerClientePorIdPersona(Long idPersona) {
        return clienteRepository.findByIdPersona(idPersona);
    }

        public boolean validarCliente(Cliente cliente) {
            return validarNombre(cliente.getNombre()) &&
                   validarFechaNacimiento(cliente.getFechanac()) &&
                   validarNIF(cliente.getNif()) &&
                   validarEmail(cliente.getEmail()) &&
                   validarTelefono(cliente.getTelefono()) &&
                   validarUsuario(cliente.getCredenciales().getUsuario()) &&
                   validarContraseña(cliente.getCredenciales().getPassword());
        }

        public boolean validarNombre(String nombre) {
            return nombre != null && !nombre.trim().isEmpty();
        }

        public boolean validarFechaNacimiento(LocalDate fecha) {
            return fecha != null && fecha.isBefore(LocalDate.now());
        }

        public boolean validarNIF(String nif) {
            return nif != null && Pattern.matches("^[0-9]{8}[A-Z]$", nif);
        }

        public boolean validarEmail(String email) {
            return email != null && Pattern.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$", email);
        }

        public boolean validarTelefono(String telefono) {
            return telefono != null && Pattern.matches("^\\d{9}$", telefono);
        }

        public boolean validarUsuario(String usuario) {
            return usuario != null && usuario.length() >= 4 && !usuario.contains(" ");
        }

        public boolean validarContraseña(String contrasena) {
            return contrasena != null && contrasena.length() >= 6;
        }
    
}
