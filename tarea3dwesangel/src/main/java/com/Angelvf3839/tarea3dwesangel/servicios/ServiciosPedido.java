package com.Angelvf3839.tarea3dwesangel.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Angelvf3839.tarea3dwesangel.modelo.Cliente;
import com.Angelvf3839.tarea3dwesangel.modelo.Ejemplar;
import com.Angelvf3839.tarea3dwesangel.modelo.Pedido;
import com.Angelvf3839.tarea3dwesangel.modelo.Planta;
import com.Angelvf3839.tarea3dwesangel.repositorios.EjemplarRepository;
import com.Angelvf3839.tarea3dwesangel.repositorios.PedidoRepository;
import com.Angelvf3839.tarea3dwesangel.repositorios.PlantaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ServiciosPedido {

    @Autowired
    private PedidoRepository pedidoRepository;
    
    @Autowired
    private PlantaRepository plantaRepository;
    
    @Autowired
    private EjemplarRepository ejemplarRepository;

    private ConcurrentHashMap<Long, List<Pedido>> pedidosPorSesion = new ConcurrentHashMap<>();
    private ConcurrentHashMap<Long, LocalDateTime> sesionesClientes = new ConcurrentHashMap<>();

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

    public void agregarPedidoASesion(Long clienteId, Pedido pedido) {
        limpiarSesionExpirada(clienteId);
        pedidosPorSesion.computeIfAbsent(clienteId, k -> new ArrayList<>()).add(pedido);
        sesionesClientes.putIfAbsent(clienteId, LocalDateTime.now());
    }

    public List<Pedido> obtenerPedidosDeSesion(Long clienteId) {
        limpiarSesionExpirada(clienteId);
        return pedidosPorSesion.getOrDefault(clienteId, new ArrayList<>());
    }

    public void confirmarPedido(Long clienteId) {
        limpiarSesionExpirada(clienteId);
        List<Pedido> pedidos = pedidosPorSesion.get(clienteId);
        if (pedidos != null) {
            pedidoRepository.saveAll(pedidos);
            pedidosPorSesion.remove(clienteId);
        }
    }

    public void limpiarPedidosNoConfirmados(Long clienteId) {
        pedidosPorSesion.remove(clienteId);
    }

    public void cerrarSesion(Long clienteId) {
        limpiarPedidosNoConfirmados(clienteId);
        sesionesClientes.remove(clienteId);
    }

    private void limpiarSesionExpirada(Long clienteId) {
        LocalDateTime inicioSesion = sesionesClientes.get(clienteId);
        if (inicioSesion != null && LocalDateTime.now().isAfter(inicioSesion.plusHours(6))) {
            cerrarSesion(clienteId);
        }
    }
    
    public List<Planta> obtenerTodasLasPlantas() {
        return plantaRepository.findAll();
    }

    public Map<Planta, Integer> obtenerDetallesCarrito(Map<Long, Integer> carrito) {
        Map<Planta, Integer> detallesCarrito = new HashMap<>();
        for (Map.Entry<Long, Integer> entry : carrito.entrySet()) {
            Optional<Planta> plantaOpt = plantaRepository.findById(entry.getKey());
            plantaOpt.ifPresent(planta -> detallesCarrito.put(planta, entry.getValue()));
        }
        return detallesCarrito;
    }

    public void registrarPedido(Cliente cliente, Map<Long, Integer> carrito) {
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setFecha(LocalDate.now());

        Set<Ejemplar> ejemplares = new HashSet<>();

        for (Map.Entry<Long, Integer> entry : carrito.entrySet()) {
            Optional<Planta> plantaOpt = plantaRepository.findById(entry.getKey());
            if (plantaOpt.isPresent()) {
                Planta planta = plantaOpt.get();
                int cantidad = entry.getValue();

                for (int i = 0; i < cantidad; i++) {
                    Ejemplar ejemplar = new Ejemplar();
                    ejemplar.setPlanta(planta);
                    ejemplar.setPedido(pedido);
                    ejemplares.add(ejemplar);
                }
            }
        }

        pedido.setEjemplares(ejemplares);
        pedidoRepository.save(pedido);
    }
   


}
