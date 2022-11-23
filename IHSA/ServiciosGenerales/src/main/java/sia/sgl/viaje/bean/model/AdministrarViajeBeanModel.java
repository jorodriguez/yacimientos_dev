/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.viaje.bean.model;

import com.google.common.base.Joiner;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;

import javax.inject.Named;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.modelo.GrInterseccion;
import sia.modelo.SgRutaTerrestre;
import sia.modelo.SgViaje;
import sia.modelo.SgViajeSiMovimiento;
import sia.modelo.SgViajero;
import sia.modelo.sgl.oficina.vo.OficinaVO;
import sia.modelo.sgl.viaje.vo.InvitadoVO;
import sia.modelo.sgl.viaje.vo.SolicitudViajeVO;
import sia.modelo.sgl.viaje.vo.VehiculoVO;
import sia.modelo.sgl.viaje.vo.ViajeVO;
import sia.modelo.sgl.viaje.vo.ViajeroVO;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.notificaciones.sgl.viaje.Impl.NotificacionViajeImpl;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.GerenciaImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.gr.impl.GrInterseccionImpl;
import sia.servicios.sgl.impl.SgInvitadoImpl;
import sia.servicios.sgl.impl.SgOficinaImpl;
import sia.servicios.sgl.vehiculo.impl.SgAsignarVehiculoImpl;
import sia.servicios.sgl.vehiculo.impl.SgVehiculoImpl;
import sia.servicios.sgl.viaje.impl.SgRutaTerrestreImpl;
import sia.servicios.sgl.viaje.impl.SgSolicitudViajeImpl;
import sia.servicios.sgl.viaje.impl.SgViajeImpl;
import sia.servicios.sgl.viaje.impl.SgViajeSiMovimientoImpl;
import sia.servicios.sgl.viaje.impl.SgViajeVehiculoImpl;
import sia.servicios.sgl.viaje.impl.SgViajeroImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.servicios.sistema.impl.SiMovimientoImpl;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Named(value = "administrarViajeBean")
@ViewScoped
public class AdministrarViajeBeanModel implements Serializable {

    @Inject
    private Sesion sesion;

    @Inject
    private SgSolicitudViajeImpl sgSolicitudViajeImpl;
    @Inject
    private SgViajeImpl sgViajeImpl;
    @Inject
    private SgOficinaImpl oficinaService;
    @Inject
    private SgVehiculoImpl sgVehiculoImpl;
    @Inject
    private UsuarioImpl usuarioImpl;
    @Inject
    private SgRutaTerrestreImpl sgRutaTerrestreImpl;
    @Inject
    private SgAsignarVehiculoImpl asignarVehiculoImpl;
    @Inject
    private SiManejoFechaImpl siManejoFechaLocal;
    @Inject
    private SgViajeroImpl sgViajeroImpl;
    @Inject
    private SgInvitadoImpl sgInvitadoImpl;
    @Inject
    private SgViajeVehiculoImpl sgViajeVehiculoImpl;
    @Inject
    private NotificacionViajeImpl notificacionViajeImpl;
    @Inject
    private GerenciaImpl gerenciaImpl;
    @Inject
    private GrInterseccionImpl grInterseccionImpl;
    @Inject
    private SgViajeSiMovimientoImpl sgViajeSiMovimientoImpl;
    @Inject
    private SiMovimientoImpl siMovimientoImpl;
    @Inject
    ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;

    private List<SolicitudViajeVO> solicitudesViajeros = new ArrayList<>();
    private List<ViajeVO> viajesCreados = new ArrayList<>();
    private List<ViajeVO> viajesProgramados = new ArrayList<>();
    private List<ViajeVO> viajesEnProceso = new ArrayList<>();
    private List<ViajeroVO> listaViajeros = new ArrayList<>();
    private List<ViajeroVO> listaViajerosBajar = new ArrayList<>();
    private ViajeVO viajeVO;
    private String horaSalida;
    private int idOficinaOrigen;
    private List<SelectItem> listaOficina = new ArrayList<>();
    private List<SelectItem> listaVehiculos = new ArrayList<>();
    private List<String> listaEmpleadosSGL;
    private List<SelectItem> listaRuta = new ArrayList<>();
    private List<SelectItem> destinos = new ArrayList<>();

    private int idVehiculo;
    private int idOficinaVehiculo;
    private int idOficinaRuta;
    private boolean redondoSencillo = true;
    private boolean tieneResponsable = false;
    private List<List<Object[]>> listaDestino = new ArrayList<List<Object[]>>();
    private Date fechaInt1 = null;
    private Date fechaInt2 = null;
    private String textBusqueda;
    private String empleadoEmergente = "";
    private String invitadoEmergente = "";
    private int estatusViaje = Constantes.CERO;
    private boolean modificar = Constantes.FALSE;
    private String ultimaActualizacion;
    @Getter
    @Setter
    private boolean vehiculoSgl;
    @Getter
    @Setter
    private boolean empleadosSgl;
    private List<ViajeroVO> listaViajerosSolicitud;

    public AdministrarViajeBeanModel() {
    }

