
package sia.excepciones;

/**
 * Para indicar que se tiene algún problema o conflicto con los permisos
 * asignados a un usuario.
 * 
 * Ejemplo: Con roles mutuamente excluyentes, como Emisor de Oficios de Entrada
 * y Salida del módulo de Control de Oficios en un mismo usuario.
 * 
 *
 * @author esapien
 */
public class InvalidPermissionsException extends SIAException {

    public InvalidPermissionsException(String mensaje) {
        super(mensaje);
    }

    public InvalidPermissionsException() {
    }
    
    
    
}
