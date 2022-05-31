/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.contrato.bean.backing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import sia.contrato.bean.model.AgregarContratoModel;
import sia.contrato.bean.soporte.FacesUtils;
import sia.modelo.Convenio;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.contrato.vo.ClasificacionVo;
import sia.modelo.contrato.vo.ContratoVO;
import sia.modelo.sgl.vo.Vo;
import sia.modelo.vo.StatusVO;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
public class AgregarContratoBean {

    /**
     * Creates a new instance of AgregarContratoBean
     */
    public AgregarContratoBean() {
    }

    @ManagedProperty(value = "#{agregarContratoModel}")
    private AgregarContratoModel agregarContratoModel;

    public void llenarJson() {
	//String jsonProveedores = agregarContratoModel.traerJson();
	//PrimeFaces.current().executeScript(";setJson(" + jsonProveedores + ");");
    }

    public void buscarContratoMaestroPorProveedor() {
	agregarContratoModel.traerContratoMaestroPorProveedor();
    }

    public List<SelectItem> getListaContratos() {
	List<SelectItem> contratos = new ArrayList<SelectItem>();
	try {
	    for (ContratoVO contrato : agregarContratoModel.getListaContratos()) {
		SelectItem item = new SelectItem(contrato.getId(), contrato.getNumero() + ", " + contrato.getNombre());
		contratos.add(item);
	    }
	} catch (Exception e) {
	    e.getMessage();
	}
	return contratos;
    }

    public List getTraerGerencia() {
	return agregarContratoModel.getMapaSelectItem().get("gerencias");
    }

    public List getTraerCampo() {
	List<SelectItem> listaCampo = new ArrayList<SelectItem>();
	try {
	    for (Object obj : agregarContratoModel.getMapaSelectItem().get("campos")) {
		CampoUsuarioPuestoVo cg = (CampoUsuarioPuestoVo) obj;
		SelectItem item = new SelectItem(cg.getIdCampo(), cg.getCampo());
		listaCampo.add(item);
	    }
	    return listaCampo;
	} catch (Exception e) {
	    e.getMessage();
	}
	return null;
    }

    public void traerGerenciaPorCampo(ValueChangeEvent event) {
	int idCampo = (Integer) event.getNewValue();
	agregarContratoModel.llenarGerencia(idCampo);
    }

    public String guardarContrato() {
	boolean v = false;
	try {
	    Convenio nContrato = this.agregarContratoModel.buscarContratoPorNumero();
	    if (nContrato == null) {
		if (agregarContratoModel.buscarProveedorPorId()) {
		    v = this.agregarContratoModel.guardarContrato();
		    if (v) {
			FacesUtils.addInfoMessage("Se generó el contrato");
			ContratoBean contratoBean = (ContratoBean) FacesUtils.getManagedBean(FacesContext.getCurrentInstance(), "contratoBean");
			return contratoBean.regrearPrincipal();
		    } else {
			FacesUtils.addErrorMessage("Ocurrió un error . . .");
			return "";
		    }
		} else {
		    FacesUtils.addErrorMessage("El proveedor no está en el sistema");
		    return "";
		}
	    } else {
		FacesUtils.addErrorMessage("El número ya se encuentra en la base de datos");
		return "";
	    }
	} catch (Exception e) {
	    UtilLog4j.log.fatal(e);
	    FacesUtils.addErrorMessage("No hay permisos para esta operación . . .");
	    return null;
	}
    }

    public List getTraerTipo() {
	List<SelectItem> listCvTipo = new ArrayList<SelectItem>();
	try {
	    for (Object obj : this.agregarContratoModel.getMapaSelectItem().get("tipos")) {
		Vo vo = (Vo) obj;
		SelectItem item = new SelectItem(vo.getId(), vo.getNombre());
		listCvTipo.add(item);
	    }
	    return listCvTipo;
	} catch (Exception e) {
	    e.getMessage();
	}
	return null;
    }

    public List getTraerEstatus() {
	List<SelectItem> listEstatus = new ArrayList<SelectItem>();
	try {
	    for (Object obj : agregarContratoModel.getMapaSelectItem().get("estados")) {
		StatusVO est = (StatusVO) obj;
		SelectItem item = new SelectItem(est.getIdStatus(), est.getNombre());
		listEstatus.add(item);
	    }
	    return listEstatus;
	} catch (Exception e) {
	    e.getMessage();
	}
	return null;
    }

