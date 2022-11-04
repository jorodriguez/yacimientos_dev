/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.contrato.bean.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import sia.constantes.Constantes;
import sia.contrato.bean.backing.ContratoBean;
import sia.contrato.bean.soporte.FacesUtils;
import sia.ihsa.contratos.Sesion;
import sia.modelo.Convenio;
import sia.modelo.CvConvenioEvaluacion;
import sia.modelo.CvEvaluacion;
import sia.modelo.CvEvaluacionTemplate;
import sia.modelo.Gerencia;
import sia.modelo.Proveedor;
import sia.modelo.contrato.vo.ContratoEvaluacionVo;
import sia.modelo.contrato.vo.ContratoVO;
import sia.modelo.contrato.vo.EvaluacionTemplateVo;
import sia.modelo.gerencia.vo.GerenciaVo;
import sia.modelo.usuario.vo.UsuarioRolVo;
import sia.notificaciones.evaluacion.impl.NotificacionEvaluacionImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.convenio.impl.ConvenioImpl;
import sia.servicios.convenio.impl.CvConvenioGerenciaImpl;
import sia.servicios.evaluacion.impl.CvConvenioEvaluacionImpl;
import sia.servicios.evaluacion.impl.CvEvaluacionImpl;
import sia.servicios.evaluacion.impl.CvEvaluacionTemplateImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author jcarranza
 */
@Named(value = "evaluacionBean")
@ViewScoped
public class EvaluacionModel implements Serializable {

    static final long serialVersionUID = 1;
    /**
     * Creates a new instance of ProveedorModel
     */
    @Inject
    private Sesion sesion;

    @Inject
    private CvConvenioGerenciaImpl cvConvenioGerenciaImpl;
    @Inject
    private SiUsuarioRolImpl siUsuarioRolImpl;
    @Inject
    private ConvenioImpl convenioImpl;
    @Inject
    private CvEvaluacionTemplateImpl cvEvaluacionTemplateImpl;
    @Inject
    private UsuarioImpl usuarioImpl;
    @Inject
    private CvConvenioEvaluacionImpl cvConvenioEvaluacionImpl;
    @Inject
    private CvEvaluacionImpl cvEvaluacionImpl;
    @Inject
    NotificacionEvaluacionImpl notificacionEvaluacionImpl;

    private int contratoId;
    private int contratoEvaId;
    private ContratoVO contratoVO;
    private ContratoEvaluacionVo vo;
    private boolean nuevaEvaluacion;
    private List<GerenciaVo> gerencias;
    private List<UsuarioRolVo> usuarios;
    private List<EvaluacionTemplateVo> templates;
    private int geranciaId;
    private CvConvenioEvaluacion conEva;

    public EvaluacionModel() {
    }

    @PostConstruct
    public void iniciar() {
        vo = new ContratoEvaluacionVo();
    }

    ContratoBean contratoBean = (ContratoBean) FacesUtils.getManagedBean(FacesContext.getCurrentInstance(), "contratoBean");

    private void cargarGerencias() {
        this.setGerencias(cvConvenioGerenciaImpl.convenioPorGerenica(this.getContratoId()));
    }

        private void cargarTemplates() {
        this.setTemplates(cvEvaluacionTemplateImpl.traerTemplatePorTipo(this.getContratoVO().getIdClasificacion(), this.getContratoVO().getCompania()));
    }

    public void cargarListas() {
        this.cargarGerencias();
        this.cargarUsuarios();
        this.cargarTemplates();
    }

    public void cargarContrato() {
        if (this.getContratoId() > 0) {
            if (this.getContratoVO() == null) {
                this.setContratoVO(convenioImpl.buscarPorId(this.getContratoId(), 0, "", false));
            } else if (this.getContratoVO() != null) {
                if (this.getContratoVO().getId() != this.getContratoId()) {
                    this.setContratoVO(convenioImpl.buscarPorId(this.getContratoId(), 0, "", false));
                }
            }
        } else {
            this.contratoVO = null;
        }
    }

    public void cargarVO() {
        this.setVo(cvConvenioEvaluacionImpl.traerEvaluacionTemplateID(this.getContratoEvaId()));
    }

    private void cargarUsuarios() {
        this.setUsuarios(siUsuarioRolImpl.traerRolPorCodigo(Constantes.COD_ROL_EVALUADOR, sesion.getUsuarioSesion().getIdCampo(), Constantes.MODULO_CONTRATO));
    }

