package com.Angelvf3839.tarea3dwesangel.controladores;


import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.Angelvf3839.tarea3dwesangel.modelo.Cliente;
import com.Angelvf3839.tarea3dwesangel.modelo.Credenciales;
import com.Angelvf3839.tarea3dwesangel.modelo.Ejemplar;
import com.Angelvf3839.tarea3dwesangel.modelo.Mensaje;
import com.Angelvf3839.tarea3dwesangel.modelo.Pedido;
import com.Angelvf3839.tarea3dwesangel.modelo.Perfil;
import com.Angelvf3839.tarea3dwesangel.modelo.Persona;
import com.Angelvf3839.tarea3dwesangel.modelo.Planta;
import com.Angelvf3839.tarea3dwesangel.modelo.Sesion;
import com.Angelvf3839.tarea3dwesangel.repositorios.CredencialesRepository;
import com.Angelvf3839.tarea3dwesangel.repositorios.EjemplarRepository;
import com.Angelvf3839.tarea3dwesangel.repositorios.MensajeRepository;
import com.Angelvf3839.tarea3dwesangel.repositorios.PersonaRepository;
import com.Angelvf3839.tarea3dwesangel.repositorios.PlantaRepository;
import com.Angelvf3839.tarea3dwesangel.servicios.Controlador;
import com.Angelvf3839.tarea3dwesangel.servicios.ServiciosCliente;
import com.Angelvf3839.tarea3dwesangel.servicios.ServiciosCredenciales;
import com.Angelvf3839.tarea3dwesangel.servicios.ServiciosEjemplar;
import com.Angelvf3839.tarea3dwesangel.servicios.ServiciosMensaje;
import com.Angelvf3839.tarea3dwesangel.servicios.ServiciosPedido;
import com.Angelvf3839.tarea3dwesangel.servicios.ServiciosPersona;
import com.Angelvf3839.tarea3dwesangel.servicios.ServiciosPlanta;

import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;

@Controller
public class MainController {

    @Autowired
    private PlantaRepository plantaRepository;
    
    @Autowired
    private PersonaRepository personaRepository;
    
    @Autowired
    private EjemplarRepository ejemplarRepository;
    
    @Autowired
    private MensajeRepository mensajesRepository;
    
    @Autowired
    private CredencialesRepository credencialesRepository;
    
    @Autowired
    @Lazy
    private Controlador controlador;
    
    @Autowired
    private ServiciosCredenciales serviciosCredenciales;
    
    @Autowired
    private ServiciosPlanta serviciosPlanta;
    
    @Autowired
    private ServiciosEjemplar serviciosEjemplar;

    @Autowired
    private ServiciosPersona serviciosPersona;
    
    @Autowired
    private ServiciosMensaje serviciosMensaje;
    
    @Autowired
    private ServiciosCliente serviciosCliente;
    
    @Autowired
    private ServiciosPedido serviciosPedido;
    
    
    @GetMapping("/verPlantas")
    public String verPlantas(Model model) {
        List<Planta> listaPlantas = plantaRepository.findAll();
        
        if (listaPlantas == null || listaPlantas.isEmpty()) {
            System.out.println("No hay plantas en la base de datos.");
        } else {
            System.out.println("Se encontraron " + listaPlantas.size() + " plantas.");
        }

        model.addAttribute("plantas", listaPlantas);
        return "verPlantas";
    }
    
    @GetMapping("/menuAdmin")
    public String menuAdmin() {
        return "menuAdmin"; 
    }
    
    @GetMapping("/menuCliente")
    public String menuCliente() {
        return "menuCliente"; 
    }
    
    @GetMapping("/menuPersonal")
    public String menuPersonal() {
        return "menuPersonal"; 
    }
    
    @GetMapping("/")
    public String mostrarIndex(Model model) {
        List<Planta> listaPlantas = plantaRepository.findAll();
        model.addAttribute("plantas", listaPlantas);
        return "index";
    }
    
