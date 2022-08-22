/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.viaje.cadenas.bean.backing;

import java.io.Serializable;
import java.util.List;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import sia.modelo.*;
import sia.modelo.sgl.viaje.vo.CadenaAprobacionSolicitudVO;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.sgl.viaje.cadenas.bean.model.CadenaBeanModel;
import sia.util.UtilLog4j;

/**
 *
 * @author jrodriguez
 */
@Named(value = "cadenasBean")
@RequestScoped
public class CadenaBean implements Serializable {

    @Inject
    Sesion sesion;
    @Inject
    private CadenaBeanModel cadenasBeanModel;

    public CadenaBean() {
    }

    public String goToAdministrarCadenas() {
        if (sesion.getOficinaActual() == null) {
            return "/principal";
        } else {
            cadenasBeanModel.beginConversationCadenasAprobacion();
            cadenasBeanModel.traerListaGerenciasItems();
//            cadenasBeanModel.traerListaTipoEspecificoItems();
            cadenasBeanModel.traerListaSolicitudesItems();
//            cadenasBeanModel.setIdTipoEspecifico(-1);
            return "/vistas/sgl/cadenas/administrarCadenasAprobacion";
        }
    }

    public String guardarListaFragmentosCadena() {
        UtilLog4j.log.info(this, "guardarListaFragmentosCadena");
        String ret = "";
        try {
            if (!cadenasBeanModel.validarListaCadena()) {
                cadenasBeanModel.guardarListaFragmentosCadenaAprobacion();
                cadenasBeanModel.traerCadenasAprobacionPorTipoSolicitud();                
                ret = "administrarCadenasAprobacion";
            }else{                
                ret = "asignarCadena";
                FacesUtils.addInfoMessage("Por favor asigne todas las cadenas de aprobaciones...");
            }
        } catch (Exception e) {
            FacesUtils.addInfoMessage("Existio un error al intentar guardar la lista de cadenas...");
            UtilLog4j.log.info(this, "Excepcion al guardar fragmento de cadena de aprobacion " + e.getMessage());
            ret = "asignarCadena";
        }
        return ret;
    }
    
//    public String cancelarAsignacion(){
//        String ret ="";
//        try {
//            if (!cadenasBeanModel.validarExistenciaDatosListaCadena()) {                
//                ret = "administrarCadenasAprobacion";
//            }else{                
//                ret = "asignarCadena";
//                FacesUtils.addInfoMessage("Por favor asigne todas las cadenas de aprobaciones...");
//            }
//        } catch (Exception e) {
//            FacesUtils.addInfoMessage("Existio un error al intentar guardar la lista de cadenas...");
//            UtilLog4j.log.info(this, "Excepcion al guardar fragmento de cadena de aprobacion " + e.getMessage());
//            ret = "asignarCadena";
//        }
//        return ret;
//    }

    public void addCadenaLista(ActionEvent event) {
        UtilLog4j.log.info(this, "addCAdenaLista");
        if (cadenasBeanModel.getIdGerencia() != -1) {
            cadenasBeanModel.addGerenciaFragmentoCadenaLista();
            cadenasBeanModel.cadenaAsignarDatamodelTMP();
            cadenasBeanModel.setMrPopup(false);
        } else {
            FacesUtils.addErrorMessage("Por favor seleccione una gerencia de la lista..");
        }
    }

