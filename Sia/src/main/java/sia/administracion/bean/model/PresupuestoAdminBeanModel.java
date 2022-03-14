/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.bean.model;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import sia.archivador.AlmacenDocumentos;
import sia.archivador.DocumentoAnexo;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.constantes.Constantes;
import sia.modelo.ApCampo;
import sia.modelo.Compania;
import sia.modelo.Moneda;
import sia.modelo.OcActividadPetrolera;
import sia.modelo.OcCodigoSubtarea;
import sia.modelo.OcCodigoTarea;
import sia.modelo.OcNombreTarea;
import sia.modelo.OcPresupuesto;
import sia.modelo.OcPresupuestoDetalle;
import sia.modelo.OcSubTarea;
import sia.modelo.OcTarea;
import sia.modelo.OcUnidadCosto;
import sia.modelo.ProyectoOt;
import sia.modelo.SiAdjunto;
import sia.modelo.Usuario;
import sia.modelo.presupuesto.vo.MontosPresupuestoVO;
import sia.modelo.presupuesto.vo.PresupuestoDetVO;
import sia.modelo.presupuesto.vo.PresupuestoVO;
import sia.modelo.proyectoOT.vo.ProyectoOtVo;
import sia.modelo.requisicion.vo.OcActividadVO;
import sia.modelo.requisicion.vo.OcSubtareaVO;
import sia.modelo.requisicion.vo.OcTareaVo;
import sia.servicios.catalogos.impl.MonedaImpl;
import sia.servicios.catalogos.impl.ProyectoOtImpl;
import sia.servicios.orden.impl.OcUnidadCostoImpl;
import sia.servicios.requisicion.impl.OcActividadPetroleraImpl;
import sia.servicios.requisicion.impl.OcCodigoSubtareaImpl;
import sia.servicios.requisicion.impl.OcCodigoTareaImpl;
import sia.servicios.requisicion.impl.OcNombreTareaImpl;
import sia.servicios.requisicion.impl.OcPresupuestoAdjuntoImpl;
import sia.servicios.requisicion.impl.OcPresupuestoDetalleImpl;
import sia.servicios.requisicion.impl.OcPresupuestoImpl;
import sia.servicios.requisicion.impl.OcPresupuestoMovimientosImpl;
import sia.servicios.requisicion.impl.OcSubTareaImpl;
import sia.servicios.requisicion.impl.OcTareaImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.sistema.bean.backing.Sesion;
import sia.util.LecturaLibro;
import sia.util.UtilLog4j;
import sia.util.ValidadorNombreArchivo;

/**
 *
 * @author jcarranza
 */
@ManagedBean
@ViewScoped
public class PresupuestoAdminBeanModel {

    private static final UtilLog4j LOGGER = UtilLog4j.log;

    @ManagedProperty(value = "#{sesion}")
    private Sesion sesion;
    @EJB
    private OcPresupuestoImpl ocPresupuestoImpl;
    @EJB
    private OcPresupuestoDetalleImpl ocPresupuestoDetalleImpl;
    @EJB
    private SiParametroImpl parametrosSistemaServicioRemoto;
    @EJB
    private OcTareaImpl ocTareaImpl;
    @EJB
    private OcCodigoTareaImpl ocCodigoTareaLocal;
    @EJB
    private OcNombreTareaImpl ocNombreTareaImpl;
    @EJB
    private OcSubTareaImpl ocSubTareaImpl;
    @EJB
    private OcActividadPetroleraImpl ocActividadPetroleraImpl;
    @EJB
    private OcUnidadCostoImpl ocUnidadCostoImpl;
    @EJB
    private OcCodigoSubtareaImpl ocCodigoSubtareaImpl;
    @EJB
    private ProyectoOtImpl proyectoOtImpl;
    @EJB
    private MonedaImpl monedaImpl;
    @EJB
    private OcPresupuestoAdjuntoImpl ocPresupuestoAdjuntoImpl;
    @EJB
    private OcPresupuestoMovimientosImpl ocPresupuestoMovimientosImpl;
    @EJB
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;
    @EJB
    private SiAdjuntoImpl siAdjuntoImpl;

    private int idCampo;
    private List<PresupuestoVO> presupuestos;
    private List<PresupuestoDetVO> detalle;

    private int idPres;
    private PresupuestoVO presVO;
    private OcPresupuesto presObj;
    private OcPresupuestoDetalle presDetObj;

    private List<SelectItem> anios = new ArrayList<>();
    private List<SelectItem> meses = new ArrayList<>();

    private int anio;
    private int mes;

    private List<SelectItem> lstUnidadCosto = new ArrayList<>();
    private List<SelectItem> lstActividad = new ArrayList<>();
    private List<OcTareaVo> lstTarea = new ArrayList<>();
    private List<SelectItem> lstSubTarea = new ArrayList<>();
    private List<SelectItem> lstTipos = new ArrayList<>();
    private List<SelectItem> lstOldProyectosOTs = new ArrayList<>();
    private List<SelectItem> lstNewProyectosOTs = new ArrayList<>();
    private int tipo;
    private int idNewProy;
    private int idActPetro;
    private OcActividadVO partidaProy;

//    private HashMap<String, OcActividadVO> nuevasActs = new HashMap<>();
    private PresupuestoDetVO partidaModificar;

    private PresupuestoDetVO partidaDisplay;

    private List<PresupuestoDetVO> lstPartidas = new ArrayList<>();

    private List<SelectItem> monedas = new ArrayList<>();

    private List<OcCodigoTarea> nuevosCodigosTarea = new ArrayList<>();
    private List<OcNombreTarea> nuevosNombresTarea = new ArrayList<>();
    private List<ProyectoOt> nuevosProyectosOts = new ArrayList<>();
    private List<OcCodigoSubtarea> nuevosSubTareas = new ArrayList<>();
    private List<OcUnidadCosto> nuevosSubActividadP = new ArrayList<>();
    private List<OcActividadPetrolera> nuevosActividadP = new ArrayList<>();

    public PresupuestoAdminBeanModel() {

    }

    public void inicia() {
        setIdCampo(getSesion().getUsuario().getApCampo().getId());
        cargarPresupuestos();
    }

    public void cargarMonedas() {
        setIdCampo(getSesion().getUsuario().getApCampo().getId());
        setMonedas(monedaImpl.traerMonedasPorCompaniaItems(getSesion().getUsuario().getApCampo().getCompania().getRfc(), null));
    }

    public void cargarPresupuestos() {
        setPresupuestos(ocPresupuestoImpl.getPresupuestos(getIdCampo(), true));
    }

    /**
     * @return the presupuestos
     */
    public List<PresupuestoVO> getPresupuestos() {
        return presupuestos;
    }

    /**
     * @param presupuestos the presupuestos to set
     */
    public void setPresupuestos(List<PresupuestoVO> presupuestos) {
        this.presupuestos = presupuestos;
    }

    /**
     * @return the idCampo
     */
    public int getIdCampo() {
        return idCampo;
    }

    /**
     * @param idCampo the idCampo to set
     */
    public void setIdCampo(int idCampo) {
        this.idCampo = idCampo;
    }

    /**
     * @return the presVO
     */
    public PresupuestoVO getPresVO() {
        return presVO;
    }

    /**
     * @param presVO the presVO to set
     */
    public void setPresVO(PresupuestoVO presVO) {
        this.presVO = presVO;
    }

    /**
     * @return the presObj
     */
    public OcPresupuesto getPresObj() {
        return presObj;
    }

    /**
     * @param presObj the presObj to set
     */
    public void setPresObj(OcPresupuesto presObj) {
        this.presObj = presObj;
    }

    /**
     * @return the idPres
     */
    public int getIdPres() {
        return idPres;
    }

    /**
     * @param idPres the idPres to set
     */
    public void setIdPres(int idPres) {
        this.idPres = idPres;
    }

