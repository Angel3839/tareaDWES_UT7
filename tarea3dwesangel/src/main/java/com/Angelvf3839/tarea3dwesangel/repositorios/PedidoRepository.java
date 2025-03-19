package com.Angelvf3839.tarea3dwesangel.repositorios;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.Angelvf3839.tarea3dwesangel.modelo.Cliente;
import com.Angelvf3839.tarea3dwesangel.modelo.Pedido;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
	
	Optional<Pedido> findTopByClienteOrderByFechaDesc(Cliente cliente);
	
	Optional<Pedido> findTopByClienteAndConfirmadoFalseOrderByFechaDesc(Cliente cliente);

}
