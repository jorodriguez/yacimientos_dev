/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mx.ihsa.excepciones;

/**
 * Se lanza cuando se intenta promover un oficio al siguiente estatus y se falla 
 * en el cumplimiento de una o más reglas de negocio.
 * 
 * @author esapien
 */
public class PromotionFailedException extends GeneralException {
    

    public PromotionFailedException(String mensaje) {
        super(mensaje);
    }

    public PromotionFailedException() {
    }
    
    
    
}
