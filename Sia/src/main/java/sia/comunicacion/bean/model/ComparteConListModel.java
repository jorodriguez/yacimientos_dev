/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


package sia.comunicacion.bean.model;

import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import sia.modelo.comunicacion.ComparteCon;
import sia.servicios.comunicacion.impl.ComparteConImpl;

/**
 *
 * @author hacosta
 */
@ManagedBean (name="comparteConListModel")
@ViewScoped
public class ComparteConListModel {

    @EJB
    private ComparteConImpl servicioComparteCon;

    /** Creates a new instance of ElementoListModel */
    public ComparteConListModel() {
    }

    public List<ComparteCon> getElementos(String idUsuario) {
        return this.servicioComparteCon.getComparteCon(idUsuario);
    }
    
     public List<ComparteCon> getGrupos(String idUsuario) {
        return this.servicioComparteCon.getGrupos(idUsuario);
    }
}