    public void modificarAsignacionCadena(ActionEvent event) {
        try {
            if (cadenasBeanModel.getIdGerencia() != -1) {
                cadenasBeanModel.modificarFragmentoCadenaAprobacion();
            } else {
                FacesUtils.addErrorMessage("Por favor seleccione una gerencia de la lista..");
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepciomn " + e.getMessage());
        }
    }
    
    public void modificarCadenaNegacion(ActionEvent event) {
        try {
            if (cadenasBeanModel.getIdEstatusSeleccionado() != -1) {
                cadenasBeanModel.modificarCadenaNegacion();
            } else {
                FacesUtils.addErrorMessage("Por favor seleccione un estatus de la lista..");
            }
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepciomn " + e.getMessage());
        }
    }

    public void seleccionarTipoSolicitud(ValueChangeEvent valueChangeEvent) {
        try {
            cadenasBeanModel.setIdTipoSolicitud((Integer) valueChangeEvent.getNewValue());
            cadenasBeanModel.traerCadenasAprobacionPorTipoSolicitud();
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepcion en seleccionar el tipo de solicitud");
        }
    }

    public void seleccionarGerencia(ValueChangeEvent valueChangeEvent) {
        UtilLog4j.log.info(this, "selccionar gerencia");
        try {
            cadenasBeanModel.setIdGerencia((Integer) valueChangeEvent.getNewValue());
            cadenasBeanModel.traerGerenciaSeleccionada();
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepcion en seleccionar el la gerencia ");
        }
    }

    public void seleccionarTipoEspecifico(ValueChangeEvent valueChangeEvent) {
        try {
            cadenasBeanModel.setIdTipoEspecifico((Integer) valueChangeEvent.getNewValue());
            cadenasBeanModel.traerListaSolicitudesItems();
            cadenasBeanModel.setIdTipoSolicitud(-1);
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepcion en seleccionar el tipo especifico");
        }
    }
    
    public void seleccionarEstatusActivo(ValueChangeEvent valueChangeEvent) {
        try {
            cadenasBeanModel.setIdEstatusSeleccionado((Integer) valueChangeEvent.getNewValue());
//            cadenasBeanModel.traerEstatusSeleccionado();
//            cadenasBeanModel.setIdTipoSolicitud(0);
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepcion en seleccionar el tipo especifico");
        }
    }

    public void seleccionarRadio(ValueChangeEvent valueChangeEvent) {
        try {
            cadenasBeanModel.setSeleccionRadio((String) valueChangeEvent.getNewValue());
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepcion en seleccionar el radio");
        }
    }
    
    public void cabioValorHtmlInputHiden(ValueChangeEvent valueChangeEvent) {
        UtilLog4j.log.info(this, "Traer cadenas negacio model");
        try {
            cadenasBeanModel.setIdTipoSolicitud((Integer) valueChangeEvent.getNewValue());
            cadenasBeanModel.traerCadenasNegacionModel();
        } catch (Exception e) {
            UtilLog4j.log.info(this, "Excepcion al traer cadenas negacion model");
        }
    }
   

    public String getSeleccionRadio() {
        return cadenasBeanModel.getSeleccionRadio();
    }

    public void setSeleccionRadio(String seleccionRadio) {
        cadenasBeanModel.setSeleccionRadio(seleccionRadio);
    }

    public void seleccionarEstatusActivo() {
        cadenasBeanModel.setEstatusActivo((Estatus) cadenasBeanModel.getEstatusModel().getRowData());
    }

    public int getIdTipoSolicitud() {
        return cadenasBeanModel.getIdTipoSolicitud();
    }

    public void setIdTipoSolicitud(int idTipoSolicitud) {
        cadenasBeanModel.setIdTipoSolicitud(idTipoSolicitud);
    }

    public int getIdGerencia() {
        return cadenasBeanModel.getIdGerencia();
    }

    public void setIdGerencia(int idGerencia) {
        cadenasBeanModel.setIdGerencia(idGerencia);
    }

    public int getIdTipoEspecifico() {
        return cadenasBeanModel.getIdTipoEspecifico();
    }

    public void setIdTipoEspecifico(int idTipoEspecifico) {
        cadenasBeanModel.setIdTipoEspecifico(idTipoEspecifico);
    }

    public SgCadenaNegacion getCadenaNegacionActiva() {
        return cadenasBeanModel.getCadenaNegacionActiva();
    }

    public void setCadenaNegacionActiva(SgCadenaNegacion sgCadenaNegacion) {
        cadenasBeanModel.setCadenaNegacionActiva(sgCadenaNegacion);
    }

    
    public int getIdEstatusCadenaNegacionSeleccionado() {
        return cadenasBeanModel.getIdEstatusSeleccionado();
    }

    public void setIdEstatusCadenaNegacionSeleccionado(int idEstatusSeleccionado) {
        cadenasBeanModel.setIdEstatusSeleccionado(idEstatusSeleccionado);
    }
    
    public int getSizeColumnsEstatus() {
        UtilLog4j.log.info(this, "size " + cadenasBeanModel.getSizeColumns());
        return cadenasBeanModel.getSizeColumns();
    }

    public DataModel getCadenaAsignarDatamodelTMP() {
        return cadenasBeanModel.cadenaAsignarDatamodelTMP();
    }

    public Gerencia getGerenciaActiva() {
        return cadenasBeanModel.getGerenciaActiva();
    }

    public void setIdGerenciaActiva(Gerencia gerenciaActiva) {
        cadenasBeanModel.setGerenciaActiva(gerenciaActiva);
    }

    public Estatus getEstatusActivo() {
        return cadenasBeanModel.getEstatusActivo();
    }

    public void setIdEstatusActivo(Estatus EstatusActivo) {
        cadenasBeanModel.setEstatusActivo(EstatusActivo);
    }

    public SgCadenaAprobacion getCadenaAprobacionActiva() {
        return cadenasBeanModel.getCadenaAprobacionActiva();
    }

    public SgTipoSolicitudViaje getTipoSolicitudViajeActiva() {
        return cadenasBeanModel.getTipoSolicitudActiva();
    }

    public void setTipoSolicitudViajeActiva(SgTipoSolicitudViaje tipoSolicitud) {
        cadenasBeanModel.setTipoSolicitudActiva(tipoSolicitud);
    }

    public DataModel getCadenaNegacion() {
        return cadenasBeanModel.traerCadenasNegacionModel();
    }

    public DataModel getEstatus() {
        return cadenasBeanModel.getEstatusModel();
    }

    public List<SelectItem> getGerenciaItems() {
        return cadenasBeanModel.getListaGerenciaItems();
    }

    public List<SelectItem> getTipoSolicitudesItems() {
        return cadenasBeanModel.getListaTiposSolicitudesItems();
    }

    public List<SelectItem> getTipoEspecificoItems() {
        return cadenasBeanModel.getListaTiposEspecificoItems();
    }
    
     public List<SelectItem> getListaEstatusItems() {
         return cadenasBeanModel.getListaEstatusItems();
    }

    public DataModel getCadenaAprobacionPorTipoSolicitud() {
        return cadenasBeanModel.getCadenasAprobacionModel();
    }

    public boolean getMrPopupCrear() {
        return cadenasBeanModel.isMrPopup();
    }

    public boolean getMrPopupCadenaNegacion(){
        return cadenasBeanModel.isMrPopupCadenaNegacion();
    }
    public void mostrarEstatusPorAsignarCadenas(ActionEvent event) {
        if (cadenasBeanModel.getIdTipoSolicitud() != -1) {
            cadenasBeanModel.extraerListaParaAsigar();
            cadenasBeanModel.traerEstatusModel();
        } else {
            FacesUtils.addErrorMessage("Debe de seleccionar un tipo de solicitud para asignar la cadena de aprobación..");
        }
    }

    public void asignarCadenas(ActionEvent event){
        Integer i = Integer.parseInt(FacesUtils.getRequestParameter("idTipoSolicitudViaje"));        
        UtilLog4j.log.info(this, "Parametro "+i);
        cadenasBeanModel.setIdTipoSolicitud(i);
        if (cadenasBeanModel.getIdTipoSolicitud() != -1) {
            cadenasBeanModel.extraerListaParaAsigar();
            cadenasBeanModel.traerEstatusModel();
        } else {
            FacesUtils.addErrorMessage("Debe de seleccionar un tipo de solicitud para asignar la cadena de aprobación..");
        }
    }
    
   
    
    public void mostrarPopupAsignarGerencia(ActionEvent event) {
        if (cadenasBeanModel.getIdTipoSolicitud() != -1) {
            cadenasBeanModel.setIdEstatus(Integer.parseInt(FacesUtils.getRequestParameter("estatusId")));            
            cadenasBeanModel.traerEstatusSeleccionado();
            cadenasBeanModel.setIdGerencia(-1);
            cadenasBeanModel.traerListaGerenciasItems();
            cadenasBeanModel.setMrPopup(true);
        }
    }

    public void mostrarPopupModificarAsignarGerencia(ActionEvent event) {
        if (cadenasBeanModel.getIdTipoSolicitud() != -1) {
             CadenaAprobacionSolicitudVO cadVo = (CadenaAprobacionSolicitudVO) cadenasBeanModel.getCadenasAprobacionModel().getRowData();
             cadenasBeanModel.setCadenaAprobacionActiva(cadenasBeanModel.traerCadenaAprobacionSeleccionada(cadVo.getId()));
            
             //cadenasBeanModel.setCadenaAprobacionActiva((SgCadenaAprobacion) cadenasBeanModel.getCadenasAprobacionModel().getRowData());

            cadenasBeanModel.setEstatusActivo(cadenasBeanModel.getCadenaAprobacionActiva().getEstatus());
            UtilLog4j.log.info(this, "el estatus de tomo ok ");
            cadenasBeanModel.traerListaGerenciasItems();
            cadenasBeanModel.setIdGerencia(cadenasBeanModel.getCadenaAprobacionActiva().getGerencia().getId());
            cadenasBeanModel.setMrPopup(true);
        }
    }

    public void ocultarPopup(ActionEvent event) {
        cadenasBeanModel.setMrPopup(false);
    }
    
    
     public void mostrarPopupModificarCadenaNegacion(ActionEvent event) {
         cadenasBeanModel.setIdCadenaNegacion(Integer.valueOf(FacesUtils.getRequestParameter("idCadenaNegacion")));
         UtilLog4j.log.info(this, "idCadenaNegacion "+cadenasBeanModel.getIdCadenaNegacion());
         
        if (cadenasBeanModel.getIdCadenaNegacion() != 0) {
            cadenasBeanModel.setCadenaAprobacionActiva((SgCadenaAprobacion) cadenasBeanModel.getCadenasAprobacionModel().getRowData());
            cadenasBeanModel.traerCadeNegacionSeleccionada();
            cadenasBeanModel.traerEstatusSeleccionado();
            cadenasBeanModel.traerListaEstatusItems();
////            cadenasBeanModel.setIdEstatus(Integer.parseInt(FacesUtils.getRequestParameter("estatusId")));
//            cadenasBeanModel.setEstatusActivo(cadenasBeanModel.getCadenaAprobacionActiva().getEstatus());
//            UtilLog4j.log.info(this, "el estatus de tomo ok ");
//            cadenasBeanModel.traerListaGerenciasItems();
//            cadenasBeanModel.setIdGerencia(cadenasBeanModel.getCadenaAprobacionActiva().getGerencia().getId());
            cadenasBeanModel.setMrPopupCadenaNegacion(true);
        }
    }

    public void ocultarPopupModificarCadenaNegacion(ActionEvent event) {
        cadenasBeanModel.setMrPopupCadenaNegacion(false);
    }
}
