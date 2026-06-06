package ezecasado.tallerapp.service;


import ezecasado.tallerapp.models.Vehiculo;
import ezecasado.tallerapp.repository.ClienteRepository;
import ezecasado.tallerapp.repository.vehiculoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VehiculoService {

    private final vehiculoRepository vehiculoRepository;

    private final ClienteRepository clienteRepository;


    public VehiculoService(vehiculoRepository vehiculoRepository, ClienteRepository clienteRepository) {

        this.vehiculoRepository = vehiculoRepository;
        this.clienteRepository = clienteRepository;

    }


    public Vehiculo crearVehiculo(Vehiculo vehiculo){

        if(clienteRepository.findById(vehiculo.getCliente().getId()).isEmpty()){


            throw new IllegalArgumentException("Cliente no encontrado en el sistema");

        }

        if (vehiculoRepository.findByPatente(vehiculo.getPatente()).isPresent()){

            throw new IllegalArgumentException("Patente ya existe en el sistema");

        }

        return vehiculoRepository.save(vehiculo);


    }


    public void eliminarVehiculo(Long vehiculoId){

        Vehiculo vehiculo = vehiculoRepository.findById(vehiculoId)
                .orElseThrow(() -> new RuntimeException("Vehiculo no encontrado con el id" + vehiculoId));

        vehiculo.setActivo(false);


        vehiculoRepository.save(vehiculo);


    }


    public List<Vehiculo> listarVehiculosActivos(){
        return vehiculoRepository.findByActivo(true);
    }


}
