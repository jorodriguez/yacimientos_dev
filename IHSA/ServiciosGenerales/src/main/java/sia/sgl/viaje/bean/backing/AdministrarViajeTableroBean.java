/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.viaje.bean.backing;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import org.primefaces.PrimeFaces;
import sia.modelo.gr.vo.GrIntercepcionVO;
import sia.modelo.sgl.viaje.vo.ItinerarioTerrestreVO;
import sia.modelo.sgl.viaje.vo.ViajeVO;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.sgl.viaje.bean.model.AdministrarViajeTableroBeanModel;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Named(value = "administrarViajeTableroBean")
@RequestScoped
public class AdministrarViajeTableroBean implements Serializable {


    @Inject
    AdministrarViajeTableroBeanModel administrarViajeTableroBeanModel;

    public AdministrarViajeTableroBean() {
    }

    /**
     * @param administrarViajeTableroBeanModel the administrarViajeTableroBeanModel to set
     */
    public void setAdministrarViajeTableroBeanModel(AdministrarViajeTableroBeanModel administrarViajeTableroBeanModel) {
        this.administrarViajeTableroBeanModel = administrarViajeTableroBeanModel;
    }
         
    public void iniciarConversasionCrearViajeTablero(ActionEvent actionEvent) {
        try {
            this.administrarViajeTableroBeanModel.cargarViajes();
            this.administrarViajeTableroBeanModel.changeTab();
            String metodo = ";initControlChofer();";
            PrimeFaces.current().executeScript(metodo);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }
        
    public String getUsrID(){       
        return this.administrarViajeTableroBeanModel.getUsrID();
    }
    
    /**
     * @return the viajeReferencia
     */
    public ItinerarioTerrestreVO getItinerarioReferencia() {
        return this.administrarViajeTableroBeanModel.getItinerarioReferencia();
    }

    /**
     * @param itinerarioReferencia the itinerarioReferencia to set
     */
    public void setItinerarioReferencia(ItinerarioTerrestreVO itinerarioReferencia) {
        this.administrarViajeTableroBeanModel.setItinerarioReferencia(itinerarioReferencia);
    }      
    
    /**
     * @return the viajesProgramadosMTY
     */
    public List<ItinerarioTerrestreVO> getViajesProgramadosMTY() {
        return this.administrarViajeTableroBeanModel.getViajesProgramadosMTY();
    }

    /**
     * @param viajesProgramadosMTY the viajesProgramadosMTY to set
     */
    public void setViajesProgramadosMTY(List<ItinerarioTerrestreVO> viajesProgramadosMTY) {
        this.administrarViajeTableroBeanModel.setViajesProgramadosMTY(viajesProgramadosMTY);
    }

    /**
     * @return the viajesProgramadosREY
     */
    public List<ItinerarioTerrestreVO> getViajesProgramadosREY() {
        return this.administrarViajeTableroBeanModel.getViajesProgramadosREY();
    }

    /**
     * @param viajesProgramadosREY the viajesProgramadosREY to set
     */
    public void setViajesProgramadosREY(List<ItinerarioTerrestreVO> viajesProgramadosREY) {
        this.administrarViajeTableroBeanModel.setViajesProgramadosREY(viajesProgramadosREY);
    }

    /**
     * @return the viajesProgramadosSF
     */
    public List<ItinerarioTerrestreVO> getViajesProgramadosSF() {
        return this.administrarViajeTableroBeanModel.getViajesProgramadosSF();
    }

    /**
     * @param viajesProgramadosSF the viajesProgramadosSF to set
     */
    public void setViajesProgramadosSF(List<ItinerarioTerrestreVO> viajesProgramadosSF) {
        this.administrarViajeTableroBeanModel.setViajesProgramadosSF(viajesProgramadosSF);
    }

    /**
     * @return the viajesProgramadosInt
     */
    public List<ItinerarioTerrestreVO> getViajesProgramadosInt() {
        return this.administrarViajeTableroBeanModel.getViajesProgramadosInt();
    }

    /**
     * @param viajesProgramadosInt the viajesProgramadosInt to set
     */
    public void setViajesProgramadosInt(List<ItinerarioTerrestreVO> viajesProgramadosInt) {
        this.administrarViajeTableroBeanModel.setViajesProgramadosInt(viajesProgramadosInt);
    }
    
     /**
     * @return the viajesProgramadosMTYCd
     */
    public List<ItinerarioTerrestreVO> getViajesProgramadosMTYCd() {
        return this.administrarViajeTableroBeanModel.getViajesProgramadosMTYCd();
    }

    /**
     * @param viajesProgramadosMTYCd the viajesProgramadosMTYCd to set
     */
    public void setViajesProgramadosMTYCd(List<ItinerarioTerrestreVO> viajesProgramadosMTYCd) {
        this.administrarViajeTableroBeanModel.setViajesProgramadosMTYCd(viajesProgramadosMTYCd);
    }

    /**
     * @return the viajesProgramadosREYCd
     */
    public List<ItinerarioTerrestreVO> getViajesProgramadosREYCd() {
        return this.administrarViajeTableroBeanModel.getViajesProgramadosREYCd();
    }

    /**
     * @param viajesProgramadosREYCd the viajesProgramadosREYCd to set
     */
    public void setViajesProgramadosREYCd(List<ItinerarioTerrestreVO> viajesProgramadosREYCd) {
        this.administrarViajeTableroBeanModel.setViajesProgramadosREYCd(viajesProgramadosREYCd);
    }

    /**
     * @return the viajesProgramadosSFCd
     */
    public List<ItinerarioTerrestreVO> getViajesProgramadosSFCd() {
        return this.administrarViajeTableroBeanModel.getViajesProgramadosSFCd();
    }

    /**
     * @param viajesProgramadosSFCd the viajesProgramadosSFCd to set
     */
    public void setViajesProgramadosSFCd(List<ItinerarioTerrestreVO> viajesProgramadosSFCd) {
        this.administrarViajeTableroBeanModel.setViajesProgramadosSFCd(viajesProgramadosSFCd);
    }
    
    /**
     * @return the fechaInt1
     */
    public Date getFechaInt1() {
        return this.administrarViajeTableroBeanModel.getFechaInt1();
    }

    /**
     * @param fechaInt1 the fechaInt1 to set
     */
    public void setFechaInt1(Date fechaInt1) {
        this.administrarViajeTableroBeanModel.setFechaInt1(fechaInt1);
    }

    /**
     * @return the fechaInt2
     */
    public Date getFechaInt2() {
        return this.administrarViajeTableroBeanModel.getFechaInt2();
    }

    /**
     * @param fechaInt2 the fechaInt2 to set
     */
    public void setFechaInt2(Date fechaInt2) {
        this.administrarViajeTableroBeanModel.setFechaInt2(fechaInt2);
    }
    
    public void goInterceptarViaje(ActionEvent actionEvent) {
	try {	    
	    int idViaje = Integer.parseInt(FacesUtils.getRequestParameter("idViaje"));            
            if(idViaje > 0){
                this.administrarViajeTableroBeanModel.goInterceptarViaje(idViaje);
                String metodo = ";abrirDialogoInterceptarViajeTV();";
                PrimeFaces.current().executeScript(metodo);
            } else {
                FacesUtils.addErrorMessage("No se identifico correctamente el viaje a interceptar. Favor de intentarlo de nuevo.");
            }
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e);
	           FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
	}
    }
    
