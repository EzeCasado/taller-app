package ezecasado.tallerapp.controller;


import ezecasado.tallerapp.models.Empleado;
import ezecasado.tallerapp.service.EmpleadoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {

    private final EmpleadoService empleadoService;
    private final PasswordEncoder passwordEncoder;

    public EmpleadoController(EmpleadoService empleadoService, PasswordEncoder passwordEncoder) {
        this.empleadoService = empleadoService;
        this.passwordEncoder = passwordEncoder;
    }


    @PostMapping("/crear")
    public ResponseEntity<Empleado> crearEmpleado(@RequestBody Empleado nuevoEmpleado){

        // Encriptamos la contraseña antes de guardar
        nuevoEmpleado.setContrasenia(passwordEncoder.encode(nuevoEmpleado.getContrasenia()));
        Empleado empleado = empleadoService.crearEmpleado(nuevoEmpleado);

        return ResponseEntity.status(HttpStatus.CREATED).body(empleado);

    }

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

    @GetMapping("/{id}")
    public ResponseEntity<Empleado> buscarEmpleadoPorId(@PathVariable("id") Long id){

        return ResponseEntity.ok(empleadoService.buscarEmpleadoPorId(id));

    }

    @GetMapping
    public ResponseEntity<List<Empleado>> listarEmpleadosActivos(){
        return ResponseEntity.ok(empleadoService.listarEmpleadosActivos());
    }


    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminarEmpleado(@PathVariable("id") Long id){

        empleadoService.eliminarEmpleado(id);

        return ResponseEntity.ok("empleado con el id " + id + " fue eliminado correctamente");

    }

}
