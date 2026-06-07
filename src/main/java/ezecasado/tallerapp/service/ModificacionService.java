package ezecasado.tallerapp.service;


import ezecasado.tallerapp.models.Modificacion;
import ezecasado.tallerapp.repository.ModificacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ModificacionService {

    private final ModificacionRepository modificacionRepository;

    // 1. Inyección por constructor (Buenas prácticas)
    public ModificacionService(ModificacionRepository modificacionRepository) {
        this.modificacionRepository = modificacionRepository;
    }

    // 2. Método de Creación (básico, después le sumás las validaciones que quieras)
    public Modificacion crearModificacion(Modificacion modificacion) {
        return modificacionRepository.save(modificacion);
    }

    // 3. EL BORRADO LÓGICO (El que faltaba)
    public void eliminarModificacion(Long id) {
        // Buscamos la modificación por ID, si no existe saltamos con excepción limpia
        Modificacion modificacion = modificacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Modificación no encontrada con el ID: " + id));

        // Cambiamos el estado a false (Soft Delete)
        modificacion.setActiva(false);

        // Persistimos el cambio en MySQL
        modificacionRepository.save(modificacion);
    }

    // 4. Listar solo las modificaciones activas
    public List<Modificacion> listarModificacionesActivas() {
        return modificacionRepository.findByActiva(true);
    }


}