    @GetMapping("/cerrarSesion")
    public String cerrarSesion() {
        controlador.setUsuarioAutenticado(null);
        System.out.println("Sesión cerrada correctamente.");
        return "redirect:/";
    }

    
    @GetMapping("/volverMenu")
    public String volverMenu() {
        Sesion sesionActual = controlador.getUsuarioAutenticado();

        if (sesionActual != null) {
            Perfil perfilUsuario = sesionActual.getPerfilusuarioAutenticado();

            if (perfilUsuario == Perfil.ADMIN) {
                return "redirect:/menuAdmin";
            } else if (perfilUsuario == Perfil.PERSONAL) {
                return "redirect:/menuPersonal";
            }
        }

        return "redirect:/index";
    }
    
    

    @GetMapping("/filtrarPorPersona")
    public String filtrarMensajesPorPersona(@RequestParam("idPersona") Long idPersona, Model model) {
        if (idPersona == null) {
            model.addAttribute("error", "Debe seleccionar una persona válida.");
            return "MensajesForm";
        }

        List<Mensaje> mensajesFiltradosPorPersona = mensajesRepository.buscarPorPersona(idPersona);

        List<Mensaje> todosMensajes = mensajesRepository.findAll();
        List<Planta> listaPlantas = plantaRepository.findAll();
        List<Persona> listaPersonas = personaRepository.findAll();
        List<Ejemplar> listaEjemplares = ejemplarRepository.findAll();

        model.addAttribute("mensajes", todosMensajes);
        model.addAttribute("personas", listaPersonas);
        model.addAttribute("plantas", listaPlantas);
        model.addAttribute("ejemplares", listaEjemplares);
        
        model.addAttribute("mensajesFiltradosPorPersona", mensajesFiltradosPorPersona);
        
        return "MensajesForm";
    }



    @GetMapping("/filtrarPorPlanta")
    public String filtrarMensajesPorPlanta(@RequestParam(value = "codigoPlanta", required = false) String codigoPlanta, Model model) {
        List<Mensaje> mensajesFiltradosPorPlanta = new ArrayList<>();

        if (codigoPlanta != null && !codigoPlanta.isEmpty()) {
            mensajesFiltradosPorPlanta = mensajesRepository.buscarMensajesPorCodigoPlanta(codigoPlanta);
        }

        List<Mensaje> todosMensajes = mensajesRepository.findAll();
        List<Planta> listaPlantas = plantaRepository.findAll();
        List<Persona> listaPersonas = personaRepository.findAll();
        List<Ejemplar> listaEjemplares = ejemplarRepository.findAll();

        model.addAttribute("mensajes", todosMensajes);
        model.addAttribute("plantas", listaPlantas);
        model.addAttribute("personas", listaPersonas);
        model.addAttribute("ejemplares", listaEjemplares);
        
        model.addAttribute("mensajesFiltradosPorPlanta", mensajesFiltradosPorPlanta);
        
        return "MensajesForm";
    }

    @GetMapping("/filtrarPorRangoFechas")
    public String filtrarMensajesPorRangoFechas(
            @RequestParam("fechaInicio") String fechaInicioStr,
            @RequestParam("fechaFin") String fechaFinStr,
            Model model) {
        
        try {
            LocalDateTime fechaInicio = LocalDateTime.parse(fechaInicioStr + "T00:00:00");
            LocalDateTime fechaFin = LocalDateTime.parse(fechaFinStr + "T23:59:59");

            List<Mensaje> mensajesFiltradosPorFecha = mensajesRepository.findMensajesBetweenFechas(fechaInicio, fechaFin);

            List<Mensaje> todosMensajes = mensajesRepository.findAll();
            List<Planta> listaPlantas = plantaRepository.findAll();
            List<Persona> listaPersonas = personaRepository.findAll();
            List<Ejemplar> listaEjemplares = ejemplarRepository.findAll();

            model.addAttribute("mensajes", todosMensajes);
            model.addAttribute("plantas", listaPlantas);
            model.addAttribute("personas", listaPersonas);
            model.addAttribute("ejemplares", listaEjemplares);
            
            model.addAttribute("mensajesFiltradosPorFecha", mensajesFiltradosPorFecha);

        } catch (Exception e) {
            model.addAttribute("error", "Formato de fecha incorrecto.");
        }
        
        return "MensajesForm";
    }


