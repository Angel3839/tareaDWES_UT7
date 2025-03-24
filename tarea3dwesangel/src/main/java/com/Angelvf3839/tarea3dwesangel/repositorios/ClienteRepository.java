package com.Angelvf3839.tarea3dwesangel.repositorios;

import com.Angelvf3839.tarea3dwesangel.modelo.Cliente;
import com.Angelvf3839.tarea3dwesangel.modelo.Persona;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    
    Optional<Cliente> findByNombre(String nombre);

    boolean existsByNif(String nif);

    @Query("SELECT c FROM Cliente c WHERE c.persona.id = :idPersona")
    Optional<Cliente> findByIdPersona(@Param("idPersona") Long idPersona);
    
    Optional<Cliente> findByNif(String nif);

}
