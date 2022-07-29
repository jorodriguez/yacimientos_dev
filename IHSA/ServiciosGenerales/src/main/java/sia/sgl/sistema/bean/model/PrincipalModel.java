/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.sistema.bean.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.view.ViewScoped;

import sia.constantes.Constantes;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.servicios.sistema.impl.SiRelRolOpcionImpl;
import sia.servicios.sistema.vo.SiOpcionVo;
import sia.sgl.sistema.bean.backing.Sesion;

/**
 *
 * @author ihsa
 */
@Named(value = "principalBean")
@ViewScoped
public class PrincipalModel implements Serializable {

    /**
     * Creates a new instance of PrincipalModel
     */
    public PrincipalModel() {
    }

    @Inject
    private SiRelRolOpcionImpl siRelRolOpcionImpl;
    //
    @Inject
    Sesion sesion;

    private List<SiOpcionVo> listPagina = null;
    private List<SiOpcionVo> listaPaginaSecundaria = null;

    @PostConstruct
    public void opcionesPriciles() {
	if (sesion.getUsuario() != null) {
	    StringBuilder r = new StringBuilder();
	    for (UsuarioRolVo role : sesion.getRoles()) {
		if (r.length() == 0) {
		    r.append(role.getIdRol());
		} else {
		    r.append(",").append(role.getIdRol());
		}

	    }
	    List<SiOpcionVo> lo = siRelRolOpcionImpl.traerOpcionePorRol(Constantes.MODULO_SGYL, r.toString(), sesion.getUsuario().getId());
	    int i = 0;
	    listPagina = new ArrayList<>();
	    listaPaginaSecundaria = new ArrayList<>();
	    for (SiOpcionVo listPagina1 : lo) {
		i++;
		if (i <= 8) {
		    listPagina.add(listPagina1);
		} else {
		    listaPaginaSecundaria.add(listPagina1);
		}
		if (i == 16) {
		    break;
		}
	    }
	}
    }

    /**
     * @return the listPagina
     */
    public List<SiOpcionVo> getListPagina() {
	return listPagina;
    }

    /**
     * @param listPagina the listPagina to set
     */
    public void setListPagina(List<SiOpcionVo> listPagina) {
	this.listPagina = listPagina;
    }

    /**
     * @return the listaPaginaSecundaria
     */
    public List<SiOpcionVo> getListaPaginaSecundaria() {
	return listaPaginaSecundaria;
    }

    /**
     * @param listaPaginaSecundaria the listaPaginaSecundaria to set
     */
    public void setListaPaginaSecundaria(List<SiOpcionVo> listaPaginaSecundaria) {
	this.listaPaginaSecundaria = listaPaginaSecundaria;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
	this.sesion = sesion;
    }
}