    public void nuevaEvaluacion(int idConv) {
        try {
            if (idConv > 0) {
                setContratoId(idConv);
                setVo(new ContratoEvaluacionVo());
                cargarContrato();
                setNuevaEvaluacion(true);
                cargarListas();
                PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoEval);");
            }

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void editarEvaluacion(int idConv, int idConvTem) {
        try {
            if (idConv > 0 && idConvTem > 0) {
                setContratoId(idConv);
                setContratoEvaId(idConvTem);
                setVo(new ContratoEvaluacionVo());
                cargarContrato();
                cargarVO();
                setNuevaEvaluacion(false);
                cargarListas();
                PrimeFaces.current().executeScript(";abrirDialogoModal(dialogoEval);");
            }

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void borrarEvaluacion(int idConv, int idConvTem) {
        try {
            if (idConv > 0 && idConvTem > 0) {
                setContratoId(idConv);
                setContratoEvaId(idConvTem);
                eliminarEvaluacion();
                contratoBean.actualizarEvaluaciones();
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void soliciatarEvaluacion(int idConv, int idConvTem) {
        try {
            if (idConv > 0 && idConvTem > 0) {
                setContratoId(idConv);
                setContratoEvaId(idConvTem);
                cargarContrato();
                cargarVO();
                cargarConEva();
                if (solicitarEvaluacion()) {
                    notificacionEvaluacionImpl.notificacionEvaluacion(
                            getConEva().getResponsable().getEmail(),
                            "",
                            "",
                            "Solicitud de evaluación de proveedor del contrato " + getConEva().getConvenio().getCodigo(),
                            getConEva());
                    contratoBean.actualizarEvaluacionesPendientes();
                    FacesUtils.addInfoMessage("Se solicito la evaluación correctamente. ");
                } else {
                    FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void guardarEvaluacion() {
        boolean guardar = false;
        try {
            CvConvenioEvaluacion nuevaCV = null;
            if (this.isNuevaEvaluacion()) {
                guardar = true;
                nuevaCV = new CvConvenioEvaluacion();

                nuevaCV.setResponsable(usuarioImpl.find(this.getVo().getUsuario()));
                nuevaCV.setGerencia(new Gerencia(this.getVo().getIdGerencia()));
                nuevaCV.setCvEvaluacionTemplate(new CvEvaluacionTemplate(this.getVo().getIdEvaTemp()));
                nuevaCV.setConvenio(new Convenio(this.getContratoVO().getId()));

                nuevaCV.setEliminado(Constantes.BOOLEAN_FALSE);
                nuevaCV.setGenero(usuarioImpl.find(sesion.getUsuarioSesion().getId()));
                nuevaCV.setFechaGenero(new Date());
                nuevaCV.setHoraGenero(new Date());
                cvConvenioEvaluacionImpl.create(nuevaCV);
            } else {
                nuevaCV = cvConvenioEvaluacionImpl.find(this.getVo().getId());

                if (!nuevaCV.getResponsable().equals(this.getVo().getUsuario())) {
                    nuevaCV.setResponsable(usuarioImpl.find(this.getVo().getUsuario()));
                    guardar = true;
                }

                if (nuevaCV.getGerencia().getId() != this.getVo().getIdGerencia()) {
                    nuevaCV.setGerencia(new Gerencia(this.getVo().getIdGerencia()));
                    guardar = true;
                }

                if (nuevaCV.getCvEvaluacionTemplate().getId() != this.getVo().getIdEvaTemp()) {
                    nuevaCV.setCvEvaluacionTemplate(new CvEvaluacionTemplate(this.getVo().getIdEvaTemp()));
                    guardar = true;
                }

                if (guardar) {
                    nuevaCV.setModifico(usuarioImpl.find(sesion.getUsuarioSesion().getId()));
                    nuevaCV.setFechaModifico(new Date());
                    nuevaCV.setHoraModifico(new Date());
                    cvConvenioEvaluacionImpl.edit(nuevaCV);
                }
            }
            if (guardar) {
                contratoBean.actualizarEvaluaciones();
            }
            PrimeFaces.current().executeScript(";cerrarDialogoModal(dialogoEval);");
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtils.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public boolean eliminarEvaluacion() {
        boolean guardar = false;
        try {
            CvConvenioEvaluacion nuevaCV = cvConvenioEvaluacionImpl.find(this.getContratoEvaId());
            nuevaCV.setEliminado(true);
            guardar = true;
            if (guardar) {
                nuevaCV.setModifico(usuarioImpl.find(sesion.getUsuarioSesion().getId()));
                nuevaCV.setFechaModifico(new Date());
                nuevaCV.setHoraModifico(new Date());
                cvConvenioEvaluacionImpl.edit(nuevaCV);
            }

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            guardar = false;
        }
        return guardar;
    }

    public boolean solicitarEvaluacion() {
        boolean guardar = false;
        try {
            CvEvaluacion nuevaCV = null;
            guardar = true;
            nuevaCV = new CvEvaluacion();

            nuevaCV.setCvEvaluacionTemplate(new CvEvaluacionTemplate(this.getVo().getIdEvaTemp()));
            nuevaCV.setProveedor(new Proveedor(this.getContratoVO().getProveedor()));
            nuevaCV.setGerencia(new Gerencia(this.getVo().getIdGerencia()));
            nuevaCV.setConvenio(new Convenio(this.getContratoVO().getId()));
            if (this.getContratoVO().getProveedorVo() != null
                    && this.getContratoVO().getProveedorVo().getContactos() != null
                    && this.getContratoVO().getProveedorVo().getContactos().size() > 0) {
                nuevaCV.setNombreProveedor(this.getContratoVO().getProveedorVo().getNombre());
                nuevaCV.setCorreo(this.getContratoVO().getProveedorVo().getContactos().get(0).getCorreo());
            }
            nuevaCV.setContestada(false);
            nuevaCV.setResponsable(usuarioImpl.find(this.getVo().getUsuario()));
            nuevaCV.setNombreGerencia(this.getVo().getNombreGerencia());
            nuevaCV.setEliminado(Constantes.BOOLEAN_FALSE);
            nuevaCV.setGenero(usuarioImpl.find(sesion.getUsuarioSesion().getId()));
            nuevaCV.setFechaGenero(new Date());
            nuevaCV.setHoraGenero(new Date());
            cvEvaluacionImpl.create(nuevaCV);

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e.getMessage());
            guardar = false;
        }
        return guardar;
    }

    /**
     * @return the contratoId
     */
    public int getContratoId() {
        return contratoId;
    }

    /**
     * @param contratoId the contratoId to set
     */
    public void setContratoId(int contratoId) {
        this.contratoId = contratoId;
    }

    /**
     * @return the vo
     */
    public ContratoEvaluacionVo getVo() {
        return vo;
    }

    /**
     * @param vo the vo to set
     */
    public void setVo(ContratoEvaluacionVo vo) {
        this.vo = vo;
    }

    /**
     * @return the nuevaEvaluacion
     */
    public boolean isNuevaEvaluacion() {
        return nuevaEvaluacion;
    }

    /**
     * @param nuevaEvaluacion the nuevaEvaluacion to set
     */
    public void setNuevaEvaluacion(boolean nuevaEvaluacion) {
        this.nuevaEvaluacion = nuevaEvaluacion;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

    /**
     * @return the gerencias
     */
    public List<GerenciaVo> getGerencias() {
        return gerencias;
    }

    /**
     * @param gerencias the gerencias to set
     */
    public void setGerencias(List<GerenciaVo> gerencias) {
        this.gerencias = gerencias;
    }

    /**
     * @return the geranciaId
     */
    public int getGeranciaId() {
        return geranciaId;
    }

    /**
     * @param geranciaId the geranciaId to set
     */
    public void setGeranciaId(int geranciaId) {
        this.geranciaId = geranciaId;
    }

    /**
     * @return the usuarios
     */
    public List<UsuarioRolVo> getUsuarios() {
        return usuarios;
    }

    /**
     * @param usuarios the usuarios to set
     */
    public void setUsuarios(List<UsuarioRolVo> usuarios) {
        this.usuarios = usuarios;
    }

    /**
     * @return the contratoVO
     */
    public ContratoVO getContratoVO() {
        return contratoVO;
    }

    /**
     * @param contratoVO the contratoVO to set
     */
    public void setContratoVO(ContratoVO contratoVO) {
        this.contratoVO = contratoVO;
    }

    /**
     * @return the templates
     */
    public List<EvaluacionTemplateVo> getTemplates() {
        return templates;
    }

    /**
     * @param templates the templates to set
     */
    public void setTemplates(List<EvaluacionTemplateVo> templates) {
        this.templates = templates;
    }

    /**
     * @return the contratoEvaId
     */
    public int getContratoEvaId() {
        return contratoEvaId;
    }

    /**
     * @param contratoEvaId the contratoEvaId to set
     */
    public void setContratoEvaId(int contratoEvaId) {
        this.contratoEvaId = contratoEvaId;
    }

    /**
     * @return the conEva
     */
    public CvConvenioEvaluacion getConEva() {
        return conEva;
    }

    /**
     * @param conEva the conEva to set
     */
    public void setConEva(CvConvenioEvaluacion conEva) {
        this.conEva = conEva;
    }

    public void cargarConEva() {
        this.setConEva(cvConvenioEvaluacionImpl.find(this.getContratoEvaId()));
    }

}