    public List getTraerClasificacion() {
	List<SelectItem> listaClasi = new ArrayList<SelectItem>();
	try {
	    for (Object obj : agregarContratoModel.getMapaSelectItem().get("clasificaciones")) {
		ClasificacionVo est = (ClasificacionVo) obj;
		SelectItem item = new SelectItem(est.getId(), est.getNombre());
		listaClasi.add(item);
	    }
	    return listaClasi;
	} catch (Exception e) {
	    e.getMessage();
	}
	return null;
    }

    public void cabiarClasificacion(ValueChangeEvent event) {
	agregarContratoModel.setId((Integer) event.getNewValue());
    }

    public void cabiarClasificacionDos(ValueChangeEvent event) {
	agregarContratoModel.setIdS((Integer) event.getNewValue());
    }

    public void cabiarClasificacionTres(ValueChangeEvent event) {
	agregarContratoModel.setIdT((Integer) event.getNewValue());
    }

    public void cabiarClasificacionCuatro(ValueChangeEvent event) {
	agregarContratoModel.setIdC((Integer) event.getNewValue());
    }

    public List getTraerClasificacion2() {
	if (agregarContratoModel.getId() > 0) {
	    return agregarContratoModel.llenarSub(agregarContratoModel.getId());
	}
	return null;
    }

    public List getTraerClasificacion3() {
	if (agregarContratoModel.getIdS() > 0) {
	    return agregarContratoModel.llenarSub(agregarContratoModel.getIdS());
	}
	return null;
    }

    public List getTraerClasificacion4() {
	if (agregarContratoModel.getIdT() > 0) {
	    return agregarContratoModel.llenarSub(agregarContratoModel.getIdT());
	}
	return null;
    }

    public List getTraerClasificacion5() {
	if (agregarContratoModel.getIdC() > 0) {
	    return agregarContratoModel.llenarSub(agregarContratoModel.getIdC());
	}
	return null;
    }

    /**
     * @return the contratoVO
     */
    public ContratoVO getContratoVO() {
	return agregarContratoModel.getContratoVO();
    }

    /**
     * @param contratoVO the contratoVO to set
     */
    public void setContratoVO(ContratoVO contratoVO) {
	agregarContratoModel.setContratoVO(contratoVO);
    }

    /**
     * @param agregarContratoModel the agregarContratoModel to set
     */
    public void setAgregarContratoModel(AgregarContratoModel agregarContratoModel) {
	this.agregarContratoModel = agregarContratoModel;
    }

    /**
     * @return
     */
    public Map<String, List> getMapaSelectItem() {
	return agregarContratoModel.getMapaSelectItem();
    }

    /**
     * @return the indice
     */
    public int getIndice() {
	return agregarContratoModel.getIndice();
    }

    /**
     * @param indice the indice to set
     */
    public void setIndice(int indice) {
	agregarContratoModel.setIndice(indice);
    }

    /**
     * @return the id
     */
    public int getId() {
	return agregarContratoModel.getId();
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
	agregarContratoModel.setId(id);
    }

    /**
     * @return the idS
     */
    public int getIdS() {
	return agregarContratoModel.getIdS();
    }

    /**
     * @param idS the idS to set
     */
    public void setIdS(int idS) {
	agregarContratoModel.setIdS(idS);
    }

    /**
     * @return the idT
     */
    public int getIdT() {
	return agregarContratoModel.getIdT();
    }

    /**
     * @param idT the idT to set
     */
    public void setIdT(int idT) {
	agregarContratoModel.setIdT(idT);
    }

    /**
     * @return the idC
     */
    public int getIdC() {
	return agregarContratoModel.getIdC();
    }

    /**
     * @param idC the idC to set
     */
    public void setIdC(int idC) {
	agregarContratoModel.setIdC(idC);
    }

    /**
     * @return the idCi
     */
    public int getIdCi() {
	return agregarContratoModel.getIdCi();
    }

    /**
     * @param idCi the idCi to set
     */
    public void setIdCi(int idCi) {
	agregarContratoModel.setIdCi(idCi);
    }
}
