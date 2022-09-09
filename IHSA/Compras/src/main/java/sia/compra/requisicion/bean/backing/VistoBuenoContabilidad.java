/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.requisicion.bean.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;

import javax.faces.model.ArrayDataModel;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import sia.compra.sistema.bean.backing.ContarBean;
import sia.constantes.Constantes;
import sia.constantes.TipoRequisicion;
import sia.modelo.OcUsoCFDI;
import sia.modelo.Rechazo;
import sia.modelo.Requisicion;
import sia.modelo.requisicion.vo.RequisicionDetalleVO;
import sia.modelo.sgl.vo.RequisicionVO;
import sia.servicios.requisicion.impl.OcRequisicionCoNoticiaImpl;
import sia.servicios.requisicion.impl.OcUsoCFDIImpl;
import sia.servicios.requisicion.impl.ReRequisicionEtsImpl;
import sia.servicios.requisicion.impl.RequisicionImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Named(value = "vistoBuenoContabilidad")
@ViewScoped
public class VistoBuenoContabilidad implements Serializable {

    /**
     * Creates a new instance of VistoBuenoContabilidad
     */
    public VistoBuenoContabilidad() {
    }
    private static final UtilLog4j LOGGER = UtilLog4j.log;
    @Inject
    private RequisicionImpl requisicionRemoto;
    @Inject
    private OcRequisicionCoNoticiaImpl ocRequisicionCoNoticiaImpl;
    @Inject
    private ReRequisicionEtsImpl reRequisicionEtsImpl;
    @Inject
    private OcUsoCFDIImpl ocUsoCFDIImpl;
    //
    @Inject
    private UsuarioBean usuarioBean;

    private DataModel listaRequisiciones;
    private Requisicion requisicionActual;
    private RequisicionVO requisicionVO;
    private DataModel listaItems = null;
    private DataModel listaEts;
    private String motivo;
    private int idCfdi;
    private List<SelectItem> listaUso;
    private List<Rechazo> listaRechazo;

    @PostConstruct
    public void iniciar() {
        listaItems = new ArrayDataModel<>();
        listaEts = new ArrayDataModel<>();
        listaRequisiciones = new ArrayDataModel();
        requisicionesSinVistoBueno();

    }

    public void requisicionesSinVistoBueno() {
        try {
            if (usuarioBean.getMapaRoles().containsKey("Visto Bueno Contabilidad")) {
                listaRequisiciones = new ListDataModel(requisicionRemoto.requisicionesSinVistoBueno(usuarioBean.getUsuarioConectado().getId(),
                        usuarioBean.getUsuarioConectado().getApCampo().getId(),
                        TipoRequisicion.AF.name(), Constantes.ROL_VISTO_BUENO_CONTABILIDAD));
                List<OcUsoCFDI> lista = ocUsoCFDIImpl.traerCFDIPorTipo(TipoRequisicion.AF.toString());
                setListaUso(new ArrayList<>());
                for (OcUsoCFDI ocUsoCFDI : lista) {
                    getListaUso().add(new SelectItem(ocUsoCFDI.getId(), ocUsoCFDI.getCodigo() + " - " + ocUsoCFDI.getNombre()));
                }
            }
        } catch (Exception ex) {
            LOGGER.fatal(this, "Ex : : : : " + ex.getMessage(), ex);
        }
    }

    public void vistoBuenoCosto() {
        try {
            if (this.getRequisicionActual() == null) {
                for (Object object : listaRequisiciones) {
                    RequisicionVO o = (RequisicionVO) object;
                    if (o.isSelected()) {
                        if (o.getIdCfdi() > Constantes.CERO || o.getCompania().equals(Constantes.RFC_IHSA_CQ)) {
                            vistoBuenoCostoMth(o);
                        } else {
                            FacesUtilsBean.addErrorMessage("Para revisar la requisicion " + o.getConsecutivo() + " es necesario seleccionar el USO CFDI, en la requisición.");
                        }
                    }
                }
                //
                FacesUtilsBean.addInfoMessage("Se envió al proceso de aprobación . . . ");
                cambiarRequisicion(0);
                //
                String jsMetodo = ";limpiarTodos();";
                PrimeFaces.current().executeScript(jsMetodo);
            } else {
                if (idCfdi > Constantes.CERO || requisicionActual.getCompania().getRfc().equals(Constantes.RFC_IHSA_CQ)) {
                    requisicionVO.setIdCfdi(idCfdi);
                    vistoBuenoCostoMth(requisicionVO);
                    FacesUtilsBean.addInfoMessage("Se envió al proceso de aprobación . . . ");
                    this.cambiarRequisicion(0);
                    String jsMetodo = ";regresar('divTabla', 'divDatos', 'divOperacion', 'divAutoriza');";
                    PrimeFaces.current().executeScript(jsMetodo);
                } else {
                    FacesUtilsBean.addErrorMessage("Seleccione el Uso CFDI, para la requisición.");
                }
            }
            ContarBean contarBean = (ContarBean) FacesUtilsBean.getManagedBean("contarBean");
            contarBean.llenarReqSinVoBoConta();
            contarBean.llenarReqSinVistoBueno();
            //
            requisicionesSinVistoBueno();
        } catch (Exception ex) {
            LOGGER.fatal(this, "Ex : : : : " + ex.getMessage(), ex);
        }
    }

