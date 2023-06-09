/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.ihsa.excepciones;

/**
 *
 * Deberá usarse cuando se intente utilizar un recurso o elemento que por  
 * reglas de negocio ya no se encuentre disponible.
 * 
 * <p/>
 * Ejemplo: En el módulo de Control de Oficios cuando se intente asociar un 
 * oficio a otro que ya se encuentre asociado.
 * 
 * @author esapien
 */
public class UnavailableItemException extends LectorException {

    public UnavailableItemException() {
    }
    
    public UnavailableItemException(String mensaje) {
        super(mensaje);
    }
    
    
}
