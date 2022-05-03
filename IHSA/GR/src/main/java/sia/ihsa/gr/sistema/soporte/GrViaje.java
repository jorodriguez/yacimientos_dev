package sia.ihsa.gr.sistema.soporte;

import org.primefaces.PrimeFaces;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import sia.constantes.Constantes;
import sia.modelo.SgAsignarVehiculo;
import sia.modelo.SgViaje;
import sia.modelo.SiAdjunto;
import sia.modelo.gr.vo.GrRutaZonasVO;
import sia.modelo.licencia.vo.LicenciaVo;
import sia.modelo.sgl.oficina.vo.OficinaVO;
import sia.modelo.sgl.viaje.vo.InvitadoVO;
import sia.modelo.sgl.viaje.vo.RutaTerrestreVo;
import sia.modelo.sgl.viaje.vo.VehiculoVO;
import sia.modelo.sgl.viaje.vo.ViajeroVO;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.comunicacion.impl.CoCompartidaImpl;
import sia.servicios.gr.impl.GrRutasZonasImpl;
import sia.servicios.sgl.impl.SgInvitadoImpl;
import sia.servicios.sgl.impl.SgOficinaImpl;
import sia.servicios.sgl.vehiculo.impl.SgVehiculoImpl;
import sia.servicios.sgl.viaje.impl.SgRutaTerrestreImpl;
import sia.servicios.sgl.viaje.impl.SgViajeImpl;
import sia.servicios.sgl.viaje.impl.SgViajeroImpl;
import sia.servicios.sistema.impl.SiUsuarioRolImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@ManagedBean(name = "grViaje")
@CustomScoped(value = "#{window}")
public class GrViaje implements Serializable {

    @EJB
    private SgOficinaImpl oficinaService;
    @EJB
    private SgVehiculoImpl sgVehiculoImpl;
    @EJB
    private SgRutaTerrestreImpl sgRutaTerrestreImpl;
    @EJB
    private SgViajeImpl sgViajeImpl;
    @EJB
    private UsuarioImpl usuarioImpl;
    @EJB
    private SgInvitadoImpl sgInvitadoImpl;
    @EJB
    private SgViajeroImpl sgViajeroImpl;
    @EJB
    private CoCompartidaImpl coCompartidaImpl;
    @EJB
    private SiUsuarioRolImpl siUsuarioRolImpl;
    @EJB
    private GrRutasZonasImpl grRutasZonasImpl;

    private SoporteListas soporteListas = (SoporteListas) FacesUtilsBean.getManagedBean("soporteListas");
    private Sesion sesionBean = (Sesion) FacesUtilsBean.getManagedBean("sesion");
    private int horaSalida;
    private int horaRegreso;
    private int minutoSalida;
    private int minutoRegreso;
    private int idOficinaVehiculo;
    private int idOficinaRuta;
    private String opcionSeleccionada = "";
    private VehiculoVO vehiculoVO;
    private SgAsignarVehiculo sgAsignarVehiculo;
    private LicenciaVo licenciaVo;
    private UsuarioVO usuarioVO;
    private UsuarioVO usuarioAutorizoVO;
    private String invitado;
    private boolean agregarResponsable = false;
    private int idVehiculo;
    private int idSiOperacion = 21;
    private int idSiOperacionViajero = 1;
    private boolean viajeFueraOficina;
    private int idRuta;
    private List<SelectItem> listaOficinaVehiculo = new ArrayList<SelectItem>();
    private List<SelectItem> listaOficinaRuta = new ArrayList<SelectItem>();
    private List<SelectItem> listaVehiculos = new ArrayList<SelectItem>();
    private List<SelectItem> listaRuta = new ArrayList<SelectItem>();
    private List<SelectItem> listaHoras = new ArrayList<SelectItem>();
    private List<SelectItem> listaMinutos = new ArrayList<SelectItem>();
    private Date fechaProgramada = new Date();
    private String responsable;
    private String empleado;
    private String autorizo;
    private List<SelectItem> listaUsuario = new ArrayList<SelectItem>();
    private List<SelectItem> listaEmpleados = new ArrayList<SelectItem>();
    private List<SelectItem> listaAutorizo = new ArrayList<SelectItem>();
    private List<SelectItem> listaInvitados = new ArrayList<SelectItem>();
    private List<ViajeroVO> listaViajeros = new ArrayList<ViajeroVO>();
    private String msgExitoCrearViaje;
    private List<GrRutaZonasVO> zonas;