    private void vistoBuenoCostoMth(RequisicionVO requiActual) {
        try {
            requisicionRemoto.vistoBuenoRequisicion(usuarioBean.getUsuarioConectado(), requiActual);
        } catch (Exception e) {
            FacesUtilsBean.addInfoMessage(FacesUtilsBean.getKeyResourceBundle("requisiciones.correos.APRnoenviado"));
        }
        // Finalizar notas si tiene
        finalizarNotas(requiActual);
    }

    public void limpiarRequisicion() {
        this.cambiarRequisicion(0);
    }

    public void cambiarRequisicion(int idRequisicion) {
        requisicionActual = null;
        requisicionVO = null;
    }

    private void finalizarNotas(RequisicionVO reqVo) {
        ocRequisicionCoNoticiaImpl.finalizarNotas(usuarioBean.getUsuarioConectado().getId(), reqVo.getId());
    }

    public void iniciodevolverVariasRequisiciones() {
        boolean continuar = false;
        for (Object object : listaRequisiciones) {
            RequisicionVO o = (RequisicionVO) object;
            if (o.isSelected()) {
                continuar = true;
                break;
            }
        }
        if (continuar) {
            PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoDevVariasReq);");
        } else {
            FacesUtilsBean.addErrorMessage("Es necesario seleccionar al menos una requisición");
        }
    }

    public void devolverRequisicion() {
        try {
            PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoDevReq);");
        } catch (Exception e) {
            LOGGER.fatal(this, e.getMessage(), e);
        }
    }

    public void devolverVariasRequisicion() {
        try {
            for (Object object : listaRequisiciones) {
                RequisicionVO o = (RequisicionVO) object;
                if (o.isSelected()) {
                    requisicionRemoto.rechazar(usuarioBean.getUsuarioConectado(), o, motivo);
                }
            }
            String jsMetodo = ";limpiarTodos();";
            PrimeFaces.current().executeScript(jsMetodo);
            PrimeFaces.current().executeScript(";cerrarDevolver();");
            //
            ContarBean contarBean = (ContarBean) FacesUtilsBean.getManagedBean("contarBean");
            contarBean.llenarReqSinVistoBueno();
            //
            requisicionesSinVistoBueno();
        } catch (Exception ex) {
            LOGGER.fatal(this, "Ex : : : : " + ex.getMessage(), ex);
        }
    }

    public void inicioCancelarVariasRequisiciones() {
        boolean continuar = false;
        for (Object object : listaRequisiciones) {
            RequisicionVO o = (RequisicionVO) object;
            if (o.isSelected()) {
                continuar = true;
                break;
            }
        }
        if (continuar) {
            PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoCancelarVariasReq);");
        } else {
            FacesUtilsBean.addErrorMessage("Es necesario seleccionar al menos una requisición");
        }
    }

    public void cancelarVariasRequisicion() {
        try {
            for (Object object : listaRequisiciones) {
                RequisicionVO o = (RequisicionVO) object;
                if (o.isSelected()) {
                    requisicionVO = o;
                    cancelarRequisicion();
                }
            }
            requisicionVO = null;
            //
            PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoCancelarVariasReq);");
            this.cambiarRequisicion(0);
            String jsMetodo = ";limpiarTodos();";
            PrimeFaces.current().executeScript(jsMetodo);
            //

            ContarBean contarBean = (ContarBean) FacesUtilsBean.getManagedBean("contarBean");
            contarBean.llenarReqSinVoBoConta();
            //
            requisicionesSinVistoBueno();
        } catch (Exception ex) {
            LOGGER.fatal(this, "Ex : : : : " + ex.getMessage(), ex);
        }
    }

    public void cancelarRequisicion() {
        requisicionRemoto.cancelar(usuarioBean.getUsuarioConectado(), requisicionVO, motivo);
        // Finalizar notas si tiene
        finalizarNotas(requisicionVO);
        //---- Mostrar mensaje  ----
        FacesUtilsBean.addInfoMessage("Requisición(es) cancelada(s) correctamente...");
        //Esto es para Quitar las lineas seleccionadas
        this.cambiarRequisicion(0);
        //Esto es para cerrar el panel emergente de cancelar requisicion
        PrimeFaces.current().executeScript(";cerrarCancelar();");
    }

    public void seleccionarRequisicionCostosConta(int idCfId, int idReq) {
        try {
            //
            idCfdi = idCfId;
            //
            setRequisicionActual(requisicionRemoto.find(idReq));
            //
            itemsProcesoAprobar();
            //
            etsPorRequisicion();
            //
            rechazosRequisicion();
            String jsMetodo = ";activarTab('tabOCSProc',0, 'divDatos', 'divTabla', 'divOperacion', 'divAutoriza');";
            PrimeFaces.current().executeScript(jsMetodo);
        } catch (Exception e) {
            LOGGER.fatal(this, e.getMessage(), e);
        }
    }

    public void itemsProcesoAprobar() {
        try {

            List<RequisicionDetalleVO> lo = this.requisicionRemoto.getItemsPorRequisicion(this.requisicionActual.getId(), false, false);
            setListaItems(new ListDataModel(lo));
        } catch (Exception ex) {
            this.setListaItems(null);
            LOGGER.fatal(this, ex.getMessage(), ex);
        }
    }

    public void itemsProcesoAprobarMulti() {
        try {
            if (requisicionActual != null && requisicionActual.getId() > 0) {
                List<RequisicionDetalleVO> lo = this.requisicionRemoto.getItemsPorRequisicionMulti(this.requisicionActual.getId(), false, false);
                setListaItems(new ListDataModel(lo));
            }
        } catch (Exception ex) {
            this.setListaItems(null);
            LOGGER.fatal(this, ex.getMessage(), ex);
        }
    }

    public void rechazarRequisicion() {
        try {
            requisicionRemoto.rechazar(usuarioBean.getUsuarioConectado(), requisicionVO, motivo);
            //
            finalizarNotas(requisicionVO);
            //
            FacesUtilsBean.addInfoMessage("Requisición(es) devuelta(s) correctamente...");
            //
            this.cambiarRequisicion(0);
            //
            requisicionesSinVistoBueno();
            //
            ContarBean contarBean = (ContarBean) FacesUtilsBean.getManagedBean("contarBean");
            contarBean.llenarReqSinVoBoConta();
            //
            String jsMetodo = ";limpiarTodos();";
            PrimeFaces.current().executeScript(jsMetodo);
            PrimeFaces.current().executeScript(";cerrarDevolver();");
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            FacesUtilsBean.addInfoMessage("Requisición(es) devuelta(s) correctamente...");
        }
    }

    public void etsPorRequisicion() {
        try {
            setListaEts(new ListDataModel(reRequisicionEtsImpl.traerAdjuntosPorRequisicion(getRequisicionActual().getId())));
        } catch (Exception ex) {
            setListaEts(null);
            LOGGER.fatal(this, null, ex);
        }
    }

    public void rechazosRequisicion() {
        try {
            setListaRechazo(requisicionRemoto.getRechazosPorRequisicion(this.requisicionActual.getId()));
        } catch (RuntimeException ex) {
            LOGGER.fatal(this, ex.getMessage(), ex);
        }
    }

    /**
     * @return the listaRequisiciones
     */
    public DataModel getListaRequisiciones() {
        return listaRequisiciones;
    }

    /**
     * @param listaRequisiciones the listaRequisiciones to set
     */
    public void setListaRequisiciones(DataModel listaRequisiciones) {
        this.listaRequisiciones = listaRequisiciones;
    }

    /**
     * @return the requisicionActual
     */
    public Requisicion getRequisicionActual() {
        return requisicionActual;
    }

    /**
     * @param requisicionActual the requisicionActual to set
     */
    public void setRequisicionActual(Requisicion requisicionActual) {
        this.requisicionActual = requisicionActual;
    }

    /**
     * @return the listaItems
     */
    public DataModel getListaItems() {
        return listaItems;
    }

    /**
     * @param listaItems the listaItems to set
     */
    public void setListaItems(DataModel listaItems) {
        this.listaItems = listaItems;
    }

    /**
     * @return the motivo
     */
    public String getMotivo() {
        return motivo;
    }

    /**
     * @param motivo the motivo to set
     */
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    /**
     * @return the listaEts
     */
    public DataModel getListaEts() {
        return listaEts;
    }

    /**
     * @param listaEts the listaEts to set
     */
    public void setListaEts(DataModel listaEts) {
        this.listaEts = listaEts;
    }

    /**
     * @return the requisicionVO
     */
    public RequisicionVO getRequisicionVO() {
        return requisicionVO;
    }

    /**
     * @param requisicionVO the requisicionVO to set
     */
    public void setRequisicionVO(RequisicionVO requisicionVO) {
        this.requisicionVO = requisicionVO;
    }

    /**
     * @return the listaUso
     */
    public List<SelectItem> getListaUso() {
        return listaUso;
    }

    /**
     * @param listaUso the listaUso to set
     */
    public void setListaUso(List<SelectItem> listaUso) {
        this.listaUso = listaUso;
    }

    /**
     * @return the idCfdi
     */
    public int getIdCfdi() {
        return idCfdi;
    }

    /**
     * @param idCfdi the idCfdi to set
     */
    public void setIdCfdi(int idCfdi) {
        this.idCfdi = idCfdi;
    }

    /**
     * @return the listaRechazo
     */
    public List<Rechazo> getListaRechazo() {
        return listaRechazo;
    }

    /**
     * @param listaRechazo the listaRechazo to set
     */
    public void setListaRechazo(List<Rechazo> listaRechazo) {
        this.listaRechazo = listaRechazo;
    }

}
