package ezecasado.tallerapp.controller;


import ezecasado.tallerapp.models.Modificacion;
import ezecasado.tallerapp.models.Vehiculo;
import ezecasado.tallerapp.service.ModificacionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;


@RestController
@RequestMapping("/api/modificaciones")
@Tag(name = "Modificaciones", description = "Endpoints para la gestión de modificaciones y accesorios instalados en vehículos")
/**
 * Clase: ModificacionController.
 * 
 * Esta clase es responsable de proveer las funcionalidades relacionadas con ModificacionController
 * dentro del dominio de la aplicación.
 * 
 * @author EzeCasado
 * @version 1.0
 */
public class ModificacionController {

    private final ModificacionService modificacionService;


    public ModificacionController(ModificacionService modificacionService) {
        this.modificacionService = modificacionService;
    }

    @Operation(summary = "Registrar nueva modificación", description = "Crea un registro de una nueva modificación o accesorio instalado en un vehículo")
    @PostMapping("/crear")
    public ResponseEntity<Modificacion> crearModificacion(@RequestBody Modificacion nuevaModificacion) {

        Modificacion modificacion = modificacionService.crearModificacion(nuevaModificacion);


        return ResponseEntity.status(HttpStatus.CREATED).body(modificacion);


    }

    @Operation(summary = "Obtener historial de modificaciones", description = "Devuelve todas las modificaciones instaladas históricamente en un vehículo específico")
    @GetMapping("/vehiculo/{id}")
    public ResponseEntity<List<Modificacion>> obtenerModificacionesPorVehiculo(@PathVariable("id") Long id) {


        List<Modificacion> historial = modificacionService.listarModificacionesPorVehiculo(id);


        return ResponseEntity.ok(historial);


    }

    @Operation(summary = "Eliminar modificación", description = "Elimina permanentemente un registro de modificación. Solo accesible por Administradores")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminarModificacion(@PathVariable("id") Long id) {

        modificacionService.eliminarModificacion(id);

        return ResponseEntity.ok("Modificacion con el id: " + id + " fue eliminada correctamente del sistema");

    }


}
