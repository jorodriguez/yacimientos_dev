/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.vehiculo.bean.model;

import com.itextpdf.text.DocumentException;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import javax.inject.Named;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import org.primefaces.model.file.UploadedFile;
import sia.archivador.ProveedorAlmacenDocumentos;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.Proveedor;
import sia.modelo.SgAsignarVehiculo;
import sia.modelo.SgCaracteristica;
import sia.modelo.SgChecklist;
import sia.modelo.SgChecklistExtVehiculo;
import sia.modelo.SgChecklistLlantas;
import sia.modelo.SgColor;
import sia.modelo.SgKilometraje;
import sia.modelo.SgMarca;
import sia.modelo.SgModelo;
import sia.modelo.SgOficina;
import sia.modelo.SgPagoServicio;
import sia.modelo.SgPagoServicioVehiculo;
import sia.modelo.SgTallerMantenimiento;
import sia.modelo.SgTipo;
import sia.modelo.SgTipoEspecifico;
import sia.modelo.SgVehiculo;
import sia.modelo.SgVehiculoChecklist;
import sia.modelo.SgVehiculoMantenimiento;
import sia.modelo.SiAdjunto;
import sia.modelo.SiPais;
import sia.modelo.Usuario;
import sia.modelo.licencia.vo.LicenciaVo;
import sia.modelo.sgl.oficina.vo.OficinaVO;
import sia.modelo.sgl.vehiculo.vo.SgKilometrajeVo;
import sia.modelo.sgl.viaje.vo.TipoEspecificoVo;
import sia.modelo.sgl.viaje.vo.VehiculoVO;
import sia.modelo.sgl.vo.CaracteristicaVo;
import sia.modelo.sgl.vo.CheckListDetalleVo;
import sia.modelo.sgl.vo.Vo;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.modelo.vo.StatusVO;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.EstatusImpl;
import sia.servicios.catalogos.impl.MonedaImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;
import sia.servicios.sgl.impl.SgCaracteristicaImpl;
import sia.servicios.sgl.impl.SgChecklistDetalleImpl;
import sia.servicios.sgl.impl.SgChecklistImpl;
import sia.servicios.sgl.impl.SgColorImpl;
import sia.servicios.sgl.impl.SgKilometrajeImpl;
import sia.servicios.sgl.impl.SgMarcaImpl;
import sia.servicios.sgl.impl.SgModeloImpl;
import sia.servicios.sgl.impl.SgOficinaImpl;
import sia.servicios.sgl.impl.SgPagoServicioImpl;
import sia.servicios.sgl.impl.SgPagoServicioVehiculoImpl;
import sia.servicios.sgl.impl.SgTallerMantenimientoImpl;
import sia.servicios.sgl.impl.SgTipoEspecificoImpl;
import sia.servicios.sgl.impl.SgTipoImpl;
import sia.servicios.sgl.impl.SgTipoTipoEspecificoImpl;
import sia.servicios.sgl.impl.SgVehiculoChecklistImpl;
import sia.servicios.sgl.impl.SgVehiculoMantenimientoImpl;
import sia.servicios.sgl.vehiculo.impl.SgAsignarVehiculoImpl;
import sia.servicios.sgl.vehiculo.impl.SgChecklistExtVehiculoImpl;
import sia.servicios.sgl.vehiculo.impl.SgChecklistLlantasImpl;
import sia.servicios.sgl.vehiculo.impl.SgLicenciaImpl;
import sia.servicios.sgl.vehiculo.impl.SgVehiculoImpl;
import sia.servicios.sgl.vehiculo.impl.SgVehiculoMovimientoImpl;
import sia.servicios.sistema.impl.SiAdjuntoImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.servicios.sistema.impl.SiPaisImpl;
import sia.servicios.sistema.impl.SiParametroImpl;
import sia.servicios.sistema.vo.MonedaVO;
import sia.sgl.checklist.bean.model.CheckListVehiculoModel;
import sia.sgl.mantenimiento.bean.model.MantenimientoBeanModel;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.sgl.sistema.bean.support.SoporteProveedor;
import sia.util.Env;
import sia.util.ProveedorEnum;
import sia.util.UtilLog4j;

/**
 *
 * @author b75ckd35th
 * @modify MLUIS
 */
@Named(value = "vehiculoBean")
@ViewScoped
public class VehiculoBeanModel implements Serializable {

    private final static UtilLog4j LOGGER = UtilLog4j.log;

    //Sistema
    @Inject
    private Sesion sesion;
    //ManagedBeans
    @Inject
    private SoporteProveedor soporteProveedor;
    @Inject
    private MantenimientoBeanModel mantenimientoBeanModel;

    @Inject
    private ProveedorAlmacenDocumentos proveedorAlmacenDocumentos;
    @Getter
    @Setter
    private UploadedFile fileInfo;
    //Servicios
    @Inject
    private UsuarioImpl usuarioImpl;
    @Inject
    ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
    @Inject
    private SgMarcaImpl sgMarcaImpl;
    @Inject
    private SgModeloImpl sgModeloImpl;
    @Inject
    private SgLicenciaImpl sgLicenciaImpl;
    @Inject
    private SgTipoImpl sgTipoImpl;
    @Inject
    private SiAdjuntoImpl siAdjuntoImpl;
    @Inject
    private SiManejoFechaImpl siManejoFechaImpl;
    @Inject
    private SgTipoTipoEspecificoImpl sgTipoTipoEspecificoImpl;
    @Inject
    private SgAsignarVehiculoImpl sgAsignarVehiculoImpl;
    @Inject
    private SgVehiculoImpl sgVehiculoImpl;
    @Inject
    private SgChecklistImpl sgChecklistImpl;
    @Inject
    private SgVehiculoChecklistImpl sgVehiculoChecklistImpl;
    @Inject
    private SgChecklistDetalleImpl sgChecklistDetalleImpl;
    @Inject
    private SgChecklistExtVehiculoImpl sgChecklistExtVehiculoImpl;
    @Inject
    private SgChecklistLlantasImpl sgChecklistLlantasImpl;
    @Inject
    private SgPagoServicioVehiculoImpl sgPagoServicioVehiculoImpl;
    @Inject
    private MonedaImpl monedaImpl;
    @Inject
    private SgTipoEspecificoImpl sgTipoEspecificoImpl;
    @Inject
    private SgPagoServicioImpl sgPagoServicioImpl;
    @Inject
    private SgCaracteristicaImpl sgCaracteristicaImpl;
    @Inject
    private SgKilometrajeImpl sgKilometrajeImpl;
    @Inject
    private ProveedorServicioImpl proveedorImpl;
    @Inject
    private SiParametroImpl siParametroService;
    @Inject
    private SiPaisImpl siPaisImpl;
    @Inject
    private SgColorImpl sgColorImpl;
    @Inject
    private SgOficinaImpl sgOficinaImpl;
    @Inject
    private SgVehiculoMantenimientoImpl sgVehiculoMantenimientoImpl;
    @Inject
    private SgVehiculoMovimientoImpl vehiculoMovimientoImpl;
    @Inject
    private EstatusImpl estatusImpl;
    @Inject
    private SgTallerMantenimientoImpl sgTallerMantenimientoImpl;
//    @Inject
//    private SgVehiculoMantenimientoImpl sgMantenimientoService;

    //Entidades
    private VehiculoVO vehiculo;
    private Usuario usuario;
    // private SgTipo sgTipo;
    private SgAsignarVehiculo sgAsignarVehiculo;
    private SgChecklist checklist;
    private SgVehiculoChecklist sgVehiculoChecklist;
    private SgChecklistLlantas checklistLlantas;
    private SgChecklistLlantas sgChecklistLlantas;
    private SgChecklistExtVehiculo checklistExtVehiculo;
    private SgChecklistExtVehiculo sgChecklistExtVehiculo;
    private SgVehiculoChecklist vehiculoChecklist;
    private SgKilometraje sgKilometraje;
    private SgKilometraje kilometrajeActual;
    private SgPagoServicioVehiculo sgPagoServicioVehiculo;
    private SgTipoEspecifico sgTipoEspecifico;
    private SgPagoServicio sgPagoServicio;
    private SgMarca marca;
    private SgModelo modelo;
    private SgAsignarVehiculo sgAsignarVehiculoRecibido;
    private CaracteristicaVo caracteristicaVehiculo;
    private SgColor sgColor;
    //Clases
    private String mensaje;
    private String user;
    private String alerta;
    private String opcionSeleccionada = "todo";
    private String cadenaBuscar;
    private String pro;
    private String prefijo = "";
    //Colecciones
    private DataModel dataModel; //DataModel general
    private Map<String, DataModel> mapaDatos = new HashMap<String, DataModel>();
    private DataModel lista;
    private DataModel checklistVODataModel;
    private DataModel pagos;
    private List<SelectItem> listaProveedor;
    private List<String> listaProveedorBuscar;
    private List<Vo> modelos;
    private List<SelectItem> caracteristicas;
    private List<SelectItem> colorListItem;
    private List<SelectItem> matchesList;
    private List<SelectItem> listaOficinas;
    private List<SelectItem> listaTaller;
    private List<SelectItem> listaEstado;
    //Primitivos
    private int numerDias; //Usado a)KilometrajeInicial en Crear Vehículo
    private int idTipoEspecifico;
    private int idChecklist;
    private int idModelo;
    private int idMarca;
    private int idColor;
    private int idMoneda; //Usado: a)Modificación de kilometraje
    private int idAsignaVehiculo; //Usado a)Periodicidad aviso mantenimiento en Crear Vehículo
    private int idPais;
    private int idOficina;
    private int idOficina2;
    private boolean flag;
    private boolean popUp = false;
    private boolean pagoPop = false;
    private boolean modificarPopUp = false;
    private boolean crearPopUp = false;
    private boolean crearPop = false;
    private boolean eliminarPop = false;
    private boolean modificarPop = false;
    private boolean verDetallePop = false;
    private boolean subirArchivoPop = false;
    private boolean recibirVehiculoPop = false;
    private boolean reactivateVehiculo = false;
    private boolean asignacionSinTerminar = false;
    private int idEstado;
    private int idTipo;

