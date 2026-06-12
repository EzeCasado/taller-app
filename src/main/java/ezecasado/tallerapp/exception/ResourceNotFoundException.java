package ezecasado.tallerapp.exception;

/**
 * Clase: ResourceNotFoundException.
 * 
 * Esta clase es responsable de proveer las funcionalidades relacionadas con ResourceNotFoundException
 * dentro del dominio de la aplicación.
 * 
 * @author EzeCasado
 * @version 1.0
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {

        super(message);

    }

}
