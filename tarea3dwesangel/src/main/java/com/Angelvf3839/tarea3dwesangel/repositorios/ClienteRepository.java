package com.Angelvf3839.tarea3dwesangel.repositorios;

import com.Angelvf3839.tarea3dwesangel.modelo.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    Optional<Cliente> findByNombre(String nombre);

    boolean existsByNif(String nif);
}
