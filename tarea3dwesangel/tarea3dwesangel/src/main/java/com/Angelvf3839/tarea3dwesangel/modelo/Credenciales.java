package com.Angelvf3839.tarea3dwesangel.modelo;

import java.io.Serializable;
import jakarta.persistence.*;

@Entity
@Table(name = "credenciales")
public class Credenciales implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String usuario;

    @Column(nullable = false)
    private String password;

    @OneToOne
    @JoinColumn(name = "id_persona", referencedColumnName = "id", unique = true)
    private Persona persona;

    @OneToOne
    @JoinColumn(name = "id_cliente", referencedColumnName = "id", unique = true)
    private Cliente cliente;


    public Credenciales() {
    }

    public Credenciales(String usuario, String password, Persona persona, Cliente cliente) {
        this.usuario = usuario;
        this.password = password;
        this.persona = persona;
        this.cliente = cliente;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Persona getPersona() {
        return persona;
    }

    public void setPersona(Persona persona) {
        this.persona = persona;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    @Override
    public String toString() {
        return "Credenciales [id=" + id + ", usuario=" + usuario + ", persona=" + persona + ", cliente=" + cliente + "]";
    }
}
