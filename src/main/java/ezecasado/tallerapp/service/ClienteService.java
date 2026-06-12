package ezecasado.tallerapp.service;

import ezecasado.tallerapp.exception.ResourceNotFoundException;
import ezecasado.tallerapp.models.Cliente;
import ezecasado.tallerapp.repository.ClienteRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
/**
 * Clase: ClienteService.
 * 
 * Esta clase es responsable de proveer las funcionalidades relacionadas con ClienteService
 * dentro del dominio de la aplicación.
 * 
 * @author EzeCasado
 * @version 1.0
 */
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }


    public Cliente registrarCliente(Cliente cliente) {

        if(clienteRepository.findByEmail(cliente.getEmail()).isPresent()){

            throw new DataIntegrityViolationException("El email " +  cliente.getEmail() + " ya existe en el sistema");

        }

        return clienteRepository.save(cliente);


    }


   public List<Cliente> listarClientesActivos(){

        return clienteRepository.findByActivoTrue();

   }

   public void eliminarCliente(Long idCliente){

        Cliente cliente = clienteRepository.findById(idCliente)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con el id: " + idCliente));


        cliente.setActivo(false);

        clienteRepository.save(cliente);

   }

   public Cliente buscarCliente(Long idCliente){

        return clienteRepository.findById(idCliente)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con el id: " + idCliente));

   }


   public Cliente actualizarCliente(Cliente cliente) {

        Cliente clienteModificar = buscarCliente(cliente.getId());

       return  clienteRepository.save(clienteModificar);

   }

}
