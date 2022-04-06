

package sia.util.ui;

import lombok.Data;

/**
 * Contiene la información de la configuración de un elemento de acción en 
 * la interfaz de usuario, como un botón o commandLink.
 *
 * @author esapien
 */
public @Data class AccionUI {
    
    private String valor;
    private String titulo;
    private String accion;
    private String estiloClase;
    private boolean visible;
    
}
