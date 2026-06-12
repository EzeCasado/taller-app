package ezecasado.tallerapp.service;


import ezecasado.tallerapp.exception.ResourceNotFoundException;
import ezecasado.tallerapp.models.Modificacion;
import ezecasado.tallerapp.repository.ModificacionRepository;
import ezecasado.tallerapp.repository.vehiculoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
/**
 * Clase: ModificacionService.
 * 
 * Esta clase es responsable de proveer las funcionalidades relacionadas con ModificacionService
 * dentro del dominio de la aplicación.
 * 
 * @author EzeCasado
 * @version 1.0
 */
public class ModificacionService {

    private final ModificacionRepository modificacionRepository;
    private final vehiculoRepository vehiculoRepository;

    // 1. Inyección por constructor (Buenas prácticas)
    public ModificacionService(ModificacionRepository modificacionRepository, vehiculoRepository vehiculoRepository) {
        this.modificacionRepository = modificacionRepository;
        this.vehiculoRepository = vehiculoRepository;
    }

    // 2. Método de Creación (básico, después le sumás las validaciones que quieras)
    public Modificacion crearModificacion(Modificacion modificacion) {

        if(vehiculoRepository.existsById(modificacion.getVehiculo().getId())) {

            throw new ResourceNotFoundException("Vehiculo con el id: " + modificacion.getVehiculo().getId() + " no existe en el sistema");

        }

        return modificacionRepository.save(modificacion);
    }

    // 3. EL BORRADO LÓGICO (El que faltaba)
    public void eliminarModificacion(Long id) {

        Modificacion modificacion = modificacionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Modificación no encontrada con el ID: " + id));

        // Cambiamos el estado a false (Soft Delete)
        modificacion.setActiva(false);

        // Persistimos el cambio en MySQL
        modificacionRepository.save(modificacion);
    }

    // 4. Listar solo las modificaciones activas
    public List<Modificacion> listarModificacionesActivas() {
        return modificacionRepository.findByActiva(true);
    }

    public List<Modificacion> listarModificacionesPorVehiculo(Long id) {

        return modificacionRepository.findByVehiculoId(id);


    }


}
