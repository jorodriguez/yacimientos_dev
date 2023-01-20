
package lector.excepciones;

/**
 * Deberá usarse cuando se intente realizar una operación de negocio, y falte uno o más
 * valores obligatorios para poderla realizar.
 * <p/>
 * Ejemplo: Cuando en un proceso de alta o modificación de un registro falte uno o más 
 * campos requeridos.
 * 
 * @author esapien
 */
public class MissingRequiredValuesException extends SIAException {
    
    /**
     * 
     * El mensaje puede contener la lista de campos o valores faltantes, 
     * ejemplo: "Campo1, campo2, campo3".
     * 
     */
    private String valoresFaltantes;
    
    
    public MissingRequiredValuesException() {
    }

    public MissingRequiredValuesException(String mensaje) {
        super(mensaje);
    }

    public String getValoresFaltantes() {
        return valoresFaltantes;
    }

    public void setValoresFaltantes(String valoresFaltantes) {
        this.valoresFaltantes = valoresFaltantes;
    }
    
    
    
}
