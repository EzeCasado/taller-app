package ezecasado.tallerapp.service;


import ezecasado.tallerapp.models.Empleado;
import ezecasado.tallerapp.repository.EmpleadoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmpleadoService {

    private final EmpleadoRepository empleadoRepository;



    public EmpleadoService(EmpleadoRepository empleadoRepository) {
        this.empleadoRepository = empleadoRepository;
    }

    public Empleado crearEmpleado(Empleado empleado) {

        if(empleadoRepository.findByUsuario(empleado.getUsuario()).isPresent()){

            throw new IllegalArgumentException("El usuario ya existe en el sistema");

        }

        return empleadoRepository.save(empleado);

    }


    public List<Empleado> listarEmpleadosActivos(){

        return empleadoRepository.findByActivo(true);

    }




}
