package ezecasado.tallerapp.service;


import ezecasado.tallerapp.DTO.GastoVehiculoDTO;
import ezecasado.tallerapp.models.Vehiculo;
import ezecasado.tallerapp.repository.ClienteRepository;
import ezecasado.tallerapp.repository.MantenimientoRepository;
import ezecasado.tallerapp.repository.ModificacionRepository;
import ezecasado.tallerapp.repository.vehiculoRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class VehiculoService {

    private final vehiculoRepository vehiculoRepository;

    private final ClienteRepository clienteRepository;

    private final MantenimientoRepository mantenimientoRepository;

    private final ModificacionRepository modificacionRepository;


    public VehiculoService(vehiculoRepository vehiculoRepository, ClienteRepository clienteRepository, MantenimientoRepository mantenimientoRepository, ModificacionRepository modificacionRepository) {

        this.vehiculoRepository = vehiculoRepository;
        this.clienteRepository = clienteRepository;
        this.mantenimientoRepository = mantenimientoRepository;
        this.modificacionRepository = modificacionRepository;

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

    public GastoVehiculoDTO getGastoVehiculoDTO(Long vehiculoId){

        BigDecimal gastoTotalMantenimiento = mantenimientoRepository.sumarCostoTotalMantenimiento(vehiculoId);
        BigDecimal gastoTotalModificaciones = modificacionRepository.sumarCostoTotalModificacion(vehiculoId);

        if(gastoTotalMantenimiento == null){

            gastoTotalMantenimiento = BigDecimal.ZERO;

        }

        if(gastoTotalModificaciones == null){

            gastoTotalModificaciones = BigDecimal.ZERO;
        }


        BigDecimal gastoTotal = gastoTotalMantenimiento.add(gastoTotalModificaciones);


        return new GastoVehiculoDTO(gastoTotal,gastoTotalMantenimiento,gastoTotalModificaciones);

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
