package ezecasado.tallerapp.controller;


import ezecasado.tallerapp.models.Cliente;
import ezecasado.tallerapp.repository.ClienteRepository;
import ezecasado.tallerapp.service.ClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.lang.ref.Cleaner;
import java.util.List;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping("/crear")
    public ResponseEntity<Cliente> createCliente(@RequestBody Cliente neuvoCliente) {

        Cliente cliente = clienteService.registrarCliente(neuvoCliente);

        return ResponseEntity.status(HttpStatus.CREATED).body(cliente);

    }

    @GetMapping("/listar")
    public ResponseEntity<List<Cliente>> listarClientesActivos() {

        List<Cliente> list = clienteService.listarClientesActivos();

        return ResponseEntity.ok(list);

    }

    @GetMapping("/cliente/{id}")
    public ResponseEntity<Cliente> obtenerClientePorId(@PathVariable("id") Long id) {

        Cliente cliente = clienteService.buscarCliente(id);

        return ResponseEntity.ok(cliente);

    }


    @GetMapping("/eliminar/{id}")
    public ResponseEntity<String> eliminarCliente(@PathVariable("id") Long id) {

        clienteService.eliminarCliente(id);


        return ResponseEntity.ok("Cliente con el id: " + id + " fue borrado dado de baja correctamente del sistema");

    }

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
