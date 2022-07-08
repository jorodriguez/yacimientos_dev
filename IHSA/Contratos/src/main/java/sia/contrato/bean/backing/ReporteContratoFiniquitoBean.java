/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.contrato.bean.backing;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedProperty;


import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import sia.constantes.Constantes;
import sia.contrato.bean.soporte.FacesUtils;
import sia.ihsa.contratos.Sesion;
import sia.modelo.ApCampo;
import sia.modelo.CvResumenReporteFiniquito;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.contrato.vo.ContratoVO;
import sia.modelo.contrato.vo.ResumenHistoricoAvanceFiniquitoVo;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.convenio.impl.ConvenioImpl;
import sia.servicios.convenio.impl.CvResumenReporteFiniquitoImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Named
@ViewScoped
public class ReporteContratoFiniquitoBean implements Serializable {

    /**
     * Creates a new instance of ReporteContratoFiniquitoBean
     */
    public ReporteContratoFiniquitoBean() {
    }

    @Inject
    private Sesion sesion;

    @Inject
    ConvenioImpl convenioImpl;
    @Inject
    ApCampoUsuarioRhPuestoImpl campoUsuarioRhPuestoImpl;
    @Inject
    CvResumenReporteFiniquitoImpl resumenReporteFiniquitoImpl;

    @Getter
    @Setter
    int campoId;
    @Getter
    @Setter
    private double avance;
    @Getter
    @Setter
    private double avanceGlobal;
    @Getter
    @Setter
    List<SelectItem> campos;
    @Getter
    @Setter
    private List<ContratoVO> contratos;
    @Getter
    @Setter
    private List<CvResumenReporteFiniquito> historicoAvances;
    @Getter
    @Setter
    private List<ResumenHistoricoAvanceFiniquitoVo> historicoGlobalAvances;

    @PostConstruct
    public void init() {
        contratos = new ArrayList<ContratoVO>();
        campoId = Constantes.CERO;
        contratos = convenioImpl.reporteContratoFiniquito(campoId);
        campos = new ArrayList<SelectItem>();
        historicoAvances = new ArrayList<CvResumenReporteFiniquito>();
        historicoGlobalAvances = new ArrayList<ResumenHistoricoAvanceFiniquitoVo>();
        //        
        llenarCampos();
        //
        calcularAvance(contratos);
    }

    private double calcularAvance(List<ContratoVO> contratosCampo) {
        int total = 0;
        avance = 0.0;
        if (!contratosCampo.isEmpty()) {
            for (ContratoVO contrato : contratosCampo) {
                if (!contrato.getTotalFormas().isEmpty()) {
                    int totalF = Integer.parseInt(contrato.getTotalFormas());
                    total += totalF;
                }
            }
            double av = (double) ((total * 100.0) / (contratosCampo.size() * 100.0));
            avance = new BigDecimal(av).setScale(2, RoundingMode.FLOOR).doubleValue();
        }
        return avance;
    }

    private void llenarCampos() {
        List<CampoUsuarioPuestoVo> cupVos = campoUsuarioRhPuestoImpl.getAllPorUsurio(sesion.getUsuarioSesion().getId());
        for (CampoUsuarioPuestoVo cupVo : cupVos) {
            campos.add(new SelectItem(cupVo.getIdCampo(), cupVo.getCampo()));
        }
    }

    public void llenarContratos() {
        contratos = convenioImpl.reporteContratoFiniquito(campoId);
        //
        calcularAvance(contratos);
    }

    public void mostrarAvancesProcesoFiniquito() {
        historicoAvances = resumenReporteFiniquitoImpl.traerTodos(campoId);
        //
        historicoGlobalAvances = resumenReporteFiniquitoImpl.traerResumenGlobal();
        //
        if (historicoGlobalAvances != null) {
            double total = 0.0;
            for (ResumenHistoricoAvanceFiniquitoVo historicoGlobalAvance : historicoGlobalAvances) {
                total += Double.valueOf(historicoGlobalAvance.getAvance().replace("%", ""));
            }
            avanceGlobal = total / historicoGlobalAvances.size();
        }
        PrimeFaces.current().executeScript( "$(dialogoResumenFiniquito).modal('show');");
    }

    public void cambiarCampoHistorico() {
        historicoAvances = resumenReporteFiniquitoImpl.traerTodos(campoId);
    }

    public void generarNuevoReporte() {
        try {
            Map<Integer, List<ContratoVO>> mapaContratosCampo = new HashMap<>();
            List<ContratoVO> contTemp = convenioImpl.reporteContratoFiniquito(Constantes.CERO);
            if (!contTemp.isEmpty()) {
                Set<Integer> camposId = new HashSet();
                for (ContratoVO contrato : contTemp) {
                    camposId.add(contrato.getIdCampo());
                }
                List<ContratoVO> contratosCampo = new ArrayList<>();
                for (Integer idConv : camposId) {
                    contratosCampo = new ArrayList<>();
                    for (ContratoVO contrato : contTemp) {
                        if (idConv == contrato.getIdCampo()) {
                            contratosCampo.add(contrato);
                        }
                    }
                    mapaContratosCampo.put(idConv, contratosCampo);
                }
                for (Map.Entry<Integer, List<ContratoVO>> entry : mapaContratosCampo.entrySet()) {

                    List<ContratoVO> value = entry.getValue();

                }
                for (Map.Entry<Integer, List<ContratoVO>> entry : mapaContratosCampo.entrySet()) {
                    Integer idCampo = entry.getKey();
                    List<ContratoVO> contratoVOs = entry.getValue();
                    CvResumenReporteFiniquito crrf = new CvResumenReporteFiniquito();
                    crrf.setApCampo(new ApCampo(idCampo));
                    crrf.setAvance(calcularAvance(contratoVOs));
                    crrf.setTotalContabilizado(contratoVOs.size());
                    resumenReporteFiniquitoImpl.guardar(sesion.getUsuarioSesion().getId(), crrf);
                    //
                }
                FacesUtils.addInfoMessage("Se generó el corte de avance del proceso de finiquito.");

            } else {
                FacesUtils.addErrorMessage("No hay datos para generar el reporte.");

            }
        } catch (IllegalArgumentException e) {
            UtilLog4j.log.error(e);
        }
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }
}
