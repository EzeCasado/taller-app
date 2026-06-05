package ezecasado.tallerapp.service;

import ezecasado.tallerapp.models.Cliente;
import ezecasado.tallerapp.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.ExecutionException;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }


    public Cliente registrarCliente(Cliente cliente) {

        if(clienteRepository.findByEmail(cliente.getEmail()).isPresent()){

            throw new IllegalArgumentException("El email ya existe en el sistema");

        }

        return clienteRepository.save(cliente);


    }


   public List<Cliente> listarClientesActivos(){

        return clienteRepository.findByActivoTrue();

   }



}
