package ezecasado.tallerapp.controller;


import ezecasado.tallerapp.models.Mantenimiento;
import ezecasado.tallerapp.service.MantenimientoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/mantenimientos")
public class MantenimientoController {


    private  final MantenimientoService mantenimientoService;

    public MantenimientoController(MantenimientoService mantenimientoService) {

        this.mantenimientoService = mantenimientoService;

    }


    @PostMapping("/crear")
    public ResponseEntity<Mantenimiento> crearMantenimiento(@RequestBody Mantenimiento nuevoMantenimiento){

        Mantenimiento mantenimiento = mantenimientoService.agregarMantenimiento(nuevoMantenimiento);


        return ResponseEntity.status(HttpStatus.CREATED).body(mantenimiento);


    }


    @GetMapping("/vehiculo/{id}")
    public ResponseEntity<List<Mantenimiento>> obtenerMantenimientosporVehiculo(@PathVariable("id") Long id){

        List<Mantenimiento> historial = mantenimientoService.obtenerMantenimientosPorVehiculo(id);

        return ResponseEntity.ok(historial);

    }


    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminarMantenimiento(@PathVariable("id") Long id){

        mantenimientoService.eliminarMantenimiento(id);

        return ResponseEntity.ok("Matenimiento id " + id + " fue eliminado correctamente" );


    }





}