    public void goInfoViaje(ActionEvent actionEvent) {
	try {	    
	    int idViajeInfo = Integer.parseInt(FacesUtils.getRequestParameter("idViajeInfo"));            
            if(idViajeInfo > 0){
                this.administrarViajeTableroBeanModel.goInfoViaje(idViajeInfo);
                String metodo = ";abrirDialogoInfoViajeTV();";
                PrimeFaces.current().executeScript(metodo);
            } else {
                FacesUtils.addErrorMessage("No se identifico correctamente el viaje a interceptar. Favor de intentarlo de nuevo.");
            }
	} catch (Exception e) {
	    UtilLog4j.log.fatal(this, e);
	           FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
	}
    }
    
    public void interceptarViaje(ActionEvent actionEvent) {
	try {
	    if (this.getIntercepcion() == null) {
		if (getIdViajeIntA() > 0 && getIdViajeIntB() > 0) {
		    this.administrarViajeTableroBeanModel.interceptarViaje();		    
		}
	    } else if (this.getIntercepcion() != null && this.getIdViajeIntA() > 0 && this.getIdViajeIntB() > 0) {
		this.administrarViajeTableroBeanModel.interceptarViaje();
	    }
	    String metodo = ";cerrarDialogoInterceptarViajeTV();";
	    PrimeFaces.current().executeScript(metodo);
	} catch (Exception ex) {
	    UtilLog4j.log.fatal(this, ex);
	}
    }
    
