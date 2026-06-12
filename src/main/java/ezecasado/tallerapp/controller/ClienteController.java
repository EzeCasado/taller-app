package ezecasado.tallerapp.controller;


import ezecasado.tallerapp.models.Cliente;
import ezecasado.tallerapp.repository.ClienteRepository;
import ezecasado.tallerapp.service.ClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.ref.Cleaner;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "Endpoints para la gestión de clientes del taller")
/**
 * Clase: ClienteController.
 * 
 * Esta clase es responsable de proveer las funcionalidades relacionadas con ClienteController
 * dentro del dominio de la aplicación.
 * 
 * @author EzeCasado
 * @version 1.0
 */
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @Operation(summary = "Crear nuevo cliente", description = "Registra un nuevo cliente en el sistema")
    @PostMapping("/crear")
    public ResponseEntity<Cliente> createCliente(@RequestBody Cliente neuvoCliente) {

        Cliente cliente = clienteService.registrarCliente(neuvoCliente);

        return ResponseEntity.status(HttpStatus.CREATED).body(cliente);

    }

    @Operation(summary = "Listar clientes activos", description = "Devuelve una lista de todos los clientes que no han sido dados de baja")
    @GetMapping("/listar")
    public ResponseEntity<List<Cliente>> listarClientesActivos() {

        List<Cliente> list = clienteService.listarClientesActivos();

        return ResponseEntity.ok(list);

    }

    @Operation(summary = "Buscar cliente por ID", description = "Devuelve los datos de un cliente en particular")
    @GetMapping("/cliente/{id}")
    public ResponseEntity<Cliente> obtenerClientePorId(@PathVariable("id") Long id) {

        Cliente cliente = clienteService.buscarCliente(id);

        return ResponseEntity.ok(cliente);

    }


    @Operation(summary = "Eliminar cliente (Borrado lógico)", description = "Marca al cliente como inactivo en el sistema")
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminarCliente(@PathVariable("id") Long id) {

        clienteService.eliminarCliente(id);


        return ResponseEntity.ok("Cliente con el id: " + id + " fue borrado dado de baja correctamente del sistema");

    }

    @Operation(summary = "Actualizar cliente", description = "Modifica los datos personales de un cliente existente")
    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Cliente> actualizarCliente(@PathVariable("id" ) Long id, @RequestBody Cliente neuvoCliente) {

        Cliente clienteExistente = clienteService.buscarCliente(id);

        clienteExistente.setNombre(neuvoCliente.getNombre());
        clienteExistente.setApellido(neuvoCliente.getApellido());
        clienteExistente.setEmail(neuvoCliente.getEmail());
        clienteExistente.setTelefono(neuvoCliente.getTelefono());


        Cliente clienteActualizado = clienteService.actualizarCliente(clienteExistente);

        return ResponseEntity.ok(clienteActualizado);

    }


}