    /**
     * Creates a new instance of VehiculoBeanModel
     */
    public VehiculoBeanModel() {
    }

    @PostConstruct
    public void beginConversationCatalogoVehiculo() {
        try {
            //Finalizar variables
            setLista(new ListDataModel(sgVehiculoImpl.traerVehiculoOficinaEstado(sesion.getOficinaActual().getId(), Constantes.ESTADO_VEHICULO_ACTIVO)));
            setCadenaBuscar("");
            vehiculo = new VehiculoVO();
            idOficina = sesion.getOficinaActual().getId();
            idEstado = Constantes.ESTADO_VEHICULO_ACTIVO;
            this.idTipoEspecifico = -1;
            this.idMarca = -1;
            this.idModelo = -1;
            this.idColor = -1;
            idTipo = Constantes.TIPO_PAGO_VEHICULO;
            traerColorsItems();
            //Limpiar Variables
            this.setVehiculo(null);
            sesion.getControladorPopups().put("popupUpdateVehiculo", Boolean.FALSE);
            sesion.getControladorPopups().put("popupDeleteVehiculo", Boolean.FALSE);
            sesion.getControladorPopups().put("popupBajaVehiculo", Boolean.FALSE);
            sesion.getControladorPopups().put("popupCreateMarca", Boolean.FALSE);
            sesion.getControladorPopups().put("popupCreateTipoEspecificoVehiculo", Boolean.FALSE);
            sesion.getControladorPopups().put("popupCreateModelo", Boolean.FALSE);
//            sesion.getControladorPopups().put("popupActivarVehiculo", Boolean.FALSE);
            sesion.getControladorPopups().put("popupRestartKilometraje", Boolean.FALSE);
            sesion.getControladorPopups().put("popupModifyKilometraje", Boolean.FALSE);
            try {
                //
                List<StatusVO> le = estatusImpl.traerPorTipo(Constantes.CODIGO_ESTATUS_VEHICULO);
                listaEstado = new ArrayList<SelectItem>();
                for (StatusVO le1 : le) {
                    listaEstado.add(new SelectItem(le1.getIdStatus(), le1.getNombre()));
                }
            } catch (Exception ex) {
                Logger.getLogger(VehiculoBeanModel.class.getName()).log(Level.SEVERE, null, ex);
            }

        } catch (Exception ex) {
            Logger.getLogger(VehiculoBeanModel.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void goToCreate() {
        traerCaracteristicas();
        if (getCaracteristicas().isEmpty() || getCaracteristicas().size() <= 10) {
            FacesUtils.addErrorMessage("Deben existir al menos 10 características de Vehículos antes de que puedas dar de alta un Vehículo");
        } else {
            setVehiculo(new VehiculoVO());
            setIdTipoEspecifico(-1);
            setIdMarca(-1);
            setIdModelo(-1);
            setIdColor(-1);
            idAsignaVehiculo = 7000;
            numerDias = 0;
            this.setCaracteristicas(null);
            PrimeFaces.current().executeScript("PF(dlgCrearVehiculo).show();");
        }
    }

    public void idOficinaSel() {
        traerVehiculoEstado();
    }

    public void cambiarEstado() {
        traerVehiculoEstado();

    }

    public String usuariosJson() {
        return usuarioImpl.traerUsuarioActivoJson();
    }

    public void traerTaller() {
        List<SgTallerMantenimiento> le = sgTallerMantenimientoImpl.findAllTalleres(idOficina);
        listaTaller = new ArrayList<SelectItem>();
        for (SgTallerMantenimiento le1 : le) {
            listaTaller.add(new SelectItem(le1.getProveedor().getId(), le1.getProveedor().getNombre()));
        }
    }

    public void goToChecklistVehiculo() {
        CheckListVehiculoModel checkListVehiculoModel = (CheckListVehiculoModel) FacesUtils.getManagedBean("checkListVehiculoModel");
        checkListVehiculoModel.setVehiculoVO(getVehiculo());
        checkListVehiculoModel.beginConversationChecklistVehiculo();
        //Metiendo popups a Map de Popups
        checkListVehiculoModel.controlarPopFalse("popupObservacionToAdjunto");
        checkListVehiculoModel.controlarPopFalse("popupUpdateObservacionToAdjunto");
        checkListVehiculoModel.controlarPopFalse("popupUploadChecklistExterior");
        checkListVehiculoModel.setDisabledChecklistInterior(false);
        checkListVehiculoModel.setDisabledChecklistExterior(true);
        checkListVehiculoModel.setDisabledChecklistLlantas(true);
    }

    public void controlarPop(String pop, boolean estado) {
        sesion.getControladorPopups().put(pop, estado);
    }

    public boolean estadoControlarPop(String pop) {
        return sesion.getControladorPopups().get(pop);
    }

    public String administrarVehiculo(int idVehiculo) {
        try {
            setChecklist(null);
            setChecklistExtVehiculo(null);
            setChecklistLlantas(null);
            setSgKilometraje(null);
            setSgVehiculoChecklist(null);
            setVehiculoChecklist(null);
            setSgChecklistExtVehiculo(null);
            setSgChecklistLlantas(null);
            setFlag(false);
            setChecklistVODataModel(null);
            setIdMoneda(0);
            if (idVehiculo > 0) {
                Env.setContext(sesion.getCtx(), "VEHICULO_ID", idVehiculo);
                traerTaller();
                mantenimientoBeanModel.iniciarMantenimiento(getVehiculo());
                setUsuario(null);
                mantenimientoBeanModel.setSgVehiculoSeleccionado(getVehiculo());
                mantenimientoBeanModel.traerKilometrajeActualOld();
                mantenimientoBeanModel.traerEstadoVehiculoActual();
                mantenimientoBeanModel.controlarPop("popupCaracteristicasVehiculo", false);
                mantenimientoBeanModel.controlarPop("popupDetalleChecklistVehiculo", false);
                mantenimientoBeanModel.traerVehiculoMantenimientoNoTerminado(); //<-- Saber si el vehicululo esta en mantenimiento

                mapaDatos.put("asignar", traerAsignacionVehiculo());
                mapaDatos.put("checklist", traerChecklistPorVehiculo());
                mapaDatos.put("pagos", traerPagoVehiculo());
                mapaDatos.put("movimiento", traerMovimientosOficinaVehiculo());
                Env.setContext(sesion.getCtx(), "", idMarca);
                return "administrarVehiculo.xhtml?faces-redirect=true";
            } else {
                FacesUtils.addInfoMessage("Paso algo, favor de contactar al equipo de desarrollo del SIA");
                return "";
            }
        } catch (Exception ex) {
            Logger.getLogger(VehiculoBeanModel.class.getName()).log(Level.SEVERE, null, ex);
                return "";
        }
    }

    public void traerCaracteristicas() {
        try {
            this.caracteristicas = new ArrayList<SelectItem>();
            this.matchesList = new ArrayList<SelectItem>();
            List<SgCaracteristica> cars = sgCaracteristicaImpl.getAllCaracteristicasByTipoAndPrincipalList(Constantes.CARACTERISTICA_SECUNDARIA, Constantes.UNO, Constantes.NO_ELIMINADO);
            if (cars != null && !cars.isEmpty()) {
                for (SgCaracteristica c : cars) {
                    SelectItem si = new SelectItem(c.getNombre());
                    this.caracteristicas.add(si);
                }
            }
        } catch (Exception e) {
        }
    }

    public void allCaracteristicasVehiculo() throws SIAException, Exception {
        mapaDatos.put("caracteristica", new ListDataModel(sgVehiculoImpl.getAllCaracteristicasVehiculoList(this.getVehiculo().getId())));
    }

    public void addCaracteristica() throws SIAException, Exception {
        SgCaracteristica car = sgVehiculoImpl.addCaracteristica(this.getVehiculo().getId(), this.prefijo, idTipo, sesion.getUsuario().getId());
        //Si se devolvió una Característica que se agregó nueva y hay que agregarla a la lista 'caracteristicasListSelectItemAll'
        if (car != null) {
            this.caracteristicas.add(new SelectItem(car.getNombre()));
        }
        //Recargar DataModel que mustra las relaciones
        mapaDatos.put("caracteristica", new ListDataModel(sgVehiculoImpl.getAllCaracteristicasVehiculoList(this.getVehiculo().getId())));
    }

    /**
     * Quita una relación entre una Característica y un vehículo
     *
     * @param o
     * @throws SIAException
     * @throws Exception
     */
    public void removeCaracteristica() throws SIAException, Exception {
        sgVehiculoImpl.removeCaracteristica(caracteristicaVehiculo.getIdRelacion(), sesion.getUsuario().getId());
        mapaDatos.put("caracteristica", new ListDataModel(sgVehiculoImpl.getAllCaracteristicasVehiculoList(this.getVehiculo().getId())));
    }

    public DataModel traerChecklistPorVehiculo() throws SIAException, Exception {
        return new ListDataModel(sgChecklistImpl.getAllChecklistByVehiculoList(this.getVehiculo().getId(), false));
    }

    public void getItemsVOForVehiculoChecklist() {
        this.setChecklistVODataModel(new ListDataModel(sgChecklistImpl.getChecklistVOItemsByChecklist(this.vehiculoChecklist.getSgChecklist())));
    }

    public boolean kilometrajeGreat() {
        return this.vehiculoChecklist.getSgKilometraje().getKilometraje() >= kilometrajeActual.getKilometraje();
    }

    public void getChecklistExteriorAndLlantas() {
        UtilLog4j.log.info(this, "Checklist: " + this.vehiculoChecklist.getSgChecklist().getId());
        UtilLog4j.log.info(this, "ChecklistVehiculo: " + this.vehiculoChecklist.getId());

        this.checklistExtVehiculo
                = sgChecklistExtVehiculoImpl.buscarPorChecklist(
                        this.vehiculoChecklist.getSgChecklist()
                );

        UtilLog4j.log.info(this, "Checklist de Exterior: " + (this.checklistExtVehiculo != null ? this.checklistExtVehiculo.getId() : null));

        this.checklistLlantas = sgChecklistLlantasImpl.buscarPorChecklist(this.vehiculoChecklist.getSgChecklist());
        if (this.checklistLlantas != null) {
            this.flag = (this.checklistLlantas.isBuenEstado());
        }
        UtilLog4j.log.info(this, "Checklist de Llantas: " + (this.checklistLlantas != null ? this.checklistLlantas.getId() : null));
        if (this.checklistLlantas == null) {
            this.checklistLlantas = new SgChecklistLlantas();
            this.checklistLlantas.setDelanteraDerecha("100");
            this.checklistLlantas.setDelanteraIzquierda("100");
            this.checklistLlantas.setTraseraDerecha("100");
            this.checklistLlantas.setTraseraIzquierda("100");
            this.checklistLlantas.setRefaccion("0");
            this.flag = true;
        }
    }

    public void getItemsVOForChecklistVehiculo() {
        List<CheckListDetalleVo> checklistItemsVOList = sgChecklistImpl.getChecklistVOItemsByChecklist(this.checklist);
        this.checklistVODataModel = new ListDataModel(checklistItemsVOList);
    }

    public List<SelectItem> traerUsuarioActivo(String cadena) {
        return soporteProveedor.regresaUsuarioActivoVO(cadena);
    }

    public Usuario buscarEmpledoPorNombre() {
        UtilLog4j.log.info(this, "Usuario a buscar " + getUser());
        try {
            if (getUser() != null) {
                setUsuario(usuarioImpl.buscarPorNombre(getUser()));
                return getUsuario();
            } else {
                return null;
            }
        } catch (Exception e) {
            return null;
        }
    }

    @Deprecated
    public List<SelectItem> listaPais() {
        List<SelectItem> l = new ArrayList<SelectItem>();
        try {
            List<SiPais> lt = siPaisImpl.findAll("nombre", true, false);
            for (SiPais siPais : lt) {
                SelectItem item = new SelectItem(siPais.getId(), siPais.getNombre());
                l.add(item);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Fui a ver que tenia la excepción de traer paises");
        }
        return l;
    }

    @Deprecated
    public DataModel traerLiciencia() {
        if (getUsuario() != null) {
            setLista(new ListDataModel(sgLicenciaImpl.traerLicienciaPorUsuario(getUsuario().getId())));
        } else {
            setLista(new ListDataModel(sgLicenciaImpl.traerLiciencia(sesion.getUsuario().getApCampo().getId(), "todo")));
        }
        return getLista();
    }

    @Deprecated
    public LicenciaVo buscarLiecinciaVigente() {
        try {
            return sgLicenciaImpl.buscarLicenciaVigentePorUsuario(getUsuario().getId());
        } catch (Exception e) {
            return null;
        }
    }

    @Deprecated
    public int licenciaPorVencer() {
        LicenciaVo l = sgLicenciaImpl.buscarLicenciaVigentePorUsuario(getUsuario().getId());
        if (l != null) {
//            UtilLog4j.log.info(this, "Fecha vence: " + l.getFechaVencimiento());
            setNumerDias(siManejoFechaImpl.dias(l.getVencimiento(), new Date()));
//            UtilLog4j.log.info(this, "Numero de dias " + getNumerDias());
            return getNumerDias();
        } else {
            return 0;
        }
    }

    public List<SelectItem> listaTipoEspecifico() {
        List<SelectItem> l = new ArrayList<SelectItem>();
        try {
            List<TipoEspecificoVo> lt = sgTipoTipoEspecificoImpl.traerPorTipo(idTipo, Constantes.BOOLEAN_FALSE);
            for (TipoEspecificoVo sgTipoTipoEspecifico : lt) {
                SelectItem item = new SelectItem(sgTipoTipoEspecifico.getId(), sgTipoTipoEspecifico.getNombre());
                l.add(item);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Fui a ver que tenia la excepción de traer tipo especifico");
        }
        return l;
    }

    public List<SelectItem> getSgTipoEspecificoBySgTipoSelectItem() {
        List<SelectItem> l = new ArrayList<SelectItem>();
        try {
            List<TipoEspecificoVo> lt = sgTipoTipoEspecificoImpl.traerPorTipo(getIdTipo(), Constantes.BOOLEAN_FALSE);
            for (TipoEspecificoVo sgTipoTipoEspecifico : lt) {
                SelectItem item = new SelectItem(sgTipoTipoEspecifico.getId(), sgTipoTipoEspecifico.getNombre());
                l.add(item);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepción en VehiculoBeanModel.getSgTipoEspecificoBySgTipoSelectItem()");
        }
        return l;
    }

    public VehiculoVO getVehiculoById(int idVehiculo) {
        return sgVehiculoImpl.buscarVehiculoPorId(idVehiculo);
    }

    public List<Vo> getMarcasByTipo() throws SIAException, Exception {
        return sgMarcaImpl.traerMarcaPorTipo(idTipo);
    }

    public List<SgModelo> getModelosByTipo() throws SIAException, Exception {
        UtilLog4j.log.info(this, "tipo especifico para filtrar modelos: " + this.idTipoEspecifico);
        return sgModeloImpl.findAll(sgTipoEspecificoImpl.find(this.idTipoEspecifico), sgMarcaImpl.find(this.idMarca), "nombre", Constantes.ORDER_BY_ASC, false);
    }

    public void getModelosByTipoEspecifico() throws SIAException, Exception {
        if (vehiculo.getIdTipoEspecifico() > 0) {
            if (vehiculo.getIdMarca() > 0) {
                this.modelos = sgModeloImpl.traerModeloPorTipo(vehiculo.getIdTipoEspecifico(), vehiculo.getIdMarca());
            } else {
                this.modelos = null;
            }
        } else {
            this.modelos = null;
        }
    }

    @Deprecated
    public boolean quitarLicenciaVigente(LicenciaVo licenciaVo) {
        return sgLicenciaImpl.quitarLicenciaVigente(sesion.getUsuario(), licenciaVo.getId());
    }

    public void saveVehiculo() throws SIAException, Exception {
        this.vehiculo.setPeriodoKmMantenimiento(getIdAsignaVehiculo());
        this.vehiculo.setPartida(numerDias);

        SgVehiculo v = sgVehiculoImpl.save(this.vehiculo, sesion.getUsuario().getId(), sesion.getOficinaActual().getId());
        //Mandar a guardar el kilometraje del Vehículo
        this.sgKilometrajeImpl.createKilometrajeActual(v.getId(), 11, getNumerDias(), this.sesion.getUsuario());

        //Recargar lista de Vehículos
        setLista(new ListDataModel(sgVehiculoImpl.traerVehiculoOficinaEstado(getIdOficina(), Constantes.CERO)));
    }

    public void reactivateVehiculo() throws SIAException, Exception {
        sgVehiculoImpl.reactivate(this.getVehiculo(), sesion.getUsuario().getId());
        //Recargar lista de Vehículos
        setLista(new ListDataModel(sgVehiculoImpl.getAllVehiculoByOficinaList(sesion.getOficinaActual().getId())));
    }

    public void updateVehiculo() throws SIAException, Exception {
        sgVehiculoImpl.udpate(this.getVehiculo(), sesion.getUsuario().getId());
        vehiculo = sgVehiculoImpl.buscarVehiculoPorId(vehiculo.getId());
    }

    public void seleccionarTaller() {
        sgVehiculoImpl.actualizarPropietario(getVehiculo(), sesion.getUsuario().getId());
        vehiculo = sgVehiculoImpl.buscarVehiculoPorId(vehiculo.getId());
    }

    public void seleccionarEstado() {
        sgVehiculoImpl.actualizarEstado(getVehiculo(), sesion.getUsuario().getId());
        vehiculo = sgVehiculoImpl.buscarVehiculoPorId(vehiculo.getId());
    }

    public void deleteVehiculo() throws SIAException, Exception {
        sgVehiculoImpl.delete(this.getVehiculo(), sesion.getUsuario().getId());
        //Recargar lista de Vehículos
        setLista(new ListDataModel(sgVehiculoImpl.getAllVehiculoByOficinaList(sesion.getOficinaActual().getId())));
    }

    public void bajaVehiculo() throws SIAException, Exception {
        sgVehiculoImpl.baja(this.getVehiculo().getId(), sesion.getUsuario().getId());
        //Recargar lista de Vehículos
        setLista(new ListDataModel(sgVehiculoImpl.getAllVehiculoByOficinaList(sesion.getOficinaActual().getId())));
    }

    public void reloadVehiculo() {
        this.setVehiculo(sgVehiculoImpl.buscarVehiculoPorId(getVehiculo().getId()));
    }

    public void restartKilometrajeVehiculo() throws SIAException, Exception {
        sgKilometrajeImpl.restartKilometraje(this.getVehiculo().getId(), sesion.getOficinaActual().getId(), this.mensaje, sesion.getUsuario().getId());
        //quitar todos los mantenimientos preventivos

    }

    public void modifyKilometrajeVehiculo() throws SIAException, Exception {
        this.sgKilometrajeImpl.updateSgKilometrajeActual(this.getVehiculo().getId(), sesion.getOficinaActual().getId(), getIdMoneda(), getMensaje(), this.sesion.getUsuario().getId());
    }

    public void saveMarca() throws SIAException, Exception {
        marca.setSgTipo(new SgTipo(idTipo));
        sgMarcaImpl.save(sesion.getUsuario().getId(), this.marca.getNombre());
    }

    public void saveModelo() throws SIAException, Exception {
        this.modelo.setSgMarca(sgMarcaImpl.find(this.idMarca));
        this.modelo.setSgTipoEspecifico(sgTipoEspecificoImpl.find(this.idTipoEspecifico));
        sgModeloImpl.save(sesion.getUsuario().getId(), this.modelo.getNombre(), modelo.getSgMarca().getId(), modelo.getSgTipoEspecifico().getId());
        getModelosByTipoEspecifico();
    }

    public void saveTipoEspecificoVehiculo() throws SIAException, Exception {
        this.sgTipoEspecifico.setPago(Constantes.BOOLEAN_FALSE);
        sgTipoEspecificoImpl.save(idTipo, this.sgTipoEspecifico, sesion.getUsuario().getId());
    }

    /**
     * Asignar vehiculos
     *
     * @return
     */
    public boolean buscarLicenciaVigente() {
        try {
            return sgLicenciaImpl.buscarLicenciaVigentePorUsuario(getUser()) != null;
        } catch (Exception e) {
            UtilLog4j.log.error(e);
            return false;
        }
    }

    public boolean verificaMantenimiento() {
        SgVehiculoMantenimiento sgVehiculoMantenimiento = sgVehiculoMantenimientoImpl.findRegistroEntradaNOTerminado(getVehiculo().getId());
        if (sgVehiculoMantenimiento != null) {
            return sgVehiculoMantenimiento.getImporte().intValue() <= 0; //Esta en mantenimiento
            //No Esta en mantenimiento
        } else {
            return false;// No hay manto
        }
    }

    public SgVehiculoChecklist buscarUltimoCheckList() {
        try {

            return sgVehiculoChecklistImpl.buscarUltimoChecklist(getVehiculo().getId());
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio una excepción al traer checklist " + e.getMessage());
            return null;
        }
    }

    public void traerVehiculoAsignadoPorVehiculo() {
        try {
            setLista(new ListDataModel(sgAsignarVehiculoImpl.traerUsuarioPorVehiculo(getVehiculo().getId())));
//            UtilLog4j.log.info(this, "Lista: " + getLista().getRowCount());
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al traer vehiculos" + e.getMessage());
        }
    }

    public boolean completarAsignacionVehiculo() throws DocumentException {
        boolean v = false;
        v = sgAsignarVehiculoImpl.guardarAsignacionVehiculo(sesion.getUsuario(), getUser(), getSgAsignarVehiculo(), getVehiculo().getId(), getSgVehiculoChecklist().getSgChecklist().getId());
//        Guarda el registro/
        if (v) {
            mapaDatos.put("asignar", traerAsignacionVehiculo());
            FacesUtils.addInfoMessage("Se asignó el vehiculo al usuario");
        } else {
            FacesUtils.addInfoMessage("No se pudo, no se pudo, no se puede");
        }
        return v;
    }

    public boolean completarModificacionVehiculo() {
        return sgAsignarVehiculoImpl.modificarAsiganacion(sesion.getUsuario(), getSgAsignarVehiculo());
    }

    public void eliminarAsignarVehiculo() {
        sgAsignarVehiculoImpl.eliminarAsiganacion(sesion.getUsuario(), getSgAsignarVehiculo());
    }

    public void eliminarRecepcionVehiculo() {
        boolean v;
        v = sgAsignarVehiculoImpl.eliminarRecepcion(sesion.getUsuario(), getSgAsignarVehiculo());
        if (v) {
            SgAsignarVehiculo asignar = sgAsignarVehiculoImpl.find(getSgAsignarVehiculo().getPertenece());
            if (asignar != null) {
                sgAsignarVehiculoImpl.asignacionSinTerminar(sesion.getUsuario(), asignar);
            }
        }
    }

    public boolean guardarCartaAsignacion(String fileName, String ruta, String contentType, long size) throws SIAException, Exception {
        boolean v = false;
        SiAdjunto siAdjunto
                = siAdjuntoImpl.save(
                        fileName,
                        ruta + File.separator + fileName,
                        contentType,
                        size,
                        sesion.getUsuario().getId()
                );
//        UtilLog4j.log.info(this, "Aqui después de guardar el archivo");
        if (siAdjunto != null) {
            if (getSgAsignarVehiculo().getSiOperacion().getId() == 1) {
                v = sgAsignarVehiculoImpl.guardarCartaAsignacion(sesion.getUsuario(), getSgAsignarVehiculo(), siAdjunto);
            } else if (getSgAsignarVehiculo().getSiOperacion().getId() == 2) {
                v = sgAsignarVehiculoImpl.guardarCartaRecepcion(sesion.getUsuario(), getSgAsignarVehiculo(), siAdjunto);
            }

        }
//        else {
//            siAdjuntoImpl.eliminarArchivo(siAdjunto, sesion.getUsuario().getId(), Constantes.BOOLEAN_TRUE);
//        }
        return v;
    }

    public String dirCartaAsigna() {
        //return siParametroImpl.find(1).getUploadDirectory() + "SGyL/Vehiculo/carta" + "/" + getSgAsignarVehiculo().getId() + "/";
        return "SGyL/Vehiculo/carta/" + getSgAsignarVehiculo().getId();
    }

    public void quitarCartaAsigna() {

        SiAdjunto adjunto = getSgAsignarVehiculo().getSiAdjunto();

        if (adjunto == null) {
            FacesUtils.addErrorMessage("No se localizó el archivo adjunto.");
        } else {
            try {
                proveedorAlmacenDocumentos.getAlmacenDocumentos().borrarDocumento(adjunto.getUrl());

                getSgAsignarVehiculo().setSiAdjunto(null);
                sgAsignarVehiculoImpl.quitarCarta(
                        sesion.getUsuario(),
                        getSgAsignarVehiculo(),
                        getIdTipoEspecifico()
                );

                siAdjuntoImpl.eliminarArchivo(
                        getSgAsignarVehiculo().getSiAdjunto(),
                        sesion.getUsuario().getId(),
                        Constantes.BOOLEAN_TRUE
                );
            } catch (SIAException ex) {
                LOGGER.error("Eliminando adjunto " + adjunto.getUrl(), ex);
            }
        }
    }

    public SgAsignarVehiculo buscarRecepcionVehiculo() {
        try {
            return sgAsignarVehiculoImpl.buscarRecepcionVehiculo(getSgAsignarVehiculo());
        } catch (Exception e) {
            return null;
        }
    }

    public DataModel traerAsignacionVehiculo() {
        return new ListDataModel(sgAsignarVehiculoImpl.traerAsignacionVehiculo(getVehiculo().getId()));
    }

    public List<String> autocompletar(String cadena) {
        //usuarioImpl.
        List<UsuarioVO> lu = apCampoUsuarioRhPuestoImpl.traerUsuarioCampo(sesion.getUsuario().getApCampo().getId());
        List<String> nombres = new ArrayList<>();
        lu.stream().filter(us -> us.getNombre().toLowerCase().startsWith(cadena.toLowerCase())).forEach(u -> {
            nombres.add(u.getNombre());
        });
        return nombres;

    }

    public void traerVehiculoPorUsuario() {
        setLista(new ListDataModel(sgAsignarVehiculoImpl.buscarVehiculoAsignado(getUser())));
    }

    public void openPopupDeleteVehiculo(VehiculoVO vVo) {
        try {
            setVehiculo(vVo);
            deleteVehiculo();
        } catch (Exception ex) {
            Logger.getLogger(VehiculoBeanModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void openPopupCambioOficina(int idVehiculo) {
        setVehiculo(getVehiculoById(idVehiculo));

        if (verificaViaje()) {
            FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sgl.vehiculo.viaje"));
        } else {
            if (verificaMantenimiento()) {
                FacesUtils.addErrorMessage(FacesUtils.getKeyResourceBundle("sgl.vehiculo.mantenimiento"));
            } else {
                setIdOficina2(-1);
                setMensaje("");
                PrimeFaces.current().executeScript("PF('dlgCambiarVehiculoOficina').show();");
            }
        }
    }

    //>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> Catálogo colores - INICIO <<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<
    public void traerColorsItems() {
        List<SgColor> colorList = null;
        //List<SelectItem> colorListItem = null;
        try {
            colorList = sgColorImpl.getAllColors(Constantes.BOOLEAN_FALSE);
            this.colorListItem = new ArrayList<SelectItem>();
            for (SgColor c : colorList) {
                SelectItem item = new SelectItem(c.getId(), c.getNombre());
                this.colorListItem.add(item);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion en traer colores " + e.getMessage());
            FacesUtils.addErrorMessage(new SIAException().getMessage());
        }
    }

    public DataModel traerColores() {
        try {
            setLista(new ListDataModel(sgColorImpl.getAllColors(Constantes.BOOLEAN_FALSE)));
            return getLista();
        } catch (Exception e) {
            return null;
        }
    }

    public boolean buscarColorOcupado() {
        boolean v = false;
        v = sgColorImpl.buscarColorOcupado(getSgColor().getId());
        return v;
    }

    public void eliminarColor() {
        sgColorImpl.eliminarColor(sesion.getUsuario(), getSgColor(), Constantes.ELIMINADO);
    }

    public SgColor buscarColorPorNombre() {
        try {
            return sgColorImpl.buscarPorNombre(getSgColor().getNombre());
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Excepcion al buscar el color " + e.getMessage());
            return null;
        }
    }

    public void guardarColor() {
        UtilLog4j.log.info(this, "Color; " + getSgColor().getNombre());
        sgColorImpl.guardarColor(sesion.getUsuario(), getSgColor());
    }

    public void modificarColor() {
        sgColorImpl.modificarColor(sesion.getUsuario(), getSgColor());
    }

//*********************** FIN CATALOGO DE COLORES       * * ************/
    /**
     * CATALOGO VE
     *
     * @return
     * @throws sia.excepciones.SIAException
     */
    public DataModel traerVehiculoPorOficina() throws SIAException, Exception {
        return getLista();
    }

    public void traerChecklistVehiculo() {
        try {
            mapaDatos.put("checkListVehiAsig", new ListDataModel(sgChecklistDetalleImpl.getAllItemsChecklistList(getSgAsignarVehiculo().getSgChecklist().getId(), Constantes.NO_ELIMINADO)));

        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
    }

    public DataModel traerChecklistVehiculoRecepcion() {
        try {
            return new ListDataModel(sgChecklistDetalleImpl.getAllItemsChecklistList(getSgAsignarVehiculoRecibido().getSgChecklist().getId(), Constantes.NO_ELIMINADO));
        } catch (Exception e) {
            return null;
        }
    }

    public SgChecklistExtVehiculo buscarChecklistExterior() {
        try {
            return sgChecklistExtVehiculoImpl.buscarPorChecklist(getSgAsignarVehiculo().getSgChecklist());
        } catch (Exception e) {
            return null;
        }
    }

    public SgChecklistLlantas buscarChecklistLlantas() {
        try {
            return sgChecklistLlantasImpl.buscarPorChecklist(getSgAsignarVehiculo().getSgChecklist());
        } catch (Exception e) {
            return null;
        }
    }

    public boolean updateChecklistVehiculo() {
        boolean updateChecklistSuccessfull = sgChecklistImpl.updateChecklist(this.checklist, this.checklistVODataModel, sesion.getUsuario().getId());
        return updateChecklistSuccessfull;
    }

    public void createChecklistLlantasVehiculo() throws SIAException, Exception {
        if (this.checklistLlantas.getRefaccion() == null || this.checklistLlantas.getRefaccion().isEmpty()) {
            this.checklistLlantas.setRefaccion("0");
        }
        this.checklistLlantas.setSgChecklist(this.checklist);
        this.checklistLlantas.setBuenEstado(this.flag ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE);
        sgChecklistLlantasImpl.create(this.checklistLlantas, sesion.getUsuario().getId());
    }

    public void updateChecklistLlantasVehiculo() throws SIAException, Exception {
        if (this.checklistLlantas.getRefaccion() == null || this.checklistLlantas.getRefaccion().isEmpty()) {
            this.checklistLlantas.setRefaccion("0");
        }
        this.checklistLlantas.setBuenEstado(this.flag ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE);
        sgChecklistLlantasImpl.update(this.checklistLlantas, sesion.getUsuario().getId());
    }

    public String getDirectoryChecklistExteriorVehiculo() {
        String directorio = siParametroService.find(1).getUploadDirectory();
        if (this.checklist != null) {
            return directorio + Constantes.NOMBRE_MODULO_SERVICIOS_GENERALES + "/" + "Checklist/" + this.checklist.getId() + "/";
        } else {
            return "";
        }
    }

    /**
     * Guarda el adjunto del Checklist exterior de un Vehículo
     *
     * @param nombreArchivo
     * @param contentType
     * @param tamanioArchivo
     * @return
     * @throws SIAException
     * @throws Exception
     */
    public boolean guardarArchivoChecklistExteriorVehiculo(String nombreArchivo, String contentType, Long tamanioArchivo) throws SIAException, Exception {
        String ruta = Constantes.NOMBRE_MODULO_SERVICIOS_GENERALES + "/" + "Checklist/" + this.checklist.getId() + "/" + nombreArchivo;
        this.checklistExtVehiculo.setSiAdjunto(siAdjuntoImpl.save(nombreArchivo, ruta, contentType, tamanioArchivo, sesion.getUsuario().getId()));
        return this.checklistExtVehiculo.getSiAdjunto() != null ? true : false;
    }

    /**
     * Guarda el adjunto del Checklist exterior de un Vehículo
     *
     * @param nombreArchivo
     * @param contentType
     * @param tamanioArchivo
     * @return
     * @throws SIAException
     * @throws Exception
     */
    public boolean guardarArchivoChecklistExteriorVehiculoNuevo(String nombreArchivo, String contentType, long tamanioArchivo) throws SIAException, Exception {
        String ruta = Constantes.NOMBRE_MODULO_SERVICIOS_GENERALES + "/" + "Checklist/" + this.checklist.getId() + "/" + nombreArchivo;
        SiAdjunto adjunto = siAdjuntoImpl.save(nombreArchivo, ruta, contentType, tamanioArchivo, sesion.getUsuario().getId());

        if (adjunto != null) {
            this.checklistExtVehiculo = new SgChecklistExtVehiculo();
            checklistExtVehiculo.setSiAdjunto(adjunto);
            checklistExtVehiculo.setSgChecklist(this.checklist);

            this.checklistExtVehiculo = sgChecklistExtVehiculoImpl.create(checklistExtVehiculo, sesion.getUsuario().getId());
        }

        return adjunto != null;
    }

    /**
     * Elimina físicamente un archivo
     *
     * @param url
     * @return
     */
    public boolean eliminarArchivoFisicamente(String url) throws SIAException {
        LOGGER.info(this, "Url a eliminar: " + url);
        boolean retVal = false;

        try {
            //Files.delete(Paths.get(siParametroService.find(1).getUploadDirectory() + url));

            proveedorAlmacenDocumentos.getAlmacenDocumentos().borrarDocumento(url);

            retVal = true;
        } catch (SIAException e) {
            LOGGER.fatal(this, e);
            throw e;
        }

        return retVal;
    }

    public void deleteAdjuntoChecklistExterior() throws SIAException, Exception {
        if (eliminarArchivoFisicamente(this.checklistExtVehiculo.getSiAdjunto().getUrl())) {
            siAdjuntoImpl.delete(this.checklistExtVehiculo.getSiAdjunto(), sesion.getUsuario().getId());
            //Eliminar registro Checklist Exterior
            sgChecklistExtVehiculoImpl.delete(this.checklistExtVehiculo, sesion.getUsuario().getId());
            this.checklistExtVehiculo = null;
        }
    }

    public void updateAdjunto() throws Exception {
        siAdjuntoImpl.update(this.checklistExtVehiculo.getSiAdjunto(), sesion.getUsuario().getId());
    }

    public SgAsignarVehiculo buscarAsignacionVehiculo() {
        setSgAsignarVehiculoRecibido(sgAsignarVehiculoImpl.buscarRecepcionVehiculo(getSgAsignarVehiculo()));
        return getSgAsignarVehiculoRecibido();
    }

    //Pagos
    public void traerPagoPorServicioVehiculo() {
        mapaDatos.put("pagoVehiculo", new ListDataModel(sgPagoServicioVehiculoImpl.traerPagoPorVheiculo(getVehiculo().getId())));
    }

    public DataModel traerPagoVehiculo() {
        return new ListDataModel(sgPagoServicioVehiculoImpl.traerPagoPorVheiculo(getVehiculo().getId()));
    }

    /**
     * Creo: NLopez
     *
     * @return
     */
    public DataModel traerMovimientosOficinaVehiculo() {
        return new ListDataModel(vehiculoMovimientoImpl.traerMovimientosOficinaVehiculo(getVehiculo().getId()));
    }

    public boolean mofificarDespuesAsignar() {
        return sgAsignarVehiculoImpl.modificarAsignaVehiculoDespuesRecibir(sesion.getUsuario(), getSgAsignarVehiculo().getId());
    }

    public void completarRecibirVehiculo() {
        sgAsignarVehiculoImpl.recibirVehiculo(sesion.getUsuario(), getSgAsignarVehiculo().getId(), getSgAsignarVehiculo(), getSgVehiculoChecklist(), getSgAsignarVehiculo().getSgVehiculo());
        mapaDatos.put("asignar", traerAsignacionVehiculo());
    }

    /**
     * PAgo de vehiculo
     *
     * @param cadenaDigitada
     * @return
     */
    public List<SelectItem> regresaProveedorActivo(String cadenaDigitada) {
        setListaProveedor(soporteProveedor.regresaNombreProveedorActivo(cadenaDigitada, sesion.getRfcEmpresa()));
        return getListaProveedor();
    }

    public List<SelectItem> traerTipoEspecificoPorTipoVehiculo() {
        List<SelectItem> l = new ArrayList<SelectItem>();
        List<TipoEspecificoVo> lc;
        try {
            lc = sgTipoTipoEspecificoImpl.traerPorTipo(idTipo, Constantes.BOOLEAN_TRUE);
            for (TipoEspecificoVo tipoEsp : lc) {
                SelectItem item = new SelectItem(tipoEsp.getId(), tipoEsp.getNombre());
                l.add(item);
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Aqui en la excepción" + e.getMessage());
        }
        return l;
    }

    public void traerPagoPorVehiculo() {
        setSgTipoEspecifico(sgTipoEspecificoImpl.find(getIdTipoEspecifico()));
        mapaDatos.put("pagoVehiculo", new ListDataModel(sgPagoServicioVehiculoImpl.traerPagoPorVheiculo(this.getVehiculo().getId(), idTipo, getSgTipoEspecifico(), Constantes.BOOLEAN_FALSE)));
        setSgPagoServicio(null);
    }

    public boolean existNumeroSerie() throws SIAException, Exception {
        SgVehiculo vehiculoExistenteConMismoNumeroSerie = sgVehiculoImpl.findByNumeroSerie(this.getVehiculo().getSerie(), false, false);

        if (vehiculoExistenteConMismoNumeroSerie != null) {
            return ((this.getVehiculo().getId() != vehiculoExistenteConMismoNumeroSerie.getId()));
        } else {
            return false;
        }
    }

    public boolean existNumeroSerieBaja() throws SIAException, Exception {
//        UtilLog4j.log.info(this, "VehiculoBeanModel.existNumeroSerieBaja()");
        UtilLog4j.log.info(this, "NúmeroSerie: " + this.getVehiculo().getSerie());
        SgVehiculo v = sgVehiculoImpl.findByNumeroSerie(this.getVehiculo().getSerie(), false, true);
        UtilLog4j.log.info(this, "Existe el Vehículo en baja: " + (v != null));
        return (v != null);
    }

    public void buscarTipoEspecificoPorId() {
        setSgTipoEspecifico(sgTipoEspecificoImpl.find(getIdTipoEspecifico()));
    }

    public boolean guardarArchivoPago(String fileName, String path, String contentType, long size) throws SIAException, Exception {
//        UtilLog4j.log.info(this, "Absolute path " + getDirectorio());
        boolean v = false;
        SiAdjunto siAdjunto
                = siAdjuntoImpl.save(
                        fileName,
                        path + File.separator + fileName,
                        contentType,
                        size,
                        sesion.getUsuario().getId()
                );
//        UtilLog4j.log.info(this, "Aqui después de guardar el archivo");
        if (siAdjunto != null) {
            v
                    = sgPagoServicioImpl.agregarArchivoPagoServicio(
                            getSgPagoServicio(),
                            sesion.getUsuario(),
                            siAdjunto
                    );
//            UtilLog4j.log.info(this, "Ahora dspués de agreegar a pago servicio");
            if (!v) {
                siAdjuntoImpl.remove(siAdjunto);
            }
        }
        return v;
    }

    public String getDirectorio() {
        return "SGyL/Pago/" + sgTipoImpl.find(idTipo).getNombre() + "/" + getSgPagoServicio().getId();
    }

    public List<SelectItem> traerMoneda() {
        List<MonedaVO> lc;
        try {
            List<SelectItem> l = new ArrayList<SelectItem>();
            lc = monedaImpl.traerMonedaActiva(Constantes.AP_CAMPO_DEFAULT);
//            UtilLog4j.log.info(this, "LMon: " + lc.size());
            for (MonedaVO mon : lc) {
                SelectItem item = new SelectItem(mon.getId(), mon.getSiglas());
                l.add(item);
            }
            return l;
        } catch (Exception e) {
            return null;
        }
    }

    public void modificarPagoServicio() {
        sgPagoServicioImpl.modificarPagoServicio(getSgPagoServicio(), sesion.getUsuario(), getIdMoneda());
    }

    public void eliminarPagoServicioVehiculo() {
        sgPagoServicioImpl.eliminarPagoServicio(
                this.getVehiculo(),
                getSgPagoServicioVehiculo(),
                sesion.getUsuario(),
                Constantes.BOOLEAN_TRUE
        );

        if (getSgPagoServicio().getSiAdjunto() != null) {
            eliminarComprobante();
        }
    }

    public void eliminarComprobante() {
        try {
            proveedorAlmacenDocumentos.getAlmacenDocumentos().borrarDocumento(
                    getSgPagoServicio().getSiAdjunto().getUrl()
            );

            siAdjuntoImpl.eliminarArchivo(
                    getSgPagoServicio().getSiAdjunto(),
                    sesion.getUsuario().getId(),
                    Constantes.BOOLEAN_TRUE
            );

            getSgPagoServicio().setSiAdjunto(null);
            sgPagoServicioImpl.modificarPagoServicio(
                    getSgPagoServicio(),
                    sesion.getUsuario(),
                    getSgPagoServicio().getMoneda().getId()
            );
        } catch (SIAException ex) {
            LOGGER.error(ex);
        }

        //Se eliminan fisicamente los archivos
//	String path = this.siParametroImpl.find(1).getUploadDirectory();
//	try {
//	    File file = new File(path + getSgPagoServicio().getSiAdjunto().getUrl());
//	    if (file.delete()) {
//		getSgPagoServicio().setSiAdjunto(null);
//		sgPagoServicioImpl.modificarPagoServicio(getSgPagoServicio(), sesion.getUsuario(), getSgPagoServicio().getMoneda().getId());
//		siAdjuntoImpl.eliminarArchivo(getSgPagoServicio().getSiAdjunto(), sesion.getUsuario().getId(), Constantes.BOOLEAN_TRUE);
//	    }
//	    //Elimina la carpeta
//	    //         for (PcTipoDocumento pcTipoDocumento : this.pcClasificacionServicioRemoto.traerArchivoActivo()) {
//	    String dir = "SGyL/Pago/" + getSgTipo().getNombre() + "/" + getSgPagoServicio().getId();
////            UtilLog4j.log.info(this, "Ruta carpeta: " + dir);
//	    File sessionfileUploadDirectory = new File(path + dir);
//	    if (sessionfileUploadDirectory.isDirectory()) {
//		try {
//		    sessionfileUploadDirectory.delete();
//		} catch (SecurityException e) {
//		    UtilLog4j.log.fatal(this, "Error : " + e.getMessage());
//		}
//	    }
//	    //        }
//	} catch (Exception e) {
//	    UtilLog4j.log.fatal(this, "Error : " + e.getMessage());
//	}
    }

    public Proveedor buscarProveedorPorNombre() {
        try {
            return proveedorImpl.getPorNombre(getPro(), sesion.getRfcEmpresa());
        } catch (Exception e) {
            return null;
        }
    }

    public boolean guardarPagoServicioVehiculo() {
        boolean retVal = false;

        try {
            sgPagoServicioImpl.guardarPagoServicio(
                    idTipo,
                    getSgTipoEspecifico(),
                    getSgPagoServicio(),
                    getVehiculo(),
                    sesion.getUsuario(),
                    Constantes.BOOLEAN_FALSE,
                    getIdMoneda(),
                    "Vehiculo",
                    getPro(),
                    sesion.getRfcEmpresa());
            traerPagoPorVehiculo();
            retVal = true;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
        }

        return retVal;
    }

    public SgMarca getMarcaById() {
        if (this.idMarca > 0) {
            return sgMarcaImpl.find(this.idMarca);
        } else {
            return null;
        }
    }

    public SgColor getColorById() {
        if (this.idColor > 0) {
            return sgColorImpl.find(this.idColor);
        } else {
            return null;
        }
    }

    public SgTipoEspecifico getTipoEspecificoById() {
        if (this.idTipoEspecifico > 0) {
            return sgTipoEspecificoImpl.find(this.idTipoEspecifico);
        } else {
            return null;
        }
    }

    //FIN CATALOGO VEHICULO
    //********************Cambiar Vehiculo Oficina
    public List<SelectItem> listaOficina() {
        List<SelectItem> l = new ArrayList<SelectItem>();
        List<OficinaVO> lc;
        try {
            lc = sgOficinaImpl.findByVistoBuenoList(true, false);
            for (OficinaVO ca : lc) {
                SelectItem item = new SelectItem(ca.getId(), ca.getNombre());
                l.add(item);
            }
            return l;
        } catch (Exception e) {
            return null;
        }
    }

    public List<SelectItem> listaEstado() {
        List<SelectItem> l = new ArrayList<SelectItem>();
        List<StatusVO> lc;
        try {
            lc = estatusImpl.traerPorTipo(Constantes.CODIGO_ESTATUS_VEHICULO);
            for (StatusVO ca : lc) {
                SelectItem item = new SelectItem(ca.getIdStatus(), ca.getNombre());
                l.add(item);
            }
            return l;
        } catch (Exception e) {
            return null;
        }
    }

    public List<SelectItem> listaOficina2() {
        List<SelectItem> l = new ArrayList<SelectItem>();
        List<SgOficina> lc;
        try {
            lc = sgOficinaImpl.getOfficeWhitoutCurrent(this.getIdOficina());
            for (SgOficina ca : lc) {
                if (!sesion.getOficinaActual().getNombre().equals(ca.getNombre())) {
                    SelectItem item = new SelectItem(ca.getId(), ca.getNombre());
                    l.add(item);
                }
            }
            return l;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean verificaViaje() {
        if (this.sgVehiculoImpl.vehiculoEnViaje(this.getVehiculo().getId())) {
            return true;
        } else {
            return false;
        }
    }

    public DataModel getAllVehiculoOficina() throws SIAException, Exception {
        setLista(new ListDataModel(sgVehiculoImpl.getAllVehiculoByOficinaList(idOficina)));
        return getLista();
    }

    public void reloadAllVehiculoOficina() throws SIAException, Exception {
        setLista(new ListDataModel(sgVehiculoImpl.getAllVehiculoByOficinaList(idOficina)));
    }

    public void traerVehiculoEstado() {
        try {
            setLista(new ListDataModel(sgVehiculoImpl.traerVehiculoOficinaEstado(idOficina, idEstado)));
        } catch (Exception ex) {
            Logger.getLogger(VehiculoBeanModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Modifico: NLopez 30/10/2013
     *
     * @throws SIAException
     * @throws Exception
     */
    public boolean saveCambioOficina() throws SIAException, Exception {
        return this.sgVehiculoImpl.cambiarOficina(this.getVehiculo().getId(), idOficina2, getMensaje(), this.sesion.getUsuario().getId());
    }
    //**************Fin Cambiar Vehiculo Oficina

    /**
     * ******** Actualizar Kilometrajes de Vehículo *********
     * ******************************************************
     */
    public List getSgKilometrajeForUpdateList() {
        return this.sgKilometrajeImpl.getAllSgKilometrajeBySgVehiculoSgOficina(this.sesion.getOficinaActual().getId(), "numero_placa", true);
    }

    public void updateAllKilometraje() {
//        UtilLog4j.log.info(this, "VehiculoBeanModel.updateAllKilometraje()");

        DataModel<SgKilometrajeVo> dm = getDataModel();
        List<SgKilometrajeVo> l = new ArrayList<SgKilometrajeVo>();

        for (SgKilometrajeVo vo : dm) {
            l.add(vo);
        }

        this.sgKilometrajeImpl.updateAllKilometrajes(l, this.sesion.getUsuario().getId());

        setDataModel(new ListDataModel(getSgKilometrajeForUpdateList()));
    }

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }

    /**
     * @param user the user to set
     */
    public void setUser(String user) {
        this.user = user;
    }

    /**
     * @return the usuario
     */
    public Usuario getUsuario() {
        return usuario;
    }

    /**
     * @param usuario the usuario to set
     */
    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public List<SelectItem> getListaUsuarioSoporte(String cadenaDigitada) {
        return soporteProveedor.regresaUsuarioActivo(cadenaDigitada, 1, "nombre", true, true, false);
    }

    /**
     * @return the mensaje
     */
    public String getMensaje() {
        return mensaje;
    }

    /**
     * @param mensaje the mensaje to set
     */
    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    /**
     * @return the popUp
     */
    public boolean isPopUp() {
        return popUp;
    }

    /**
     * @param popUp the popUp to set
     */
    public void setPopUp(boolean popUp) {
        this.popUp = popUp;
    }

    /**
     * @return the crearPop
     */
    public boolean isCrearPop() {
        return crearPop;
    }

    /**
     * @param crearPop the crearPop to set
     */
    public void setCrearPop(boolean crearPop) {
        this.crearPop = crearPop;
    }

    /**
     * @return the eliminarPop
     */
    public boolean isEliminarPop() {
        return eliminarPop;
    }

    /**
     * @param eliminarPop the eliminarPop to set
     */
    public void setEliminarPop(boolean eliminarPop) {
        this.eliminarPop = eliminarPop;
    }

    /**
     * @return the modificarPop
     */
    public boolean isModificarPop() {
        return modificarPop;
    }

    /**
     * @param modificarPop the modificarPop to set
     */
    public void setModificarPop(boolean modificarPop) {
        this.modificarPop = modificarPop;
    }

    /**
     * @return the subirArchivoPop
     */
    public boolean isSubirArchivoPop() {
        return subirArchivoPop;
    }

    /**
     * @param subirArchivoPop the subirArchivoPop to set
     */
    public void setSubirArchivoPop(boolean subirArchivoPop) {
        this.subirArchivoPop = subirArchivoPop;
    }

    /**
     * @return the lista
     */
    public DataModel getLista() {
        return lista;
    }

    /**
     * @param lista the lista to set
     */
    public void setLista(DataModel lista) {
        this.lista = lista;
    }

    /**
     * @return the idTipoEspecifico
     */
    public int getIdTipoEspecifico() {
        return idTipoEspecifico;
    }

    /**
     * @param idTipoEspecifico the idTipoEspecifico to set
     */
    public void setIdTipoEspecifico(int idTipoEspecifico) {
        this.idTipoEspecifico = idTipoEspecifico;
    }

    /**
     * @return the alerta
     */
    public String getAlerta() {
        return alerta;
    }

    /**
     * @param alerta the alerta to set
     */
    public void setAlerta(String alerta) {
        this.alerta = alerta;
    }

    /**
     * @return the numerDias
     */
    public int getNumerDias() {
        return numerDias;
    }

    /**
     * @param numerDias the numerDias to set
     */
    public void setNumerDias(int numerDias) {
        this.numerDias = numerDias;
    }

    /**
     * @return the sgKilometraje
     */
    public SgKilometraje getSgKilometraje() {
        return sgKilometraje;
    }

    /**
     * @param sgKilometraje the sgKilometraje to set
     */
    public void setSgKilometraje(SgKilometraje sgKilometraje) {
        this.sgKilometraje = sgKilometraje;
    }

    /**
     * @return the idCheclist
     */
    public int getIdChecklist() {
        return idChecklist;
    }

    /**
     * @param idCheclist the idCheclist to set
     */
    public void setIdChecklist(int idChecklist) {
        this.idChecklist = idChecklist;
    }

    /**
     * @return the sgAsignarVehiculo
     */
    public SgAsignarVehiculo getSgAsignarVehiculo() {
        return sgAsignarVehiculo;
    }

    /**
     * @param sgAsignarVehiculo the sgAsignarVehiculo to set
     */
    public void setSgAsignarVehiculo(SgAsignarVehiculo sgAsignarVehiculo) {
        this.sgAsignarVehiculo = sgAsignarVehiculo;
    }

    /**
     * @return the idModulo
     */
    public int getIdModelo() {
        return idModelo;
    }

    /**
     * @param idModulo the idModulo to set
     */
    public void setIdModelo(int idModelo) {
        this.idModelo = idModelo;
    }

    /**
     * @return the idMarca
     */
    public int getIdMarca() {
        return idMarca;
    }

    /**
     * @param idMarca the idMarca to set
     */
    public void setIdMarca(int idMarca) {
        this.idMarca = idMarca;
    }

    /**
     * @return the opcionSeleccionada
     */
    public String getOpcionSeleccionada() {
        return opcionSeleccionada;
    }

    /**
     * @param opcionSeleccionada the opcionSeleccionada to set
     */
    public void setOpcionSeleccionada(String opcionSeleccionada) {
        this.opcionSeleccionada = opcionSeleccionada;
    }

    /**
     * @return the cadenaBuscar
     */
    public String getCadenaBuscar() {
        return cadenaBuscar;
    }

    /**
     * @param cadenaBuscar the cadenaBuscar to set
     */
    public void setCadenaBuscar(String cadenaBuscar) {
        this.cadenaBuscar = cadenaBuscar;
    }

    /**
     * @return the sgChecklistLlantas
     */
    public SgChecklistLlantas getSgChecklistLlantas() {
        return sgChecklistLlantas;
    }

    /**
     * @param sgChecklistLlantas the sgChecklistLlantas to set
     */
    public void setSgChecklistLlantas(SgChecklistLlantas sgChecklistLlantas) {
        this.sgChecklistLlantas = sgChecklistLlantas;
    }

    /**
     * @return the sgChecklistExtVehiculo
     */
    public SgChecklistExtVehiculo getSgChecklistExtVehiculo() {
        return sgChecklistExtVehiculo;
    }

    /**
     * @param sgChecklistExtVehiculo the sgChecklistExtVehiculo to set
     */
    public void setSgChecklistExtVehiculo(SgChecklistExtVehiculo sgChecklistExtVehiculo) {
        this.sgChecklistExtVehiculo = sgChecklistExtVehiculo;
    }

    /**
     * @return the recibirVehiculoPop
     */
    public boolean isRecibirVehiculoPop() {
        return recibirVehiculoPop;
    }

    /**
     * @param recibirVehiculoPop the recibirVehiculoPop to set
     */
    public void setRecibirVehiculoPop(boolean recibirVehiculoPop) {
        this.recibirVehiculoPop = recibirVehiculoPop;
    }

    /**
     * @return the idAsignaVehiculo
     */
    public int getIdAsignaVehiculo() {
        return idAsignaVehiculo;
    }

    /**
     * @param idAsignaVehiculo the idAsignaVehiculo to set
     */
    public void setIdAsignaVehiculo(int idAsignaVehiculo) {
        this.idAsignaVehiculo = idAsignaVehiculo;
    }

    /**
     * @return the sgPagoServicioVehiculo
     */
    public SgPagoServicioVehiculo getSgPagoServicioVehiculo() {
        return sgPagoServicioVehiculo;
    }

    /**
     * @param sgPagoServicioVehiculo the sgPagoServicioVehiculo to set
     */
    public void setSgPagoServicioVehiculo(SgPagoServicioVehiculo sgPagoServicioVehiculo) {
        this.sgPagoServicioVehiculo = sgPagoServicioVehiculo;
    }

    /**
     * @return the idMoneda
     */
    public int getIdMoneda() {
        return idMoneda;
    }

    /**
     * @param idMoneda the idMoneda to set
     */
    public void setIdMoneda(int idMoneda) {
        this.idMoneda = idMoneda;
    }

    /**
     * @return the sgTipoEspecifico
     */
    public SgTipoEspecifico getSgTipoEspecifico() {
        return sgTipoEspecifico;
    }

    /**
     * @param sgTipoEspecifico the sgTipoEspecifico to set
     */
    public void setSgTipoEspecifico(SgTipoEspecifico sgTipoEspecifico) {
        this.sgTipoEspecifico = sgTipoEspecifico;
    }

    /**
     * @return the sgPagoServicio
     */
    public SgPagoServicio getSgPagoServicio() {
        return sgPagoServicio;
    }

    /**
     * @param sgPagoServicio the sgPagoServicio to set
     */
    public void setSgPagoServicio(SgPagoServicio sgPagoServicio) {
        this.sgPagoServicio = sgPagoServicio;
    }

    /**
     * @return the listaProveedor
     */
    public List<SelectItem> getListaProveedor() {
        return listaProveedor;
    }

    /**
     * @param listaProveedor the listaProveedor to set
     */
    public void setListaProveedor(List<SelectItem> listaProveedor) {
        this.listaProveedor = listaProveedor;
    }

    /**
     * @return the listaProveedorBuscar
     */
    public List<String> getListaProveedorBuscar() {
        return listaProveedorBuscar;
    }

    /**
     * @param listaProveedorBuscar the listaProveedorBuscar to set
     */
    public void setListaProveedorBuscar(List<String> listaProveedorBuscar) {
        this.listaProveedorBuscar = listaProveedorBuscar;
    }

    public List<String> traerProveedor() {
        return proveedorImpl.traerNombreProveedorQueryNativo(sesion.getRfcEmpresa(), ProveedorEnum.ACTIVO.getId());
    }

    /**
     * @return the pro
     */
    public String getPro() {
        return pro;
    }

    /**
     * @param pro the pro to set
     */
    public void setPro(String pro) {
        this.pro = pro;
    }

    /**
     * @return the sgVehiculoChecklist
     */
    public SgVehiculoChecklist getSgVehiculoChecklist() {
        return sgVehiculoChecklist;
    }

    /**
     * @param sgVehiculoChecklist the sgVehiculoChecklist to set
     */
    public void setSgVehiculoChecklist(SgVehiculoChecklist sgVehiculoChecklist) {
        this.sgVehiculoChecklist = sgVehiculoChecklist;
    }

    /*
     * @return the modelos
     */
    public List<Vo> getModelos() {
        return modelos;
    }

    /**
     * @param modelos the modelos to set
     */
    public void setModelos(List<Vo> modelos) {
        this.modelos = modelos;
    }

    /**
     * @return the marca
     */
    public SgMarca getMarca() {
        return marca;
    }

    /**
     * @param marca the marca to set
     */
    public void setMarca(SgMarca marca) {
        this.marca = marca;
    }

    /**
     * @return the modelo
     */
    public SgModelo getModelo() {
        return modelo;
    }

    /**
     * @param modelo the modelo to set
     */
    public void setModelo(SgModelo modelo) {
        this.modelo = modelo;
    }

    /**
     * @return the reactivateVehiculo
     */
    public boolean isReactivateVehiculo() {
        return reactivateVehiculo;
    }

    /**
     * @param reactivateVehiculo the reactivateVehiculo to set
     */
    public void setReactivateVehiculo(boolean reactivateVehiculo) {
        this.reactivateVehiculo = reactivateVehiculo;
    }

    /*
     * @return the sgAsignarVehiculoRecibido
     */
    public SgAsignarVehiculo getSgAsignarVehiculoRecibido() {
        return sgAsignarVehiculoRecibido;
    }

    /**
     * @param sgAsignarVehiculoRecibido the sgAsignarVehiculoRecibido to set
     */
    public void setSgAsignarVehiculoRecibido(SgAsignarVehiculo sgAsignarVehiculoRecibido) {
        this.sgAsignarVehiculoRecibido = sgAsignarVehiculoRecibido;
    }

    /*
     * @return the caracteristicaVehiculo
     */
    public CaracteristicaVo getCaracteristicaVo() {
        return caracteristicaVehiculo;
    }

    /**
     * @param caracteristicaVehiculo the caracteristicaVehiculo to set
     */
    public void setCaracteristicaVo(CaracteristicaVo caracteristicaVehiculo) {
        this.caracteristicaVehiculo = caracteristicaVehiculo;
    }

    /**
     * @return the matchesList
     */
    public List<SelectItem> getMatchesList() {
        return matchesList;
    }

    /**
     * @param matchesList the matchesList to set
     */
    public void setMatchesList(List<SelectItem> matchesList) {
        this.matchesList = matchesList;
    }

    /**
     * @return the prefijo
     */
    public String getPrefijo() {
        return prefijo;
    }

    /**
     * @param prefijo the prefijo to set
     */
    public void setPrefijo(String prefijo) {
        this.prefijo = prefijo;
    }

    /**
     * @return the caracteristicas
     */
    public List<SelectItem> getCaracteristicas() {
        return caracteristicas;
    }

    /**
     * @param caracteristicas the caracteristicas to set
     */
    public void setCaracteristicas(List<SelectItem> caracteristicas) {
        this.caracteristicas = caracteristicas;
    }

    /**
     * @return the vehiculoChecklist
     */
    public SgVehiculoChecklist getVehiculoChecklist() {
        return vehiculoChecklist;
    }

    /**
     * @param vehiculoChecklist the vehiculoChecklist to set
     */
    public void setVehiculoChecklist(SgVehiculoChecklist vehiculoChecklist) {
        this.vehiculoChecklist = vehiculoChecklist;
    }

    /**
     * @return the checklistVODataModel
     */
    public DataModel getChecklistVODataModel() {
        return checklistVODataModel;
    }

    /**
     * @param checklistVODataModel the checklistVODataModel to set
     */
    public void setChecklistVODataModel(DataModel checklistVODataModel) {
        this.checklistVODataModel = checklistVODataModel;
    }

    /**
     * @return the asignacionSinTerminar
     */
    public boolean isAsignacionSinTerminar() {
        return asignacionSinTerminar;
    }

    /**
     * @param asignacionSinTerminar the asignacionSinTerminar to set
     */
    public void setAsignacionSinTerminar(boolean asignacionSinTerminar) {
        this.asignacionSinTerminar = asignacionSinTerminar;
    }

    /*
     * @return the checklistExtVehiculo
     */
    public SgChecklistExtVehiculo getChecklistExtVehiculo() {
        return checklistExtVehiculo;
    }

    /**
     * @param checklistExtVehiculo the checklistExtVehiculo to set
     */
    public void setChecklistExtVehiculo(SgChecklistExtVehiculo checklistExtVehiculo) {
        this.checklistExtVehiculo = checklistExtVehiculo;
    }

    /**
     * @return the checklistLlantas
     */
    public SgChecklistLlantas getChecklistLlantas() {
        return checklistLlantas;
    }

    /**
     * @param checklistLlantas the checklistLlantas to set
     */
    public void setChecklistLlantas(SgChecklistLlantas checklistLlantas) {
        this.checklistLlantas = checklistLlantas;
    }

    /**
     * @return the checklist
     */
    public SgChecklist getChecklist() {
        return checklist;
    }

    /**
     * @param checklist the checklist to set
     */
    public void setChecklist(SgChecklist checklist) {
        this.checklist = checklist;
    }

    /**
     * @return the flag
     */
    public boolean isFlag() {
        return flag;
    }

    /**
     * @param flag the flag to set
     */
    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    /*
     * @return the verDetallePop
     */
    public boolean isVerDetallePop() {
        return verDetallePop;
    }

    /**
     * @param verDetallePop the verDetallePop to set
     */
    public void setVerDetallePop(boolean verDetallePop) {
        this.verDetallePop = verDetallePop;
    }

    /**
     * @return the pagoPop
     */
    public boolean isPagoPop() {
        return pagoPop;
    }

    /**
     * @param pagoPop the pagoPop to set
     */
    public void setPagoPop(boolean pagoPop) {
        this.pagoPop = pagoPop;
    }

    /**
     * @return the idPais
     */
    public int getIdPais() {
        return idPais;
    }

    /**
     * @param idPais the idPais to set
     */
    public void setIdPais(int idPais) {
        this.idPais = idPais;
    }

    /*
     * @return the sgColor
     */
    public SgColor getSgColor() {
        return sgColor;
    }

    /**
     * @param sgColor the sgColor to set
     */
    public void setSgColor(SgColor sgColor) {
        this.sgColor = sgColor;
    }

    /**
     * @return the modificarPopUp
     */
    public boolean isModificarPopUp() {
        return modificarPopUp;
    }

    /**
     * @param modificarPopUp the modificarPopUp to set
     */
    public void setModificarPopUp(boolean modificarPopUp) {
        this.modificarPopUp = modificarPopUp;
    }

    /**
     * @return the crearPopUp
     */
    public boolean isCrearPopUp() {
        return crearPopUp;
    }

    /**
     * @param crearPopUp the crearPopUp to set
     */
    public void setCrearPopUp(boolean crearPopUp) {
        this.crearPopUp = crearPopUp;
    }

    /**
     * @return the colorListItem
     */
    public List<SelectItem> getColorListItem() {
        return colorListItem;
    }

    /**
     * @param colorListItem the colorListItem to set
     */
    public void setColorListItem(List<SelectItem> colorListItem) {
        this.colorListItem = colorListItem;
    }

    /**
     * @return the idColor
     */
    public int getIdColor() {
        return idColor;
    }

    /**
     * @param idColor the idColor to set
     */
    public void setIdColor(int idColor) {
        this.idColor = idColor;
    }

    /**
     * @return the kilometrajeActual
     */
    public SgKilometraje getKilometrajeActual() {
        this.kilometrajeActual = sgKilometrajeImpl.findKilometrajeActualVehiculo(this.getVehiculo().getId());
        if (this.kilometrajeActual == null) {
            this.kilometrajeActual = new SgKilometraje();
            this.kilometrajeActual.setKilometraje(0);
        }
        return kilometrajeActual;
    }

    /**
     * @param kilometrajeActual the kilometrajeActual to set
     */
    public void setKilometrajeActual(SgKilometraje kilometrajeActual) {
        this.kilometrajeActual = kilometrajeActual;
    }

    /**
     * @return the listaOficinas
     */
    public List<SelectItem> getListaOficinas() {
        return listaOficinas;
    }

    /**
     * @param listaOficinas the listaOficinas to set
     */
    public void setListaOficinas(List<SelectItem> listaOficinas) {
        this.listaOficinas = listaOficinas;
    }

    /**
     * @return the sgOficinaImpl
     */
    public SgOficinaImpl getSgOficinaImpl() {
        return sgOficinaImpl;
    }

    /**
     * @param sgOficinaImpl the sgOficinaImpl to set
     */
    public void setSgOficinaImpl(SgOficinaImpl sgOficinaImpl) {
        this.sgOficinaImpl = sgOficinaImpl;
    }

    /**
     * @return the idOficina
     */
    public int getIdOficina() {
        return idOficina;
    }

    /**
     * @param idOficina the idOficina to set
     */
    public void setIdOficina(int idOficina) {
        this.idOficina = idOficina;
    }

    /**
     * @return the dataModel
     */
    public DataModel getDataModel() {
        return dataModel;
    }

    /**
     * @param dataModel the dataModel to set
     */
    public void setDataModel(DataModel dataModel) {
        this.dataModel = dataModel;
    }

    /**
     * @return the idOficina2
     */
    public int getIdOficina2() {
        return idOficina2;
    }

    /**
     * @param idOficina2 the idOficina2 to set
     */
    public void setIdOficina2(int idOficina2) {
        this.idOficina2 = idOficina2;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

    /**
     * @param soporteProveedor the soporteProveedor to set
     */
    public void setSoporteProveedor(SoporteProveedor soporteProveedor) {
        this.soporteProveedor = soporteProveedor;
    }

    /**
     * @return the vehiculo
     */
    public VehiculoVO getVehiculo() {
        return vehiculo;
    }

    /**
     * @param vehiculo the vehiculo to set
     */
    public void setVehiculo(VehiculoVO vehiculo) {
        this.vehiculo = vehiculo;
    }

    /**
     * @return the pagos
     */
    public DataModel getPagos() {
        return pagos;
    }

    /**
     * @param pagos the pagos to set
     */
    public void setPagos(DataModel pagos) {
        this.pagos = pagos;
    }

    /**
     * @return the idEstado
     */
    public int getIdEstado() {
        return idEstado;
    }

    /**
     * @param idEstado the idEstado to set
     */
    public void setIdEstado(int idEstado) {
        this.idEstado = idEstado;
    }

    /**
     * @return the listaTaller
     */
    public List<SelectItem> getListaTaller() {
        return listaTaller;
    }

    /**
     * @param listaTaller the listaTaller to set
     */
    public void setListaTaller(List<SelectItem> listaTaller) {
        this.listaTaller = listaTaller;
    }

    /**
     * @return the listaEstado
     */
    public List<SelectItem> getListaEstado() {
        return listaEstado;
    }

    /**
     * @param listaEstado the listaEstado to set
     */
    public void setListaEstado(List<SelectItem> listaEstado) {
        this.listaEstado = listaEstado;
    }

    /**
     * @return the mapaDatos
     */
    public Map<String, DataModel> getMapaDatos() {
        return mapaDatos;
    }

    /**
     * @param mapaDatos the mapaDatos to set
     */
    public void setMapaDatos(Map<String, DataModel> mapaDatos) {
        this.mapaDatos = mapaDatos;
    }

    /**
     * @return the idTipo
     */
    public int getIdTipo() {
        return idTipo;
    }

    /**
     * @param idTipo the idTipo to set
     */
    public void setIdTipo(int idTipo) {
        this.idTipo = idTipo;
    }
}
