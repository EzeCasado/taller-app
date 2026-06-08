package ezecasado.tallerapp.controller;


import ezecasado.tallerapp.models.Empleado;
import ezecasado.tallerapp.service.EmpleadoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empleados")
public class EmpleadoController {

    private final EmpleadoService empleadoService;

    public EmpleadoController(EmpleadoService empleadoService) {
        this.empleadoService = empleadoService;
    }


    @PostMapping("/crear")
    public ResponseEntity<Empleado> crearEmpleado(@RequestBody Empleado nuevoEmpleado){


        Empleado empleado = empleadoService.crearEmpleado(nuevoEmpleado);

        return ResponseEntity.status(HttpStatus.CREATED).body(empleado);

    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Empleado> actualizarEmpleado(@PathVariable("id") Long id, @RequestBody Empleado nuevoEmpleado){

        Empleado empleadoExistente = empleadoService.buscarEmpleadoPorId(id);

        empleadoExistente.setNombre(nuevoEmpleado.getNombre());
        empleadoExistente.setUsuario(nuevoEmpleado.getUsuario());
        empleadoExistente.setContrasenia(nuevoEmpleado.getContrasenia());

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
