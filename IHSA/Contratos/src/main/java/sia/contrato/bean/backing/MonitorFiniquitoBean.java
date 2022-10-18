/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.contrato.bean.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;


import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import sia.constantes.Constantes;
import sia.contrato.bean.soporte.FacesUtils;
import sia.ihsa.contratos.Sesion;
import sia.modelo.contrato.vo.ContratoFormasVo;
import sia.modelo.contrato.vo.ContratoVO;
import sia.modelo.contrato.vo.ExhortoVo;
import sia.servicios.convenio.impl.ConvenioImpl;
import sia.servicios.convenio.impl.CvConvenioExhortoImpl;
import sia.servicios.convenio.impl.CvConvenioFormasImpl;
import sia.servicios.convenio.impl.CvConvenioFormasNotificacionesImpl;

/**
 *
 * @author mluis
 */
@ManagedBean
@ViewScoped
public class MonitorFiniquitoBean implements Serializable {

    /**
     * Creates a new instance of MonitorFiniquitoBean
     */
    public MonitorFiniquitoBean() {
    }

    @Inject
    private Sesion sesion;
    //
    @Inject
    CvConvenioFormasImpl convenioFormasImpl;
    @Inject
    CvConvenioExhortoImpl convenioExhortoImpl;
    @Inject
    ConvenioImpl convenioImpl;
    @Inject
    CvConvenioFormasNotificacionesImpl convenioFormasNotificacionesImpl;

    @Getter
    @Setter
    List<ContratoVO> contratos;
    @Getter
    @Setter
    List<ContratoFormasVo> contratoFormas;
    @Getter
    @Setter
    List<ExhortoVo> exhortosPorContrato;
    @Getter
    @Setter
    ContratoVO contratoVo;
    @Getter
    @Setter
    ContratoVO contratoFiniquitoVo;
    @Getter
    @Setter
    private ContratoFormasVo contratoFormaVo;
    @Getter
    @Setter
    int statusId;

    @PostConstruct
    public void init() {
        contratoFormas = new ArrayList<ContratoFormasVo>();
        statusId = Constantes.ESTADO_CONVENIO_PROCESO_FINIQUITO;
        contratoVo = new ContratoVO();
        contratoFormaVo = new ContratoFormasVo();
        llenarContratos();
        //
        
    }

    private void llenarContratos() {
        contratos = convenioImpl.traerConvenioPorStatusCampo(statusId, sesion.getUsuarioSesion().getIdCampo());
    }

    public void cambiarStatusContrato(AjaxBehaviorEvent event) {
        llenarContratos();
    }

    public void mostrarMonitorContrato(int ind) {
        contratoVo = contratos.get(ind);
        //
        contratoFormas = convenioFormasImpl.traerFormasPorConvenio(contratoVo.getId());
        //
        exhortosPorContrato = convenioExhortoImpl.traerPorConvenio(contratos.get(ind).getId());
    }

    public void mostrarNotificaciones(int idForma) {
        System.out.println("Forma: " + idForma);
        contratoFormaVo = new ContratoFormasVo();
        contratoFormaVo = contratoFormas.get(idForma);
        // notificacionesForma = convenioFormasNotificacionesImpl.traerPorForma(idForma);
        //System.out.println("Not : " + (notificacionesForma != null ? notificacionesForma.size() : 0));
        //
        PrimeFaces.current().executeScript( "$(dialogoNotificacionesFormas).modal('show');");
    }

    public void inicioAgregarContratoFiniquito(int ind) {
        contratoVo = contratos.get(ind);
        //
        contratoFormas = convenioFormasImpl.traerFormasPorConvenio(contratoVo.getId());
        boolean continuar = true;
        for (ContratoFormasVo contratoForma : contratoFormas) {
            if (!contratoForma.isValidado()) {
                continuar = false;
                break;
            }
        }
        //
        if (continuar) {
            contratoFiniquitoVo = new ContratoVO();
            contratoFiniquitoVo.setIdCampo(contratoVo.getIdCampo());
            contratoFiniquitoVo.setProveedor(contratoVo.getProveedor());
            contratoFiniquitoVo.setIdTipo(4);
            contratoFiniquitoVo.setIdClasificacion(contratoVo.getIdClasificacion());
            contratoFiniquitoVo.setIdContratoRelacionado(contratoVo.getId());
            FacesUtils.addErrorMessage("Registrar el contrato de finiquito");
            PrimeFaces.current().executeScript( "$(dialogoContratoNuevo).modal('show');");
        } else {
            PrimeFaces.current().executeScript( "alert('Para continuar es necesario validar todas las formas.')");
        }
    }

    public void guardarContratoFiniquito() {
        convenioImpl.guardarContratoFiniquito(sesion.getUsuarioSesion().getId(), contratoFiniquitoVo);
        contratoFiniquitoVo = new ContratoVO();
        llenarContratos();
        PrimeFaces.current().executeScript( "$(dialogoContratoNuevo).modal('hide');");
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

}
