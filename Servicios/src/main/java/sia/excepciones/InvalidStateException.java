/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.excepciones;

/**
 * Se lanza cuando se desea indicar que se ha presentado un estado inválido en 
 * algún punto del sistema o proceso en curso. 
 * 
 * <p/>
 * Ejemplo: Cuando se desea hacer un cambio en la base de datos pero el estado
 * en ésta ha cambiado antes de la acción del usuario.
 *
 * @author esapien
 */
public class InvalidStateException extends SIAException {

    public InvalidStateException(String mensaje) {
        super(mensaje);
    }

    public InvalidStateException() {
    }
    
    
    
}
