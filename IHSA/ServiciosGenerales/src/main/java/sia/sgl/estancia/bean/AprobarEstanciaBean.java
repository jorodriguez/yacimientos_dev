/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.estancia.bean;

import java.io.Serializable;
import java.util.List;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;
import org.primefaces.PrimeFaces;
import sia.modelo.sgl.estancia.vo.SgSolicitudEstanciaVo;
import sia.sgl.estancia.bean.model.AprobarEstanciaModel;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Named(value = "aprobarEstanciaBean")
@RequestScoped
public class AprobarEstanciaBean implements Serializable {

    private final static UtilLog4j LOGGER = UtilLog4j.log;
    @ManagedProperty(value = "#{aprobarEstanciaModel}")
    private AprobarEstanciaModel aprobarEstanciaModel;

    public AprobarEstanciaBean() {
    }

    public void aprobarEstancia(ActionEvent event) {
	aprobarEstanciaModel.getSolicitudEstanciaVo().setId(Integer.parseInt(FacesUtils.getRequestParameter("idSolEstancia")));
	aprobarEstanciaModel.aprobarEstancia();
    }

    public void cancelarEstancia(ActionEvent event) {
	aprobarEstanciaModel.getSolicitudEstanciaVo().setId(Integer.parseInt(FacesUtils.getRequestParameter("idSolEstancia")));
	aprobarEstanciaModel.solicitudEstanciaPorId();
	aprobarEstanciaModel.cancelarEstancia();
    }

    public void verDetalle(ActionEvent event) {
	aprobarEstanciaModel.getSolicitudEstanciaVo().setId(Integer.parseInt(FacesUtils.getRequestParameter("idSolEstancia")));
	aprobarEstanciaModel.solicitudEstanciaPorId();
	PrimeFaces.current().executeScript(";$(dialogoSolEstancia).modal('show');;");
    }

    /**
     * @return the solicitudEstanciaVo
     */
    public SgSolicitudEstanciaVo getSolicitudEstanciaVo() {
	return aprobarEstanciaModel.getSolicitudEstanciaVo();
    }

    /**
     * @param solicitudEstanciaVo the solicitudEstanciaVo to set
     */
    public void setSolicitudEstanciaVo(SgSolicitudEstanciaVo solicitudEstanciaVo) {
	aprobarEstanciaModel.setSolicitudEstanciaVo(solicitudEstanciaVo);
    }

    /**
     * @return the listaSolicitud
     */
    public List<SgSolicitudEstanciaVo> getListaSolicitud() {
	return aprobarEstanciaModel.getListaSolicitud();
    }

    /**
     * @param listaSolicitud the listaSolicitud to set
     */
    public void setListaSolicitud(List<SgSolicitudEstanciaVo> listaSolicitud) {
	aprobarEstanciaModel.setListaSolicitud(listaSolicitud);
    }

    /**
     * @param aprobarEstanciaModel the aprobarEstanciaModel to set
     */
    public void setAprobarEstanciaModel(AprobarEstanciaModel aprobarEstanciaModel) {
	this.aprobarEstanciaModel = aprobarEstanciaModel;
    }
}
