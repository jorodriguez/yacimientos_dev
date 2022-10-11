/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package sia.comunicacion.bean.model;

import java.util.List;
import javax.inject.Inject;

import javax.inject.Named;
import sia.modelo.CoPrivacidad;
import sia.servicios.comunicacion.impl.CoPrivacidadImpl;


/**
 *
 * @author hacosta
 */
@Named
@javax.enterprise.context.RequestScoped
public class PrivacidadListModel {
    @Inject
    private CoPrivacidadImpl servicioPrivacidad;

    /** Creates a new instance of PrivacidadListModel */
    public PrivacidadListModel() {
    }

    public List<CoPrivacidad> getListaPrivacidad(){
        return this.servicioPrivacidad.getListaPrivacidad();
    }

}
