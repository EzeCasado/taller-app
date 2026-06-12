package ezecasado.tallerapp.service;


import ezecasado.tallerapp.exception.ResourceNotFoundException;
import ezecasado.tallerapp.models.Empleado;
import ezecasado.tallerapp.repository.EmpleadoRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
/**
 * Clase: EmpleadoService.
 * 
 * Esta clase es responsable de proveer las funcionalidades relacionadas con EmpleadoService
 * dentro del dominio de la aplicación.
 * 
 * @author EzeCasado
 * @version 1.0
 */
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;



    public EmpleadoService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    public Empleado crearEmpleado(Empleado empleado) {

        if(empleadoRepository.findByUsuario(empleado.getUsuario()).isPresent()){

            throw new DataIntegrityViolationException("El usuario " + empleado.getUsuario() + " ya existe en el sistema");

        }

        return empleadoRepository.save(empleado);

    }


    public List<Empleado> listarEmpleadosActivos(){

        return empleadoRepository.findByActivo(true);

    }

    public Empleado buscarEmpleadoPorId(long id){

        return empleadoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Empleado no encontrado"));

    }

    public Empleado actualizarEmpleado(Empleado empleado){

        Empleado empleadoModificar = buscarEmpleadoPorId(empleado.getId());

        return empleadoRepository.save(empleado);
    }

    public void eliminarEmpleado(long id){

        Empleado empleado = buscarEmpleadoPorId(id);

        empleado.setActivo(false);

        empleadoRepository.save(empleado);

    }




}