    @GetMapping("/ejemplares/verMensajes")
    public String verMensajesIniciales(@RequestParam Long idEjemplar, Model model) {
        System.out.println("ID del ejemplar seleccionado: " + idEjemplar);
        
        Ejemplar ejemplar = ejemplarRepository.findById(idEjemplar).orElse(null);
        
        if (ejemplar != null) {
            List<Mensaje> mensajesIniciales = mensajesRepository.findByEjemplarIdOrderByFechaHoraAsc(idEjemplar);
            System.out.println("Mensajes encontrados: " + mensajesIniciales.size());
            
            model.addAttribute("mensajesIniciales", mensajesIniciales);
        } else {
            model.addAttribute("error", "No se encontró el ejemplar seleccionado.");
        }
        
        List<Ejemplar> listaEjemplares = ejemplarRepository.findAll();
        List<Planta> listaPlantas = plantaRepository.findAll();
        model.addAttribute("ejemplares", listaEjemplares);
        model.addAttribute("plantas", listaPlantas);
        
        return "EjemplaresForm";
    }



    
    /* Método para loguearse */
    @GetMapping("/index")
    public String login(@RequestParam(value = "usuario", defaultValue = "") String usuario, 
                        @RequestParam(value = "password", defaultValue = "") String password, 
                        Model model) {

        List<Planta> plantas = serviciosPlanta.verTodas();
        model.addAttribute("plantas", plantas);

        if (usuario.isEmpty() || password.isEmpty()) {
            model.addAttribute("error", "Por favor, completa todos los campos.");
            return "index";
        }

        try {
            boolean autenticado = serviciosCredenciales.autenticar(usuario, password);
            if (autenticado) {
                long idUsuario = serviciosPersona.idUsuarioAutenticado(usuario);
                if (idUsuario == -1) {
                    System.out.println("Error al obtener los datos del usuario.");
                    model.addAttribute("error", "Error al obtener los datos del usuario.");
                    return "index";
                }

                Perfil perfil;
                if ("admin".equalsIgnoreCase(usuario)) {
                    perfil = Perfil.ADMIN;
                } else if (serviciosCliente.esCliente(idUsuario)) {
                    perfil = Perfil.CLIENTE;
                } else {
                    perfil = Perfil.PERSONAL;
                }

                controlador.setUsuarioAutenticado(new Sesion(idUsuario, usuario, perfil));
                System.out.println("Sesión iniciada con éxito como: " + usuario);

                switch (perfil) {
                    case ADMIN:
                        return "redirect:/menuAdmin";
                    case CLIENTE:
                        return "redirect:/menuCliente";
                    default:
                        return "redirect:/menuPersonal";
                }
            } else {
                System.out.println("Usuario o contraseña incorrectos.");
                model.addAttribute("error", "Usuario o contraseña incorrectos.");
                return "index";
            }
        } catch (Exception e) {
            System.out.println("Error al iniciar sesión: " + e.getMessage());
            model.addAttribute("error", "Error al iniciar sesión: " + e.getMessage());
            return "index";
        }
    }



