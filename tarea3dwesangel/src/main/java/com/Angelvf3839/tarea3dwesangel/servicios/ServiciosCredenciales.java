package com.Angelvf3839.tarea3dwesangel.servicios;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.Angelvf3839.tarea3dwesangel.modelo.Credenciales;
import com.Angelvf3839.tarea3dwesangel.modelo.Persona;
import com.Angelvf3839.tarea3dwesangel.repositorios.CredencialesRepository;
import com.Angelvf3839.tarea3dwesangel.repositorios.PersonaRepository;

import jakarta.servlet.http.HttpSession;

	
	@Service
	public class ServiciosCredenciales {
		@Autowired
		CredencialesRepository credencialesRepo;
		
		@Autowired
		PersonaRepository personaRepo;
		
		@Autowired
	    private ServiciosCliente serviciosCliente;
	
		public boolean autenticar(String usuario, String password, HttpSession session) {
	        Optional<Credenciales> credencialesOpt = credencialesRepo.findByUsuario(usuario);

	        if (credencialesOpt.isPresent()) {
	            Credenciales credenciales = credencialesOpt.get();

	            if (credenciales.getPassword().equals(password)) {
	                session.setAttribute("idUsuario", credenciales.getPersona().getId());
	                session.setAttribute("usuario", usuario);
	                session.setAttribute("perfil", determinarPerfil(credenciales.getPersona().getId()));

	                System.out.println("Sesión iniciada con éxito como: " + usuario);
	                return true;
	            }
	        }

	        System.out.println("Usuario o contraseña incorrectos.");
	        return false;
	    }

		private String determinarPerfil(Long idUsuario) {
		    if (idUsuario == null) {
		        return "INVITADO";
		    } else if (idUsuario == 1) {
		        return "ADMIN";
		    } else if (serviciosCliente.esCliente(idUsuario)) {
		        return "CLIENTE";
		    } 
		    return "PERSONAL";
		}



	    public void cerrarSesion(HttpSession session) {
	        session.invalidate();
	    }
	
	public boolean usuarioExistente(String usuario) {
        return credencialesRepo.existsByUsuario(usuario);
    }
	
	public void guardarCredenciales(Credenciales credenciales) {
	    credencialesRepo.save(credenciales);
	}

	
	public void insertar(String usuario, String password, Long idPersona) {
        Persona p = personaRepo.findById(idPersona).orElse(null);
        Credenciales credenciales = new Credenciales();
        credenciales.setUsuario(usuario);
        credenciales.setPassword(password);
        credenciales.setPersona(p);
        credencialesRepo.save(credenciales);
    }
	
	/* Método para validar una contraseña */
	
	public boolean validarContraseña(String contraseña) {
		if (contraseña.matches("^(?=.*[.,])[A-Za-z0-9.,]{8,}$")) {
			return true;
		}
		return false;
	}
}