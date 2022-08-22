/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.sistema.bean.backing;

import java.io.Serializable;
import java.util.List;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import sia.servicios.sistema.vo.SiOpcionVo;
import sia.sgl.sistema.bean.model.PrincipalModel;

/**
 *
 * @author ihsa
 */
@Named(value = "principalBean_old")
@RequestScoped
public class PrincipalBean implements Serializable {

    /**
     * Creates a new instance of PrincipalBean
     */
    public PrincipalBean() {
    }

    @ManagedProperty(value = "#{principalModel}")
    PrincipalModel principalModel;

    /**
     * @return the listPagina
     */
    public List<SiOpcionVo> getListPagina() {
	return principalModel.getListPagina();
    }

    /**
     * @param listPagina the listPagina to set
     */
    public void setListPagina(List<SiOpcionVo> listPagina) {
	principalModel.setListPagina(listPagina);
    }

    /**
     * @return the listaPaginaSecundaria
     */
    public List<SiOpcionVo> getListaPaginaSecundaria() {
	return principalModel.getListaPaginaSecundaria();
    }

    /**
     * @param listaPaginaSecundaria the listaPaginaSecundaria to set
     */
    public void setListaPaginaSecundaria(List<SiOpcionVo> listaPaginaSecundaria) {
	principalModel.setListaPaginaSecundaria(listaPaginaSecundaria);
    }

    /**
     * @param principalModel the principalModel to set
     */
    public void setPrincipalModel(PrincipalModel principalModel) {
	this.principalModel = principalModel;
    }
    
    public void opcionesPrincipales(){
        principalModel.opcionesPriciles();
    }
    
}
