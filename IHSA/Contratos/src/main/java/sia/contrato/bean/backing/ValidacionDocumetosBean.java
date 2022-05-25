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
import javax.faces.bean.ManagedProperty;
import javax.faces.view.ViewScoped;


import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import sia.constantes.Constantes;
import sia.contrato.bean.soporte.FacesUtils;
import sia.ihsa.contratos.Sesion;
import sia.modelo.contrato.vo.ContratoFormasNotasVo;
import sia.modelo.contrato.vo.ContratoFormasVo;
import sia.servicios.convenio.impl.CvConvenioFormasImpl;
import sia.servicios.convenio.impl.CvConvenioFormasNotasImpl;

/**
 *
 * @author mluis
 */
@Named
@ViewScoped
public class ValidacionDocumetosBean implements Serializable {

    /**
     * Creates a new instance of ValidacionDocumetosBean
     */
    public ValidacionDocumetosBean() {
    }

    @Inject
    private Sesion sesion;
    //
    @Inject
    CvConvenioFormasImpl convenioFormasImpl;
    @Inject
    CvConvenioFormasNotasImpl convenioFormasNotasImpl;

    @Setter
    @Getter
    List<ContratoFormasVo> formas;
    @Setter
    @Getter
    ContratoFormasVo contratoFormasVo;
    @Setter
    @Getter
    ContratoFormasNotasVo contratoFormasNotasVo;

    @PostConstruct
    public void iniciar() {
        formas = new ArrayList<ContratoFormasVo>();
        llenarContratos();
        contratoFormasVo = new ContratoFormasVo();
        contratoFormasNotasVo = new ContratoFormasNotasVo();
    }

    private void llenarContratos() {
        formas = convenioFormasImpl.traerFormasPorGerenciaSinValidar(sesion.getUsuarioSesion().getIdGerencia(), Constantes.ESTADO_CONVENIO_PROCESO_FINIQUITO);
    }

    public void inicioRechazo() {
        int indice = Integer.parseInt(FacesUtils.getRequestParam("indice"));
        contratoFormasVo = formas.get(indice);
        PrimeFaces.current().executeScript( "");
        PrimeFaces.current().executeScript( "$(dialogoRechazarForma).modal('show');");
    }

    public void cancelarEnviarObservacion() {
        contratoFormasNotasVo = new ContratoFormasNotasVo();
        contratoFormasVo = new ContratoFormasVo();
        PrimeFaces.current().executeScript( "$(dialogoRechazarForma).modal('hide');");
    }

    public void enviarObservacion() {
        convenioFormasNotasImpl.guardar(sesion.getUsuarioSesion(), contratoFormasNotasVo, contratoFormasVo);
        //
        contratoFormasNotasVo = new ContratoFormasNotasVo();
        contratoFormasVo = new ContratoFormasVo();
        PrimeFaces.current().executeScript( "$(dialogoRechazarForma).modal('hide');");
    }

    public void validarDocumento() {
        int indice = Integer.parseInt(FacesUtils.getRequestParam("indice"));
        convenioFormasImpl.validarDocumentacion(sesion.getUsuarioSesion(), formas.get(indice));
        llenarContratos();
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

}
