package com.Angelvf3839.tarea3dwesangel.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.Angelvf3839.tarea3dwesangel.modelo.Pedido;
import com.Angelvf3839.tarea3dwesangel.repositorios.PedidoRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ServiciosPedido {

    @Autowired
    private PedidoRepository pedidoRepository;

    public Pedido guardarPedido(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    public Pedido obtenerPedidoPorId(Long id) {
        Optional<Pedido> pedido = pedidoRepository.findById(id);
        return pedido.orElse(null);
    }

    public List<Pedido> obtenerTodosLosPedidos() {
        return pedidoRepository.findAll();
    }

    public void eliminarPedido(Long id) {
        pedidoRepository.deleteById(id);
    }
}