    @PostMapping("/ejemplares/guardar")
    public String guardarEjemplar(@RequestParam String codigoPlanta, RedirectAttributes redirectAttributes) {
        Planta planta = plantaRepository.findByCodigo(codigoPlanta).orElse(null);

        if (planta == null) {
            System.out.println("Planta no encontrada con el código: " + codigoPlanta);
            redirectAttributes.addFlashAttribute("error", "Planta no encontrada con el código proporcionado.");
            return "redirect:/EjemplaresForm?error=true";
        }

        List<Ejemplar> ejemplaresDePlanta = ejemplarRepository.findByPlantaCodigoOrderByNombreAsc(codigoPlanta);
        
        int nuevoNumero = 1;

        if (!ejemplaresDePlanta.isEmpty()) {
            for (Ejemplar e : ejemplaresDePlanta) {
                String nombre = e.getNombre();
                if (nombre.matches(codigoPlanta + "_EJ\\d+")) {
                    String numeroStr = nombre.substring(nombre.lastIndexOf("_EJ") + 3);
                    try {
                        int numero = Integer.parseInt(numeroStr);
                        if (numero >= nuevoNumero) {
                            nuevoNumero = numero + 1;
                        }
                    } catch (NumberFormatException ex) {
                        System.out.println("Error al convertir número del ejemplar: " + nombre);
                    }
                }
            }
        }

        String nombreEjemplar = String.format("%s_EJ%03d", planta.getCodigo(), nuevoNumero);

        Ejemplar ejemplar = new Ejemplar();
        ejemplar.setNombre(nombreEjemplar);
        ejemplar.setPlanta(planta);

        if (!serviciosEjemplar.validarEjemplar(ejemplar)) {
            System.out.println("Código de ejemplar no válido.");
            redirectAttributes.addFlashAttribute("error", "Código de ejemplar no válido.");
            return "redirect:/EjemplaresForm?error=true";
        }

        ejemplarRepository.save(ejemplar);
        System.out.println("Ejemplar guardado correctamente con nombre: " + nombreEjemplar);

        Sesion sesionActual = controlador.getUsuarioAutenticado();
        if (sesionActual == null) {
            System.out.println("No hay sesión activa.");
            redirectAttributes.addFlashAttribute("error", "No hay sesión activa para registrar el mensaje.");
            return "redirect:/EjemplaresForm?error=true";
        }

        Persona persona = serviciosPersona.buscarPorNombre(sesionActual.getUsuarioAutenticado());
        if (persona == null) {
            System.out.println("No se encontró la persona que realizó el registro.");
            redirectAttributes.addFlashAttribute("error", "Error al asociar el mensaje al usuario.");
            return "redirect:/EjemplaresForm?error=true";
        }

        String contenidoMensaje = "Ejemplar registrado por " + persona.getNombre() + " el " + LocalDateTime.now().toString();
        Mensaje mensajeInicial = new Mensaje(LocalDateTime.now(), contenidoMensaje, persona, ejemplar);

        mensajesRepository.save(mensajeInicial);
        System.out.println("Mensaje inicial creado correctamente.");

        redirectAttributes.addFlashAttribute("success", "Ejemplar y mensaje inicial guardados con éxito.");
        return "redirect:/EjemplaresForm?success=true";
    }

    @GetMapping("/ejemplares/filtrar")
    public String filtrarEjemplaresPorPlanta(@RequestParam("codigoPlantas") List<String> codigosPlantas, Model model) {
        if (codigosPlantas == null || codigosPlantas.isEmpty()) {
            model.addAttribute("error", "Debe seleccionar al menos una planta.");
            return "EjemplaresForm";
        }

        List<Ejemplar> ejemplaresFiltrados = ejemplarRepository.findByPlantaCodigoIn(codigosPlantas);

        List<Planta> listaPlantas = plantaRepository.findAll();
        List<Ejemplar> listaEjemplares = ejemplarRepository.findAll();

        model.addAttribute("plantas", listaPlantas);
        model.addAttribute("ejemplares", listaEjemplares);
        model.addAttribute("ejemplaresFiltrados", ejemplaresFiltrados);

        return "EjemplaresForm";
    }

    
    @GetMapping("/verEjemplares")
    public String verEjemplares(Model model) {
        List<Ejemplar> listaEjemplares = ejemplarRepository.findAll();
        model.addAttribute("ejemplares", listaEjemplares);
        return "EjemplaresForm";
    }
    
