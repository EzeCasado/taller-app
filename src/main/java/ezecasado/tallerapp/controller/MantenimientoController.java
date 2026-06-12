package ezecasado.tallerapp.controller;


import ezecasado.tallerapp.models.Mantenimiento;
import ezecasado.tallerapp.service.MantenimientoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/mantenimientos")
@Tag(name = "Mantenimientos", description = "Endpoints para la gestión de mantenimientos de vehículos")
/**
 * Clase: MantenimientoController.
 * 
 * Esta clase es responsable de proveer las funcionalidades relacionadas con MantenimientoController
 * dentro del dominio de la aplicación.
 * 
 * @author EzeCasado
 * @version 1.0
 */
public class MantenimientoController {


    private  final MantenimientoService mantenimientoService;

    public MantenimientoController(MantenimientoService mantenimientoService) {

        this.mantenimientoService = mantenimientoService;

    }


    @Operation(summary = "Registrar nuevo mantenimiento", description = "Crea un registro de mantenimiento asociado a un vehículo y a un empleado")
    @PostMapping("/crear")
    public ResponseEntity<Mantenimiento> crearMantenimiento(@RequestBody Mantenimiento nuevoMantenimiento) {

        Mantenimiento mantenimiento = mantenimientoService.agregarMantenimiento(nuevoMantenimiento);


        return ResponseEntity.status(HttpStatus.CREATED).body(mantenimiento);


    }


    @Operation(summary = "Obtener historial de mantenimientos", description = "Devuelve todos los mantenimientos realizados a un vehículo específico")
    @GetMapping("/vehiculo/{vehiculoId}")
    public ResponseEntity<List<Mantenimiento>> obtenerMantenimientosPorVehiculo(@PathVariable("vehiculoId") Long vehiculoId) {

        List<Mantenimiento> historial = mantenimientoService.obtenerMantenimientosPorVehiculo(vehiculoId);

        return ResponseEntity.ok(historial);

    }


    @Operation(summary = "Eliminar mantenimiento", description = "Elimina permanentemente un registro de mantenimiento. Solo accesible por Administradores")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminarMantenimiento(@PathVariable("id") Long id) {

        mantenimientoService.eliminarMantenimiento(id);

        return ResponseEntity.ok("Matenimiento id " + id + " fue eliminado correctamente" );


    }





}
