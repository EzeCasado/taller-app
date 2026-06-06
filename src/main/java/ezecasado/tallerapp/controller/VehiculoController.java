package ezecasado.tallerapp.controller;


import ezecasado.tallerapp.DTO.GastoVehiculoDTO;
import ezecasado.tallerapp.models.Vehiculo;
import ezecasado.tallerapp.service.VehiculoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {


    private final VehiculoService vehiculoService;

    public VehiculoController(VehiculoService vehiculoService) {

        this.vehiculoService = vehiculoService;
    }


    @GetMapping("/{id}/gastos")
    public ResponseEntity<GastoVehiculoDTO> obtenerGastoVehiculo(@PathVariable("id") Long vehiculoId) {


        GastoVehiculoDTO gasto = vehiculoService.getGastoVehiculoDTO(vehiculoId);


        return ResponseEntity.ok(gasto);

    }


    @PostMapping("/crear")
    public ResponseEntity<Vehiculo> crearVehiculo(@RequestBody Vehiculo nuevovehiculo){

            Vehiculo vehiculoGuardado = vehiculoService.crearVehiculo(nuevovehiculo);


        return ResponseEntity.status(HttpStatus.CREATED).body(vehiculoGuardado);

    }

    @GetMapping("/listar")
    public ResponseEntity<List<Vehiculo>> obtenerVehiculosActivos(){

        List<Vehiculo> lista = vehiculoService.listarVehiculosActivos();

        return ResponseEntity.ok(lista);

    }


    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminarVehiculo(@PathVariable("id") Long id){

        vehiculoService.eliminarVehiculo(id);


        return ResponseEntity.ok("El vehiculo con ID " + id +" fue dado de baja correctamente");

    }




}