    /**
     * @return the intercepcion
     */
    public GrIntercepcionVO getIntercepcion() {
        return this.administrarViajeTableroBeanModel.getIntercepcion();
    }

    /**
     * @param intercepcion the intercepcion to set
     */
    public void setIntercepcion(GrIntercepcionVO intercepcion) {
        this.administrarViajeTableroBeanModel.setIntercepcion(intercepcion);
    }

    /**
     * @return the idViajeIntA
     */
    public int getIdViajeIntA() {
        return this.administrarViajeTableroBeanModel.getIdViajeIntA();
    }

    /**
     * @param idViajeIntA the idViajeIntA to set
     */
    public void setIdViajeIntA(int idViajeIntA) {
        this.administrarViajeTableroBeanModel.setIdViajeIntA(idViajeIntA);
    }

    /**
     * @return the idViajeIntB
     */
    public int getIdViajeIntB() {
        return this.administrarViajeTableroBeanModel.getIdViajeIntB();
    }

    /**
     * @param idViajeIntB the idViajeIntB to set
     */
    public void setIdViajeIntB(int idViajeIntB) {
        this.administrarViajeTableroBeanModel.setIdViajeIntB(idViajeIntB);
    }

    /**
     * @return the lstViajesInt
     */
    public List<ViajeVO> getLstViajesInt() {
        return this.administrarViajeTableroBeanModel.getLstViajesInt();
    }

    /**
     * @param lstViajesInt the lstViajesInt to set
     */
    public void setLstViajesInt(List<ViajeVO> lstViajesInt) {
        this.administrarViajeTableroBeanModel.setLstViajesInt(lstViajesInt);
    }

    /**
     * @return the lstPuntos
     */
    public List<SelectItem> getLstPuntos() {
        return this.administrarViajeTableroBeanModel.getLstPuntos();
    }

    /**
     * @param lstPuntos the lstPuntos to set
     */
    public void setLstPuntos(List<SelectItem> lstPuntos) {
        this.administrarViajeTableroBeanModel.setLstPuntos(lstPuntos);
    }
    
    /**
     * @return the idPSLlegada
     */
    public int getIdPSLlegada() {
        return this.administrarViajeTableroBeanModel.getIdPSLlegada();
    }

    /**
     * @param idPSLlegada the idPSLlegada to set
     */
    public void setIdPSLlegada(int idPSLlegada) {
        this.administrarViajeTableroBeanModel.setIdPSLlegada(idPSLlegada);
    }
    
     /**
     * @return the infoViaje
     */
    public ViajeVO getInfoViaje() {
        return this.administrarViajeTableroBeanModel.getInfoViaje();
    }

    /**
     * @param infoViaje the infoViaje to set
     */
    public void setInfoViaje(ViajeVO infoViaje) {
        this.administrarViajeTableroBeanModel.setInfoViaje(infoViaje);
    }
    
    /**
     * @return the oficinaID
     */
    public int getOficinaID() {
        return this.administrarViajeTableroBeanModel.getOficinaID();
    }

    /**
     * @param oficinaID the oficinaID to set
     */
    public void setOficinaID(int oficinaID) {
        this.administrarViajeTableroBeanModel.setOficinaID(oficinaID);
    }

    /**
     * @return the lstOficinasOrigen
     */
    public List<SelectItem> getLstOficinasOrigen() {
        return this.administrarViajeTableroBeanModel.getLstOficinasOrigen();
    }

    /**
     * @param lstOficinasOrigen the lstOficinasOrigen to set
     */
    public void setLstOficinasOrigen(List<SelectItem> lstOficinasOrigen) {
        this.administrarViajeTableroBeanModel.setLstOficinasOrigen(lstOficinasOrigen);
    }
    
      /**
     * @return the conChofer
     */
    public boolean isConChofer() {
        return this.administrarViajeTableroBeanModel.isConChofer();
    }

    /**
     * @param conChofer the conChofer to set
     */
    public void setConChofer(boolean conChofer) {
        this.administrarViajeTableroBeanModel.setConChofer(conChofer);
    }
    
        /**
     * @return the viajesEnCursoMTY
     */
    public List<ItinerarioTerrestreVO> getViajesEnCursoMTY() {
        return this.administrarViajeTableroBeanModel.getViajesEnCursoMTY();
    }

