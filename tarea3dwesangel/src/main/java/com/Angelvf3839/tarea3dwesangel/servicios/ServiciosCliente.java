package com.Angelvf3839.tarea3dwesangel.servicios;

import com.Angelvf3839.tarea3dwesangel.modelo.Cliente;
import com.Angelvf3839.tarea3dwesangel.repositorios.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ServiciosCliente {

    @Autowired
    private ClienteRepository clienteRepository;

 
    public Cliente guardarCliente(Cliente cliente) {
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
    
    public boolean esCliente(Long idUsuario) {
        return clienteRepository.findById(idUsuario).isPresent();
    }

}
