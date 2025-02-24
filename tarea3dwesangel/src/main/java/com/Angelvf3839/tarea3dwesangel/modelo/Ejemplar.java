package com.Angelvf3839.tarea3dwesangel.modelo;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "ejemplares")
public class Ejemplar implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @ManyToOne
    @JoinColumn(name = "idplanta", nullable = false)
    private Planta planta;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "pedido_id", nullable = true)
    private Pedido pedido;

    @OneToMany(mappedBy = "ejemplar", cascade = CascadeType.ALL)
    private List<Mensaje> mensajes = new LinkedList<>();

    public Ejemplar() {
    }

    public Ejemplar(String nombre, Planta planta) {
        this.nombre = nombre;
        this.planta = planta;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public Planta getPlanta() {
        return planta;
    }

    public void setPlanta(Planta planta) {
        this.planta = planta;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public List<Mensaje> getMensajes() {
        return mensajes;
    }

    public void setMensajes(List<Mensaje> mensajes) {
        this.mensajes = mensajes;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, nombre, planta, pedido);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Ejemplar other = (Ejemplar) obj;
        return Objects.equals(id, other.id) && Objects.equals(nombre, other.nombre)
                && Objects.equals(planta, other.planta) && Objects.equals(pedido, other.pedido);
    }

    @Override
    public String toString() {
        return "Ejemplar [id=" + id + ", nombre=" + nombre + ", planta=" + planta + ", pedido=" + pedido + "]";
    }
}
