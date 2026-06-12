package ezecasado.tallerapp.controller;


import ezecasado.tallerapp.DTO.GastoVehiculoDTO;
import ezecasado.tallerapp.models.Vehiculo;
import ezecasado.tallerapp.service.VehiculoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/vehiculos")
@Tag(name = "Vehículos", description = "Endpoints para la gestión de vehículos del taller")
/**
 * Clase: VehiculoController.
 * 
 * Esta clase es responsable de proveer las funcionalidades relacionadas con VehiculoController
 * dentro del dominio de la aplicación.
 * 
 * @author EzeCasado
 * @version 1.0
 */
public class VehiculoController {


    private final VehiculoService vehiculoService;

    public VehiculoController(VehiculoService vehiculoService) {

        this.vehiculoService = vehiculoService;
    }


    @Operation(summary = "Obtener gastos del vehículo", description = "Calcula la suma total de dinero gastado en mantenimientos y modificaciones para un vehículo específico")
    @GetMapping("/{id}/gastos")
    public ResponseEntity<GastoVehiculoDTO> obtenerGastoVehiculo(@PathVariable("id") Long vehiculoId) {


        GastoVehiculoDTO gasto = vehiculoService.getGastoVehiculoDTO(vehiculoId);


        return ResponseEntity.ok(gasto);

    }


    @Operation(summary = "Crear nuevo vehículo", description = "Registra un nuevo vehículo asociándolo a un cliente existente")
    @PostMapping("/crear")
    public ResponseEntity<Vehiculo> crearVehiculo(@RequestBody Vehiculo nuevovehiculo){

            Vehiculo vehiculoGuardado = vehiculoService.crearVehiculo(nuevovehiculo);


        return ResponseEntity.status(HttpStatus.CREATED).body(vehiculoGuardado);

    }

    @Operation(summary = "Listar vehículos activos", description = "Devuelve una lista de todos los vehículos que no han sido dados de baja")
    @GetMapping("/listar")
    public ResponseEntity<List<Vehiculo>> obtenerVehiculosActivos(){

        List<Vehiculo> lista = vehiculoService.listarVehiculosActivos();

        return ResponseEntity.ok(lista);

    }


    @Operation(summary = "Eliminar vehículo (Borrado lógico)", description = "Marca un vehículo como inactivo sin borrar su historial de gastos. Solo accesible por Administradores")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminarVehiculo(@PathVariable("id") Long id){

        vehiculoService.eliminarVehiculo(id);


        return ResponseEntity.ok("El vehiculo con ID " + id +" fue dado de baja correctamente");

    }




}