    public GrViaje() {
    }

    /**
     * @return the horaSalida
     */
    public int getHoraSalida() {
        return horaSalida;
    }

    /**
     * @param horaSalida the horaSalida to set
     */
    public void setHoraSalida(int horaSalida) {
        this.horaSalida = horaSalida;
    }

    /**
     * @return the horaRegreso
     */
    public int getHoraRegreso() {
        return horaRegreso;
    }

    /**
     * @param horaRegreso the horaRegreso to set
     */
    public void setHoraRegreso(int horaRegreso) {
        this.horaRegreso = horaRegreso;
    }

    /**
     * @return the minutoSalida
     */
    public int getMinutoSalida() {
        return minutoSalida;
    }

    /**
     * @param minutoSalida the minutoSalida to set
     */
    public void setMinutoSalida(int minutoSalida) {
        this.minutoSalida = minutoSalida;
    }

    /**
     * @return the minutoRegreso
     */
    public int getMinutoRegreso() {
        return minutoRegreso;
    }

    /**
     * @param minutoRegreso the minutoRegreso to set
     */
    public void setMinutoRegreso(int minutoRegreso) {
        this.minutoRegreso = minutoRegreso;
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
     * @return the vehiculoVO
     */
    public VehiculoVO getVehiculoVO() {
        return vehiculoVO;
    }

    /**
     * @param vehiculoVO the vehiculoVO to set
     */
    public void setVehiculoVO(VehiculoVO vehiculoVO) {
        this.vehiculoVO = vehiculoVO;
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
     * @return the licenciaVo
     */
    public LicenciaVo getLicenciaVo() {
        return licenciaVo;
    }

    /**
     * @param licenciaVo the licenciaVo to set
     */
    public void setLicenciaVo(LicenciaVo licenciaVo) {
        this.licenciaVo = licenciaVo;
    }

    /**
     * @return the usuarioVO
     */
    public UsuarioVO getUsuarioVO() {
        return usuarioVO;
    }

    /**
     * @param usuarioVO the usuarioVO to set
     */
    public void setUsuarioVO(UsuarioVO usuarioVO) {
        this.usuarioVO = usuarioVO;
    }

    /**
     * @return the invitado
     */
    public String getInvitado() {
        return invitado;
    }

    /**
     * @param invitado the invitado to set
     */
    public void setInvitado(String invitado) {
        this.invitado = invitado;
    }

    /**
     * @return the agregarResponsable
     */
    public boolean isAgregarResponsable() {
        return agregarResponsable;
    }

    /**
     * @param agregarResponsable the agregarResponsable to set
     */
    public void setAgregarResponsable(boolean agregarResponsable) {
        this.agregarResponsable = agregarResponsable;
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
     * @return the idSiOperacion
     */
    public int getIdSiOperacion() {
        return idSiOperacion;
    }

    /**
     * @param idSiOperacion the idSiOperacion to set
     */
    public void setIdSiOperacion(int idSiOperacion) {
        this.idSiOperacion = idSiOperacion;
    }

    /**
     * @return the viajeFueraOficina
     */
    public boolean isViajeFueraOficina() {
        return viajeFueraOficina;
    }

    /**
     * @param viajeFueraOficina the viajeFueraOficina to set
     */
    public void setViajeFueraOficina(boolean viajeFueraOficina) {
        this.viajeFueraOficina = viajeFueraOficina;
    }

    /**
     * @return the idRuta
     */
    public int getIdRuta() {
        return idRuta;
    }

    /**
     * @param idRuta the idRuta to set
     */
    public void setIdRuta(int idRuta) {
        this.idRuta = idRuta;
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
     * @return the listaHoras
     */
    public List<SelectItem> getListaHoras() {
        return listaHoras;
    }

    /**
     * @param listaHoras the listaHoras to set
     */
    public void setListaHoras(List<SelectItem> listaHoras) {
        this.listaHoras = listaHoras;
    }

    /**
     * @return the listaMinutos
     */
    public List<SelectItem> getListaMinutos() {
        return listaMinutos;
    }

    /**
     * @param listaMinutos the listaMinutos to set
     */
    public void setListaMinutos(List<SelectItem> listaMinutos) {
        this.listaMinutos = listaMinutos;
    }

    /**
     * @return the fechaProgramada
     */
    public Date getFechaProgramada() {
        return fechaProgramada;
    }

    /**
     * @param fechaProgramada the fechaProgramada to set
     */
    public void setFechaProgramada(Date fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

    /**
     * @return the responsable
     */
    public String getResponsable() {
        return responsable;
    }

    /**
     * @param responsable the responsable to set
     */
    public void setResponsable(String responsable) {
        this.responsable = responsable;
    }

    /**
     * @return the listaUsuario
     */
    public List<SelectItem> getListaUsuario() {
        return listaUsuario;
    }

    /**
     * @param listaUsuario the listaUsuario to set
     */
    public void setListaUsuario(List<SelectItem> listaUsuario) {
        this.listaUsuario = listaUsuario;
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

    /**
     * @return the listaOficinaVehiculo
     */
    public List<SelectItem> getListaOficinaVehiculo() {
        return listaOficinaVehiculo;
    }

    /**
     * @param listaOficinaVehiculo the listaOficinaVehiculo to set
     */
    public void setListaOficinaVehiculo(List<SelectItem> listaOficinaVehiculo) {
        this.listaOficinaVehiculo = listaOficinaVehiculo;
    }

    /**
     * @return the listaOficinaRuta
     */
    public List<SelectItem> getListaOficinaRuta() {
        return listaOficinaRuta;
    }

    /**
     * @param listaOficinaRuta the listaOficinaRuta to set
     */
    public void setListaOficinaRuta(List<SelectItem> listaOficinaRuta) {
        this.listaOficinaRuta = listaOficinaRuta;
    }

    /**
     * @return the idSiOperacionViajero
     */
    public int getIdSiOperacionViajero() {
        return idSiOperacionViajero;
    }

    public String getDisplayEmpleado() {
        String css = "display:  none;";
        if (getIdSiOperacionViajero() == 1) {
            css = "display:  block;";
        }
        return css;
    }

    public String getDisplayInvitado() {
        String css = "display:  none;";
        if (getIdSiOperacionViajero() == 2) {
            css = "display:  block;";
        }
        return css;
    }

    /**
     * @param idSiOperacionViajero the idSiOperacionViajero to set
     */
    public void setIdSiOperacionViajero(int idSiOperacionViajero) {
        this.idSiOperacionViajero = idSiOperacionViajero;
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

    /**
     * @return the empleado
     */
    public String getEmpleado() {
        return empleado;
    }

    /**
     * @param empleado the empleado to set
     */
    public void setEmpleado(String empleado) {
        this.empleado = empleado;
    }

    /**
     * @return the listaEmpleados
     */
    public List<SelectItem> getListaEmpleados() {
        return listaEmpleados;
    }

    /**
     * @param listaEmpleados the listaEmpleados to set
     */
    public void setListaEmpleados(List<SelectItem> listaEmpleados) {
        this.listaEmpleados = listaEmpleados;
    }

    /**
     * @return the listaInvitados
     */
    public List<SelectItem> getListaInvitados() {
        return listaInvitados;
    }

    /**
     * @param listaInvitados the listaInvitados to set
     */
    public void setListaInvitados(List<SelectItem> listaInvitados) {
        this.listaInvitados = listaInvitados;
    }

    public List<SelectItem> itemsHoras() {
        List<SelectItem> listSelectItem = new ArrayList<SelectItem>();

        for (Integer i = 1; i < 10; i++) { //01am - 09am
            listSelectItem.add(new SelectItem(i, ("0" + i.toString() + " am")));
        }
        for (Integer i = 10; i < 12; i++) { //10am - 11am
            listSelectItem.add(new SelectItem(i, i.toString() + " am"));
        }
        listSelectItem.add(new SelectItem(12, "12" + " pm")); //12 pm

        for (Integer i = 13; i < 22; i++) { //01pm - 09pm
            listSelectItem.add(new SelectItem(i, ("0" + (i - 12) + " pm")));
        }
        for (Integer i = 22; i < 24; i++) { //10pm - 11pm
            listSelectItem.add(new SelectItem(i, (i - 12) + " pm"));
        }
        listSelectItem.add(new SelectItem(0, "12" + " am")); //12am
        return listSelectItem;
    }

    public List<SelectItem> itemsMinutos() {
        List<SelectItem> listSelectItem = new ArrayList<SelectItem>();

        for (Integer i = 0; i < 60; i++) {
            listSelectItem.add(new SelectItem(i, ((i < 10) ? ("0".toString().concat(i.toString())) : i.toString())));
        }
        return listSelectItem;
    }

    public List<SelectItem> lstVehiculosFinal() {
        try {
            if (getOpcionSeleccionada().equals(Constantes.VEHICULO_EMPRESA)) {
                this.setListaVehiculos(mtlistaVehiculos());
            } else {
                this.setListaVehiculos(new ArrayList<SelectItem>());
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Error enLista ve" + e.getMessage());
        }
        return this.listaVehiculos;
    }

    private List<SelectItem> mtlistaVehiculos() {
        List<SelectItem> l = new ArrayList<SelectItem>();
        try {
            List<VehiculoVO> lv = sgVehiculoImpl.traerVehiculoPorOficina(getIdOficinaVehiculo(), Constantes.NO_ELIMINADO);
            for (VehiculoVO sgV : lv) {
                l.add(new SelectItem(sgV.getId(), sgV.getMarca() + " - " + sgV.getModelo() + " - " + sgV.getNumeroPlaca() + " - " + sgV.getColor()));
            }
        } catch (Exception ex) {
            Logger.getLogger(GrViaje.class.getName()).log(Level.SEVERE, null, ex);
        }

        return l;
    }

    public void traerListaVehiculo(ValueChangeEvent event) {
        setIdOficinaVehiculo((Integer) event.getNewValue());
        if (getIdOficinaVehiculo() != -1) {
            this.setListaVehiculos(mtlistaVehiculos());
        }
    }

    public void traerListaRutas(ValueChangeEvent event) {
        setIdOficinaRuta((Integer) event.getNewValue());
        if (getIdOficinaRuta() != -1) {
            setListaRuta(mtlistaRutas());
        }
    }

    public void cabiarDestino(ValueChangeEvent event) {
        setIdSiOperacion((Integer) event.getNewValue());

        if (getIdSiOperacion() == Constantes.RUTA_TIPO_OFICINA) {
            setViajeFueraOficina(false);
        } else {
            setViajeFueraOficina(true);
        }
        setListaRuta(mtlistaRutas());
    }

    public List<SelectItem> mtlistaRutas() {
        List<SelectItem> l = new ArrayList<SelectItem>();
        List<RutaTerrestreVo> lc;
        try {
            int ofic = getIdOficinaRuta();

            if (isViajeFueraOficina()) {
                lc = sgRutaTerrestreImpl.traerRutaTerrestrePorOficina(ofic, Constantes.RUTA_TIPO_CIUDAD);
                for (RutaTerrestreVo rt : lc) {
                    System.out.println("Ruta : " + rt.getNombre());
                    SelectItem item = new SelectItem(rt.getId(), rt.getNombre());
                    l.add(item);
                }
                System.out.println("a ciudad ");
            } else {
                System.out.println("a oficina ");
                lc = sgRutaTerrestreImpl.traerRutaTerrestrePorOficina(ofic, Constantes.RUTA_TIPO_OFICINA);
                for (RutaTerrestreVo rt : lc) {
                    System.out.println("Ruta : " + rt.getNombre());
                    SelectItem item = new SelectItem(rt.getId(), rt.getNombre());
                    l.add(item);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            l = new ArrayList<SelectItem>();
        }
        return l;
    }

    public void completarCrearViajeEmergencia(ActionEvent event) {
        boolean continuar = false;

        Calendar c = Calendar.getInstance();
        c.setTime(getFechaProgramada());
        c.set(Calendar.HOUR_OF_DAY, getHoraSalida());
        c.set(Calendar.MINUTE, getMinutoSalida());
        c.set(Calendar.SECOND, 0);

        Calendar now = Calendar.getInstance();
        now.setTime(new Date());

        if (getFechaProgramada() != null && c.getTimeInMillis() >= now.getTimeInMillis()) {
            continuar = true;
        } else {
            continuar = false;
            FacesUtilsBean.addErrorMessage("La fecha programada del viaje debe ser mayor o igual a la actual.");
        }

        if (continuar && getResponsable() != null && !getResponsable().isEmpty()) {
            usuarioVO = usuarioImpl.findByName(getResponsable());
            continuar = true;
        } else if (continuar) {
            continuar = false;
            FacesUtilsBean.addErrorMessage("Seleccione al responsable de viaje. ");
        }

        if (continuar && getAutorizo() != null && !getAutorizo().isEmpty()) {
            usuarioAutorizoVO = usuarioImpl.findByName(getAutorizo());
            continuar = true;
        } else if (continuar) {
            continuar = false;
            FacesUtilsBean.addErrorMessage("Seleccione al usuario que autorizo el viaje. ");
        }

        if (continuar && getIdVehiculo() > 0) {
            if (getIdRuta() > 0) {
                if (usuarioVO != null && usuarioVO.getId() != null && !usuarioVO.getId().isEmpty()) {
                    StringBuilder msgUser = new StringBuilder();
                    SgViaje newViaje = sgViajeImpl.guardarViajeEmergenteVO(sesionBean.getUsuario().getId(), getOpcionSeleccionada(), getFechaProgramada(),
                            getHoraSalida(), getMinutoSalida(), getIdVehiculo(), usuarioVO.getId(), getIdRuta(), getIdOficinaRuta(), getIdSiOperacion(),
                            usuarioAutorizoVO.getId(), Constantes.FALSE, Constantes.ESTATUS_VIAJE_POR_SALIR, Constantes.BOOLEAN_TRUE, Constantes.BOOLEAN_FALSE);
                    if (newViaje != null && newViaje.getId() > 0) {
                        msgUser.append("Se creo correctamente el viaje. ");
                        if (getListaViajeros() != null && getListaViajeros().size() > 0) {
                            try {
                                boolean error = false;
                                for (ViajeroVO vo : getListaViajeros()) {
                                    if (!sgViajeroImpl.agregarViajeroAViaje(sesionBean.getUsuario().getId(),
                                            newViaje.getId(),
                                            vo.getIdUsuario(),
                                            vo.isEsEmpleado() ? Constantes.SG_TIPO_ESPECIFICO_EMPLEADO : Constantes.SG_TIPO_ESPECIFICO_INVITADO,
                                            vo.getIdInvitado(),
                                            "",
                                            "", Constantes.ID_SI_OPERACION_AGREGAR_VIAJERO)) {
                                        error = true;
                                    }
                                }
                                if (getListaViajeros().size() > 0) {
                                    if (error) {
                                        msgUser.append("Se trato de agregar a los viajeros pero algunos de ellos no fue posible agregarlos. ");
                                    } else {
                                        msgUser.append("Se agregaron correctamente los viajeros. ");
                                    }
                                }
                                SiAdjunto siAdjunto = sgViajeImpl.generarDocumentoAutomatico(newViaje.getId());
                                if (siAdjunto != null) {
                                    msgUser.append("Se genero correctamente el documento del viaje. ");
                                }
                                //sgViajeImpl.exitTrip(sesionBean.getUsuario(), newViaje, Constantes.ESTATUS_VIAJE_PROCESO, getListaViajeros(), true);				
                            } catch (Exception ex) {
                                UtilLog4j.log.fatal(this, ex);
                            }
                        }
                        sesionBean.setRutas();
                        cancelarCrearViajeEmergencia(event);
                        setMsgExitoCrearViaje(msgUser.toString());
                        String metodo = ";cerrarDialogoCrearViaje();";
                        PrimeFaces.current().executeScript(metodo);
                    } else {
                        FacesUtilsBean.addErrorMessage("Ha ocurrido un problema en la aplicación, por favor contacte al equipo de soporte de SIA (soportesia@ihsa.mx).");
                    }
                } else {
                    FacesUtilsBean.addErrorMessage("Ha ocurrido un problema en la aplicación, por favor contacte al equipo de soporte de SIA (soportesia@ihsa.mx).");
                }
            } else {
                FacesUtilsBean.addErrorMessage("Seleccione la ruta del viaje . . . ");
            }
        } else {
            if (continuar) {
                FacesUtilsBean.addErrorMessage("Seleccione el vehiculo ");
            }
        }
    }

    public void cancelarCrearViajeEmergencia(ActionEvent event) {
        setOpcionSeleccionada("");
        setResponsable("");
        setEmpleado("");
        setInvitado("");
        setAutorizo("");
        setIdSiOperacion(0);
        setIdOficinaRuta(-1);
        setIdOficinaVehiculo(-1);
        setIdVehiculo(-1);
        setIdRuta(-1);
        setViajeFueraOficina(false);
    }

    public List<SelectItem> mtlistaOficina() {
        List<SelectItem> l = new ArrayList<SelectItem>();
        try {
            List<OficinaVO> lv = oficinaService.traerListaOficina();
            for (OficinaVO sgO : lv) {
                l.add(new SelectItem(sgO.getId(), sgO.getNombre()));
            }
        } catch (Exception ex) {
            Logger.getLogger(GrViaje.class.getName()).log(Level.SEVERE, null, ex);
        }
        return l;
    }

    public void goPopupRutaDet(ActionEvent actionEvent) {
        try {
            int idRuta = Integer.parseInt(FacesUtilsBean.getRequestParameter("idRuta"));
            if (idRuta > 0) {
                RutaTerrestreVo vo = new RutaTerrestreVo(idRuta);
                setZonas(grRutasZonasImpl.zonasPorRuta(vo, true));
                String metodo = ";abrirDialogoRutaDet();";
                PrimeFaces.current().executeScript(metodo);
            }

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void goPopupCrearViaje(ActionEvent actionEvent) {
        try {
            setResponsable("");
            setFechaProgramada(new Date());
            setListaHoras(itemsHoras());
            setListaMinutos(itemsMinutos());
            setListaOficinaRuta(mtlistaOficina());
            setListaOficinaVehiculo(mtlistaOficina());
            setListaRuta(new ArrayList<SelectItem>());
            setListaVehiculos(new ArrayList<SelectItem>());
            setListaEmpleados(new ArrayList<SelectItem>());
            setListaInvitados(new ArrayList<SelectItem>());
            setListaUsuario(new ArrayList<SelectItem>());
            setListaViajeros(new ArrayList<ViajeroVO>());
            setEmpleado("");
            setInvitado("");
            setResponsable("");
            setIdOficinaRuta(-1);
            setIdOficinaVehiculo(-1);
            setIdVehiculo(-1);
            setIdRuta(-1);
            setIdSiOperacion(21);
            setIdSiOperacionViajero(1);
            String metodo = ";abrirDialogoCrearViaje();";
            PrimeFaces.current().executeScript(metodo);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void usuarioListener(ValueChangeEvent event) {
        setListaUsuario(traerUsuario(event.getNewValue().toString()));

    }

    public void empleadoListener(ValueChangeEvent event) {
        setListaEmpleados(traerUsuario(event.getNewValue().toString()));

    }

    public void autorizoListener(ValueChangeEvent event) {
        setListaAutorizo(traerUsuario(event.getNewValue().toString()));

    }

    public void invitadoListener(ValueChangeEvent event) {
        setListaInvitados(traerInvitados(event.getNewValue().toString()));

    }

    private List<SelectItem> traerUsuario(String cadena) {
        List<SelectItem> list = new ArrayList<SelectItem>();
        try {
            if (cadena != null && !cadena.isEmpty() && cadena.length() > 2) {
                list = soporteListas.regresaUsuarioActivo(cadena, sesionBean.getUsuario().getApCampo().getId());
            }
        } catch (Exception e) {
            list = new ArrayList<SelectItem>();
        }
        return list;
    }

    private List<SelectItem> traerInvitados(String cadena) {
        List<SelectItem> list = new ArrayList<SelectItem>();
        try {
            if (cadena != null && !cadena.isEmpty() && cadena.length() > 2) {
                list = soporteListas.regresaInvitadosActivo(cadena);
            }
        } catch (Exception e) {
            list = new ArrayList<SelectItem>();
        }
        return list;
    }

    public void eliminarViajero(ActionEvent actionEvent) {
        try {
            int indice = Integer.parseInt(FacesUtilsBean.getRequestParameter("indice"));
            getListaViajeros().remove(indice);
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    public void agregarViajero(ActionEvent event) {
        try {
            ViajeroVO newViajero = new ViajeroVO();
            if (getIdSiOperacionViajero() == 1) {
                UsuarioVO newEmpleado = usuarioImpl.findByName(getEmpleado());
                newViajero.setUsuario(newEmpleado.getNombre());
                newViajero.setIdUsuario(newEmpleado.getId());
                newViajero.setEsEmpleado(true);
                newViajero.setIdInvitado(0);
            } else {
                InvitadoVO newInvitado = sgInvitadoImpl.buscarInvitado(getInvitado());
                newViajero.setIdInvitado(newInvitado.getIdInvitado());
                newViajero.setInvitado(newInvitado.getNombre());
                newViajero.setEsEmpleado(false);
                newViajero.setUsuario("");
            }
            newViajero.setAgregado(false);
            newViajero.setViajo(Constantes.BOOLEAN_TRUE);
            newViajero.setId(getListaViajeros().size());
            setEmpleado("");
            setInvitado("");
            if (!getListaViajeros().contains(newViajero)) {
                getListaViajeros().add(newViajero);
            } else {
                FacesUtilsBean.addErrorMessage("El viajero ya esta agregado al viaje.");
            }

        } catch (Exception e) {
            UtilLog4j.log.fatal(this, e);
            FacesUtilsBean.addErrorMessage("Ocurrió una excepción, favor de comunicar a sia@ihsa.mx");
        }
    }

    /**
     * @return the msgExitoCrearViaje
     */
    public String getMsgExitoCrearViaje() {
        return msgExitoCrearViaje;
    }

    /**
     * @param msgExitoCrearViaje the msgExitoCrearViaje to set
     */
    public void setMsgExitoCrearViaje(String msgExitoCrearViaje) {
        this.msgExitoCrearViaje = msgExitoCrearViaje;
    }

    /**
     * @return the autorizo
     */
    public String getAutorizo() {
        return autorizo;
    }

    /**
     * @param autorizo the autorizo to set
     */
    public void setAutorizo(String autorizo) {
        this.autorizo = autorizo;
    }

    /**
     * @return the listaAutorizo
     */
    public List<SelectItem> getListaAutorizo() {
        return listaAutorizo;
    }

    /**
     * @param listaAutorizo the listaAutorizo to set
     */
    public void setListaAutorizo(List<SelectItem> listaAutorizo) {
        this.listaAutorizo = listaAutorizo;
    }

    /**
     * @return the usuarioAutorizoVO
     */
    public UsuarioVO getUsuarioAutorizoVO() {
        return usuarioAutorizoVO;
    }

    /**
     * @param usuarioAutorizoVO the usuarioAutorizoVO to set
     */
    public void setUsuarioAutorizoVO(UsuarioVO usuarioAutorizoVO) {
        this.usuarioAutorizoVO = usuarioAutorizoVO;
    }

    /**
     * @return the zonas
     */
    public List<GrRutaZonasVO> getZonas() {
        return zonas;
    }

    /**
     * @param zonas the zonas to set
     */
    public void setZonas(List<GrRutaZonasVO> zonas) {
        this.zonas = zonas;
    }
}
