/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.excepciones;

/**
 * Para indicar que en función de los permisos del usuario no se cuenta con 
 * el acceso suficiente para realizar alguna operación de negocio.
 * 
 * 
 * @author esapien
 */
public class InsufficientPermissionsException extends SIAException {

    public InsufficientPermissionsException(String mensaje) {
        super(mensaje);
    }

    public InsufficientPermissionsException() {
    }
    
    
    
}