    @PostConstruct
    public void iniciarConversasionCrearViaje() {
        setTextBusqueda("");
        vehiculoSgl = true;
        empleadosSgl = true;
        listaViajerosSolicitud = new ArrayList<>();
        fechaInt1 = new Date();
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 4);
        fechaInt2 = c.getTime();
        cargarSolicitudesYViajes();
    }

    public void cargarSolicitudesYViajes() {
        setSolicitudesViajeros(traerSolicitudesList());
        setViajesCreados(traerLstViajesCreados());
        setViajesProgramados(traerLstViajesProgramados());
        setViajesEnProceso(traerLstViajesEnProceso());
        setUltimaActualizacion(Constantes.FMT_ddMMyyyh_mm_a.format(new Date()));
    }

    /**
     * @return the viajesCreados
     */
    public List<ViajeVO> getViajesCreados() {
        return viajesCreados;
    }

    /**
     * @param viajesCreados the viajesCreados to set
     */
    public void setViajesCreados(List<ViajeVO> viajesCreados) {
        this.viajesCreados = viajesCreados;
    }

    /**
     * @return the viajesProgramados
     */
    public List<ViajeVO> getViajesProgramados() {
        return viajesProgramados;
    }

    /**
     * @param viajesProgramados the viajesProgramados to set
     */
    public void setViajesProgramados(List<ViajeVO> viajesProgramados) {
        this.viajesProgramados = viajesProgramados;
    }

    /**
     * @return the viajesEnProceso
     */
    public List<ViajeVO> getViajesEnProceso() {
        return viajesEnProceso;
    }

    /**
     * @param viajesEnProceso the viajesEnProceso to set
     */
    public void setViajesEnProceso(List<ViajeVO> viajesEnProceso) {
        this.viajesEnProceso = viajesEnProceso;
    }

    public List<SolicitudViajeVO> traerSolicitudesList() {
        List<SolicitudViajeVO> l = new ArrayList<>();
        try {
            l.addAll(sgSolicitudViajeImpl.traerSolicitudesTerrestre(sesion.getOficinaActual().getId(),
                    Constantes.ESTATUS_PARA_HACER_VIAJE,
                    sesion.getUsuario().getId(),
                    getFechaInt1(), getFechaInt2(),
                    getTextBusqueda(),
                    0));
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
        return l;
    }

    public List<ViajeVO> traerLstViajesCreados() {
        List<ViajeVO> l = new ArrayList<>();
        try {
            l.addAll(sgViajeImpl.getRoadTripByExit(sesion.getOficinaActual().getId(),
                    Constantes.ESTATUS_VIAJE_CREADO,
                    Constantes.CERO,
                    true,
                    getFechaInt1(), getFechaInt2(),
                    false,
                    0,
                    true,
                    null));
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
        return l;
    }

    public List<ViajeVO> traerLstViajesProgramados() {
        List<ViajeVO> l = new ArrayList<>();
        try {
            l.addAll(sgViajeImpl.getRoadTripByExit(sesion.getOficinaActual().getId(),
                    Constantes.ESTATUS_VIAJE_POR_SALIR,
                    Constantes.CERO,
                    true,
                    getFechaInt1(), getFechaInt2(),
                    false,
                    0,
                    true,
                    null));
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
        return l;
    }

    public List<ViajeVO> traerLstViajesEnProceso() {
        List<ViajeVO> l = new ArrayList<ViajeVO>();
        try {
            //seria mejor un select in, en vez de 2 select distintos
            l.addAll(sgViajeImpl.getRoadTripDesOffice(sesion.getOficinaActual().getId(),
                    Constantes.ESTATUS_VIAJE_PROCESO, sesion.getUsuario().getId(), Constantes.TRUE));
            l.addAll(sgViajeImpl.getRoadTripDesOffice(sesion.getOficinaActual().getId(),
                    Constantes.ESTATUS_VIAJE_EN_DESTINO, sesion.getUsuario().getId(), Constantes.TRUE));
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
        return l;
    }

    /**
     * @return the solicitudesViajeros
     */
    public List<SolicitudViajeVO> getSolicitudesViajeros() {
        return solicitudesViajeros;
    }

    /**
     * @param solicitudesViajeros the solViajeros to set
     */
    public void setSolicitudesViajeros(List<SolicitudViajeVO> solicitudesViajeros) {
        this.solicitudesViajeros = solicitudesViajeros;
    }

    /**
     * @return the viajeVO
     */
    public ViajeVO getViajeVO() {
        return viajeVO;
    }

    /**
     * @param viajeVO the viajeVO to set
     */
    public void setViajeVO(ViajeVO viajeVO) {
        this.viajeVO = viajeVO;
    }

    public void inicializarCrearViaje() {
        try {
            setViajeVO(new ViajeVO());
            getViajeVO().setId(Constantes.CERO);
            getViajeVO().setResponsable("");
            setTieneResponsable(false);
            getViajeVO().setIdResponsable("");
            getViajeVO().setFechaProgramada(new Date());
            setHoraSalida("07:00AM");
            setRedondoSencillo(true);
            getViajeVO().setRedondo(Constantes.BOOLEAN_TRUE);
            getViajeVO().setIdOficinaOrigen(sesion.getOficinaActual().getId());
            getViajeVO().setOrigen(sesion.getOficinaActual().getNombre());
            setIdOficinaVehiculo(getViajeVO().getIdOficinaOrigen());
            setListaOficina(listaOficina());
            setListaVehiculos(listaVehiculos());
            llenarListaEmpleadosSGL(Constantes.FALSE);
            llenarListaDestino();
            setIdVehiculo(-1);
            getViajeVO().setIdRuta(Constantes.CERO);
            getViajeVO().setDestino("");
            getViajeVO().setSgViaje(Constantes.CERO);
            setModificar(Constantes.FALSE);
            empleadoEmergente = "";
            invitadoEmergente = "";
            PrimeFaces.current().executeScript("$(dialogoPopUpCrearViaje).modal('show');");
        } catch (Exception e) {
            UtilLog4j.log.error(this, e.getMessage());
            UtilLog4j.log.error(e);
        }
    }

    /**
     * @return the horaSalida
     */
    public String getHoraSalida() {
        return horaSalida;
    }

    /**
     * @param horaSalida the horaSalida to set
     */
    public void setHoraSalida(String horaSalida) {
        this.horaSalida = horaSalida;
    }

    /**
     * @return the idOficinaOrigen
     */
    public int getIdOficinaOrigen() {
        return idOficinaOrigen;
    }

    /**
     * @param idOficinaOrigen the idOficinaOrigen to set
     */
    public void setIdOficinaOrigen(int idOficinaOrigen) {
        this.idOficinaOrigen = idOficinaOrigen;
    }

    /**
     * @return the listaOficina
     */
    public List<SelectItem> getListaOficina() {
        return listaOficina;
    }

    /**
     * @param listaOficina the listaOficinaVehiculo to set
     */
    public void setListaOficina(List<SelectItem> listaOficina) {
        this.listaOficina = listaOficina;
    }

    /**
     * @return the listaVehiculos
     */
    public List<SelectItem> getListaVehiculos() {
        return listaVehiculos;
    }

    /**
     * @param listaVehiculos the listaVehiculos to set
     */
    public void setListaVehiculos(List<SelectItem> listaVehiculos) {
        this.listaVehiculos = listaVehiculos;
    }

    /**
     * @return the listaRuta
     */
    public List<SelectItem> getListaRuta() {
        return listaRuta;
    }

    /**
     * @param listaRuta the listaRuta to set
     */
    public void setListaRuta(List<SelectItem> listaRuta) {
        this.listaRuta = listaRuta;
    }

    /**
     * @return the idVehiculo
     */
    public int getIdVehiculo() {
        return idVehiculo;
    }

    /**
     * @param idVehiculo the idVehiculo to set
     */
    public void setIdVehiculo(int idVehiculo) {
        this.idVehiculo = idVehiculo;
    }

    /**
     * @return the idOficinaVehiculo
     */
    public int getIdOficinaVehiculo() {
        return idOficinaVehiculo;
    }

    /**
     * @param idOficinaVehiculo the idOficinaVehiculo to set
     */
    public void setIdOficinaVehiculo(int idOficinaVehiculo) {
        this.idOficinaVehiculo = idOficinaVehiculo;
    }

    /**
     * @return the idOficinaRuta
     */
    public int getIdOficinaRuta() {
        return idOficinaRuta;
    }

    /**
     * @param idOficinaRuta the idOficinaRuta to set
     */
    public void setIdOficinaRuta(int idOficinaRuta) {
        this.idOficinaRuta = idOficinaRuta;
    }

    public List<SelectItem> listaOficina() {
        List<SelectItem> l = new ArrayList<>();
        try {
            List<OficinaVO> lv = oficinaService.traerListaOficina();
            for (OficinaVO sgO : lv) {
                l.add(new SelectItem(sgO.getId(), sgO.getNombre()));
            }
        } catch (Exception ex) {
            Logger.getLogger(AdministrarViajeBeanModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return l;
    }

    private List<SelectItem> listaVehiculos() {
        List<SelectItem> l = new ArrayList<>();
        try {
            List<VehiculoVO> lv = sgVehiculoImpl.traerVehiculoPorOficinaAndGerencia(getIdOficinaVehiculo(), Constantes.NO_ELIMINADO, Constantes.GERENCIA_ID_SERVICIOS_GENERALES);
            for (VehiculoVO sgV : lv) {
                l.add(new SelectItem(sgV.getId(), sgV.getMarca() + " - " + sgV.getModelo() + " - " + sgV.getNumeroPlaca() + " - " + sgV.getColor()));
            }
        } catch (Exception ex) {
            Logger.getLogger(AdministrarViajeBeanModel.class.getName()).log(Level.SEVERE, null, ex);
        }

        return l;
    }

    public void llenarListaEmpleadosSGL(boolean todos) {
        try {
            listaEmpleadosSGL = new ArrayList<>();
            if (todos) {
                listaEmpleadosSGL = usuarioImpl.traerUsuarioActivoByGerencia(Constantes.CERO, sesion.getOficinaActual().getId());
            } else {
                listaEmpleadosSGL = usuarioImpl.traerUsuarioActivoByGerencia(Constantes.GERENCIA_ID_SERVICIOS_GENERALES, sesion.getOficinaActual().getId());
            }
        } catch (Exception e) {
            e.getStackTrace();

        }
    }

    /**
     * @return the listaEmpleadosSGL
     */
    public List<String> getListaEmpleadosSGL() {
        return listaEmpleadosSGL;
    }

    /**
     * @param listaEmpleadosSGL the listaEmpleadosSGL to set
     */
    public void setListaEmpleadosSGL(List<String> listaEmpleadosSGL) {
        this.listaEmpleadosSGL = listaEmpleadosSGL;
    }

    public void llenarListaDestino() {
        try {
            List<Object[]> listaTerrestre = new ArrayList<>();
            setListaDestino(sgRutaTerrestreImpl.traerDestinosJson(getViajeVO().getIdOficinaOrigen()));
            listaTerrestre = getListaDestino().get(0);
            destinos.clear();
            for (Object[] o : listaTerrestre) {
                if (listaTerrestre != null) {
                    destinos.add(new SelectItem((Integer) o[0], (String) o[1] + " // " + (String) o[2]));
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
    }

    public void actualizarListaVehiculos() {
        if (vehiculoSgl) {
            try {
                List<SelectItem> l = new ArrayList<>();
                List<VehiculoVO> lv = sgVehiculoImpl.traerVehiculoPorOficinaAndGerencia(getIdOficinaVehiculo(), Constantes.NO_ELIMINADO, Constantes.GERENCIA_ID_SERVICIOS_GENERALES);
                for (VehiculoVO sgV : lv) {
                    l.add(new SelectItem(sgV.getId(), sgV.getMarca() + " - " + sgV.getModelo() + " - " + sgV.getNumeroPlaca() + " - " + sgV.getColor()));
                }
                setListaVehiculos(l);
            } catch (Exception ex) {
                Logger.getLogger(AdministrarViajeBeanModel.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            tdosLosVehiculosByOficina();
        }

    }

    /**
     * @return the redondoSencillo
     */
    public boolean isRedondoSencillo() {
        return redondoSencillo;
    }

    /**
     * @param redondoSencillo the redondoSencillo to set
     */
    public void setRedondoSencillo(boolean redondoSencillo) {
        redondoSencillo = redondoSencillo;
    }

    public void traerResponsableVehiculo() {
        try {
            if (getViajeVO().getCodigo() == null || getViajeVO().getCodigo().isEmpty()) {
                UsuarioVO vo = asignarVehiculoImpl.traerResponsableVehiculo(getIdVehiculo());
                if (vo != null) {
                    getViajeVO().setResponsable(vo.getNombre());
                    getViajeVO().setIdResponsable(vo.getId());
                    setTieneResponsable(Constantes.TRUE);
                }
            }

            llenarListaEmpleadosSGL(Constantes.FALSE);
            llenarListaDestino();
        } catch (Exception ex) {
            Logger.getLogger(AdministrarViajeBeanModel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @return the tieneResponsable
     */
    public boolean isTieneResponsable() {
        return tieneResponsable;
    }

    /**
     * @param tieneResponsable the tieneResponsable to set
     */
    public void setTieneResponsable(boolean tieneResponsable) {
        this.tieneResponsable = tieneResponsable;
    }

    public void cambiarResponsable() {
        //cambiar por javaScript
        UsuarioVO usuarioVO = usuarioImpl.findByName(viajeVO.getIdResponsable());
        getViajeVO().setResponsable(usuarioVO.getNombre());
        getViajeVO().setIdResponsable(usuarioVO.getId());
        setTieneResponsable(Constantes.TRUE);
    }

    public void cargarVehiculos() {
        if (getIdOficinaVehiculo() != -1) {
            actualizarListaVehiculos();
        }

    }

    public void cargarConductoresSGL() {
        if (isEmpleadosSgl()) {
            llenarListaEmpleadosSGL(Constantes.FALSE);
        } else {
            llenarListaEmpleadosSGL(Constantes.TRUE);
        }
    }

    public void cargarListaVehiculos() {
        if (getIdOficinaVehiculo() != -1) {
            tdosLosVehiculosByOficina();
        }

    }

    public void cargarResponsableVehiculo() {
        if (getIdVehiculo() != -1) {
            traerResponsableVehiculo();
        }

    }

    public boolean crearViaje() throws ParseException {

        // SimpleDateFormat tiempo = new SimpleDateFormat("h:mma");
        // Date horaSali = tiempo.parse(getHoraSalida());
        Calendar fechaCompleta = Calendar.getInstance();
        fechaCompleta.setTime(viajeVO.getHoraSalida());
        Calendar fs = Calendar.getInstance();
        fs.setTime(getViajeVO().getFechaProgramada());
        fechaCompleta.set(Calendar.YEAR, fs.get(Calendar.YEAR));
        fechaCompleta.set(Calendar.MONTH, fs.get(Calendar.MONTH));
        fechaCompleta.set(Calendar.DATE, fs.get(Calendar.DATE));
        getViajeVO().setFechaProgramada(fechaCompleta.getTime());
        int tipoViaje = Constantes.CERO;
        boolean cerrar = false;
        if (getViajeVO().getIdRuta() < 1) {
            SelectItem item = destinos.stream().filter(s -> Integer.parseInt(s.getValue().toString()) == viajeVO.getIdRuta()
            ).findFirst().get();

            if (item != null && item.getLabel().contains("a Oficina")) {
                tipoViaje = Constantes.RUTA_TIPO_OFICINA;
            } else {
                tipoViaje = Constantes.RUTA_TIPO_CIUDAD;
            }
        }
        boolean validar = true;
        if (siManejoFechaLocal.dayIsToday(getViajeVO().getFechaProgramada())) {
            if (getViajeVO().getFechaProgramada().getTime() < new Date().getTime()) {
                FacesUtils.addErrorMessage("No se puede crear un viaje en Fecha y/o hora pasada");
                validar = false;
            }
        } else if (getViajeVO().getFechaProgramada().before(new Date())) {
            FacesUtils.addErrorMessage("No se puede crear un viaje en Fecha y/o hora pasada");
            validar = false;
        }
        if (validar) {
            if (getIdVehiculo() == -1) {
                FacesUtils.addErrorMessage("Favor de seleccionar un vehiculo");
            } else {
                if (getViajeVO().getIdResponsable() == null || getViajeVO().getIdResponsable().equals("")) {
                    FacesUtils.addErrorMessage("Nose a selecionado un reponsable del viaje");
                } else {
                    if (getViajeVO().getIdRuta() < 1) {
                        FacesUtils.addErrorMessage("Favor de selecionar un destino");
                    } else {
                        UsuarioVO uRes = usuarioImpl.findByName(viajeVO.getIdResponsable());
                        SgViaje v = sgViajeImpl.guardarViajeEmergenteVO(
                                sesion.getUsuario().getId(), null, getViajeVO().getFechaProgramada(),
                                Constantes.CERO, Constantes.CERO, getIdVehiculo(), uRes.getId(),
                                getViajeVO().getIdRuta(), getViajeVO().getIdOficinaOrigen(), tipoViaje, null, isRedondoSencillo(),
                                Constantes.ESTATUS_VIAJE_CREADO, Constantes.BOOLEAN_TRUE, (getViajeVO().isConInter() ? Constantes.BOOLEAN_TRUE : Constantes.BOOLEAN_FALSE));
                        iniciarConversasionCrearViaje();
                        cerrar = true;
                        FacesUtils.addInfoMessage("El viaje " + v.getCodigo() + " se a creado con exito");
                        String metodo = ";cerrarDialogoCrearViaje();";
                        PrimeFaces.current().executeScript(metodo);

                    }

                }
            }
        }
        return cerrar;
    }

    public void actualizaHoraSalida() {
        String h = FacesUtils.getRequestParameter("inputHoraSalida");
        setHoraSalida(h);
    }

    public List<String> usuarioListener(String cadena) {
        setInvitadoEmergente("");
        List<String> nombres = new ArrayList<>();
        List<UsuarioVO> usVos = traerUsuarios(cadena);
        usVos.stream().forEach(us -> {
            nombres.add(us.getNombre());
        });
        return nombres;
    }

    public List<String> invitadoListener(String cadena) {
        setInvitadoEmergente("");
        List<String> nombres = new ArrayList<>();
        List<InvitadoVO> invs = traerInvitados(cadena);
        invs.stream().forEach(us -> {
            nombres.add(us.getNombre() + " // " + us.getEmpresa());
        });
        return nombres;
    }

    public void agregarInvitado() {
        agreagarEmpleadoOInvitadoEmergente(Constantes.FALSE);
        setInvitadoEmergente("");
    }

    public void agregarEmpleado() {
        agreagarEmpleadoOInvitadoEmergente(Constantes.TRUE);
        setEmpleadoEmergente("");
    }

    public void selecionarSolicitud(int idSol) {
        llenarlistaViajerosPorSolicitud(idSol);
    }

    public void addAndOrRemoveViajeros() throws SIAException {
        addAndOrRemoveViajeros(Constantes.FALSE);
//        String metodo = ";draggableInit();";
//        PrimeFaces.current().executeScript(metodo);
    }

    /**
     * @return the listaDestino
     */
    public List<List<Object[]>> getListaDestino() {
        return listaDestino;
    }

    /**
     * @param listaDestino the listaDestino to set
     */
    public void setListaDestino(List<List<Object[]>> listaDestino) {
        this.listaDestino = listaDestino;
    }

    /**
     * @return the fechaInt1
     */
    public Date getFechaInt1() {
        return fechaInt1;
    }

    /**
     * @param fechaInt1 the fechaInt1 to set
     */
    public void setFechaInt1(Date fechaInt1) {
        this.fechaInt1 = fechaInt1;
    }

    /**
     * @return the fechaInt2
     */
    public Date getFechaInt2() {
        return fechaInt2;
    }

    /**
     * @param fechaInt2 the fechaInt2 to set
     */
    public void setFechaInt2(Date fechaInt2) {
        this.fechaInt2 = fechaInt2;
    }

    /**
     * @return the textBusqueda
     */
    public String getTextBusqueda() {
        return textBusqueda;
    }

    /**
     * @param textBusqueda the textBusqueda to set
     */
    public void setTextBusqueda(String textBusqueda) {
        this.textBusqueda = textBusqueda;
    }

    private int diferenciaDias(Date d1, Date d2) {
        int dias = siManejoFechaLocal.dias(d2, d1);
        return dias < 1 ? 1 : dias;
    }

    public String getUsrID() {
        String usr = "";
        if (sesion != null && sesion.getUsuario() != null
                && sesion.getUsuario().getId() != null
                && !sesion.getUsuario().getId().isEmpty()) {
            usr = sesion.getUsuario().getId();
        }
        return usr;
    }

    public void llenarlistaViajeros(int viajeEnUso) {

        setViajeVO(sgViajeImpl.buscarPorId(viajeEnUso, Constantes.FALSE));
        setListaViajeros(sgViajeroImpl.getTravellersByTravel(getViajeVO().getId(), null));
        setListaViajerosBajar(new ArrayList<ViajeroVO>());
        setEstatusViaje(getViajeVO().getIdEstatus());
    }

    /**
     * @return the listaViajeros
     */
    public List<ViajeroVO> getListaViajeros() {
        return listaViajeros;
    }

    /**
     * @param listaViajeros the listaViajeros to set
     */
    public void setListaViajeros(List<ViajeroVO> listaViajeros) {
        this.listaViajeros = listaViajeros;
    }

    public void removeViajeros(String viajero, String usuario, String invitado) {
        if (viajero != null && !viajero.equals("") && !viajero.equals("null")) {
            int idViajero = Integer.parseInt(viajero);
            for (ViajeroVO vo : getListaViajeros()) {
                if (vo.getId() == idViajero) {
                    vo.setSelected(false);
                    getListaViajerosBajar().add(vo);
                    getListaViajeros().remove(vo);
                    break;
                }

            }
        } else if (usuario != null && !usuario.equals("") && !usuario.equals("null")) {
            for (ViajeroVO vo : getListaViajeros()) {
                if (vo.getIdUsuario().equals(usuario)) {
                    vo.setSelected(false);
                    getListaViajerosBajar().add(vo);
                    getListaViajeros().remove(vo);
                    break;
                }

            }
        } else {
            int idInvitado = Integer.parseInt(invitado);
            for (ViajeroVO vo : getListaViajeros()) {
                if (vo.getIdInvitado() == idInvitado) {
                    vo.setSelected(false);
                    getListaViajerosBajar().add(vo);
                    getListaViajeros().remove(vo);
                    break;
                }

            }
        }

    }

    /**
     * @return the listaViajerosBajar
     */
    public List<ViajeroVO> getListaViajerosBajar() {
        return listaViajerosBajar;
    }

    /**
     * @param listaViajerosBajar the listaViajerosBajar to set
     */
    public void setListaViajerosBajar(List<ViajeroVO> listaViajerosBajar) {
        listaViajerosBajar = listaViajerosBajar;
    }

    public void goPopUpAddOrRemoveViajeros(int idViaje) {
        llenarlistaViajeros(idViaje);
        //usuariosActivos();
        //listInvitados();
        String metodo = ";abrirDialogModal(dialogoPopUpAddOrRemoveViajeros);";
        PrimeFaces.current().executeScript(metodo);
    }

    public void usuariosActivos() {
        String usuarios = usuarioImpl.traerUsuarioActivoJson();
        PrimeFaces.current().executeScript(";llenarRuta('frmPnlAddorRemoveViajeros'," + usuarios + ",'hidenEmpleado', 'selectEmpleado','completarEmpleado','0');");
        PrimeFaces.current().executeScript(";llenarRuta('frmPnlAddorRemoveViajeros'," + usuarios + ",'hidenEmpleadoTab', 'selectEmpleadoTab','completarEmpleadoTab','0');");
    }

    public void listInvitados() {
        String invitados = sgInvitadoImpl.traerInvitadoJsonPorCampo();
        PrimeFaces.current().executeScript(";llenarRuta('frmPnlAddorRemoveViajeros'," + invitados + ",'hidenInvitado', 'selectInvitado','completarInvitado','0');");
        PrimeFaces.current().executeScript(";llenarRuta('frmPnlAddorRemoveViajeros'," + invitados + ",'hidenInvitadoTab', 'selectInvitadoTab','completarInvitadoTab','0');");
    }

    /**
     * @return the empleadoEmergente
     */
    public String getEmpleadoEmergente() {
        return empleadoEmergente;
    }

    /**
     * @param empleadoEmergente the empleadoEmergente to set
     */
    public void setEmpleadoEmergente(String empleadoEmergente) {
        this.empleadoEmergente = empleadoEmergente;
    }

    /**
     * @return the invitadoEmergente
     */
    public String getInvitadoEmergente() {
        return invitadoEmergente;
    }

    /**
     * @param invitadoEmergente the invitadoEmergente to set
     */
    public void setInvitadoEmergente(String invitadoEmergente) {
        this.invitadoEmergente = invitadoEmergente;
    }

    public void agreagarEmpleadoOInvitadoEmergente(boolean esEmpleado) {
        boolean seRepite = false;
        boolean volverASubir = false;
        if (getListaViajeros().size() < getViajeVO().getVehiculoVO().getCapacidadPasajeros()) {
            InvitadoVO invVo = new InvitadoVO();
            for (ViajeroVO viajero : getListaViajeros()) {
                if (esEmpleado) {
                    if (viajero.getIdUsuario().equals(getEmpleadoEmergente())) {
                        seRepite = true;
                        break;
                    }
                } else {
                    String[] cad = invitadoEmergente.split("//");
                    invVo = sgInvitadoImpl.buscarInvitado(cad[0].trim());
                    if (viajero.getIdInvitado() == invVo.getIdInvitado()) {
                        seRepite = true;
                        break;
                    }
                }
            }
            if (!seRepite) {

                for (ViajeroVO nv : getListaViajerosBajar()) {
                    if (esEmpleado) {
                        if (nv.getIdUsuario().equals(getEmpleadoEmergente())) {
                            nv.setSelected(true);
                            getListaViajeros().add(nv);
                            getListaViajerosBajar().remove(nv);
                            volverASubir = true;
                            break;
                        }
                    } else {
                        if (nv.getIdInvitado() == invVo.getIdInvitado()) {
                            nv.setSelected(true);
                            getListaViajeros().add(nv);
                            getListaViajerosBajar().remove(nv);
                            volverASubir = true;
                            break;
                        }
                    }
                }
                if (!volverASubir) {
                    ViajeroVO v = new ViajeroVO();
                    if (esEmpleado) {
                        UsuarioVO u = usuarioImpl.findByName(getEmpleadoEmergente());
                        v.setUsuario(u.getNombre());
                        v.setIdUsuario(u.getId());
                        v.setCorreo(u.getMail());
                        v.setInvitado("null");
                        v.setIdInvitado(Constantes.CERO);
                        v.setTipoViajero(Constantes.SG_TIPO_ESPECIFICO_EMPLEADO);
                        v.setEsEmpleado(Constantes.TRUE);
                    } else {
                        v.setIdInvitado(invVo.getIdInvitado());
                        v.setInvitado(invVo.getNombre());
                        v.setUsuario("null");
                        v.setIdUsuario("null");
                        v.setTipoViajero(Constantes.SG_TIPO_ESPECIFICO_INVITADO);
                        v.setEsEmpleado(Constantes.FALSE);
                    }

                    SgViaje viaje = sgViajeImpl.find(getViajeVO().getId());
                    v.setFechaSalida(viaje.getFechaProgramada());
                    v.setHoraSalida(viaje.getHoraProgramada());
                    v.setIdViaje(viaje.getId());
                    v.setCodigoViaje(viaje.getCodigo());
                    v.setViajeroQuedado(Constantes.UNO); //el viajero viaja por primera vez
                    v.setIdRutaViaje(viaje.getSgRutaTerrestre().getId());
                    v.setSelected(true);//usaremos este campo para saber si se sube o baja
                    getListaViajeros().add(v);
                }
            }
        } else {
            FacesUtils.addErrorMessage("El numero de pasajeros no puede ser mayor a la capacidad del Vehiculo");
        }
    }

    public List<UsuarioVO> traerUsuarios(String cadena) {
        return apCampoUsuarioRhPuestoImpl.traerUsurioPorParteNombre(cadena, sesion.getUsuario().getApCampo().getId());
    }

    public List<InvitadoVO> traerInvitados(String cadena) {
        return sgInvitadoImpl.buscarInvitadoParteNombre(cadena);
    }

    public void addAndOrRemoveViajeros(boolean regresoIntercet) throws SIAException {
        try {
            int tipoViajero = Constantes.SG_TIPO_ESPECIFICO_EMPLEADO;
            int invitado = Constantes.CERO;
            boolean finalizar = true;
            int count = 0;
            Joiner join = Joiner.on(",").skipNulls();
            List<String> correos = new ArrayList<String>();
            String motivo = "viajero agregado al Viaje -" + getViajeVO().getId() + "- de manera emergente.";
            for (ViajeroVO vo : getListaViajeros()) {
                count++;
                if (vo.isSelected()) {
                    if (!vo.isEsEmpleado()) {
                        tipoViajero = Constantes.SG_TIPO_ESPECIFICO_INVITADO;
                        invitado = vo.getIdInvitado();
                    }
                    if (vo.getId() != null) {
                        correos.add(vo.getCorreo());
                        SgViajero v = sgViajeroImpl.find(vo.getId());
                        SgViaje viaj = sgViajeImpl.find(getViajeVO().getId());
                        boolean escala = Constantes.FALSE;

                        if (vo.getIdDestino() != getViajeVO().getIdOficinaDestino()) {
                            escala = Constantes.TRUE;
                        }
                        //validar destino

                        //sgViajeroImpl.agregarViaje(sesion.getUsuario().getId(),v, sgViajeImpl.find(getViajeVO().getId()),Constantes.FALSE);
                        sgViajeroImpl.agregarViaje(sesion.getUsuario().getId(), viaj.getId(),
                                Constantes.CERO, vo.getId(), escala);
                        if (count == getListaViajeros().size()) {
                            //revisar los correos donde tenga que ir el conductor
                            String correoPara = join.join(correos);
                            notificacionViajeImpl.sendMailTravellerToTrip(correoPara, sgViajeroImpl.traerCorreoResponsableSGLySeguridad(), getViajeVO().getId(), getViajeVO().getCodigo(), v);
                        }

                    } else {
                        sgViajeroImpl.agregarViajeroAViaje(sesion.getUsuario().getId(), getViajeVO().getId(), vo.getIdUsuario(), tipoViajero, invitado,
                                sesion.getUsuario().getEmail(), motivo, Constantes.ID_SI_OPERACION_AGREGAR_VIAJERO);
                    }

                }
            }
            motivo = "El analista bajo al viajero.";
            for (ViajeroVO vo : getListaViajerosBajar()) {
                if (!vo.isSelected()) {
                    SgViajero vro = sgViajeroImpl.find(vo.getId());
                    SgViajero vroEscala = sgViajeroImpl.sgViajeroByViajeroEscala(vo.getId(), Constantes.BOOLEAN_TRUE);

                    if (vro != null && vro.getId() > 0 && vro.getSgViajero() == null
                            && vroEscala == null) {
                        sgViajeroImpl.takeOutTravellToTraveller(sesion.getUsuario(), vro, motivo, getListaViajeros().size(), regresoIntercet);
                    } else if (vro != null && vro.getId() > 0 && vro.getSgViajero() != null && vroEscala == null) {
                        vro.setSgViaje(vro.getSgViajero().getSgViaje());
                        vro.setEliminado(Constantes.BOOLEAN_TRUE);
                        sgViajeroImpl.edit(vro);
                    } else if (vro != null && vro.getId() > 0 && vro.getSgViajero() == null && vroEscala != null) {
                        //vro.setSgViaje(vro.getSgViajero().getSgViaje());

                        if (vroEscala.getSgViaje() != null) {
                            if (Objects.equals(vroEscala.getSgViaje().getId(), vro.getSgViaje().getId())) {

                                sgViajeroImpl.bajarViajeroConEscala(vro.getId(), vroEscala.getId(), sesion.getUsuario().getId());

                            } else {
                                FacesUtils.addInfoMessage("No se puede bajar al viajero debido aque ya cuenta con el viaje que sale de la escala");
                            }

                        } else {
                            vroEscala.setEliminado(Constantes.BOOLEAN_FALSE);
                            vroEscala.setSgViaje(null);
                            vroEscala.setSgViajero(null);
                            sgViajeroImpl.edit(vroEscala);

                            vro.setEliminado(Constantes.BOOLEAN_TRUE);
                            sgViajeroImpl.edit(vro);
                        }

                    }
                }

            }
            if (finalizar) {
                iniciarConversasionCrearViaje();
                String metodo = ";cerrarDialogoAdmistrarViajeros();";
                PrimeFaces.current().executeScript(metodo);
                cargarSolicitudesYViajes();
                //PrimeFaces.current().executeScript(";$(dialogoPopUpAddOrRemoveViajeros).modal('hide');");
                FacesUtils.addInfoMessage("Se realizarón los cambios con exito");
            }

        } catch (Exception e) {
            Logger.getLogger(AdministrarViajeBeanModel.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * @return the estatusViaje
     */
    public int getEstatusViaje() {
        return estatusViaje;
    }

    /**
     * @param estatusViaje the estatusViaje to set
     */
    public void setEstatusViaje(int estatusViaje) {
        this.estatusViaje = estatusViaje;
    }

    public void iniciarModifcarViaje(int idViajeActual) {
        setViajeVO(sgViajeImpl.buscarPorId(idViajeActual, Constantes.FALSE));
        setModificar(Constantes.TRUE);
        if (getViajeVO() != null) {
            SimpleDateFormat tiempo = new SimpleDateFormat("h:mma");
            setHoraSalida(tiempo.format(getViajeVO().getHoraProgramada()));
            setIdVehiculo(getViajeVO().getVehiculoVO().getId());
            setIdOficinaVehiculo(getViajeVO().getVehiculoVO().getIdOficina());
            setListaOficina(listaOficina());
            setListaVehiculos(listaVehiculos());
            setTieneResponsable(Constantes.TRUE);
            llenarListaEmpleadosSGL(Constantes.FALSE);
            setRedondoSencillo(getViajeVO().isRedondo());
            getViajeVO().setOrigen(getViajeVO().getOficina());
            llenarListaDestino();
            empleadoEmergente = "";
            invitadoEmergente = "";
            String metodo = ";abrirDialogModal(dialogoPopUpCrearViaje);";
            PrimeFaces.current().executeScript(metodo);
        } else {
            FacesUtils.addErrorMessage("Ocurrio un error al cargar los datos favor de ");
        }
    }

    public void moverViaje(int idViaje) {
        String usuario = sesion.getUsuario().getId();
        sgViajeImpl.moverViajeAProgramado(usuario, idViaje);
        cargarSolicitudesYViajes();
    }

    public void llenarlistaViajerosPorSolicitud(int idSol) {
        listaViajerosSolicitud = sgViajeroImpl.getAllViajerosList(idSol, true);
    }

    /**
     * @return the modificar
     */
    public boolean isModificar() {
        return modificar;
    }

    /**
     * @param modificar the modificar to set
     */
    public void setModificar(boolean modificar) {
        this.modificar = modificar;
    }

    public void modificarViaje() throws ParseException {
        SgViaje sgViaje = sgViajeImpl.find(getViajeVO().getId());
        sgViaje.setFechaProgramada(getViajeVO().getFechaProgramada());
        SimpleDateFormat tiempo = new SimpleDateFormat("h:mma");
        Date horaSali = tiempo.parse(getHoraSalida());
        Calendar fechaCompleta = Calendar.getInstance();
        fechaCompleta.setTime(horaSali);
        Calendar fs = Calendar.getInstance();
        fs.setTime(getViajeVO().getFechaProgramada());
        fechaCompleta.set(Calendar.YEAR, fs.get(Calendar.YEAR));
        fechaCompleta.set(Calendar.MONTH, fs.get(Calendar.MONTH));
        fechaCompleta.set(Calendar.DATE, fs.get(Calendar.DATE));
        getViajeVO().setFechaProgramada(fechaCompleta.getTime());
        sgViaje.setHoraProgramada(getViajeVO().getFechaProgramada());
        getViajeVO().setVehiculoVO(sgVehiculoImpl.buscarVehiculoPorId(getIdVehiculo()));
        if (getViajeVO().getIdResponsable() == null || getViajeVO().getIdResponsable().equals("")) {
            String h = FacesUtils.getRequestParameter("responsable");
            if (h != null && !h.equals("")) {
                getViajeVO().setIdResponsable(usuarioImpl.findByName(h).getId());
                getViajeVO().setResponsable(h);
                sgViaje.setResponsable(usuarioImpl.find(getViajeVO().getIdResponsable()));
            }
        }

        boolean validar = true;
        if (siManejoFechaLocal.dayIsToday(getViajeVO().getFechaProgramada())) {
            if (getViajeVO().getFechaProgramada().getTime() < new Date().getTime()) {
                FacesUtils.addErrorMessage("No se puede crear un viaje en Fecha y/o hora pasada");
                validar = false;
            }
        } else if (getViajeVO().getFechaProgramada().before(new Date())) {
            FacesUtils.addErrorMessage("No se puede crear un viaje en Fecha y/o hora pasada");
            validar = false;
        }
        if (validar) {
            if (getIdVehiculo() == -1) {
                FacesUtils.addErrorMessage("Favor de seleccionar un vehiculo");
            } else {
                if (getViajeVO().getIdResponsable() == null || getViajeVO().getIdResponsable().equals("")) {
                    FacesUtils.addErrorMessage("Nose a selecionado un reponsable del viaje");
                } else {
                    sgViajeImpl.updateTrips(sesion.getUsuario(), sgViaje, sgViajeVehiculoImpl.getVehicleByTravel(getViajeVO().getId()), getViajeVO().getVehiculoVO(),
                            Constantes.VEHICULO_EMPRESA, getViajeVO().getIdRuta(), (isRedondoSencillo() ? Constantes.redondoTxt : Constantes.sencilloTxt), getViajeVO().isConInter());
                    iniciarConversasionCrearViaje();
                    FacesUtils.addInfoMessage("El viaje " + getViajeVO().getCodigo() + " se actualizo con exito");
                    String metodo = ";cerrarDialogoCrearViaje();";
                    PrimeFaces.current().executeScript(metodo);
                }
            }

        }
    }

    public void crearRegreso(int idViajeActual) throws SIAException {
        SgViaje viaje = sgViajeImpl.find(idViajeActual);
        if (viaje.getEstatus().getId() != Constantes.ESTATUS_VIAJE_FINALIZAR) {
            setViajeVO(sgViajeImpl.buscarPorId(idViajeActual, Constantes.FALSE));
            SgRutaTerrestre ruta = sgRutaTerrestreImpl.findSgRutaTerrestreBySgOficinaOrigenAndSgOficinaDestino(getViajeVO().getIdOficinaDestino(),
                    getViajeVO().getIdOficinaOrigen(), Constantes.RUTA_TIPO_OFICINA);
            setListaViajeros(sgViajeroImpl.getTravellersByTravel(getViajeVO().getId(), null));
            Date fechaRegreso;
            Date horaRegreso;
            if (getViajeVO().getFechaRegreso() != null && getViajeVO().getHoraRegreso() != null && getViajeVO().getFechaRegreso().after(new Date())) {
                fechaRegreso = getViajeVO().getFechaRegreso();
                horaRegreso = getViajeVO().getHoraRegreso();
            } else {
                fechaRegreso = (getViajeVO().getFechaSalida().before(new Date()) ? new Date() : getViajeVO().getFechaSalida());
                double hR = Double.parseDouble(viaje.getSgRutaTerrestre().getTiempoViaje());
                int hrRegreso = (int) hR;
                int minRegreso = (int) ((hR - hrRegreso) * 60);
                horaRegreso = siManejoFechaLocal.sumarTiempo(getViajeVO().getHoraSalida(), hrRegreso, minRegreso);
            }
            GrInterseccion i = grInterseccionImpl.findInterseccionBySV(viaje.getId());
            if (i != null) {
                SgViajeSiMovimiento movViajeA
                        = sgViajeSiMovimientoImpl.findByTravelAndOperation(i.getSgViajeA().getId(), Constantes.ESTATUS_VIAJE_PROCESO, Constantes.ID_SI_OPERACION_RETOMAR_VIAJE);
                SgViajeSiMovimiento movViajeB
                        = sgViajeSiMovimientoImpl.findByTravelAndOperation(i.getSgViajeB().getId(), Constantes.ESTATUS_VIAJE_PROCESO, Constantes.ID_SI_OPERACION_RETOMAR_VIAJE);
                if (movViajeA != null && movViajeB != null) {
                    SgViaje viajeDeRegreso = sgViajeImpl.saveReturnTrip(sesion.getUsuario(), sesion.getOficinaActual().getId(), viaje, ruta.getId(),
                            getListaViajeros(), Constantes.FALSE, null, fechaRegreso, horaRegreso, getViajeVO().getVehiculoVO().getId());
                    setListaViajeros(new ArrayList<ViajeroVO>());
                    setListaViajerosBajar(sgViajeroImpl.getTravellersByTravel(viajeDeRegreso.getId(), ""));
                    addAndOrRemoveViajeros(Constantes.TRUE);
                    iniciarConversasionCrearViaje();
                    FacesUtils.addInfoMessage("Se creo el viaje de regreso " + viajeDeRegreso.getCodigo() + " correspondiente al viaje " + getViajeVO().getCodigo());
                } else {
                    iniciarConversasionCrearViaje();
                    FacesUtils.addInfoMessage("No se puede Crear viaje de Regreso ya que este cuenta con una intersección y todavia no retorma el viaje a su origen");
                }
            } else {
                SgViaje viajeDeRegreso = sgViajeImpl.saveReturnTrip(sesion.getUsuario(), sesion.getOficinaActual().getId(), viaje, ruta.getId(),
                        getListaViajeros(), Constantes.FALSE, null, fechaRegreso, horaRegreso, getViajeVO().getVehiculoVO().getId());
                setListaViajeros(new ArrayList<ViajeroVO>());
                setListaViajerosBajar(sgViajeroImpl.getTravellersByTravel(viajeDeRegreso.getId(), ""));
                addAndOrRemoveViajeros(Constantes.FALSE);
                iniciarConversasionCrearViaje();
                FacesUtils.addInfoMessage("Se creo el viaje de regreso " + viajeDeRegreso.getCodigo() + " correspondiente al viaje " + getViajeVO().getCodigo());
            }

        } else {
            iniciarConversasionCrearViaje();
        }

    }

    public void tdosLosVehiculosByOficina() {
        List<SelectItem> l = new ArrayList<>();
        try {
            List<VehiculoVO> lv = sgVehiculoImpl.traerVehiculoPorOficina(getIdOficinaVehiculo(), Constantes.NO_ELIMINADO);
            for (VehiculoVO sgV : lv) {
                l.add(new SelectItem(sgV.getId(), sgV.getMarca() + " - " + sgV.getModelo() + " - " + sgV.getNumeroPlaca() + " - " + sgV.getColor()));
            }
        } catch (Exception ex) {
            Logger.getLogger(AdministrarViajeBeanModel.class.getName()).log(Level.SEVERE, null, ex);
        }

        setListaVehiculos(l);

    }

    public void addViajeroConSV(int idviajero) {
        if (getListaViajeros().size() < getViajeVO().getVehiculoVO().getCapacidadPasajeros()) {
            ViajeroVO vo = sgViajeroImpl.buscarViajeroPorId(idviajero);
            boolean enViaje = false;
            for (ViajeroVO viajero : getListaViajeros()) {
                if (vo.isEsEmpleado()) {
                    if (viajero.getIdUsuario().equals(vo.getIdUsuario())) {
                        enViaje = true;
                        break;
                    }
                } else {
                    if (viajero.getIdInvitado().intValue() == vo.getIdInvitado()) {
                        enViaje = true;
                        break;
                    }
                }
            }
            if (!enViaje) {
                for (ViajeroVO viaB : getListaViajerosBajar()) {
                    if (vo.isEsEmpleado()) {
                        if (viaB.getIdUsuario().equals(vo.getIdUsuario())) {
                            if (viaB.getIdSolicitudViaje() > 0) {
                                enViaje = true;
                                break;
                            }
                        }
                    } else {
                        if (viaB.getIdInvitado().intValue() == vo.getIdInvitado()) {
                            if (viaB.getIdSolicitudViaje() > 0) {
                                enViaje = true;
                                break;
                            }
                        }
                    }
                }
                if (!enViaje) {
                    SgViaje viaje = sgViajeImpl.find(getViajeVO().getId());
                    //viajeros con flujo normal
                    if (getViajeVO().getIdOficinaOrigen() == vo.getIdOrigen() && getViajeVO().getIdOficinaDestino() == vo.getIdDestino()) {
                        vo.setFechaSalida(getViajeVO().getFechaProgramada());
                        vo.setHoraSalida(getViajeVO().getHoraProgramada());
                        vo.setIdViaje(getViajeVO().getId());
                        vo.setCodigoViaje(getViajeVO().getCodigo());
                        vo.setViajeroQuedado(Constantes.UNO); //el viajero viaja por primera vez
                        vo.setIdRutaViaje(getViajeVO().getIdRuta());
                        vo.setSelected(true);
                        getListaViajeros().add(vo);

                        //aqui entran los vaiajero que se divide su solicitud
                    } else if (getViajeVO().getIdOficinaOrigen() == vo.getIdOrigen()
                            && (vo.getIdOrigen() == Constantes.ID_OFICINA_SAN_FERNANDO || vo.getIdOrigen() == Constantes.ID_OFICINA_TORRE_MARTEL)
                            && getViajeVO().getIdOficinaDestino() != vo.getIdDestino()
                            && getViajeVO().getIdOficinaDestino() == Constantes.ID_OFICINA_REY_PRINCIPAL
                            && (vo.getIdDestino() == Constantes.ID_OFICINA_SAN_FERNANDO || vo.getIdDestino() == Constantes.ID_OFICINA_TORRE_MARTEL)) {
                        vo.setFechaSalida(getViajeVO().getFechaProgramada());
                        vo.setHoraSalida(getViajeVO().getHoraProgramada());
                        vo.setIdViaje(getViajeVO().getId());
                        vo.setCodigoViaje(getViajeVO().getCodigo());
                        vo.setViajeroQuedado(Constantes.UNO); //el viajero viaja por primera vez
                        vo.setIdRutaViaje(getViajeVO().getIdRuta());
                        vo.setSelected(true);
                        getListaViajeros().add(vo);
                        FacesUtils.addInfoMessage("El viajero " + vo.getUsuario() + " sera con escala ");

                        // el viajero sale de la escala
                    } else if (getViajeVO().getIdOficinaDestino() == vo.getIdDestino()
                            && getViajeVO().getIdOficinaOrigen() == Constantes.ID_OFICINA_REY_PRINCIPAL
                            && (vo.getIdOrigen() == Constantes.ID_OFICINA_SAN_FERNANDO || vo.getIdOrigen() == Constantes.ID_OFICINA_TORRE_MARTEL)
                            && (vo.getIdDestino() == Constantes.ID_OFICINA_SAN_FERNANDO || vo.getIdDestino() == Constantes.ID_OFICINA_TORRE_MARTEL)) {

                        /* estas 2 lineas de deben de quitar para poder realizar escalas en todas las oficinas
                        && (vo.getIdOrigen() == Constantes.ID_OFICINA_SAN_FERNANDO || vo.getIdOrigen() == Constantes.ID_OFICINA_TORRE_MARTEL)
                            &&(vo.getIdDestino() == Constantes.ID_OFICINA_SAN_FERNANDO || vo.getIdDestino() == Constantes.ID_OFICINA_TORRE_MARTEL)*/
                        vo.setFechaSalida(getViajeVO().getFechaProgramada());
                        vo.setHoraSalida(getViajeVO().getHoraProgramada());
                        vo.setIdViaje(getViajeVO().getId());
                        vo.setCodigoViaje(getViajeVO().getCodigo());
                        vo.setViajeroQuedado(Constantes.UNO); //el viajero viaja por primera vez
                        vo.setIdRutaViaje(getViajeVO().getIdRuta());
                        vo.setSelected(true);
                        getListaViajeros().add(vo);

                    } else {
                        FacesUtils.addErrorMessage("La ruta del viajero y del viaje no coinciden.");
                    }

                }
            }
        } else {
            FacesUtils.addErrorMessage("El numero de pasajeros no puede ser mayor a la capacidad del Vehiculo");
        }
    }

    public void addTodosSV(int sv) {

        boolean enViaje = false;
        List<ViajeroVO> newViajeros = sgViajeroImpl.getAllViajerosList(sv, Constantes.TRUE);
        if (getListaViajeros().size() + newViajeros.size() <= getViajeVO().getVehiculoVO().getCapacidadPasajeros()) {
            for (ViajeroVO newVO : newViajeros) {
                for (ViajeroVO vo : getListaViajeros()) {
                    enViaje = false;
                    if (newVO.isEsEmpleado()) {
                        if (newVO.getIdUsuario().equals(vo.getIdUsuario())) {
                            enViaje = true;
                            break;
                        }
                    } else {
                        if (newVO.getIdInvitado() == vo.getIdInvitado().intValue()) {
                            enViaje = true;
                            break;
                        }
                    }

                }
                if (!enViaje) {
                    for (ViajeroVO vob : getListaViajerosBajar()) {
                        if (newVO.isEsEmpleado()) {
                            if (newVO.getIdUsuario().equals(vob.getIdUsuario())) {
                                if (vob.getIdSolicitudViaje() <= 0) {
                                    getListaViajeros().add(newVO);
                                    enViaje = true;
                                    break;
                                } else {
                                    getListaViajeros().add(vob);
                                    enViaje = true;
                                    break;
                                }
                            }
                        } else {
                            if (newVO.getIdInvitado().intValue() == vob.getIdInvitado()) {
                                if (vob.getIdSolicitudViaje() <= 0) {
                                    getListaViajeros().add(newVO);
                                    getListaViajerosBajar().remove(vob);
                                    enViaje = true;
                                    break;
                                } else {
                                    getListaViajeros().add(vob);
                                    getListaViajerosBajar().remove(vob);
                                    enViaje = true;
                                    break;
                                }
                            }
                        }
                    }
                }

                if (!enViaje) {
                    SgViaje viaje = sgViajeImpl.find(getViajeVO().getId());
                    newVO.setFechaSalida(viaje.getFechaProgramada());
                    newVO.setHoraSalida(viaje.getHoraProgramada());
                    newVO.setIdViaje(viaje.getId());
                    newVO.setCodigoViaje(viaje.getCodigo());
                    newVO.setViajeroQuedado(Constantes.UNO); //el viajero viaja por primera vez
                    newVO.setIdRutaViaje(viaje.getSgRutaTerrestre().getId());
                    newVO.setSelected(true);
                    getListaViajeros().add(newVO);
                }
            }
        } else {
            FacesUtils.addErrorMessage("El numero de pasajeros no puede ser mayor a la capacidad del Vehiculo");
        }
    }

    /**
     * @return the ultimaActualizacion
     */
    public String getUltimaActualizacion() {
        return ultimaActualizacion;
    }

    /**
     * @param ultimaActualizacion the ultimaActualizacion to set
     */
    public void setUltimaActualizacion(String ultimaActualizacion) {
        this.ultimaActualizacion = ultimaActualizacion;
    }

    public void eliminarViajeById(int idViaje) {
        sgViajeImpl.limpiarViajes(idViaje, sesion.getUsuario().getId());
        iniciarConversasionCrearViaje();
    }

    /**
     * @return the listaViajerosSolicitud
     */
    public List<ViajeroVO> getListaViajerosSolicitud() {
        return listaViajerosSolicitud;
    }

    /**
     * @param listaViajerosSolicitud the listaViajerosSolicitud to set
     */
    public void setListaViajerosSolicitud(List<ViajeroVO> listaViajerosSolicitud) {
        this.listaViajerosSolicitud = listaViajerosSolicitud;
    }

    /**
     * @return the destinos
     */
    public List<SelectItem> getDestinos() {
        return destinos;
    }

    /**
     * @param destinos the destinos to set
     */
    public void setDestinos(List<SelectItem> destinos) {
        this.destinos = destinos;
    }
}
