/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.comunicacion.bean.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.model.SelectItem;
import sia.comunicacion.bean.model.PrivacidadListModel;
import sia.modelo.CoPrivacidad;
import sia.sistema.bean.support.FacesUtils;

/**
 *
 * @author hacosta
 */
@ManagedBean
@RequestScoped
public class PrivacidadBean  implements Serializable{
    //-- Managed Beans ----
    @ManagedProperty (value="#{privasidadListModel}")
    private PrivacidadListModel privasidadListModel;

    /** Creates a new instance of PrivacidadBean */
    public PrivacidadBean() {
    }

    public List getListaPrivacidad() {
        List resultList = new ArrayList();
        try {
            List<CoPrivacidad> tempList = this.privasidadListModel.getListaPrivacidad();
            if (tempList.isEmpty()) {
                //.--
            } else {
                for (CoPrivacidad lista : tempList) {
                    SelectItem item = new SelectItem(lista.getNombre());
                    resultList.add(item);
                }
            }
            return resultList;
        } catch (RuntimeException ex) {
            FacesUtils.addInfoMessage(ex.getMessage());
        }
        return resultList;
    }

    /**
     * @param privasidadListModel the privasidadListModel to set
     */
    public void setPrivacidadListModel(PrivacidadListModel privasidadListModel) {
        this.privasidadListModel = privasidadListModel;
    }
}