    @GetMapping("/EjemplaresForm")
    public String gestionEjemplares(Model model) {
        List<Planta> listaPlantas = plantaRepository.findAll();
        model.addAttribute("plantas", listaPlantas);

        List<Ejemplar> listaEjemplares = ejemplarRepository.findAll();
        model.addAttribute("ejemplares", listaEjemplares);

        return "EjemplaresForm";
    }
    
    
    @GetMapping("/PlantasForm")
    public String gestionPlantas(Model model) {
        List<Planta> listaPlantas = plantaRepository.findAll();
        model.addAttribute("plantas", listaPlantas);

        if (!model.containsAttribute("planta")) {
            model.addAttribute("planta", new Planta()); 
        }

        return "PlantasForm";
    }
    
    @PostMapping("/plantas/guardar")
    public String nuevaPlanta(@RequestParam("codigo") String codigo,
                              @RequestParam("nombreComun") String nombreComun,
                              @RequestParam("nombreCientifico") String nombreCientifico,
                              RedirectAttributes redirectAttributes) {
        try {
            if (!serviciosPlanta.validarCodigo(codigo)) {
                redirectAttributes.addFlashAttribute("error", "El código no es válido. Inténtalo de nuevo.");
                return "redirect:/PlantasForm?error=true";
            }

            if (serviciosPlanta.codigoExistente(codigo)) {
                redirectAttributes.addFlashAttribute("error", "El código ya está registrado. Inténtalo con otro.");
                return "redirect:/PlantasForm?error=true";
            }

            Planta nuevaPlanta = new Planta(codigo, nombreComun, nombreCientifico);

            if (!serviciosPlanta.validarPlanta(nuevaPlanta)) {
                redirectAttributes.addFlashAttribute("error", "Los datos de la planta no son válidos.");
                return "redirect:/PlantasForm?error=true";
            }

            serviciosPlanta.insertar(nuevaPlanta);
            redirectAttributes.addFlashAttribute("success", "Planta creada con éxito.");
            return "redirect:/PlantasForm?success=true";
            
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al crear la planta: " + e.getMessage());
        }
        return "redirect:/PlantasForm?error=true";

    }

