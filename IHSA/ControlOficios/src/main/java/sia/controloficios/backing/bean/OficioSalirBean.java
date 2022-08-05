

package sia.controloficios.backing.bean;

import java.io.IOException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import sia.constantes.Constantes;
import sia.util.UtilLog4j;

/**
 * Bean para la pantalla de Salir (salir.xhtml).
 *
 * @author esapien
 */
@ViewScoped
@ManagedBean
public class OficioSalirBean {
        
    /**
     * Redirecciona a la página principal del sistema Sia.
     * 
     * 
     * @param actionEvent 
     */
    public void siaGo(ActionEvent actionEvent) {
        FacesContext fc = FacesContext.getCurrentInstance();
        try {
            fc.getExternalContext().redirect(Constantes.URL_REL_SIA_PRINCIPAL);
        } catch (IOException ex) {
            UtilLog4j.log.info(this, "Ocurri\u00f3 un error al redireccionar: " + ex.getMessage());
        }
    }
    
}
