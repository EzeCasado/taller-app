package ezecasado.tallerapp.controller;


import ezecasado.tallerapp.models.Empleado;
import ezecasado.tallerapp.service.EmpleadoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

@RestController
@RequestMapping("/api/empleados")
@Tag(name = "Empleados", description = "Endpoints para la gestión de empleados y administradores del taller")
/**
 * Clase: EmpleadoController.
 * 
 * Esta clase es responsable de proveer las funcionalidades relacionadas con EmpleadoController
 * dentro del dominio de la aplicación.
 * 
 * @author EzeCasado
 * @version 1.0
 */
public class EmpleadoController {

    private final EmpleadoService empleadoService;
    private final PasswordEncoder passwordEncoder;

    public EmpleadoController(EmpleadoService empleadoService, PasswordEncoder passwordEncoder) {
        this.empleadoService = empleadoService;
        this.passwordEncoder = passwordEncoder;
    }


    @Operation(summary = "Crear nuevo empleado", description = "Registra un nuevo empleado. Solo accesible por Administradores")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/crear")
    public ResponseEntity<Empleado> crearEmpleado(@RequestBody Empleado nuevoEmpleado){

        // Encriptamos la contraseña antes de guardar
        nuevoEmpleado.setContrasenia(passwordEncoder.encode(nuevoEmpleado.getContrasenia()));
        Empleado empleado = empleadoService.crearEmpleado(nuevoEmpleado);

        return ResponseEntity.status(HttpStatus.CREATED).body(empleado);

    }

    @Operation(summary = "Actualizar empleado", description = "Modifica los datos de un empleado existente. Solo accesible por Administradores")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Empleado> actualizarEmpleado(@PathVariable("id") Long id, @RequestBody Empleado nuevoEmpleado){

        Empleado empleadoExistente = empleadoService.buscarEmpleadoPorId(id);

        empleadoExistente.setNombre(nuevoEmpleado.getNombre());
        empleadoExistente.setUsuario(nuevoEmpleado.getUsuario());

        // Solo actualizamos la contraseña si se envió una nueva (no vacía)
        if (nuevoEmpleado.getContrasenia() != null && !nuevoEmpleado.getContrasenia().isBlank()) {
            empleadoExistente.setContrasenia(passwordEncoder.encode(nuevoEmpleado.getContrasenia()));
        }

        Empleado empleado = empleadoService.actualizarEmpleado(empleadoExistente);

        return ResponseEntity.ok(empleado);

    }

    @Operation(summary = "Buscar empleado por ID", description = "Devuelve los datos de un empleado específico")
    @GetMapping("/{id}")
    public ResponseEntity<Empleado> buscarEmpleadoPorId(@PathVariable("id") Long id){

        return ResponseEntity.ok(empleadoService.buscarEmpleadoPorId(id));

    }

    @Operation(summary = "Listar empleados activos", description = "Devuelve la lista de todos los empleados que no han sido dados de baja")
    @GetMapping
    public ResponseEntity<List<Empleado>> listarEmpleadosActivos(){
        return ResponseEntity.ok(empleadoService.listarEmpleadosActivos());
    }


    @Operation(summary = "Eliminar empleado (Borrado lógico)", description = "Marca a un empleado como inactivo. Solo accesible por Administradores")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminarEmpleado(@PathVariable("id") Long id){

        empleadoService.eliminarEmpleado(id);

        return ResponseEntity.ok("empleado con el id " + id + " fue eliminado correctamente");

    }

    @Operation(summary = "Obtener empleado actual", description = "Devuelve los datos del empleado logueado actualmente en base a sus credenciales Basic Auth")
    @GetMapping("/me")
    public ResponseEntity<Empleado> obtenerEmpleadoActual(Authentication authentication){
        String username = authentication.getName();
        // Podemos buscar el empleado en la base de datos usando un método que tendríamos que agregar en el service
        // O buscarlo filtrando la lista completa (menos óptimo pero funciona para pocos datos)
        Empleado empleadoLogueado = empleadoService.listarEmpleadosActivos().stream()
                .filter(e -> e.getUsuario().equals(username))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Empleado logueado no encontrado"));
                
        return ResponseEntity.ok(empleadoLogueado);
    }

}