    /**
     * @return the sesion
     */
    public Sesion getSesion() {
        return sesion;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

    /**
     * @return the detalle
     */
    public List<PresupuestoDetVO> getDetalle() {
        return detalle;
    }

    /**
     * @param detalle the detalle to set
     */
    public void setDetalle(List<PresupuestoDetVO> detalle) {
        this.detalle = detalle;
    }

    public void refrescarProyectosOTs() {
        setIdNewProy(0);
        setLstOldProyectosOTs(ocPresupuestoDetalleImpl.getProyectoOtItems(
                getIdPres(),
                getIdActPetro(),
                getIdCampo(),
                getAnio(),
                getMes(),
                false));
        setLstNewProyectosOTs(ocPresupuestoDetalleImpl.getProyectoOtItems(
                getIdPres(),
                getIdActPetro(),
                getIdCampo(),
                getAnio(),
                getMes(),
                true));

    }

    public void refrescarSubact() {
        setLstUnidadCosto(ocPresupuestoDetalleImpl.getUnidadCostosItems(
                getPartidaModificar().getIdPresupuesto(),
                getPartidaModificar().getActPetroleraId(),
                getIdCampo(),
                0,
                getPartidaModificar().getAnio(),
                getPartidaModificar().getMes(),
                false));
        setLstTarea(new ArrayList<>());
        setLstSubTarea(new ArrayList<>());
        getPartidaModificar().setUnidadCostoId(0);
        getPartidaModificar().setTareaCodigoId(0);
        getPartidaModificar().setSubTareaCodigoId(0);
    }

    public void refrescarTarea() {
        setLstTarea(ocPresupuestoDetalleImpl.getTaresVOs(
                getPartidaModificar().getIdPresupuesto(),
                getPartidaModificar().getActPetroleraId(),
                getIdCampo(),
                0,
                getPartidaModificar().getUnidadCostoId(),
                getPartidaModificar().getAnio(),
                getPartidaModificar().getMes(),
                false));
        setLstSubTarea(new ArrayList<>());
        getPartidaModificar().setTareaCodigoId(0);
        getPartidaModificar().setSubTareaCodigoId(0);
    }

    public void refrescarSubtarea() {
        setLstSubTarea(ocPresupuestoDetalleImpl.getSubTareasItems(
                getPartidaModificar().getIdPresupuesto(),
                getPartidaModificar().getActPetroleraId(),
                getIdCampo(),
                0,
                getPartidaModificar().getUnidadCostoId(),
                getPartidaModificar().getTareaCodigoId(),
                getPartidaModificar().getAnio(),
                getPartidaModificar().getMes(),
                false));
        getPartidaModificar().setSubTareaCodigoId(0);
    }

    public void refrescarTipos() {
        setLstTipos(new ArrayList<>());
        SelectItem item1 = new SelectItem(1, "Mano de Obra Contenido Nacional");
        getLstTipos().add(item1);
        SelectItem item2 = new SelectItem(2, "Mano de Obra Extranjero");
        getLstTipos().add(item2);
        SelectItem item3 = new SelectItem(3, "Bienes Contenido Nacional");
        getLstTipos().add(item3);
        SelectItem item4 = new SelectItem(4, "Bienes Extranjero");
        getLstTipos().add(item4);
        SelectItem item5 = new SelectItem(5, "Servicios Contenido Nacional");
        getLstTipos().add(item5);
        SelectItem item6 = new SelectItem(6, "Servicios Extranjero");
        getLstTipos().add(item6);
        SelectItem item7 = new SelectItem(7, "Capacitación Contenido Nacional");
        getLstTipos().add(item7);
        SelectItem item8 = new SelectItem(8, "Capacitación Extranjero");
        getLstTipos().add(item8);
        SelectItem item9 = new SelectItem(9, "Transferencia de Tecnología");
        getLstTipos().add(item9);
        SelectItem item10 = new SelectItem(10, "Infraestructura (Social)");
        getLstTipos().add(item10);
        setTipo(0);
        getPartidaModificar().setMontoActual(BigDecimal.ZERO);
        getPartidaModificar().setMontoNuevo(BigDecimal.ZERO);
    }

    public void refrescarMontos() {
        setPartidaModificar(ocPresupuestoDetalleImpl.llenarPresupuestoDet(getPartidaModificar()));
        getPartidaModificar().setMontoNuevo(BigDecimal.ZERO);
        switch (getTipo()) {
            case 1:
                getPartidaModificar().setMontoActual(getPartidaModificar().getManoObraCn());
                break;
            case 2:
                getPartidaModificar().setMontoActual(getPartidaModificar().getManoObraEx());
                break;
            case 3:
                getPartidaModificar().setMontoActual(getPartidaModificar().getBienasCn());
                break;
            case 4:
                getPartidaModificar().setMontoActual(getPartidaModificar().getBienesEx());
                break;
            case 5:
                getPartidaModificar().setMontoActual(getPartidaModificar().getServiciosCn());
                break;
            case 6:
                getPartidaModificar().setMontoActual(getPartidaModificar().getServiciosEx());
                break;
            case 7:
                getPartidaModificar().setMontoActual(getPartidaModificar().getCapacitacionCn());
                break;
            case 8:
                getPartidaModificar().setMontoActual(getPartidaModificar().getCapacitacionEx());
                break;
            case 9:
                getPartidaModificar().setMontoActual(getPartidaModificar().getTransferenciaTec());
                break;
            case 10:
                getPartidaModificar().setMontoActual(getPartidaModificar().getInfraestructura());
                break;

            default:
                getPartidaModificar().setMontoActual(BigDecimal.ZERO);
        }
    }

    public void cambiarAnio() {
        if (getAnio() > 0) {
            setMeses(ocPresupuestoDetalleImpl.getMesesItems(getIdPres(), getAnio(), false));
            if (getMeses().size() > 0) {
                setMes((int) getMeses().get(0).getValue());
            }
        }
    }

    public void cargarPresupuestoDet() {
        if (getMes() > 0 && getAnio() > 0) {
            setDetalle(ocPresupuestoDetalleImpl.getPresupuestoDet(getIdPres(), getIdCampo(), getAnio(), getMes(), true, true, false, true));
        }
    }

    public void iniciarPresupuesto() {
        setPresVO(ocPresupuestoImpl.getPresupuesto(getIdPres(), false));
        setAnios(ocPresupuestoDetalleImpl.getAniosItems(getIdPres(), false));
        if (getAnios() != null && getAnios().size() > 0) {
            setAnio((int) getAnios().get(0).getValue());
        }
        setMeses(ocPresupuestoDetalleImpl.getMesesItems(getIdPres(), getAnio(), false));
        if (getMeses() != null && getMeses().size() > 0) {
            setMes((int) getMeses().get(0).getValue());
        }
        cargarPresupuestoDet();
    }

    public void iniciarImportar() {
        setPresVO(ocPresupuestoImpl.getPresupuesto(getIdPres(), false));
    }

    public void borrarPresupuesto() {
        setPresObj(ocPresupuestoImpl.find(this.getIdPres()));
        if (getPresObj() != null && getPresObj().getId() > 0) {
            getPresObj().setModifico(getSesion().getUsuario());
            getPresObj().setFechaModifico(new Date());
            getPresObj().setHoraModifico(new Date());
            getPresObj().setEliminado(true);
            ocPresupuestoImpl.edit(getPresObj());
            ocPresupuestoMovimientosImpl.inactivar(this.getIdPres(), getSesion().getUsuario().getId());
        }
    }

    public void cargarOTspartidas() {
        setLstNewProyectosOTs(ocPresupuestoDetalleImpl.getProyectoOtItems(
                getIdPres(),
                0,
                getIdCampo(),
                0,
                0,
                false));
    }

    private File creatTempFile(OcPresupuesto presupuesto) {
        File fileTempExcel = null;
        try {
            String REPOSITORYPATH = this.parametrosSistemaServicioRemoto.find(1).getUploadDirectory();
            String PLANTILLAPATH = "Plantillas/ExcelNAV";
            String URL_Temporal = new StringBuilder().append(REPOSITORYPATH).append(PLANTILLAPATH).append(File.separator).toString();
            fileTempExcel = File.createTempFile("excelTemporal", ".xlsx", new File(URL_Temporal));
            if (presupuesto != null && presupuesto.getId() > 0) {
                fileTempExcel = ocPresupuestoImpl.generarExcel(presupuesto, fileTempExcel);
            }
        } catch (Exception e) {
            LOGGER.error(Level.SEVERE, null, e);
        }
        return fileTempExcel;
    }

    public void limpiarPresupuesto() {
        setPresVO(null);
        setAnios(new ArrayList<>());
        setAnio(0);
        setMeses(new ArrayList<>());
        setMes(0);
        setDetalle(new ArrayList<>());
        setLstPartidas(new ArrayList<>());
//        setNuevasActs(new HashMap<>());
        setLstNewProyectosOTs(new ArrayList<>());
    }

    /**
     * @return the anios
     */
    public List<SelectItem> getAnios() {
        return anios;
    }

    /**
     * @param anios the anios to set
     */
    public void setAnios(List<SelectItem> anios) {
        this.anios = anios;
    }

    /**
     * @return the meses
     */
    public List<SelectItem> getMeses() {
        return meses;
    }

    /**
     * @param meses the meses to set
     */
    public void setMeses(List<SelectItem> meses) {
        this.meses = meses;
    }

    /**
     * @return the anio
     */
    public int getAnio() {
        return anio;
    }

    /**
     * @param anio the anio to set
     */
    public void setAnio(int anio) {
        this.anio = anio;
    }

    /**
     * @return the mes
     */
    public int getMes() {
        return mes;
    }

    /**
     * @param mes the mes to set
     */
    public void setMes(int mes) {
        this.mes = mes;
    }

    /**
     * @return the lstUnidadCosto
     */
    public List<SelectItem> getLstUnidadCosto() {
        return lstUnidadCosto;
    }

    /**
     * @param lstUnidadCosto the lstUnidadCosto to set
     */
    public void setLstUnidadCosto(List<SelectItem> lstUnidadCosto) {
        this.lstUnidadCosto = lstUnidadCosto;
    }

    /**
     * @return the lstActividad
     */
    public List<SelectItem> getLstActividad() {
        return lstActividad;
    }

    /**
     * @param lstActividad the lstActividad to set
     */
    public void setLstActividad(List<SelectItem> lstActividad) {
        this.lstActividad = lstActividad;
    }

    /**
     * @return the lstTarea
     */
    public List<OcTareaVo> getLstTarea() {
        return lstTarea;
    }

    /**
     * @param lstTarea the lstTarea to set
     */
    public void setLstTarea(List<OcTareaVo> lstTarea) {
        this.lstTarea = lstTarea;
    }

    /**
     * @return the lstSubTarea
     */
    public List<SelectItem> getLstSubTarea() {
        return lstSubTarea;
    }

    /**
     * @param lstSubTarea the lstSubTarea to set
     */
    public void setLstSubTarea(List<SelectItem> lstSubTarea) {
        this.lstSubTarea = lstSubTarea;
    }

    /**
     * @return the partidaModificar
     */
    public PresupuestoDetVO getPartidaModificar() {
        return partidaModificar;
    }

    /**
     * @param partidaModificar the partidaModificar to set
     */
    public void setPartidaModificar(PresupuestoDetVO partidaModificar) {
        this.partidaModificar = partidaModificar;
    }

    public void guardarPartida() {
        if (getPartidaModificar().getId() > 0) {
            setPresDetObj(ocPresupuestoDetalleImpl.find(getPartidaModificar().getId()));
            getPresDetObj().setModifico(getSesion().getUsuario());
            getPresDetObj().setFechaModifico(new Date());
            getPresDetObj().setHoraModifico(new Date());
            switch (getTipo()) {
                case 1:
                    getPresDetObj().setManoObraCN(getPartidaModificar().getMontoNuevo().doubleValue());
                    break;
                case 2:
                    getPresDetObj().setManoObraEX(getPartidaModificar().getMontoNuevo().doubleValue());
                    break;
                case 3:
                    getPresDetObj().setBienesCN(getPartidaModificar().getMontoNuevo().doubleValue());
                    break;
                case 4:
                    getPresDetObj().setBienesEX(getPartidaModificar().getMontoNuevo().doubleValue());
                    break;
                case 5:
                    getPresDetObj().setServiciosCN(getPartidaModificar().getMontoNuevo().doubleValue());
                    break;
                case 6:
                    getPresDetObj().setServiciosEX(getPartidaModificar().getMontoNuevo().doubleValue());
                    break;
                case 7:
                    getPresDetObj().setCapacitacionCN(getPartidaModificar().getMontoNuevo().doubleValue());
                    break;
                case 8:
                    getPresDetObj().setCapacitacionEX(getPartidaModificar().getMontoNuevo().doubleValue());
                    break;
                case 9:
                    getPresDetObj().setTransTecnologia(getPartidaModificar().getMontoNuevo().doubleValue());
                    break;
                case 10:
                    getPresDetObj().setInfraestructura(getPartidaModificar().getMontoNuevo().doubleValue());
                    break;
            }

            ocPresupuestoDetalleImpl.edit(getPresDetObj());
            ocPresupuestoMovimientosImpl.modificarMonto(this.getIdPres(), getPartidaModificar().getId(), getSesion().getUsuario().getId());
        }
    }

    public void guardarProyOT() {
        if (getIdNewProy() > 0) {
            OcTarea t = null;
            for (OcTareaVo vo : ocPresupuestoDetalleImpl.getTareasNuevaOT(this.getIdPres(), this.getIdActPetro(), this.getIdCampo())) {
                t = new OcTarea();

                t.setOcActividadPetrolera(new OcActividadPetrolera(this.getIdActPetro()));
                t.setOcCodigoTarea(new OcCodigoTarea(vo.getIdcodigoTarea()));
                t.setOcNombreTarea(new OcNombreTarea(vo.getIdNombreTarea()));
                t.setOcUnidadCosto(new OcUnidadCosto(vo.getIdUnidadCosto()));
                t.setProyectoOt(new ProyectoOt(this.getIdNewProy()));

                t.setEliminado(false);
                t.setGenero(getSesion().getUsuario());
                t.setFechaGenero(new Date());
                t.setHoraGenero(new Date());

                ocTareaImpl.create(t);

                guardarSubtarea(t, vo, this.getIdPres());
            }
        }
    }

    public void guardarNuevosActP() {
        try {
            for (OcActividadPetrolera nuevo : this.getNuevosActividadP()) {
                nuevo.setEliminado(false);
                nuevo.setGenero(getSesion().getUsuario());
                nuevo.setFechaGenero(new Date());
                nuevo.setHoraGenero(new Date());
                ocActividadPetroleraImpl.create(nuevo);
            }
            this.setNuevosActividadP(new ArrayList<>());
        } catch (Exception e) {
            LOGGER.error(Level.SEVERE, null, e);
        }
    }

    public void guardarNuevosCodigosTarea() {
        try {
            for (OcCodigoTarea nuevo : this.getNuevosCodigosTarea()) {

                nuevo.setEliminado(false);
                nuevo.setGenero(getSesion().getUsuario());
                nuevo.setFechaGenero(new Date());
                nuevo.setHoraGenero(new Date());

                ocCodigoTareaLocal.create(nuevo);
            }
            this.setNuevosCodigosTarea(new ArrayList<>());
        } catch (Exception e) {
            LOGGER.error(Level.SEVERE, null, e);
        }
    }

    public void guardarNuevosNombreTarea() {
        try {
            for (OcNombreTarea nuevo : this.getNuevosNombresTarea()) {

                nuevo.setEliminado(false);
                nuevo.setGenero(getSesion().getUsuario());
                nuevo.setFechaGenero(new Date());
                nuevo.setHoraGenero(new Date());

                ocNombreTareaImpl.create(nuevo);
            }
            this.setNuevosNombresTarea(new ArrayList<>());
        } catch (Exception e) {
            LOGGER.error(Level.SEVERE, null, e);
        }
    }

    public void guardarNuevosProyectosOt() {
        try {
            for (ProyectoOt nuevo : this.getNuevosProyectosOts()) {

                nuevo.setAbierto(true);
                nuevo.setVisible(true);
                nuevo.setCompania(new Compania(getSesion().getUsuario().getApCampo().getCompania().getRfc()));
                nuevo.setApCampo(new ApCampo(getIdCampo()));

                nuevo.setEliminado(false);
                nuevo.setGenero(getSesion().getUsuario());
                nuevo.setFechaGenero(new Date());
                nuevo.setHoraGenero(new Date());

                proyectoOtImpl.create(nuevo);
            }
            this.setNuevosProyectosOts(new ArrayList<>());
        } catch (Exception e) {
            LOGGER.error(Level.SEVERE, null, e);
        }
    }

    public void guardarNuevosSubActivP() {
        try {
            for (OcUnidadCosto nuevo : this.getNuevosSubActividadP()) {

                nuevo.setEliminado(false);
                nuevo.setGenero(getSesion().getUsuario());
                nuevo.setFechaGenero(new Date());
                nuevo.setHoraGenero(new Date());

                ocUnidadCostoImpl.create(nuevo);
            }
            this.setNuevosSubActividadP(new ArrayList<>());
        } catch (Exception e) {
            LOGGER.error(Level.SEVERE, null, e);
        }
    }

    public void guardarNuevosSubTarea() {
        try {
            for (OcCodigoSubtarea nuevo : this.getNuevosSubTareas()) {

                nuevo.setEliminado(false);
                nuevo.setGenero(getSesion().getUsuario());
                nuevo.setFechaGenero(new Date());
                nuevo.setHoraGenero(new Date());

                ocCodigoSubtareaImpl.create(nuevo);
            }
            this.setNuevosSubTareas(new ArrayList<>());
        } catch (Exception e) {
            LOGGER.error(Level.SEVERE, null, e);
        }
    }

    public void guardarPresupuesto() {
        if (getPresVO() != null) {
            OcPresupuesto p = new OcPresupuesto();
            p.setNombre(getPresVO().getNombre());
            p.setCodigo(getPresVO().getCodigo());
            p.setMoneda(new Moneda(getPresVO().getIdMoneda()));
            p.setApCampo(new ApCampo(getSesion().getUsuario().getApCampo().getId()));
            p.setCompania(new Compania(getSesion().getUsuario().getApCampo().getCompania().getRfc()));
            p.setEliminado(false);
            p.setGenero(getSesion().getUsuario());
            p.setFechaGenero(new Date());
            p.setHoraGenero(new Date());
            ocPresupuestoImpl.create(p);
        }
    }

    public void agregarProyOTPartida() {
        if (getPartidaProy().getIdNuevoProyecto() > 0) {
            for (int i = 0; i < this.getLstPartidas().size(); i++) {
                if (this.getLstPartidas().get(i).getActPetroleraId() == getPartidaProy().getId()) {
                    this.getLstPartidas().get(i).getNewProys().getProyectos().add(this.proyectoOtImpl.getProyectoOtVO(getPartidaProy().getIdNuevoProyecto()));
                }
            }
        }
    }

    private void guardarSubtarea(OcTarea t, OcTareaVo tarea, int idPresupuesto) {
        OcSubTarea s = null;
        for (OcSubtareaVO vo : ocPresupuestoDetalleImpl.getSubTareasNuevaOT(this.getIdPres(), this.getIdCampo(), tarea)) {
            s = new OcSubTarea();
            s.setOcTarea(new OcTarea(t.getId()));
            s.setOcCodigoSubtarea(new OcCodigoSubtarea(vo.getIdCodigoSubtarea()));
            s.setEliminado(false);
            s.setGenero(getSesion().getUsuario());
            s.setFechaGenero(new Date());
            s.setHoraGenero(new Date());

            ocSubTareaImpl.create(s);
        }

    }

    public void cargarPartida() {
        this.setPartidaModificar(new PresupuestoDetVO());
        this.getPartidaModificar().setIdPresupuesto(this.getIdPres());
        this.getPartidaModificar().setAnio(this.getAnio());
        this.getPartidaModificar().setMes(this.getMes());
        this.getPartidaModificar().setMontoNuevo(BigDecimal.ZERO);
        this.getPartidaModificar().setMontoActual(BigDecimal.ZERO);
        this.setLstActividad(ocPresupuestoDetalleImpl.getActividadesItems(this.getIdPres(), this.getAnio(), this.getMes(), true));
    }

    public void iniciarProyectosOTs() {
        this.setLstNewProyectosOTs(new ArrayList<>());
        this.setLstOldProyectosOTs(new ArrayList<>());
        this.setIdActPetro(0);
        this.setIdNewProy(0);
        this.setLstActividad(ocPresupuestoDetalleImpl.getActividadesItems(this.getIdPres(), this.getAnio(), this.getMes(), true));
    }

    public void iniciarAltaPartida() {
        this.setLstPartidas(new ArrayList<>());
    }

    /**
     * @return the lstTipos
     */
    public List<SelectItem> getLstTipos() {
        return lstTipos;
    }

    /**
     * @param lstTipos the lstTipos to set
     */
    public void setLstTipos(List<SelectItem> lstTipos) {
        this.lstTipos = lstTipos;
    }

    /**
     * @return the tipo
     */
    public int getTipo() {
        return tipo;
    }

    /**
     * @param tipo the tipo to set
     */
    public void setTipo(int tipo) {
        this.tipo = tipo;
    }

    /**
     * @return the presDetObj
     */
    public OcPresupuestoDetalle getPresDetObj() {
        return presDetObj;
    }

    /**
     * @param presDetObj the presDetObj to set
     */
    public void setPresDetObj(OcPresupuestoDetalle presDetObj) {
        this.presDetObj = presDetObj;
    }

    /**
     * @return the lstOldProyectosOTs
     */
    public List<SelectItem> getLstOldProyectosOTs() {
        return lstOldProyectosOTs;
    }

    /**
     * @param lstOldProyectosOTs the lstOldProyectosOTs to set
     */
    public void setLstOldProyectosOTs(List<SelectItem> lstOldProyectosOTs) {
        this.lstOldProyectosOTs = lstOldProyectosOTs;
    }

    /**
     * @return the lstNewProyectosOTs
     */
    public List<SelectItem> getLstNewProyectosOTs() {
        return lstNewProyectosOTs;
    }

    /**
     * @param lstNewProyectosOTs the lstNewProyectosOTs to set
     */
    public void setLstNewProyectosOTs(List<SelectItem> lstNewProyectosOTs) {
        this.lstNewProyectosOTs = lstNewProyectosOTs;
    }

    /**
     * @return the idNewProy
     */
    public int getIdNewProy() {
        return idNewProy;
    }

    /**
     * @param idNewProy the idNewProy to set
     */
    public void setIdNewProy(int idNewProy) {
        this.idNewProy = idNewProy;
    }

    /**
     * @return the idActPetro
     */
    public int getIdActPetro() {
        return idActPetro;
    }

    /**
     * @param idActPetro the idActPetro to set
     */
    public void setIdActPetro(int idActPetro) {
        this.idActPetro = idActPetro;
    }

    public void iniciarCargaPresupuesto() {
        this.setLstPartidas(new ArrayList<>());
        this.setNuevosCodigosTarea(new ArrayList<>());
        this.setNuevosNombresTarea(new ArrayList<>());
        this.setNuevosProyectosOts(new ArrayList<>());
        this.setNuevosSubActividadP(new ArrayList<>());
        this.setNuevosSubTareas(new ArrayList<>());
        this.setNuevosActividadP(new ArrayList<>());
    }

    public String uploadDirectoryPresupuesto() {
        return new StringBuilder().append("Presupuesto/").append(this.getIdPres()).toString();
    }

    public void guardarArchivoPresupuesto(FileEntryResults.FileInfo fileInfo) {
        try {
            ValidadorNombreArchivo validadorNombreArchivo = new ValidadorNombreArchivo();
            AlmacenDocumentos almacenDocumentos
                    = proveedorAlmacenDocumentos.getAlmacenDocumentos();

            boolean addArchivo = validadorNombreArchivo.isNombreValido(fileInfo.getFileName());

            if (addArchivo) {
                DocumentoAnexo documentoAnexo = new DocumentoAnexo(fileInfo.getFile());
                documentoAnexo.setRuta(uploadDirectoryPresupuesto());
                documentoAnexo.setNombreBase(fileInfo.getFileName());
                almacenDocumentos.guardarDocumento(documentoAnexo);

                SiAdjunto adj = siAdjuntoImpl.save(documentoAnexo.getNombreBase(),
                        new StringBuilder()
                                .append(documentoAnexo.getRuta())
                                .append(File.separator).append(documentoAnexo.getNombreBase()).toString(),
                        fileInfo.getContentType(), fileInfo.getSize(), sesion.getUsuario().getId());

                ocPresupuestoAdjuntoImpl.agregarArchivoPresupuesto(getIdPres(), sesion.getUsuario().getId(), adj.getId());
            }

        } catch (Exception e) {

        }

    }

    public boolean cargarPresupuestoFile(File file) {
        iniciarCargaPresupuesto();
        boolean ret = true;
        try {
            ocPresupuestoMovimientosImpl.importarTemp(this.getIdPres(), getSesion().getUsuario().getId());
            LecturaLibro lecturaLibro = new LecturaLibro();
            XSSFWorkbook archivo = lecturaLibro.loadFileXLSX(file);
            XSSFSheet workSheet = lecturaLibro.loadSheet(archivo);
            for (int i = 5; i <= 1000; i++) {
                PresupuestoDetVO vo = readSheetData(workSheet, i);
                if (vo != null && vo.isExistePresupuesto()) {
                    if (vo.getActPetroleraCodigo() != null
                            && !vo.getActPetroleraCodigo().isEmpty()) {
                        this.getLstPartidas().add(vo);
                    }
                } else {
                    if (vo.getIdPresupuesto() > 0) {
                        ret = false;
                    }
                    break;
                }
            }
        } catch (Exception ex) {
            ret = false;
            UtilLog4j.log.info("CargarPresupuesto " + new Date());
            Logger.getLogger(PresupuestoAdminBeanModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }

    private PresupuestoDetVO readSheetData(XSSFSheet workSheet, int fila) throws Exception {
        UtilLog4j.log.info("Leyendo datos ...");
        LecturaLibro lecturaLibro = new LecturaLibro();
        PresupuestoDetVO presDet = new PresupuestoDetVO();
        try {
            leerPartida(presDet, workSheet, fila);
            if (presDet.getActPetroleraCodigo() != null
                    && !presDet.getActPetroleraCodigo().isEmpty()) {
                leerMontos(presDet, workSheet, fila);
            }
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            System.out.println("weerwer: " + e);
        }
        return presDet;
    }

    private PresupuestoDetVO leerPartida(PresupuestoDetVO vo, XSSFSheet workSheet, int fila) {

        LecturaLibro lecturaLibro = new LecturaLibro();

        vo.setIdPresupuestoTxt(lecturaLibro.getValFromReference(workSheet, "A" + fila));
        if (vo.getIdPresupuestoTxt() != null && !vo.getIdPresupuestoTxt().isEmpty()) {
            int idPresTxt = new BigDecimal(vo.getIdPresupuestoTxt()).intValue();
            vo.setExistePresupuesto(idPresTxt == this.getIdPres());
            vo.setIdPresupuesto(idPresTxt);
        }
        vo.setActPetroleraCodigo(lecturaLibro.getValFromReference(workSheet, "B" + fila));
        vo.setActPetroleraNombre(lecturaLibro.getValFromReference(workSheet, "C" + fila));
        if (vo.getActPetroleraCodigo() != null && !vo.getActPetroleraCodigo().isEmpty()
                && vo.getActPetroleraNombre() != null && !vo.getActPetroleraNombre().isEmpty()) {
            int idAct = this.ocActividadPetroleraImpl.validarActividadExiste(vo.getActPetroleraCodigo(), vo.getActPetroleraNombre());
            vo.setExisteActP(idAct > 0);
            vo.setActPetroleraId(idAct);
            if (!vo.isExisteActP()) {
                this.getNuevosActividadP().add(vo.getNuevosActividadP());

            }
//            if (vo.isExisteActP()) {
//                vo.setActPetroleraId(idAct);
//                vo.setNewProys(new OcActividadVO(idAct, vo.getActPetroleraCodigo(), vo.getActPetroleraNombre()));
//                //vo.getNewProys().setProyectos(ocPresupuestoDetalleImpl.getProyectoOtVOs(getIdPres(), idAct, getIdCampo(), 0, 0, true));
//                vo.getNewProys().setOtsRelacionadas(vo.getNewProys().getProyectos().size() > 0);
//                this.getNuevasActs().put(vo.getActPetroleraCodigo(), vo.getNewProys());
//            }
        }
        vo.setUnidadCostoCodigo(lecturaLibro.getValFromReference(workSheet, "D" + fila));
        vo.setUnidadCostoNombre(lecturaLibro.getValFromReference(workSheet, "E" + fila));
        if (vo.getUnidadCostoCodigo() != null && !vo.getUnidadCostoCodigo().isEmpty()
                && vo.getUnidadCostoNombre() != null && !vo.getUnidadCostoNombre().isEmpty()) {
            int auxUnCos = this.ocUnidadCostoImpl.validarUnidadCostoExiste(vo.getUnidadCostoCodigo(), vo.getUnidadCostoNombre());
            vo.setUnidadCostoId(auxUnCos);
            vo.setExisteUnidadC(auxUnCos > 0);
            if (!vo.isExisteUnidadC()) {
                this.getNuevosSubActividadP().add(vo.getNuevosSubActividadP());
            }
        }
        vo.setTareaCodigo(lecturaLibro.getValFromReference(workSheet, "F" + fila));
        vo.setTareaNombre(lecturaLibro.getValFromReference(workSheet, "G" + fila));
        if (vo.getTareaCodigo() != null && !vo.getTareaCodigo().isEmpty()
                && vo.getTareaNombre() != null && !vo.getTareaNombre().isEmpty()) {
            int auxCodTarea = this.ocCodigoTareaLocal.existeTareaCodigo(vo.getTareaCodigo());
            vo.setExisteTareaCod(auxCodTarea > 0);
            vo.setTareaCodigoId(auxCodTarea);
            int auxNomTarea = this.ocNombreTareaImpl.existeTareaNombre(vo.getTareaNombre());
            vo.setExisteTareaNom(auxNomTarea > 0);
            vo.setTareaNombreId(auxNomTarea);
            vo.setExisteTarea(vo.isExisteTareaCod() && vo.isExisteTareaNom());
            if (!vo.isExisteTarea()) {
                if (!vo.isExisteTareaCod()) {
                    this.getNuevosCodigosTarea().add(vo.getNuevoCodigosTarea());
                }
                if (!vo.isExisteTareaNom()) {
                    this.getNuevosNombresTarea().add(vo.getNuevosNombresTarea());
                }
            }
        }
        vo.setSubTareaCodigo(lecturaLibro.getValFromReference(workSheet, "H" + fila));
        vo.setSubTareaNombre(lecturaLibro.getValFromReference(workSheet, "I" + fila));
        if (vo.getSubTareaCodigo() != null && !vo.getSubTareaCodigo().isEmpty()
                && vo.getSubTareaNombre() != null && !vo.getSubTareaNombre().isEmpty()) {
            int auxSubTareaC = this.ocCodigoSubtareaImpl.validarSubtareaExiste(vo.getSubTareaCodigo(), vo.getSubTareaNombre());
            vo.setSubTareaCodigoId(auxSubTareaC);
            vo.setExisteSubTarea(auxSubTareaC > 0);
            if (!vo.isExisteSubTarea()) {
                this.getNuevosSubTareas().add(vo.getNuevosSubTareas());
            }
        }

        vo.setProyectoOtNombre(lecturaLibro.getValFromReference(workSheet, "J" + fila));
        vo.setProyectoOtCodigo(lecturaLibro.getValFromReference(workSheet, "K" + fila));
        if (vo.getProyectoOtCodigo() != null && !vo.getProyectoOtCodigo().isEmpty()
                && vo.getProyectoOtNombre() != null && !vo.getProyectoOtNombre().isEmpty()) {
            int auxProyOT = this.proyectoOtImpl.validarOtExiste(
                    vo.getProyectoOtCodigo(),
                    vo.getProyectoOtNombre(),
                    this.getIdCampo()
            );
            vo.setProyectoOtId(auxProyOT);
            vo.setExisteOT(auxProyOT > 0);
            if (!vo.isExisteOT()) {
                this.getNuevosProyectosOts().add(vo.getNuevosProyectosOts());
            }
        } else if (vo.getProyectoOtCodigo() != null && vo.getProyectoOtCodigo().isEmpty()
                && vo.getProyectoOtNombre() != null && vo.getProyectoOtNombre().isEmpty()) {
            vo.setSinOT(true);
        }

        return vo;
    }

    private PresupuestoDetVO leerMontos(PresupuestoDetVO vo, XSSFSheet workSheet, int fila) {
        vo.setMontos(new ArrayList<>());
        fila--;
        Row filaD = workSheet.getRow(fila);
        LecturaLibro lecturaLibro = new LecturaLibro();
        MontosPresupuestoVO aux = null;
        int mesDet = 1;
        int anioDet = 1;
        for (int i = 11; i < filaD.getLastCellNum();) {
            aux = new MontosPresupuestoVO();
            int auxFecha = i++;
            aux.setFecha(lecturaLibro.getValFromReferenceDate(workSheet, 0, auxFecha));
            aux.setFechaTxt(lecturaLibro.getValFromReference(workSheet, 0, auxFecha));

            String moC = lecturaLibro.getValFromReference(workSheet, fila, auxFecha);
            if (moC == null || moC.isEmpty()) {
                aux.setManoObraCn(new BigDecimal(BigInteger.ZERO));
            } else {
                aux.setManoObraCn(new BigDecimal(moC));
            }

            String moE = lecturaLibro.getValFromReference(workSheet, fila, i++);
            if (moE == null || moE.isEmpty()) {
                aux.setManoObraEx(new BigDecimal(BigInteger.ZERO));
            } else {
                aux.setManoObraEx(new BigDecimal(moE));
            }

            String biC = lecturaLibro.getValFromReference(workSheet, fila, i++);
            if (biC == null || biC.isEmpty()) {
                aux.setBienasCn(new BigDecimal(BigInteger.ZERO));
            } else {
                aux.setBienasCn(new BigDecimal(biC));
            }

            String biE = lecturaLibro.getValFromReference(workSheet, fila, i++);
            if (biE == null || biE.isEmpty()) {
                aux.setBienesEx(new BigDecimal(BigInteger.ZERO));
            } else {
                aux.setBienesEx(new BigDecimal(biE));
            }

            String seC = lecturaLibro.getValFromReference(workSheet, fila, i++);
            if (seC == null || seC.isEmpty()) {
                aux.setServiciosCn(new BigDecimal(BigInteger.ZERO));
            } else {
                aux.setServiciosCn(new BigDecimal(seC));
            }

            String seE = lecturaLibro.getValFromReference(workSheet, fila, i++);
            if (seE == null || seE.isEmpty()) {
                aux.setServiciosEx(new BigDecimal(BigInteger.ZERO));
            } else {
                aux.setServiciosEx(new BigDecimal(seE));
            }

            String caC = lecturaLibro.getValFromReference(workSheet, fila, i++);
            if (caC == null || caC.isEmpty()) {
                aux.setCapacitacionCn(new BigDecimal(BigInteger.ZERO));
            } else {
                aux.setCapacitacionCn(new BigDecimal(caC));
            }

            String caE = lecturaLibro.getValFromReference(workSheet, fila, i++);
            if (caE == null || caE.isEmpty()) {
                aux.setCapacitacionEx(new BigDecimal(BigInteger.ZERO));
            } else {
                aux.setCapacitacionEx(new BigDecimal(caE));
            }

            String trA = lecturaLibro.getValFromReference(workSheet, fila, i++);
            if (trA == null || trA.isEmpty()) {
                aux.setTransferenciaTec(new BigDecimal(BigInteger.ZERO));
            } else {
                aux.setTransferenciaTec(new BigDecimal(trA));
            }

            String inF = lecturaLibro.getValFromReference(workSheet, fila, i++);
            if (inF == null || inF.isEmpty()) {
                aux.setInfraestructura(new BigDecimal(BigInteger.ZERO));
            } else {
                aux.setInfraestructura(new BigDecimal(inF));
            }

            if(mesDet == 1 && anioDet == 1){
                mesDet = aux.getMesFromFechaTxt();
            }
            
            aux.setMes(mesDet);
            aux.setAnio(anioDet);
            aux.setNewDetID(ocPresupuestoDetalleImpl.getPresupuestoDetByVO(vo.getTareaVO(), vo.getSubTareaCodigoId(), vo.getIdPresupuesto(), aux.getMes(), aux.getAnio()));
            if (aux.getNewDetID() > 0) {
                vo.setExistePresDet(true);
            }
            if (mesDet < 12) {
                mesDet++;
            } else {
                mesDet = 1;
                anioDet++;
            }

            vo.getMontos().add(aux);
        }

        return vo;
    }

    /**
     * @return the lstPartidas
     */
    public List<PresupuestoDetVO> getLstPartidas() {
        return lstPartidas;
    }

    /**
     * @param lstPartidas the lstPartidas to set
     */
    public void setLstPartidas(List<PresupuestoDetVO> lstPartidas) {
        this.lstPartidas = lstPartidas;
    }

//    /**
//     * @return the nuevasActs
//     */
//    public HashMap<String, OcActividadVO> getNuevasActs() {
//        return nuevasActs;
//    }
//
//    /**
//     * @param nuevasActs the nuevasActs to set
//     */
//    public void setNuevasActs(HashMap<String, OcActividadVO> nuevasActs) {
//        this.nuevasActs = nuevasActs;
//    }
    /**
     * @return the partidaProy
     */
    public OcActividadVO getPartidaProy() {
        return partidaProy;
    }

    /**
     * @param partidaProy the partidaProy to set
     */
    public void setPartidaProy(OcActividadVO partidaProy) {
        this.partidaProy = partidaProy;
    }

    /**
     * @return the monedas
     */
    public List<SelectItem> getMonedas() {
        return monedas;
    }

    /**
     * @param monedas the monedas to set
     */
    public void setMonedas(List<SelectItem> monedas) {
        this.monedas = monedas;
    }

    /**
     * @return the partidaDisplay
     */
    public PresupuestoDetVO getPartidaDisplay() {
        return partidaDisplay;
    }

    /**
     * @param partidaDisplay the partidaDisplay to set
     */
    public void setPartidaDisplay(PresupuestoDetVO partidaDisplay) {
        this.partidaDisplay = partidaDisplay;
    }

    public boolean guardarPresupuestoDet() {
        boolean ret = false;
        try {
            ocPresupuestoMovimientosImpl.importarGuardar(this.getIdPres(), getSesion().getUsuario().getId());
            for (PresupuestoDetVO det : this.getLstPartidas()) {
                if (!det.isExistePresDet() && det.isExisteOT()) {
                    OcTarea tarea = this.cargaTarea(det.getTareaVO(), getSesion().getUsuario().getId());
                    if (tarea != null && tarea.getId() > 0) {
                        OcSubTarea subTarea = cargaSubTarea(tarea.getId(), det.getSubTareaCodigoId(), getSesion().getUsuario().getId());
                        if (subTarea != null && subTarea.getId() > 0) {
                            OcPresupuestoDetalle presDet = cargaPresupuestoDet(det.getTareaVO(), det, getSesion().getUsuario().getId(), 0);
                        }
                    }
                } else if (!det.isExistePresDet() && det.isSinOT()) {
                    List<ProyectoOtVo> lstProyectos = this.proyectoOtImpl.getListaProyectosOtPorCampo(getIdCampo(), getSesion().getUsuario().getApCampo().getCompania().getRfc(), "C", false);
                    boolean primerOT = true;
                    for (ProyectoOtVo proy : lstProyectos) {
                        OcTareaVo vvo = det.getTareaVO();
                        vvo.setIdProyectoOt(proy.getId());
                        OcTarea tarea = this.cargaTarea(vvo, getSesion().getUsuario().getId());
                        if (tarea != null && tarea.getId() > 0) {
                            OcSubTarea subTarea = cargaSubTarea(tarea.getId(), det.getSubTareaCodigoId(), getSesion().getUsuario().getId());
                            if (subTarea != null && subTarea.getId() > 0) {
                                if (primerOT) {
                                    OcPresupuestoDetalle presDet = cargaPresupuestoDet(vvo, det, getSesion().getUsuario().getId(), lstProyectos.size());
                                    primerOT = false;
                                }
                            }
                        }
                    }
                } else if (det.isExistePresDet() && det.isExisteOT()) {
                    OcTarea tarea = this.cargaTarea(det.getTareaVO(), getSesion().getUsuario().getId());
                    if (tarea != null && tarea.getId() > 0) {
                        OcSubTarea subTarea = cargaSubTarea(tarea.getId(), det.getSubTareaCodigoId(), getSesion().getUsuario().getId());
                        if (subTarea != null && subTarea.getId() > 0) {
                            OcPresupuestoDetalle presDet = cargaPresupuestoDet(det.getTareaVO(), det, getSesion().getUsuario().getId(), 0);
                        }
                    }
                } else if (det.isExistePresDet() && det.isSinOT()) {
                    List<ProyectoOtVo> lstProyectos = this.proyectoOtImpl.getListaProyectosOtPorCampo(getIdCampo(), getSesion().getUsuario().getApCampo().getCompania().getRfc(), "C", false);
                    boolean primerOT = true;
                    for (ProyectoOtVo proy : lstProyectos) {
                        OcTareaVo vvo = det.getTareaVO();
                        vvo.setIdProyectoOt(proy.getId());
                        OcTarea tarea = this.cargaTarea(vvo, getSesion().getUsuario().getId());
                        if (tarea != null && tarea.getId() > 0) {
                            OcSubTarea subTarea = cargaSubTarea(tarea.getId(), det.getSubTareaCodigoId(), getSesion().getUsuario().getId());
                            if (subTarea != null && subTarea.getId() > 0) {
                                if (primerOT) {
                                    OcPresupuestoDetalle presDet = cargaPresupuestoDet(vvo, det, getSesion().getUsuario().getId(), lstProyectos.size());
                                    primerOT = false;
                                }
                            }
                        }
                    }
                }
            }
            ret = true;
        } catch (Exception e) {
            LOGGER.error(Level.SEVERE, "guardarPresupuestoDet", e);
            ret = false;
        }
        return ret;
    }

    private OcPresupuestoDetalle cargaPresupuestoDet(OcTareaVo vo, PresupuestoDetVO dett, String usuario, int proysNum) {
        OcPresupuestoDetalle newDet = null;
        try {
            if (vo != null && usuario != null && !usuario.isEmpty()
                    && dett.getSubTareaCodigoId() > 0 && vo.getIdNombreTarea() > 0 && vo.getIdcodigoTarea() > 0
                    && vo.getIdUnidadCosto() > 0 && vo.getIdActPetrolera() > 0 && dett.getIdPresupuesto() > 0) {
                for (MontosPresupuestoVO detMon : dett.getMontos()) {
                    int newDetID = 0;
                    if (proysNum > 0) {
                        newDetID = detMon.getNewDetID() > 0 ? detMon.getNewDetID() : ocPresupuestoDetalleImpl.getPresupuestoDetByVO(vo, dett.getSubTareaCodigoId(), dett.getIdPresupuesto(), detMon.getMes(), detMon.getAnio());
                    } else {
                        newDetID = detMon.getNewDetID();
                    }
                    if (newDetID > 0) {
                        newDet = ocPresupuestoDetalleImpl.find(newDetID);
                        if (newDet.isEliminado()) {
                            newDet.setGenero(new Usuario(usuario));
                            newDet.setFechaModifico(new Date());
                            newDet.setHoraModifico(new Date());
                            newDet.setEliminado(Constantes.BOOLEAN_FALSE);
                            ocPresupuestoDetalleImpl.edit(newDet);
                        }
                    } else {
                        newDet = new OcPresupuestoDetalle();
                        newDet.setOcPresupuesto(new OcPresupuesto(dett.getIdPresupuesto()));
                        newDet.setMes(detMon.getMes());
                        newDet.setAnio(detMon.getAnio());
                        newDet.setOcCodigoTarea(new OcCodigoTarea(vo.getIdcodigoTarea()));
                        newDet.setOcUnidadCosto(new OcUnidadCosto(vo.getIdUnidadCosto()));
                        newDet.setOcActividadPetrolera(new OcActividadPetrolera(vo.getIdActPetrolera()));
                        newDet.setOcCodigoSubtarea(new OcCodigoSubtarea(dett.getSubTareaCodigoId()));

                        newDet.setGenero(new Usuario(usuario));
                        newDet.setFechaGenero(new Date());
                        newDet.setHoraGenero(new Date());
                        newDet.setEliminado(Constantes.BOOLEAN_FALSE);

                        if (dett.isSinOT()) {
                            if (detMon.getManoObraCn().compareTo(BigDecimal.ZERO) > 0) {
                                newDet.setManoObraCN(detMon.getManoObraCn().doubleValue());
                            } else {
                                newDet.setManoObraCN(BigDecimal.ZERO.doubleValue());
                            }
                            if (detMon.getManoObraEx().compareTo(BigDecimal.ZERO) > 0) {
                                newDet.setManoObraEX(detMon.getManoObraEx().doubleValue());
                            } else {
                                newDet.setManoObraEX(BigDecimal.ZERO.doubleValue());
                            }
                            if (detMon.getBienasCn().compareTo(BigDecimal.ZERO) > 0) {
                                newDet.setBienesCN(detMon.getBienasCn().doubleValue());
                            } else {
                                newDet.setBienesCN(BigDecimal.ZERO.doubleValue());
                            }
                            if (detMon.getBienesEx().compareTo(BigDecimal.ZERO) > 0) {
                                newDet.setBienesEX(detMon.getBienesEx().doubleValue());
                            } else {
                                newDet.setBienesEX(BigDecimal.ZERO.doubleValue());
                            }
                            if (detMon.getServiciosCn().compareTo(BigDecimal.ZERO) > 0) {
                                newDet.setServiciosCN(detMon.getServiciosCn().doubleValue());
                            } else {
                                newDet.setServiciosCN(BigDecimal.ZERO.doubleValue());
                            }
                            if (detMon.getServiciosEx().compareTo(BigDecimal.ZERO) > 0) {
                                newDet.setServiciosEX(detMon.getServiciosEx().doubleValue());
                            } else {
                                newDet.setServiciosEX(BigDecimal.ZERO.doubleValue());
                            }
                            if (detMon.getCapacitacionCn().compareTo(BigDecimal.ZERO) > 0) {
                                newDet.setCapacitacionCN(detMon.getCapacitacionCn().doubleValue());
                            } else {
                                newDet.setCapacitacionCN(BigDecimal.ZERO.doubleValue());
                            }
                            if (detMon.getCapacitacionEx().compareTo(BigDecimal.ZERO) > 0) {
                                newDet.setCapacitacionEX(detMon.getCapacitacionEx().doubleValue());
                            } else {
                                newDet.setCapacitacionEX(BigDecimal.ZERO.doubleValue());
                            }
                            if (detMon.getTransferenciaTec().compareTo(BigDecimal.ZERO) > 0) {
                                newDet.setTransTecnologia(detMon.getTransferenciaTec().doubleValue());
                            } else {
                                newDet.setTransTecnologia(BigDecimal.ZERO.doubleValue());
                            }
                            if (detMon.getInfraestructura().compareTo(BigDecimal.ZERO) > 0) {
                                newDet.setInfraestructura(detMon.getInfraestructura().doubleValue());
                            } else {
                                newDet.setInfraestructura(BigDecimal.ZERO.doubleValue());
                            }
                        } else {
                            newDet.setManoObraCN(detMon.getManoObraCn().doubleValue());
                            newDet.setManoObraEX(detMon.getManoObraEx().doubleValue());
                            newDet.setBienesCN(detMon.getBienasCn().doubleValue());
                            newDet.setBienesEX(detMon.getBienesEx().doubleValue());
                            newDet.setServiciosCN(detMon.getServiciosCn().doubleValue());
                            newDet.setServiciosEX(detMon.getServiciosEx().doubleValue());
                            newDet.setCapacitacionCN(detMon.getCapacitacionCn().doubleValue());
                            newDet.setCapacitacionEX(detMon.getCapacitacionEx().doubleValue());
                            newDet.setTransTecnologia(detMon.getTransferenciaTec().doubleValue());
                            newDet.setInfraestructura(detMon.getInfraestructura().doubleValue());
                        }
                        ocPresupuestoDetalleImpl.create(newDet);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error(Level.SEVERE, null, e);
            newDet = null;
        }
        return newDet;
    }

    private OcTarea cargaTarea(OcTareaVo vo, String usuario) {
        OcTarea tarea = null;
        try {
            if (vo != null && usuario != null && !usuario.isEmpty()
                    && vo.getIdProyectoOt() > 0 && vo.getIdNombreTarea() > 0 && vo.getIdcodigoTarea() > 0
                    && vo.getIdUnidadCosto() > 0 && vo.getIdActPetrolera() > 0) {
                int tareaID = ocPresupuestoDetalleImpl.getTareaIDByVO(vo);
                if (tareaID > 0) {
                    tarea = ocTareaImpl.find(tareaID);
                    if (tarea.isEliminado()) {
                        tarea.setGenero(new Usuario(usuario));
                        tarea.setFechaModifico(new Date());
                        tarea.setHoraModifico(new Date());
                        tarea.setEliminado(Constantes.BOOLEAN_FALSE);
                        ocTareaImpl.edit(tarea);
                    }

                } else {
                    tarea = new OcTarea();
                    tarea.setProyectoOt(new ProyectoOt(vo.getIdProyectoOt()));
                    tarea.setOcNombreTarea(new OcNombreTarea(vo.getIdNombreTarea()));
                    tarea.setOcCodigoTarea(new OcCodigoTarea(vo.getIdcodigoTarea()));
                    tarea.setOcUnidadCosto(new OcUnidadCosto(vo.getIdUnidadCosto()));
                    tarea.setOcActividadPetrolera(new OcActividadPetrolera(vo.getIdActPetrolera()));

                    tarea.setGenero(new Usuario(usuario));
                    tarea.setFechaGenero(new Date());
                    tarea.setHoraGenero(new Date());
                    tarea.setEliminado(Constantes.BOOLEAN_FALSE);
                    ocTareaImpl.create(tarea);
                }
            }
        } catch (Exception e) {
            LOGGER.error(Level.SEVERE, "cargaTarea", e);
            tarea = null;
        }
        return tarea;
    }

    private OcSubTarea cargaSubTarea(int tareaID, int codigoSubtareaID, String usuario) {
        OcSubTarea subTarea = null;
        try {
            if (usuario != null && !usuario.isEmpty()
                    && tareaID > 0
                    && codigoSubtareaID > 0) {
                int subTareaID = ocSubTareaImpl.traerSubTareaID(tareaID, codigoSubtareaID);
                if (subTareaID > 0) {
                    subTarea = ocSubTareaImpl.find(subTareaID);
                    if (subTarea.isEliminado()) {
                        subTarea.setGenero(new Usuario(usuario));
                        subTarea.setFechaModifico(new Date());
                        subTarea.setHoraModifico(new Date());
                        subTarea.setEliminado(Constantes.BOOLEAN_FALSE);
                        ocSubTareaImpl.edit(subTarea);
                    }

                } else {
                    subTarea = new OcSubTarea();
                    subTarea.setOcTarea(new OcTarea(tareaID));
                    subTarea.setOcCodigoSubtarea(new OcCodigoSubtarea(codigoSubtareaID));

                    subTarea.setGenero(new Usuario(usuario));
                    subTarea.setFechaGenero(new Date());
                    subTarea.setHoraGenero(new Date());
                    subTarea.setEliminado(Constantes.BOOLEAN_FALSE);
                    ocSubTareaImpl.create(subTarea);
                }
            }
        } catch (Exception e) {
            LOGGER.error(Level.SEVERE, "cargaSubTarea", e);
            subTarea = null;
        }
        return subTarea;
    }

    /**
     * @return the nuevosCodigosTarea
     */
    public List<OcCodigoTarea> getNuevosCodigosTarea() {
        return nuevosCodigosTarea;
    }

    /**
     * @param nuevosCodigosTarea the nuevosCodigosTarea to set
     */
    public void setNuevosCodigosTarea(List<OcCodigoTarea> nuevosCodigosTarea) {
        this.nuevosCodigosTarea = nuevosCodigosTarea;
    }

    /**
     * @return the nuevosNombresTarea
     */
    public List<OcNombreTarea> getNuevosNombresTarea() {
        return nuevosNombresTarea;
    }

    /**
     * @param nuevosNombresTarea the nuevosNombresTarea to set
     */
    public void setNuevosNombresTarea(List<OcNombreTarea> nuevosNombresTarea) {
        this.nuevosNombresTarea = nuevosNombresTarea;
    }

    /**
     * @return the nuevosProyectosOts
     */
    public List<ProyectoOt> getNuevosProyectosOts() {
        return nuevosProyectosOts;
    }

    /**
     * @param nuevosProyectosOts the nuevosProyectosOts to set
     */
    public void setNuevosProyectosOts(List<ProyectoOt> nuevosProyectosOts) {
        this.nuevosProyectosOts = nuevosProyectosOts;
    }

    /**
     * @return the nuevosSubTareas
     */
    public List<OcCodigoSubtarea> getNuevosSubTareas() {
        return nuevosSubTareas;
    }

    /**
     * @param nuevosSubTareas the nuevosSubTareas to set
     */
    public void setNuevosSubTareas(List<OcCodigoSubtarea> nuevosSubTareas) {
        this.nuevosSubTareas = nuevosSubTareas;
    }

    /**
     * @return the nuevosSubActividadP
     */
    public List<OcUnidadCosto> getNuevosSubActividadP() {
        return nuevosSubActividadP;
    }

    /**
     * @param nuevosSubActividadP the nuevosSubActividadP to set
     */
    public void setNuevosSubActividadP(List<OcUnidadCosto> nuevosSubActividadP) {
        this.nuevosSubActividadP = nuevosSubActividadP;
    }

    /**
     * @return the nuevosActividadP
     */
    public List<OcActividadPetrolera> getNuevosActividadP() {
        return nuevosActividadP;
    }

    /**
     * @param nuevosActividadP the nuevosActividadP to set
     */
    public void setNuevosActividadP(List<OcActividadPetrolera> nuevosActividadP) {
        this.nuevosActividadP = nuevosActividadP;
    }
}
