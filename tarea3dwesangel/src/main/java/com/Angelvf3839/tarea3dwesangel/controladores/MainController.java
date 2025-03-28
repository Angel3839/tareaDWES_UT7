package com.Angelvf3839.tarea3dwesangel.controladores;


import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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
import com.Angelvf3839.tarea3dwesangel.repositorios.PedidoRepository;
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

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataIntegrityViolationException;
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
    private PedidoRepository pedidoRepository;
    
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
    public String cerrarSesion(HttpSession session) {
        session.invalidate();
        System.out.println("Sesión cerrada correctamente.");
        return "redirect:/";
    }

    
    @GetMapping("/volverMenu")
    public String volverMenu(HttpSession session) {
        Sesion sesionActual = (Sesion) session.getAttribute("usuario");

        if (sesionActual == null) {
            return "redirect:/index";
        }

        Perfil perfil = sesionActual.getPerfilusuarioAutenticado();

        switch (perfil) {
            case ADMIN:
                return "redirect:/menuAdmin";
            case CLIENTE:
                return "redirect:/menuCliente";
            case PERSONAL:
                return "redirect:/menuPersonal";
            default:
                return "redirect:/index";
        }
    }


    

    @GetMapping("/filtrarPorPersona")
    public String filtrarMensajesPorPersona(@RequestParam(value = "idPersona", required = false) Long idPersona, 
                                            Model model, 
                                            RedirectAttributes redirectAttributes) {
        if (idPersona == null) {
            redirectAttributes.addFlashAttribute("errorSeleccion", "Debe seleccionar una persona antes de filtrar.");
            return "redirect:/MensajesForm";
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
    public String filtrarMensajesPorPlanta(@RequestParam(value = "codigoPlanta", required = false) String codigoPlanta,
                                           Model model,
                                           RedirectAttributes redirectAttributes) {

        if (codigoPlanta == null || codigoPlanta.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("errorSeleccionPlanta", "Debe seleccionar una planta antes de filtrar.");
            return "redirect:/MensajesForm";
        }

        List<Mensaje> mensajesFiltradosPorPlanta = mensajesRepository.buscarMensajesPorCodigoPlanta(codigoPlanta);

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
            @RequestParam(value = "fechaInicio", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaInicio,
            @RequestParam(value = "fechaFin", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaFin,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (fechaInicio == null || fechaFin == null) {
            redirectAttributes.addFlashAttribute("errorSeleccionFechas", "Debe seleccionar ambas fechas antes de filtrar.");
            return "redirect:/MensajesForm";
        }

        try {
            LocalDateTime fechaInicioLDT = fechaInicio.atStartOfDay();
            LocalDateTime fechaFinLDT = fechaFin.atTime(23, 59, 59);

            List<Mensaje> mensajesFiltradosPorFecha = mensajesRepository.findMensajesBetweenFechas(fechaInicioLDT, fechaFinLDT);

            model.addAttribute("mensajesFiltradosPorFecha", mensajesFiltradosPorFecha);
            model.addAttribute("personas", personaRepository.findAll());
            model.addAttribute("plantas", plantaRepository.findAll());
            model.addAttribute("ejemplares", ejemplarRepository.findAll());

            return "MensajesForm";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorSeleccionFechas", "Error al procesar las fechas.");
            return "redirect:/MensajesForm";
        }
    }


    @GetMapping("/ejemplares/verMensajes")
    public String verMensajesIniciales(@RequestParam(value = "idEjemplar", required = false) Long idEjemplar, Model model, RedirectAttributes redirectAttributes) {
        if (idEjemplar == null) {
            redirectAttributes.addFlashAttribute("errorSeleccionEjemplar", "Debe seleccionar un ejemplar antes de ver los mensajes.");
            return "redirect:/EjemplaresForm";
        }

        System.out.println("ID del ejemplar seleccionado: " + idEjemplar); 

        Ejemplar ejemplar = ejemplarRepository.findById(idEjemplar).orElse(null);
        
        if (ejemplar != null) {
            List<Mensaje> mensajesIniciales = mensajesRepository.findByEjemplarIdOrderByFechaHoraAsc(idEjemplar);
            System.out.println("Mensajes encontrados: " + mensajesIniciales.size()); 
            model.addAttribute("mensajesIniciales", mensajesIniciales);
        } else {
            model.addAttribute("errorSeleccionEjemplar", "No se encontró el ejemplar seleccionado.");
        }

        List<Ejemplar> listaEjemplares = ejemplarRepository.findAll();
        List<Planta> listaPlantas = plantaRepository.findAll();
        model.addAttribute("ejemplares", listaEjemplares);
        model.addAttribute("plantas", listaPlantas);
        
        return "EjemplaresForm";
    }




    
    @GetMapping("/index")
    public String login(@RequestParam(value = "usuario", defaultValue = "") String usuario, 
                        @RequestParam(value = "password", defaultValue = "") String password, 
                        Model model, HttpSession session) {

        List<Planta> plantas = serviciosPlanta.verTodas();
        model.addAttribute("plantas", plantas);

        if (usuario.isEmpty() || password.isEmpty()) {
            model.addAttribute("error", "Por favor, completa todos los campos.");
            return "index";
        }

        try {
            boolean autenticado = serviciosCredenciales.autenticar(usuario, password, session);
            if (autenticado) {
                long idUsuario = serviciosPersona.idUsuarioAutenticado(usuario);
                if (idUsuario == -1) {
                    System.out.println("Error al obtener los datos del usuario.");
                    model.addAttribute("error", "Error al obtener los datos del usuario.");
                    return "index"; 
                }

                System.out.println("Verificando si el usuario con ID " + idUsuario + " es cliente...");
                boolean esCliente = serviciosCliente.esCliente(idUsuario);
                System.out.println("¿El usuario con ID " + idUsuario + " es cliente?: " + esCliente);

                Perfil perfil;
                Cliente cliente = null;

                if ("admin".equalsIgnoreCase(usuario)) {
                    perfil = Perfil.ADMIN;
                } else if (!esCliente) { 
                    perfil = Perfil.PERSONAL;
                } else {
                    perfil = Perfil.CLIENTE;
                    cliente = serviciosCliente.obtenerClientePorIdPersona(idUsuario).orElse(null);
                    
                    if (cliente == null) {
                        System.out.println("No se encontró el cliente con idPersona: " + idUsuario);
                    } else {
                        System.out.println("Cliente obtenido correctamente: " + cliente.getNombre());
                    }
                }

                Sesion sesionUsuario = new Sesion(idUsuario, usuario, perfil);
                session.setAttribute("usuario", sesionUsuario);
                session.setAttribute("tiempoInicio", System.currentTimeMillis());

                if (perfil == Perfil.CLIENTE && cliente != null) {
                    session.setAttribute("clienteActual", cliente);
                    System.out.println("Cliente guardado en sesión: " + cliente.getNombre());
                } else {
                    System.out.println("No se guardó el cliente en la sesión.");
                }

                Sesion sesionDebug = (Sesion) session.getAttribute("usuario");
                if (sesionDebug != null) {
                    System.out.println("Sesión activa después del login:");
                    System.out.println("Usuario: " + sesionDebug.getUsuarioAutenticado());
                    System.out.println("Perfil: " + sesionDebug.getPerfilusuarioAutenticado());
                } else {
                    System.out.println("ERROR: No se guardó la sesión correctamente.");
                }

                System.out.println("Sesión iniciada con éxito como: " + usuario);
                System.out.println("Sesión iniciada con perfil: " + perfil);

                switch (perfil) {
                    case ADMIN:
                        return "redirect:/menuAdmin";
                    case PERSONAL:
                        return "redirect:/menuPersonal";
                    case CLIENTE:
                        return "redirect:/menuCliente";
                    default:
                        System.out.println("ERROR: Perfil no reconocido. Redirigiendo a index.");
                        return "redirect:/index";
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
    public String guardarEjemplar(@RequestParam String codigoPlanta, HttpSession session, RedirectAttributes redirectAttributes) {
        System.out.println("MÉTODO guardarEjemplar EJECUTADO");

        Sesion sesionActual = (Sesion) session.getAttribute("usuario");

        if (sesionActual == null) {
            System.out.println("ERROR: No hay una sesión activa o la sesión ha expirado.");
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para registrar un ejemplar.");
            return "redirect:/EjemplaresForm?error=true";
        }

        String usuario = sesionActual.getUsuarioAutenticado();
        Perfil perfil = sesionActual.getPerfilusuarioAutenticado();

        System.out.println("Usuario autenticado en sesión: " + usuario);
        System.out.println("Perfil del usuario: " + perfil);

        if (perfil != Perfil.ADMIN && perfil != Perfil.PERSONAL) {
            System.out.println("Usuario sin permisos para guardar ejemplares: " + usuario + " (Perfil: " + perfil + ")");
            redirectAttributes.addFlashAttribute("error", "No tienes permisos para registrar un ejemplar.");
            return "redirect:/EjemplaresForm?error=true";
        }

        Planta planta = plantaRepository.findByCodigo(codigoPlanta).orElse(null);
        if (planta == null) {
            System.out.println("Planta no encontrada con el código: " + codigoPlanta);
            redirectAttributes.addFlashAttribute("error", "Planta no encontrada.");
            return "redirect:/EjemplaresForm?error=true";
        }

        List<Ejemplar> ejemplaresDePlanta = ejemplarRepository.findByPlantaCodigoOrderByNombreAsc(codigoPlanta);
        int nuevoNumero = ejemplaresDePlanta.size() + 1;
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

        Persona persona = serviciosPersona.buscarPorNombreDeUsuario(usuario);
        if (persona == null) {
            System.out.println("No se encontró la persona que realizó el registro.");
            redirectAttributes.addFlashAttribute("error", "Error al asociar el mensaje al usuario.");
            return "redirect:/EjemplaresForm?error=true";
        }

        String contenidoMensaje = "Ejemplar registrado por " + persona.getNombre() + " el " + LocalDateTime.now();
        Mensaje mensajeInicial = new Mensaje(LocalDateTime.now(), contenidoMensaje, persona, ejemplar);
        mensajesRepository.save(mensajeInicial);

        System.out.println("Mensaje inicial creado correctamente.");
        redirectAttributes.addFlashAttribute("success", "Ejemplar guardado correctamente.");
        return "redirect:/EjemplaresForm?success=true";
    }





    @GetMapping("/ejemplares/filtrar")
    public String filtrarEjemplaresPorPlanta(
            @RequestParam(value = "codigoPlantas", required = false) List<String> codigosPlantas,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        if (codigosPlantas == null || codigosPlantas.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorSeleccionPlanta", "Debe seleccionar al menos una planta antes de filtrar.");
            return "redirect:/EjemplaresForm";
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
            if (codigo == null || codigo.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorNombreComun", "Debe seleccionar una planta antes de actualizar el nombre común.");
                return "redirect:/PlantasForm";
            }

            if (nuevoNombreComun == null || nuevoNombreComun.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorNombreComun", "El nombre común no puede estar vacío.");
                return "redirect:/PlantasForm";
            }

            Planta plantaSeleccionada = serviciosPlanta.buscarPorCodigo(codigo.trim());
            if (plantaSeleccionada == null) {
                redirectAttributes.addFlashAttribute("errorNombreComun", "No existe una planta con ese código.");
                return "redirect:/PlantasForm";
            }

            boolean actualizado = serviciosPlanta.actualizarNombreComun(codigo.trim(), nuevoNombreComun.trim());

            if (actualizado) {
                redirectAttributes.addFlashAttribute("successComun", "Nombre común actualizado con éxito a: " + nuevoNombreComun);
            } else {
                redirectAttributes.addFlashAttribute("errorNombreComun", "Error: No se pudo actualizar el nombre común.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorNombreComun", "Error al modificar el nombre común: " + e.getMessage());
        }

        return "redirect:/PlantasForm";
    }


    @PostMapping("/plantas/modificarNombreCientifico")
    public String modificarNombreCientifico(@RequestParam("codigo") String codigo, 
                                            @RequestParam("nuevoNombreCientifico") String nuevoNombreCientifico, 
                                            RedirectAttributes redirectAttributes) {
        try {
            if (codigo == null || codigo.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorNombreCientifico", "Debe seleccionar una planta antes de actualizar el nombre científico.");
                return "redirect:/PlantasForm";
            }

            if (nuevoNombreCientifico == null || nuevoNombreCientifico.trim().isEmpty()) {
                redirectAttributes.addFlashAttribute("errorNombreCientifico", "El nombre científico no puede estar vacío.");
                return "redirect:/PlantasForm";
            }

            Planta plantaSeleccionada = serviciosPlanta.buscarPorCodigo(codigo.trim());
            if (plantaSeleccionada == null) {
                redirectAttributes.addFlashAttribute("errorNombreCientifico", "No existe una planta con ese código.");
                return "redirect:/PlantasForm";
            }

            boolean actualizado = serviciosPlanta.actualizarNombreCientifico(codigo.trim(), nuevoNombreCientifico.trim());

            if (actualizado) {
                redirectAttributes.addFlashAttribute("successCientifico", "Nombre científico actualizado con éxito a: " + nuevoNombreCientifico);
            } else {
                redirectAttributes.addFlashAttribute("errorNombreCientifico", "Error: No se pudo actualizar el nombre científico.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorNombreCientifico", "Error al modificar el nombre científico: " + e.getMessage());
        }

        return "redirect:/PlantasForm";
    }

    
    @PostMapping("/mensajes/guardar")
    public String guardarMensaje(@RequestParam(value = "idEjemplar", required = false) Long idEjemplar,
            @RequestParam("mensajeTexto") String mensajeTexto,
            HttpSession session,
            RedirectAttributes redirectAttributes) {
    	
        System.out.println("MÉTODO guardarMensaje EJECUTADO");

        Sesion sesionActual = (Sesion) session.getAttribute("usuario");

        if (sesionActual == null) {
            System.out.println("ERROR: No hay una sesión activa.");
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión para enviar un mensaje.");
            return "redirect:/MensajesForm";
        }
        
        if (idEjemplar == null) {
            redirectAttributes.addFlashAttribute("errorSeleccionEjemplar", "Debe seleccionar un ejemplar antes de guardar el mensaje.");
            return "redirect:/MensajesForm";
        }

        String usuario = sesionActual.getUsuarioAutenticado();
        Perfil perfil = sesionActual.getPerfilusuarioAutenticado();

        if (perfil != Perfil.CLIENTE && perfil != Perfil.PERSONAL && perfil != Perfil.ADMIN) {
            redirectAttributes.addFlashAttribute("error", "No tienes permisos para enviar mensajes.");
            return "redirect:/MensajesForm";
        }

        Ejemplar ejemplar = serviciosEjemplar.buscarPorID(idEjemplar);
        if (ejemplar == null) {
            redirectAttributes.addFlashAttribute("error", "Ejemplar no encontrado.");
            return "redirect:/MensajesForm";
        }

        if (mensajeTexto.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El mensaje no puede estar vacío.");
            return "redirect:/MensajesForm";
        }

        if (mensajeTexto.length() > 500) {
            redirectAttributes.addFlashAttribute("error", "El mensaje es demasiado largo. Máximo 500 caracteres.");
            return "redirect:/MensajesForm";
        }

        Persona persona = serviciosPersona.buscarPorNombreDeUsuario(usuario);
        if (persona == null) {
            redirectAttributes.addFlashAttribute("error", "No se pudo asociar el mensaje a un usuario.");
            return "redirect:/MensajesForm";
        }

        Mensaje mensaje = new Mensaje(LocalDateTime.now(), mensajeTexto, persona, ejemplar);
        serviciosMensaje.insertar(mensaje);

        System.out.println("Mensaje guardado correctamente.");
        redirectAttributes.addFlashAttribute("success", "Mensaje guardado correctamente.");

        return "redirect:/MensajesForm";
    }





    
    @GetMapping("/MensajesForm")
    public String gestionMensajes(Model model) {
        try {
            List<Persona> personas = personaRepository.findAll();
            model.addAttribute("personas", personas);

            List<Mensaje> mensajes = mensajesRepository.findAll();
            List<Ejemplar> listaEjemplares = ejemplarRepository.findAll();
            List<Planta> listaPlantas = plantaRepository.findAll();

            model.addAttribute("mensajes", mensajes);
            model.addAttribute("ejemplares", listaEjemplares);
            model.addAttribute("plantas", listaPlantas);

        } catch (Exception e) {
            System.out.println("Error al cargar los mensajes: " + e.getMessage());
            model.addAttribute("error", "Error al cargar los mensajes.");
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
                redirectAttributes.addFlashAttribute("error", "La contraseña no cumple con los requisitos mínimos. Debe tener al menos 8 carácteres y un punto.");
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
    public String registrarCliente(@RequestParam(required = false) String nombre,
                                   @RequestParam(required = false) String email,
                                   @RequestParam(required = false) String usuario,
                                   @RequestParam(required = false) String password,
                                   @RequestParam(required = false) String nif,
                                   @RequestParam(required = false) String direccion,
                                   @RequestParam(required = false) String telefono,
                                   @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechanac,
                                   RedirectAttributes redirectAttributes) {
        try {
            if ((nombre == null || nombre.isBlank()) || 
                (email == null || email.isBlank()) ||
                (usuario == null || usuario.isBlank()) ||
                (password == null || password.isBlank()) ||
                (nif == null || nif.isBlank()) ||
                (direccion == null || direccion.isBlank()) ||
                (telefono == null || telefono.isBlank()) || 
                fechanac == null) {

                System.out.println("Error: Algunos campos están vacíos.");
                redirectAttributes.addFlashAttribute("error", "Error. Rellena el formulario por favor.");
                return "redirect:/registroCliente";
            }

            if (personaRepository.findByEmail(email).isPresent()) {
                System.out.println("Error: El email ya está registrado.");
                redirectAttributes.addFlashAttribute("error", "El email ya está registrado.");
                return "redirect:/registroCliente";
            }
            
            if (!serviciosCliente.validarFechaNacimiento(fechanac)) {
                System.out.println("Error: Fecha de nacimiento no válida.");
                redirectAttributes.addFlashAttribute("error", "La fecha de nacimiento no es válida. No puede ser futura.");
                return "redirect:/registroCliente";
            }

            if (credencialesRepository.findByUsuario(usuario).isPresent()) {
                System.out.println("Error: El usuario ya está registrado.");
                redirectAttributes.addFlashAttribute("error", "El usuario ya está registrado.");
                return "redirect:/registroCliente";
            }

            if (serviciosCliente.buscarClientePorNif(nif).isPresent()) {
                System.out.println("Error: El NIF ya está registrado.");
                redirectAttributes.addFlashAttribute("error", "El NIF ya está registrado.");
                return "redirect:/registroCliente";
            }
            
            if (!serviciosCliente.validarTelefono(telefono)) {
                System.out.println("Error: Teléfono no válido.");
                redirectAttributes.addFlashAttribute("error", "El número de teléfono no es válido. Debe contener 9 dígitos.");
                return "redirect:/registroCliente";
            }
            
            if (serviciosCliente.telefonoYaRegistrado(telefono)) {
                System.out.println("Error: El teléfono ya está registrado.");
                redirectAttributes.addFlashAttribute("error", "El número de teléfono ya está registrado.");
                return "redirect:/registroCliente";
            }
            
            if (!direccion.matches("^[a-zA-Z0-9áéíóúÁÉÍÓÚñÑºª,\\.\\-/ ]{3,100}$")) {
                System.out.println("Error: Dirección no válida.");
                redirectAttributes.addFlashAttribute("error", "La dirección contiene caracteres no válidos.");
                return "redirect:/registroCliente";
            }
            
            if (!serviciosCliente.validarUsuario(usuario)) {
                System.out.println("Error: Usuario no válido.");
                redirectAttributes.addFlashAttribute("error", "Usuario no válido. Debe contener al menos 4 letras y no tener espacios en blanco.");
                return "redirect:/registroCliente";
            }
            
            if (!serviciosCliente.validarEmail(email)) {
                System.out.println("Error: Email no válido.");
                redirectAttributes.addFlashAttribute("error", "Formato de Email no válido.");
                return "redirect:/registroCliente";
            }
            
            if (!serviciosCliente.validarContraseña(password)) {
                System.out.println("Error: Contraseña no válida.");
                redirectAttributes.addFlashAttribute("error", "Contraseña no válida. Debe contener al menos 6 carácteres.");
                return "redirect:/registroCliente";
            }

            if (!serviciosCliente.validarNIF(nif)) {
                System.out.println("Error: NIF no válido.");
                redirectAttributes.addFlashAttribute("error", "Formato de NIF no válido.");
                return "redirect:/registroCliente";
            }
            
            if (!serviciosCliente.validarNombre(nombre) ||
                !serviciosCliente.validarEmail(email) ||
                !serviciosCliente.validarUsuario(usuario) ||
                !serviciosCliente.validarContraseña(password) ||
                !serviciosCliente.validarNIF(nif) ||
                !serviciosCliente.validarTelefono(telefono)) {

                System.out.println("Error: Algún dato no es válido.");
                redirectAttributes.addFlashAttribute("error", "Datos inválidos, revise el formulario.");
                return "redirect:/registroCliente";
            }

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

            Credenciales credenciales = new Credenciales();
            credenciales.setUsuario(usuario);
            credenciales.setPassword(password);
            credenciales.setPersona(persona);

            persona.setCredenciales(credenciales);
            credenciales = credencialesRepository.save(credenciales);

            nuevoCliente.setCredenciales(credenciales);
            credenciales.setCliente(nuevoCliente);

            nuevoCliente = serviciosCliente.guardarCliente(nuevoCliente);
            credencialesRepository.save(credenciales);

            System.out.println("Cliente registrado exitosamente.");
            redirectAttributes.addFlashAttribute("success", "Cliente registrado exitosamente.");
            return "redirect:/registroCliente";

        } catch (DataIntegrityViolationException e) {
            System.out.println("Error de integridad de datos: " + e.getMessage());
            if (e.getMessage().contains("nif")) {
                redirectAttributes.addFlashAttribute("error", "El NIF ya está registrado.");
            } else {
                redirectAttributes.addFlashAttribute("error", "El email o usuario ya están en uso.");
            }
            return "redirect:/registroCliente";
        } catch (Exception e) {
            System.out.println("Error inesperado: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error inesperado: " + e.getMessage());
            return "redirect:/registroCliente";
        }
    }



    @GetMapping("/registroCliente")
    public String gestionCliente() {
        return "registroCliente"; 
    }
    
    @GetMapping("/realizarPedido")
    public String mostrarFormularioPedido(Model model) {
        List<Planta> listaPlantas = plantaRepository.findAll();
        model.addAttribute("plantas", listaPlantas);
        return "realizarPedido";
    }

    @PostMapping("/realizarPedido")
    public String realizarPedido(
            @RequestParam Map<String, String> params,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        System.out.println("MÉTODO realizarPedido EJECUTADO");

        Sesion sesionActual = (Sesion) session.getAttribute("usuario");
        if (sesionActual == null || sesionActual.getPerfilusuarioAutenticado() != Perfil.CLIENTE) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión como Cliente para realizar un pedido.");
            return "redirect:/menuCliente";
        }

        String nombreUsuario = sesionActual.getUsuarioAutenticado();
        Persona persona = serviciosPersona.buscarPorNombreDeUsuario(nombreUsuario);
        if (persona == null) {
            redirectAttributes.addFlashAttribute("error", "No se encontró la información del usuario.");
            return "redirect:/menuCliente";
        }

        Optional<Cliente> clienteOptional = serviciosCliente.obtenerClientePorIdPersona(persona.getId());
        if (clienteOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El usuario no está registrado como cliente.");
            return "redirect:/menuCliente";
        }

        Cliente cliente = clienteOptional.get();
        List<Ejemplar> ejemplaresSeleccionados = new ArrayList<>();
        boolean algunaPlantaSeleccionadaSinCantidad = false;

        for (String key : params.keySet()) {
            if (key.startsWith("cantidad_")) {
                String codigoPlanta = key.replace("cantidad_", "");
                String valor = params.get(key);

                String plantaCheckbox = params.get("planta_" + codigoPlanta);
                boolean plantaSeleccionada = plantaCheckbox != null;

                if (plantaSeleccionada && (valor == null || valor.trim().isEmpty())) {
                    algunaPlantaSeleccionadaSinCantidad = true;
                    continue;
                }

                if (!plantaSeleccionada) {
                    continue;
                }

                int cantidad;
                try {
                    cantidad = Integer.parseInt(valor.trim());

                    if (cantidad <= 0) {
                        redirectAttributes.addFlashAttribute("error", "La cantidad debe ser un número positivo para la planta con código: " + codigoPlanta);
                        return "redirect:/realizarPedido";
                    }

                } catch (NumberFormatException e) {
                    redirectAttributes.addFlashAttribute("error", "Cantidad inválida para la planta con código: " + codigoPlanta);
                    return "redirect:/realizarPedido";
                }

                if (cantidad > 0) {
                    List<Ejemplar> ejemplaresDisponibles = ejemplarRepository.findByPlantaCodigoOrderByNombreAsc(codigoPlanta)
                            .stream()
                            .filter(e -> e.getPedido() == null && e.isDisponible())
                            .collect(Collectors.toList());

                    if (ejemplaresDisponibles.size() < cantidad) {
                        redirectAttributes.addFlashAttribute("errorEjemplares", "No hay suficientes ejemplares disponibles de la planta con código: " + codigoPlanta);
                        return "redirect:/realizarPedido";
                    }

                    for (int i = 0; i < cantidad; i++) {
                        ejemplaresSeleccionados.add(ejemplaresDisponibles.get(i));
                    }
                }
            }
        }

        if (algunaPlantaSeleccionadaSinCantidad) {
            redirectAttributes.addFlashAttribute("error", "Has seleccionado una planta sin introducir la cantidad.");
            return "redirect:/realizarPedido";
        }

        if (ejemplaresSeleccionados.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorEjemplares", "No seleccionaste ejemplares válidos.");
            return "redirect:/realizarPedido";
        }

        Pedido pedidoTemporal = new Pedido();
        pedidoTemporal.setCliente(cliente);
        pedidoTemporal.setFecha(LocalDate.now());
        pedidoTemporal.setEjemplares(new HashSet<>(ejemplaresSeleccionados));

        session.setAttribute("carritoTemporal", pedidoTemporal);

        redirectAttributes.addFlashAttribute("success", "Pedido añadido al carrito.");
        return "redirect:/carrito";
    }

    
    @GetMapping("/carrito")
    public String verCarrito(Model model, HttpSession session, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        System.out.println("MÉTODO verCarrito EJECUTADO");

        Sesion sesionActual = (Sesion) session.getAttribute("usuario");

        if (sesionActual == null || sesionActual.getPerfilusuarioAutenticado() != Perfil.CLIENTE) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión como Cliente para ver el carrito.");
            return "redirect:/menuCliente";
        }

        String nombreUsuario = sesionActual.getUsuarioAutenticado();
        Persona persona = serviciosPersona.buscarPorNombreDeUsuario(nombreUsuario);
        if (persona == null) {
            redirectAttributes.addFlashAttribute("error", "No se encontró la información del usuario.");
            return "redirect:/menuCliente";
        }

        Optional<Cliente> clienteOptional = serviciosCliente.obtenerClientePorIdPersona(persona.getId());
        if (clienteOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "El usuario no está registrado como cliente.");
            return "redirect:/menuCliente";
        }

        Pedido pedido = (Pedido) session.getAttribute("carritoTemporal");

        if (pedido == null || pedido.getEjemplares() == null || pedido.getEjemplares().isEmpty()) {
            model.addAttribute("mensaje", "Tu carrito está vacío.");
            return "carrito";
        }

        model.addAttribute("pedido", pedido);
        model.addAttribute("carritoTemporalExiste", request.getSession().getAttribute("carritoTemporal") != null);
        model.addAttribute("ejemplares", pedido.getEjemplares());

        return "carrito";
    }


    @PostMapping("/confirmarPedido")
    public String confirmarPedido(HttpSession session, RedirectAttributes redirectAttributes) {
        System.out.println("MÉTODO confirmarPedido EJECUTADO");

        Sesion sesionActual = (Sesion) session.getAttribute("usuario");
        if (sesionActual == null || sesionActual.getPerfilusuarioAutenticado() != Perfil.CLIENTE) {
            redirectAttributes.addFlashAttribute("error", "Debes iniciar sesión como Cliente para confirmar el pedido.");
            return "redirect:/menuCliente";
        }

        Pedido pedido = (Pedido) session.getAttribute("carritoTemporal");
        if (pedido == null || pedido.getEjemplares() == null || pedido.getEjemplares().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "No hay pedido en el carrito para confirmar.");
            return "redirect:/carrito";
        }

        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        Set<Ejemplar> ejemplares = new HashSet<>(pedido.getEjemplares());

        for (Ejemplar ejemplar : ejemplares) {
            ejemplar.setPedido(pedidoGuardado);
            ejemplar.setDisponible(false);
            ejemplarRepository.saveAndFlush(ejemplar);
        }

        Persona persona = serviciosPersona.buscarPorNombreDeUsuario(sesionActual.getUsuarioAutenticado());
        for (Ejemplar ejemplar : ejemplares) {
            String contenidoMensaje = "Pedido de la planta " + ejemplar.getPlanta().getNombreComun() + " realizado.";
            Mensaje mensaje = new Mensaje(LocalDateTime.now(), contenidoMensaje, persona, ejemplar);
            mensajesRepository.save(mensaje);
        }


        session.removeAttribute("carritoTemporal");
        redirectAttributes.addFlashAttribute("success", "Pedido confirmado correctamente.");
        return "redirect:/carrito";
    }


    @PostMapping("/eliminarPedido")
    public String eliminarPedido(HttpSession session, RedirectAttributes redirectAttributes) {
        Pedido pedido = (Pedido) session.getAttribute("carritoTemporal");

        if (pedido != null) {
        	for (Ejemplar ejemplar : pedido.getEjemplares()) {
        	    ejemplar.setPedido(null);
        	    ejemplar.setDisponible(true);
        	}

            session.removeAttribute("carritoTemporal");
            redirectAttributes.addFlashAttribute("success", "Pedido eliminado correctamente del carrito.");
        } else {
            redirectAttributes.addFlashAttribute("error", "No hay pedido en el carrito para eliminar.");
        }

        return "redirect:/carrito";
    }


    @GetMapping("/filtrarEjemplaresDisponibles")
    @ResponseBody
    public List<Map<String, String>> filtrarEjemplaresDisponibles(@RequestParam("codigoPlanta") String codigoPlanta) {
        List<Ejemplar> ejemplaresDisponibles = ejemplarRepository.findByPlantaCodigoOrderByNombreAsc(codigoPlanta)
                .stream()
                .filter(e -> e.getPedido() == null)
                .collect(Collectors.toList());

        return ejemplaresDisponibles.stream().map(e -> {
            Map<String, String> ejemplarData = new HashMap<>();
            ejemplarData.put("nombre", e.getNombre());
            ejemplarData.put("codigoPlanta", e.getPlanta().getCodigo());
            return ejemplarData;
        }).collect(Collectors.toList());
    }


    
    @GetMapping("/stockEjemplares")
    public String mostrarStockEjemplares(Model model) {
        List<Planta> plantas = plantaRepository.findAll();
        model.addAttribute("plantas", plantas);
        return "stockEjemplares";
    }

    @GetMapping("/ejemplares/stock")
    public String verStockEjemplares(@RequestParam(value = "codigoPlanta", required = false) String codigoPlanta, 
                                     Model model, RedirectAttributes redirectAttributes) {
        List<Planta> plantas = plantaRepository.findAll();
        model.addAttribute("plantas", plantas);

        if (codigoPlanta == null || codigoPlanta.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorSeleccion", "Debe seleccionar una planta antes de ver el stock.");
            return "redirect:/stockEjemplares";
        }

        Optional<Planta> plantaOptional = plantaRepository.findByCodigo(codigoPlanta);
        
        if (plantaOptional.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorSeleccion", "No se encontró la planta con código: " + codigoPlanta);
            return "redirect:/stockEjemplares"; 
        }

        Planta planta = plantaOptional.get();
        List<Ejemplar> ejemplaresDePlanta = ejemplarRepository.findByPlantaCodigoOrderByNombreAsc(codigoPlanta);

        List<Ejemplar> ejemplaresDisponibles = ejemplaresDePlanta.stream()
                .filter(e -> e.getPedido() == null)
                .collect(Collectors.toList());

        List<Ejemplar> ejemplaresNoDisponibles = ejemplaresDePlanta.stream()
                .filter(e -> e.getPedido() != null)
                .collect(Collectors.toList());

        model.addAttribute("planta", planta);
        model.addAttribute("codigoPlanta", codigoPlanta);
        model.addAttribute("ejemplaresDisponibles", ejemplaresDisponibles);
        model.addAttribute("ejemplaresNoDisponibles", ejemplaresNoDisponibles);

        return "stockEjemplares";
    }

    
}