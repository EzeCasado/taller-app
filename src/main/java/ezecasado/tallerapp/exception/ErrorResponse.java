package ezecasado.tallerapp.exception;

import java.time.LocalDateTime;

/**
 * Clase: ErrorResponse.
 * 
 * Esta clase es responsable de proveer las funcionalidades relacionadas con ErrorResponse
 * dentro del dominio de la aplicación.
 * 
 * @author EzeCasado
 * @version 1.0
 */
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;


    public ErrorResponse(int status, String error, String message, String path) {

        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;

    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
