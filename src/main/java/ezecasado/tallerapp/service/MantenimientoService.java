package ezecasado.tallerapp.service;


import ezecasado.tallerapp.models.Mantenimiento;
import ezecasado.tallerapp.models.Vehiculo;
import ezecasado.tallerapp.repository.MantenimientoRepository;
import ezecasado.tallerapp.repository.vehiculoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MantenimientoService {


    private final MantenimientoRepository mantenimientoRepository;

    private final vehiculoRepository vehiculoRepository;

    public MantenimientoService(MantenimientoRepository mantenimientoRepository, vehiculoRepository vehiculoRepository) {
        this.mantenimientoRepository = mantenimientoRepository;
        this.vehiculoRepository = vehiculoRepository;
    }

    public Mantenimiento agregarMantenimiento(Mantenimiento mantenimiento){

        if(vehiculoRepository.findById(mantenimiento.getVehiculo().getId()).isEmpty()){

            throw new IllegalArgumentException("Vehiculo no encontrado en el sistema");


        }

        return mantenimientoRepository.save(mantenimiento);


    }


    public List<Mantenimiento> obtenerMantenimientosPorVehiculo(Long vehiculoId){

        return mantenimientoRepository.findByVehiculoId(vehiculoId);

    }




}