    /**
     * @param viajesEnCursoMTY the viajesEnCursoMTY to set
     */
    public void setViajesEnCursoMTY(List<ItinerarioTerrestreVO> viajesEnCursoMTY) {
        this.administrarViajeTableroBeanModel.setViajesEnCursoMTY(viajesEnCursoMTY);
    }

    /**
     * @return the viajesEnCursoREY
     */
    public List<ItinerarioTerrestreVO> getViajesEnCursoREY() {
        return this.administrarViajeTableroBeanModel.getViajesEnCursoREY();
    }

    /**
     * @param viajesEnCursoREY the viajesEnCursoREY to set
     */
    public void setViajesEnCursoREY(List<ItinerarioTerrestreVO> viajesEnCursoREY) {
        this.administrarViajeTableroBeanModel.setViajesEnCursoREY(viajesEnCursoREY);
    }

    /**
     * @return the viajesEnCursoSF
     */
    public List<ItinerarioTerrestreVO> getViajesEnCursoSF() {
        return this.administrarViajeTableroBeanModel.getViajesEnCursoSF();
    }

    /**
     * @param viajesEnCursoSF the viajesEnCursoSF to set
     */
    public void setViajesEnCursoSF(List<ItinerarioTerrestreVO> viajesEnCursoSF) {
        this.administrarViajeTableroBeanModel.setViajesEnCursoSF(viajesEnCursoSF);
    }
    
    /**
     * @return the indexTab
     */
    public int getIndexTab() {
        return this.administrarViajeTableroBeanModel.getIndexTab();
    }

    /**
     * @param indexTab the indexTab to set
     */
    public void setIndexTab(int indexTab) {
        this.administrarViajeTableroBeanModel.setIndexTab(indexTab);
    }

    /**
     * @return the activeTab1
     */
    public String getActiveTab1() {
        return this.administrarViajeTableroBeanModel.getActiveTab1();
    }

    /**
     * @param activeTab1 the activeTab1 to set
     */
    public void setActiveTab1(String activeTab1) {
        this.administrarViajeTableroBeanModel.setActiveTab1(activeTab1);
    }

    /**
     * @return the activeTab2
     */
    public String getActiveTab2() {
        return this.administrarViajeTableroBeanModel.getActiveTab2();
    }

    /**
     * @param activeTab2 the activeTab2 to set
     */
    public void setActiveTab2(String activeTab2) {
        this.administrarViajeTableroBeanModel.setActiveTab2(activeTab2);
    }
    
        /**
     * @return the viajesEnCursoMTYCd
     */
    public List<ItinerarioTerrestreVO> getViajesEnCursoMTYCd() {
        return this.administrarViajeTableroBeanModel.getViajesEnCursoMTYCd();
    }

    /**
     * @param viajesEnCursoMTYCd the viajesEnCursoMTYCd to set
     */
    public void setViajesEnCursoMTYCd(List<ItinerarioTerrestreVO> viajesEnCursoMTYCd) {
        this.administrarViajeTableroBeanModel.setViajesEnCursoMTYCd(viajesEnCursoMTYCd);
    }

    /**
     * @return the viajesEnCursoREYCd
     */
    public List<ItinerarioTerrestreVO> getViajesEnCursoREYCd() {
        return this.administrarViajeTableroBeanModel.getViajesEnCursoREYCd();
    }

    /**
     * @param viajesEnCursoREYCd the viajesEnCursoREYCd to set
     */
    public void setViajesEnCursoREYCd(List<ItinerarioTerrestreVO> viajesEnCursoREYCd) {
        this.administrarViajeTableroBeanModel.setViajesEnCursoREYCd(viajesEnCursoREYCd);
    }

    /**
     * @return the viajesEnCursoSFCd
     */
    public List<ItinerarioTerrestreVO> getViajesEnCursoSFCd() {
        return this.administrarViajeTableroBeanModel.getViajesEnCursoSFCd();
    }

    /**
     * @param viajesEnCursoSFCd the viajesEnCursoSFCd to set
     */
    public void setViajesEnCursoSFCd(List<ItinerarioTerrestreVO> viajesEnCursoSFCd) {
        this.administrarViajeTableroBeanModel.setViajesEnCursoSFCd(viajesEnCursoSFCd);
    }
}

