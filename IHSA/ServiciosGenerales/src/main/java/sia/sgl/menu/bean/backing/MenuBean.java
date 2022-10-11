/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.menu.bean.backing;

import java.io.Serializable;
import javax.inject.Named;
import javax.faces.bean.SessionScoped;
import javax.inject.Inject;
import sia.sgl.sistema.bean.backing.Sesion;

/**
 *
 * @author b75ckd35th
 */
@Named(value = "menuBean")
@SessionScoped
public class MenuBean implements Serializable {

    public MenuBean() {
    }

    @Inject
    private Sesion sesion;
    //
    private String opcionViaje;

    /**
     * Metodo que devuelve lista de opciones (menus)
     *
     * @author Nestor Lopez 10/10/2013 Modifico: Nestor Lopez 10/10/2013
     * @return
     */
    /**
     * @return the opcionViaje
     */
    public String getOpcionViaje() {
	return opcionViaje;
    }

    /**
     * @param opcionViaje the opcionViaje to set
     */
    public void setOpcionViaje(String opcionViaje) {
	this.opcionViaje = opcionViaje;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
	this.sesion = sesion;
    }
}
