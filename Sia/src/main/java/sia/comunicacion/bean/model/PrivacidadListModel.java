/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sia.comunicacion.bean.model;

import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import sia.modelo.CoPrivacidad;
import sia.servicios.comunicacion.impl.CoPrivacidadImpl;


/**
 *
 * @author hacosta
 */
@ManagedBean
@RequestScoped
public class PrivacidadListModel {
    @EJB
    private CoPrivacidadImpl servicioPrivacidad;

    /** Creates a new instance of PrivacidadListModel */
    public PrivacidadListModel() {
    }

    public List<CoPrivacidad> getListaPrivacidad(){
        return this.servicioPrivacidad.getListaPrivacidad();
    }

}
