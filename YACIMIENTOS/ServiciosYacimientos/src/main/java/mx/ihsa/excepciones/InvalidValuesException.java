
package mx.ihsa.excepciones;

/**
 * 
 * Para indicar que se intenta hacer una operación en donde uno o más valores
 * son inválidos para la operación a realizar.
 * 
 * @author esapien
 */
public class InvalidValuesException extends GeneralException {
    
    public InvalidValuesException() {
    }
    
    public InvalidValuesException(String mensaje) {
        super(mensaje);
    }
}
