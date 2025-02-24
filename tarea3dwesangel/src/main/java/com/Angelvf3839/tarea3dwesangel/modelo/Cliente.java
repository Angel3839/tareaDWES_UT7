package com.Angelvf3839.tarea3dwesangel.modelo;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table (name="clientes")
public class Cliente implements Serializable {
	
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column
	private String nombre;
	
	@Column(nullable = false)
	private LocalDate fechanac;
	
	@Column(name = "nifnie", unique = true)
	private String nif;
	
	@Column
	private String direccion;

	@Column(unique = true)
	private String email;
	
	@Column(length = 9)
	private String telefono;
	
	@OneToOne
	@JoinColumn(name = "credenciales_id", referencedColumnName = "id", unique = true)
	private Credenciales credenciales;
	
	@OneToOne
	@JoinColumn(name = "id_persona", referencedColumnName = "id", unique = true)
	private Persona persona;

	@OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL)
	private Set<Pedido> pedidos = new HashSet<Pedido>();

	public Cliente() {
		super();
	}

	public Cliente(String nombre, LocalDate fechanac, String nif, String direccion, String email, String telefono,
			Credenciales credenciales) {
		this.nombre = nombre;
		this.fechanac = fechanac;
		this.nif = nif;
		this.direccion = direccion;
		this.email = email;
		this.telefono = telefono;
		this.credenciales = credenciales;
	}
	
	public Cliente(String nombre, LocalDate fechanac, String nif, String direccion, String email, String telefono) {
		this.nombre = nombre;
		this.fechanac = fechanac;
		this.nif = nif;
		this.direccion = direccion;
		this.email = email;
		this.telefono = telefono;
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

	public LocalDate getFechanac() {
		return fechanac;
	}

	public void setFechanac(LocalDate fechanac) {
		this.fechanac = fechanac;
	}

	public String getNif() {
		return nif;
	}

	public void setNif(String nif) {
		this.nif = nif;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public Credenciales getCredenciales() {
		return credenciales;
	}

	public void setCredenciales(Credenciales credenciales) {
		this.credenciales = credenciales;
	}
	
	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public Set<Pedido> getPedidos() {
		return pedidos;
	}

	public void setPedidos(Set<Pedido> pedidos) {
		this.pedidos = pedidos;
	}

	@Override
	public int hashCode() {
		return Objects.hash(nif);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Cliente other = (Cliente) obj;
		return Objects.equals(nif, other.nif);
	}

}