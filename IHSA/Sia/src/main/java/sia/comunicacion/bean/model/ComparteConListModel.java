/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


package sia.comunicacion.bean.model;

import java.io.Serializable;
import java.util.List;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;


import sia.modelo.comunicacion.ComparteCon;
import sia.servicios.comunicacion.impl.ComparteConImpl;

/**
 *
 * @author hacosta
 */
@Named
@ViewScoped
public class ComparteConListModel implements Serializable{

    @Inject
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
