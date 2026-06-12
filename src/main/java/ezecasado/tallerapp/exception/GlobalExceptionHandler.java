package ezecasado.tallerapp.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

@RestControllerAdvice
/**
 * Clase: GlobalExceptionHandler.
 * 
 * Esta clase es responsable de proveer las funcionalidades relacionadas con GlobalExceptionHandler
 * dentro del dominio de la aplicación.
 * 
 * @author EzeCasado
 * @version 1.0
 */
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> manejarRecusoNoEncontrado(ResourceNotFoundException ex, WebRequest webRequest){


        String rutaLimpia = webRequest.getDescription(false).replace("uri=","");

        ErrorResponse error = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                ex.getMessage(),
                rutaLimpia

        );

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);

    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> manejarConflictoDeDatos(DataIntegrityViolationException ex, WebRequest webRequest){

        String rutaLimpia = webRequest.getDescription(false).replace("uri=","");

        ErrorResponse error = new ErrorResponse(
                HttpStatus.CONFLICT.value(),
                "Conflict",
                ex.getMessage(),
                rutaLimpia
        );


        return new ResponseEntity<>(error, HttpStatus.CONFLICT);

    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> manejarErrorDeValidacion(MethodArgumentNotValidException ex, WebRequest webRequest){

        String rutaLimpia = webRequest.getDescription(false).replace("uri=","");

        ErrorResponse error = new ErrorResponse(
                HttpStatus.BAD_REQUEST.value(),
                "Bad Request",
                ex.getMessage(),
                rutaLimpia
        );

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> manejarErroresGenericos(Exception ex, WebRequest webRequest){
        
        // ¡Agregado temporalmente para debugear el error 500 de Swagger!
        ex.printStackTrace();

        String rutaLimpia = webRequest.getDescription(false).replace("uri=","");

        ErrorResponse error = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "Ocurrió un error inesperado en el Servidor. Intente mas tarde",
                rutaLimpia
        );

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);

    }



}
