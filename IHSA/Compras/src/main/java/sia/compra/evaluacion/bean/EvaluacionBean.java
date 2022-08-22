/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.evaluacion.bean;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedProperty;

import javax.faces.context.FacesContext;

import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import sia.compra.requisicion.bean.backing.FacesUtilsBean;
import sia.compra.requisicion.bean.backing.UsuarioBean;
import sia.compra.sistema.bean.backing.ContarBean;
import sia.constantes.Constantes;
import sia.modelo.CvEvaluacion;
import sia.modelo.CvEvaluacionResp;
import sia.modelo.contrato.vo.EvaluacionVo;
import sia.modelo.contrato.vo.PreguntaVo;
import sia.modelo.contrato.vo.SeccionVo;
import sia.notificaciones.evaluacion.impl.NotificacionEvaluacionImpl;
import sia.pdf.impl.SiaPDFImpl;
import sia.servicios.evaluacion.impl.CvEvaluacionImpl;
import sia.servicios.evaluacion.impl.CvEvaluacionRespImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author jcarranza
 */
@Named (value = "evaluacionBean")
@ViewScoped
public class EvaluacionBean implements Serializable{

    public EvaluacionBean() {

    }

    @Inject
    private UsuarioBean usuarioBean;

    private List<EvaluacionVo> lstEvaluaciones = new ArrayList<>();
    private EvaluacionVo vo;

    @Inject
    CvEvaluacionImpl cvEvaluacionImpl;
    @Inject
    CvEvaluacionRespImpl cvEvaluacionRespImpl;
    @Inject
    SiaPDFImpl siaPDFImpl;
    @Inject
    NotificacionEvaluacionImpl notificacionEvaluacionImpl;
    @Inject
    SiUsuarioRolImpl siUsuarioRolImpl;

    @PostConstruct
    public void iniciar() {
        setVo(new EvaluacionVo());
        this.setLstEvaluaciones(cvEvaluacionImpl.traerEvaluacionesPendientes(usuarioBean.getUsuarioConectado().getId(), usuarioBean.getUsuarioConectado().getApCampo().getId(), false));
    }

    public void iniciarEvaluaciones() {
        this.iniciar();
    }

    /**
     * @param usuarioBean the usuarioBean to set
     */
    public void setUsuarioBean(UsuarioBean usuarioBean) {
        this.usuarioBean = usuarioBean;
    }

    /**
     * @return the lstEvaluaciones
     */
    public List<EvaluacionVo> getLstEvaluaciones() {
        return lstEvaluaciones;
    }

    /**
     * @param lstEvaluaciones the lstEvaluaciones to set
     */
    public void setLstEvaluaciones(List<EvaluacionVo> lstEvaluaciones) {
        this.lstEvaluaciones = lstEvaluaciones;
    }

    public void seleccionarEvaluacion(int idEva, int idConv) {
        if (idConv > 0 && idEva > 0) {
            setVo(cvEvaluacionImpl.traerEvaluacionDetID(idEva));
            PrimeFaces.current().executeScript(";activarTab('tabOCSProc', 0, 'divDatos', 'divTabla', 'divOperacion', 'divAutoriza');");
        }
    }

    /**
     * @return the vo
     */
    public EvaluacionVo getVo() {
        return vo;
    }

    /**
     * @param vo the vo to set
     */
    public void setVo(EvaluacionVo vo) {
        this.vo = vo;
    }

    public void guardarEvaluacion() {
        if (getVo() != null && getVo().getConvenioId() > 0) {
            try {
                CvEvaluacion eval = this.cvEvaluacionImpl.find(getVo().getId());
                if (eval != null && eval.getId() > 0) {
                    if (guardarRespuestas(eval)) {
                        eval.setCorreo(getVo().getCorreoProveedor());
                        eval.setNombreProveedor(getVo().getNombreProveedor());
                        eval.setFecha(getVo().getFechaEvaluacion());
                        eval.setObservaciones(getVo().getObservaciones());
                        eval.setContestada(true);
                        this.cvEvaluacionImpl.edit(eval);

                        File evaPDF = siaPDFImpl.getPDF(eval, usuarioBean.getUsuarioConectado(), true);
                        notificacionEvaluacionImpl.notificacionRespuestaEvaluacion(
                                siUsuarioRolImpl.traerCorreosPorCodigoRolList("" + Constantes.ROL_ADMINISTRA_CONTRATO, usuarioBean.getUsuarioConectado().getApCampo().getId()),
                                "", "", "Evaluación del proveedor para el contrato " + getVo().getConvenioCodigo(), eval, evaPDF);

                        this.iniciar();
                        ContarBean contarBean = (ContarBean) FacesUtilsBean.getManagedBean("contarBean");
                        contarBean.llenarEvaluador();
                        FacesUtilsBean.addInfoMessage("Se guardo correctamente la evaluación y fue enviada al solicitante.");
                        PrimeFaces.current().executeScript(";regresar('divTabla', 'divDatos', 'divOperacion', 'divAutoriza');");
                    } else {
                        FacesUtilsBean.addErrorMessage("Ha ocurrido un problema en la aplicación, por favor contacte al equipo de soporte de SIA (soportesia@ihsa.mx)");
                    }
                } else {
                    FacesUtilsBean.addErrorMessage("Ha ocurrido un problema en la aplicación, por favor contacte al equipo de soporte de SIA (soportesia@ihsa.mx)");
                }

            } catch (Exception ex) {
                Logger.getLogger(EvaluacionBean.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    private boolean guardarRespuestas(CvEvaluacion eval) {
        boolean respuesta = true;
        try {
            CvEvaluacionResp resp = null;
            for (SeccionVo sec : getVo().getSecciones()) {
                for (PreguntaVo preg : sec.getPreguntas()) {
                    resp = new CvEvaluacionResp();
                    resp.setCvEvaluacion(eval);
                    resp.setSeccion(sec.getSeccionNombre());
                    resp.setMaximo(sec.getSeccionMaximo());
                    resp.setPregunta(preg.getPregunta());
                    resp.setRespuesta(preg.isRespuesta());
                    resp.setPuntos(sec.getTotal());
                    resp.setPuntosTotal(getVo().getSumatoriaResp());

                    resp.setGenero(usuarioBean.getUsuarioConectado());
                    resp.setFechaGenero(new Date());
                    resp.setHoraGenero(new Date());
                    resp.setEliminado(false);

                    cvEvaluacionRespImpl.create(resp);

                }
            }
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            respuesta = false;
        }
        return respuesta;
    }

}
