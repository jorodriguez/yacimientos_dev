/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.viaje.paqueteria.bean.backing;

import java.awt.event.ActionEvent;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import sia.modelo.SgSolicitudViaje;
import sia.modelo.SgViajero;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.sgl.viaje.paqueteria.model.PaqueteriaBeanModel;

/**
 *
 * @author jrodriguez
 */
@Named(value = "paqueteriaBean")
@RequestScoped
public class PaqueteriaBean {

    @Inject
    private Sesion sesion;
    @Inject
    private PaqueteriaBeanModel paqueteriaBeanModel;
    public PaqueteriaBean() {
    }
    
    public String goToSolicitudPaqueteria() {
        this.paqueteriaBeanModel.beginConversationSolicitudPaqueteria();       
        
        sesion.getControladorPopups().put("popupAddPaquete", Boolean.FALSE);
        return "/vistas/sgl/viaje/solicitudPaqueteria";
    }
    
    
    
    /****
     * control de Poopups     
     */
    public void openPopupAddPaquetes(ActionEvent actionEvent) {
        sesion.getControladorPopups().put("popupAddPaquete", Boolean.TRUE);
    }
    public void closePopupAddPaquetes(ActionEvent actionEvent) {
        sesion.getControladorPopups().put("popupAddPaquete", Boolean.FALSE);
    }

   public void openPopupDetailPaquetes(ActionEvent actionEvent) {
        sesion.getControladorPopups().put("popupDetailPaquete", Boolean.TRUE);
    }
    public void closePopupDetailPaquetes(ActionEvent actionEvent) {
        sesion.getControladorPopups().put("popupDetailPaquete", Boolean.FALSE);
    }

    public void openPopupAddRequest(ActionEvent actionEvent) {
        sesion.getControladorPopups().put("popupAddRequest", Boolean.TRUE);
    }
    public void closePopupAddRequest(ActionEvent actionEvent) {
        sesion.getControladorPopups().put("popupAddRequest", Boolean.FALSE);
    }

    
}
