package ezecasado.tallerapp.service;


import ezecasado.tallerapp.exception.ResourceNotFoundException;
import ezecasado.tallerapp.models.Mantenimiento;
import ezecasado.tallerapp.models.Vehiculo;
import ezecasado.tallerapp.repository.MantenimientoRepository;
import ezecasado.tallerapp.repository.vehiculoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
/**
 * Clase: MantenimientoService.
 * 
 * Esta clase es responsable de proveer las funcionalidades relacionadas con MantenimientoService
 * dentro del dominio de la aplicación.
 * 
 * @author EzeCasado
 * @version 1.0
 */
public class MantenimientoService {


    private final MantenimientoRepository mantenimientoRepository;

    private final vehiculoRepository vehiculoRepository;

    public MantenimientoService(MantenimientoRepository mantenimientoRepository, vehiculoRepository vehiculoRepository) {
        this.mantenimientoRepository = mantenimientoRepository;
        this.vehiculoRepository = vehiculoRepository;
    }

    public Mantenimiento agregarMantenimiento(Mantenimiento mantenimiento){

        if(vehiculoRepository.findById(mantenimiento.getVehiculo().getId()).isEmpty()){

            throw new ResourceNotFoundException("No se puede registrar el mantenimiento porque el vehiculo con el id: "
            + mantenimiento.getVehiculo().getId() + " no existe en el sistema");


        }

        return mantenimientoRepository.save(mantenimiento);


    }


    public List<Mantenimiento> obtenerMantenimientosPorVehiculo(Long vehiculoId){

        return mantenimientoRepository.findByVehiculoId(vehiculoId);

    }

    public void eliminarMantenimiento(Long id){

        Mantenimiento mantenimiento = mantenimientoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el mantenimiento con el id: " + id));


        mantenimiento.setActivo(false);

        mantenimientoRepository.save(mantenimiento);


    }


}
