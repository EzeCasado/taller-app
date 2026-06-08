package ezecasado.tallerapp.controller;


import ezecasado.tallerapp.models.Modificacion;
import ezecasado.tallerapp.models.Vehiculo;
import ezecasado.tallerapp.service.ModificacionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/modificaciones")
public class ModificacionController {

    private final ModificacionService modificacionService;


    public ModificacionController(ModificacionService modificacionService) {
        this.modificacionService = modificacionService;
    }

    @PostMapping("/crear")
    public ResponseEntity<Modificacion> crearModificacion(@RequestBody Modificacion nuevaModificacion) {

        Modificacion modificacion = modificacionService.crearModificacion(nuevaModificacion);


        return ResponseEntity.status(HttpStatus.CREATED).body(modificacion);


    }

    @GetMapping("/vehiculo/{id}")
    public ResponseEntity<List<Modificacion>> obtenerModificacionesPorVehiculo(@PathVariable("id") Long id) {


        List<Modificacion> historial = modificacionService.listarModificacionesPorVehiculo(id);


        return ResponseEntity.ok(historial);


    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminarModificacion(@PathVariable("id") Long id) {

        modificacionService.eliminarModificacion(id);

        return ResponseEntity.ok("Modificacion con el id: " + id + " fue eliminada correctamente del sistema");

    }


}