    @PostMapping("/plantas/modificarNombre")
    public String modificarNombreComun(@RequestParam("codigo") String codigo, 
                                       @RequestParam("nuevoNombreComun") String nuevoNombreComun, 
                                       RedirectAttributes redirectAttributes) {
        try {
            Planta plantaSeleccionada = serviciosPlanta.buscarPorCodigo(codigo.trim());

            if (plantaSeleccionada == null) {
                redirectAttributes.addFlashAttribute("error", "No existe una planta con ese código.");
                return "redirect:/plantas";
            }

            if (nuevoNombreComun == null || nuevoNombreComun.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El nombre común no puede estar vacío.");
                return "redirect:/plantas";
            }

            boolean actualizado = serviciosPlanta.actualizarNombreComun(codigo.trim(), nuevoNombreComun.trim());

            if (actualizado) {
                redirectAttributes.addFlashAttribute("success", "Nombre común actualizado con éxito a: " + nuevoNombreComun);
            } else {
                redirectAttributes.addFlashAttribute("error", "Error: No se pudo actualizar el nombre común.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al modificar el nombre común: " + e.getMessage());
        }

        return "redirect:/PlantasForm";
    }

    @PostMapping("/plantas/modificarNombreCientifico")
    public String modificarNombreCientifico(@RequestParam("codigo") String codigo, 
                                           @RequestParam("nuevoNombreCientifico") String nuevoNombreCientifico, 
                                           RedirectAttributes redirectAttributes) {
        try {
            Planta plantaSeleccionada = serviciosPlanta.buscarPorCodigo(codigo.trim());

            if (plantaSeleccionada == null) {
                redirectAttributes.addFlashAttribute("error", "No existe una planta con ese código.");
                return "redirect:/PlantasForm";
            }

            if (nuevoNombreCientifico == null || nuevoNombreCientifico.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El nombre científico no puede estar vacío.");
                return "redirect:/PlantasForm";
            }

            boolean actualizado = serviciosPlanta.actualizarNombreCientifico(codigo.trim(), nuevoNombreCientifico.trim());

            if (actualizado) {
                redirectAttributes.addFlashAttribute("success", "Nombre científico actualizado con éxito a: " + nuevoNombreCientifico);
            } else {
                redirectAttributes.addFlashAttribute("error", "Error: No se pudo actualizar el nombre científico.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al modificar el nombre científico: " + e.getMessage());
        }

        return "redirect:/PlantasForm";
    }


    @PostMapping("/mensajes/guardar")
    public String guardarMensaje(@RequestParam("idEjemplar") Long idEjemplar, 
                                 @RequestParam("mensajeTexto") String mensajeTexto) {
        try {
            System.out.println("Recibido ID Ejemplar: " + idEjemplar);
            System.out.println("Mensaje recibido: " + mensajeTexto);

            Ejemplar ejemplar = serviciosEjemplar.buscarPorID(idEjemplar);
            if (ejemplar == null) {
                System.out.println("No existe un ejemplar con el ID proporcionado.");
                return "redirect:/MensajesForm?error=EjemplarNoEncontrado";
            }

            if (!serviciosMensaje.validarMensaje(mensajeTexto)) {
                System.out.println("El mensaje no es válido (demasiado largo o vacío).");
                return "redirect:/MensajesForm?error=MensajeNoValido";
            }

            Sesion sessionn = controlador.getUsuarioAutenticado();
            String nombreUsuario = sessionn.getUsuarioAutenticado();

            Persona persona = serviciosPersona.buscarPorNombreDeUsuario(nombreUsuario);
            if (persona == null) {
                System.out.println("No se encontró la persona autenticada.");
                return "redirect:/MensajesForm?error=PersonaNoEncontrada";
            }

            Mensaje mensaje = new Mensaje(LocalDateTime.now(), mensajeTexto, persona, ejemplar);
            serviciosMensaje.insertar(mensaje);  
            System.out.println("Mensaje añadido con éxito.");

            return "redirect:/MensajesForm?success=true";

        } catch (Exception e) {
            System.out.println("Error al crear el mensaje: " + e.getMessage());
            return "redirect:/MensajesForm?error=ErrorGuardado";
        }
    }

    
    @GetMapping("/MensajesForm")
    public String gestionMensajes(Model model) {
        try {
            LocalDateTime fechaInicio = LocalDateTime.of(1900, 1, 1, 0, 0);
            LocalDateTime fechaFin = LocalDateTime.now();
            List<Mensaje> mensajesPorFecha = serviciosMensaje.verMensajesRangoFechas(fechaInicio, fechaFin);

            Set<Persona> personasUnicas = mensajesPorFecha.stream()
                    .map(Mensaje::getPersona)
                    .collect(Collectors.toSet());
            List<Persona> personas = new ArrayList<>(personasUnicas);

            Set<Ejemplar> ejemplaresUnicos = mensajesPorFecha.stream()
                    .map(Mensaje::getEjemplar)
                    .collect(Collectors.toSet());
            List<Ejemplar> listaEjemplares = new ArrayList<>(ejemplaresUnicos);

            Set<Planta> plantasUnicas = mensajesPorFecha.stream()
                    .map(m -> m.getEjemplar().getPlanta())
                    .collect(Collectors.toSet());
            List<Planta> listaPlantas = new ArrayList<>(plantasUnicas);

            model.addAttribute("mensajesPorFecha", mensajesPorFecha);
            model.addAttribute("personas", personas);
            model.addAttribute("plantas", listaPlantas);
            model.addAttribute("ejemplares", listaEjemplares);

        } catch (Exception e) {
            System.out.println("Error al cargar los mensajes: " + e.getMessage());
        }

        return "MensajesForm";
    }
    
    @PostMapping("/personas/guardar")
    public String nuevaPersona(@RequestParam String nombre,
                               @RequestParam String email,
                               @RequestParam String usuario,
                               @RequestParam String password,
                               RedirectAttributes redirectAttributes) {
        try {
            if (nombre.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "El nombre no puede estar vacío.");
                return "redirect:/PersonasForm?error=true";
            }

            if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                redirectAttributes.addFlashAttribute("error", "El email no tiene un formato válido.");
                return "redirect:/PersonasForm?error=true";
            }

            if (serviciosPersona.emailExistente(email)) {
                redirectAttributes.addFlashAttribute("error", "El email ya está registrado en el sistema.");
                return "redirect:/PersonasForm?error=true";
            }

            if (usuario.contains(" ")) {
                redirectAttributes.addFlashAttribute("error", "El nombre de usuario no puede contener espacios.");
                return "redirect:/PersonasForm?error=true";
            }

            if (serviciosCredenciales.usuarioExistente(usuario)) {
                redirectAttributes.addFlashAttribute("error", "El nombre de usuario ya está registrado en el sistema.");
                return "redirect:/PersonasForm?error=true";
            }

            if (!serviciosCredenciales.validarContraseña(password)) {
                redirectAttributes.addFlashAttribute("error", "La contraseña no cumple con los requisitos mínimos.");
                return "redirect:/PersonasForm?error=true";
            }

            Persona nuevaPersona = new Persona();
            nuevaPersona.setNombre(nombre);
            nuevaPersona.setEmail(email);

            Credenciales credenciales = new Credenciales();
            credenciales.setUsuario(usuario);
            credenciales.setPassword(password);
            credenciales.setPersona(nuevaPersona);

            nuevaPersona.setCredenciales(credenciales);

            if (!serviciosPersona.validarPersona(nuevaPersona)) {
                redirectAttributes.addFlashAttribute("error", "Los datos de la persona no son válidos.");
                return "redirect:/PersonasForm?error=true";
            }

            serviciosPersona.insertar(nuevaPersona);
            redirectAttributes.addFlashAttribute("success", "Persona registrada con éxito.");
            return "redirect:/PersonasForm?success=true";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar la persona: " + e.getMessage());
            return "redirect:/PersonasForm?error=true";
        }
    }

    
    @GetMapping("/PersonasForm")
    public String gestionPersonas(Model model) {
    	List<Persona> listaPersonas = personaRepository.findAll();
        model.addAttribute("personas", listaPersonas);
    	return "PersonasForm";
    }
    
   
    @PostMapping("/clientes/registrar")
    public String registrarCliente(@RequestParam String nombre,
                                   @RequestParam String email,
                                   @RequestParam String usuario,
                                   @RequestParam String password,
                                   @RequestParam String nif,
                                   @RequestParam String direccion,
                                   @RequestParam String telefono,
                                   @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechanac,
                                   RedirectAttributes redirectAttributes) {
        try {
            Persona persona = new Persona();
            persona.setNombre(nombre);
            persona.setEmail(email);
            persona = serviciosPersona.guardarPersona(persona);

            Cliente nuevoCliente = new Cliente();
            nuevoCliente.setNombre(nombre);
            nuevoCliente.setEmail(email);
            nuevoCliente.setNif(nif);
            nuevoCliente.setDireccion(direccion);
            nuevoCliente.setTelefono(telefono);
            nuevoCliente.setFechanac(fechanac);
            nuevoCliente.setPersona(persona);

            nuevoCliente = serviciosCliente.guardarCliente(nuevoCliente);

            Credenciales credenciales = new Credenciales();
            credenciales.setUsuario(usuario);
            credenciales.setPassword(new BCryptPasswordEncoder().encode(password));
            credenciales.setPersona(persona);
            credenciales.setCliente(nuevoCliente);

            persona.setCredenciales(credenciales);
            nuevoCliente.setCredenciales(credenciales);

            credenciales = credencialesRepository.save(credenciales);

            redirectAttributes.addFlashAttribute("success", "Cliente registrado exitosamente.");
            return "redirect:/registroCliente?success=true";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al registrar el cliente: " + e.getMessage());
            return "redirect:/registroCliente?error=true";
        }
    }

    @GetMapping("/registroCliente")
    public String gestionCliente() {
        return "registroCliente"; 
    }

  
    
}
