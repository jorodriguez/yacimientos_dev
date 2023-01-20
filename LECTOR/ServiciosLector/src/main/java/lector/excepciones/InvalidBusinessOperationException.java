/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lector.excepciones;

/**
 *
 * Deberá usarse cuando se intente realizar una operación que no cumpla con
 * alguna regla de negocio.
 * 
 * <p/>
 * Ejemplo: En el módulo de Control de Oficios, cuando se intenta anular un 
 * oficio que se encuentra en una cadena de asociaciones.
 * 
 * @author esapien
 */
public class InvalidBusinessOperationException extends SIAException {

    public InvalidBusinessOperationException() {
    }
    
    public InvalidBusinessOperationException(String mensaje) {
        super(mensaje);
    }
    
}
