/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.reporte.bean.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.model.SelectItem;
import sia.constantes.Constantes;
import sia.modelo.sgl.estancia.vo.HuespedVo;
import sia.modelo.sgl.estancia.vo.SgHuespedStaffVo;
import sia.modelo.sgl.oficina.vo.OficinaVO;
import sia.modelo.sgl.pago.vo.PagoServicioVo;
import sia.modelo.sgl.staff.vo.StaffVo;
import sia.modelo.sgl.viaje.vo.SolicitudViajeVO;
import sia.modelo.sgl.viaje.vo.TipoEspecificoVo;
import sia.modelo.sgl.viaje.vo.VehiculoVO;
import sia.modelo.sgl.viaje.vo.ViajeFacturaVo;
import sia.modelo.sgl.viaje.vo.ViajeVO;
import sia.modelo.sgl.viaje.vo.ViajeroVO;
import sia.modelo.sgl.vo.ReporteVo;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.MonedaImpl;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;
import sia.servicios.sgl.impl.SgHuespedHotelImpl;
import sia.servicios.sgl.impl.SgHuespedStaffImpl;
import sia.servicios.sgl.impl.SgOficinaImpl;
import sia.servicios.sgl.impl.SgPagoServicioImpl;
import sia.servicios.sgl.impl.SgPagoServicioOficinaImpl;
import sia.servicios.sgl.impl.SgPagoServicioStaffImpl;
import sia.servicios.sgl.impl.SgPagoServicioVehiculoImpl;
import sia.servicios.sgl.impl.SgStaffImpl;
import sia.servicios.sgl.impl.SgTipoEspecificoImpl;
import sia.servicios.sgl.impl.SgTipoTipoEspecificoImpl;
import sia.servicios.sgl.impl.SgVehiculoMantenimientoImpl;
import sia.servicios.sgl.vehiculo.impl.SgVehiculoImpl;
import sia.servicios.sgl.viaje.impl.SgSolicitudViajeImpl;
import sia.servicios.sgl.viaje.impl.SgViajeFacturaImpl;
import sia.servicios.sgl.viaje.impl.SgViajeImpl;
import sia.servicios.sgl.viaje.impl.SgViajeroImpl;
import sia.servicios.sistema.impl.SiManejoFechaImpl;
import sia.servicios.sistema.vo.MonedaVO;
import sia.sgl.sistema.bean.backing.Sesion;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Named(value = "consultaBeanModel")
@ViewScoped
public class ConsultaBeanModel implements Serializable {

    @Inject
    private SgStaffImpl sgStaffImpl;
    @Inject
    private SgPagoServicioStaffImpl sgPagoServicioStaffImpl;
    @Inject
    private SgPagoServicioOficinaImpl sgPagoServicioOficinaImpl;
    @Inject
    private SgPagoServicioImpl sgPagoServicioImpl;
    @Inject
    private SgTipoTipoEspecificoImpl sgTipoTipoEspecificoImpl;
    @Inject
    private SgTipoEspecificoImpl sgTipoEspecificoImpl;
    @Inject
    private SgOficinaImpl sgOficinaImpl;
    @Inject
    private SgPagoServicioVehiculoImpl sgPagoServicioVehiculoImpl;
    @Inject
    private SgVehiculoMantenimientoImpl sgVehiculoMantenimientoImpl;
    @Inject
    private SgHuespedHotelImpl sgHuespedHotelImpl;
    @Inject
    private ProveedorServicioImpl proveedorImpl;
    @Inject
    private SgHuespedStaffImpl sgHuespedStaffImpl;
    @Inject
    private SgSolicitudViajeImpl sgSolicitudViajeImpl;
    @Inject
    private SgViajeImpl sgViajeImpl;
    @Inject
    private MonedaImpl monedaImpl;
    @Inject
    private SgViajeFacturaImpl sgViajeFacturaImpl;
    @Inject
    private SiManejoFechaImpl siManejoFechaImpl;
    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
    @Inject
    private SgVehiculoImpl sgVehiculoImpl;
    @Inject
    private SgViajeroImpl sgViajeroImpl;
    //Sistema
    @Inject
    private Sesion sesion;
    //
    private int idStaff;
    private int idServicio;
    private int idOficina;
    private int idOficinaHotel;
    private String servicioStaff;
    private String servicioHotel;
    private String servicioVehiculo;
    private String nombre;
//    private List listaPago;
    private List listaHuesped;
    private List listaSolicitud;
    private List listaViaje;
    private List listaViajeFactura;
    private List listaViajeros;
    //
    private String inicio;
    private String fin;
    private Date fechaInicio;
    private Date fechaFin;
    private int tipoSolicitud;
    private int idMoneda;
    private String idUsuario;
    private int idVehiculo;
    //
    private Map<String, List> mapaConsulta = new HashMap<String, List>();
    //
    private ReporteVo reporteOficinaVo;

    /**
     * Creates a new instance of ConsultaBeanModel
     */
    public ConsultaBeanModel() {
    }

    @PostConstruct
    public void iniciar() {
        //
        reporteOficinaVo = new ReporteVo();
        Date d = sgPagoServicioImpl.traerPrimerRegistro();
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        int primerAnio = c.get(Calendar.YEAR);
        Calendar fecha = Calendar.getInstance();
        int anioActual = fecha.get(Calendar.YEAR);
        List<SelectItem> l = new ArrayList<SelectItem>();
        l.add(new SelectItem(0, "Todos"));
        for (int i = primerAnio; i <= anioActual; i++) {
            l.add(new SelectItem(i, "" + i));
        }
        reporteOficinaVo.setListaAnio(l);

        reporteOficinaVo.setListaMes(siManejoFechaImpl.meses());

        setInicio("Casa 1");
        setFin(siManejoFechaImpl.convertirFechaStringddMMyyyy(new Date()));
        if (sesion.getOficinaActual() != null) {
            setIdOficina(sesion.getOficinaActual().getId());
        }
        setListaHuesped(null);
        //setServicio("");
        setNombre("");
        idStaff = Constantes.UNO;
    }

    public List<PagoServicioVo> pagoStaff() {
        try {
            return sgPagoServicioStaffImpl.pagoServicioStaffPorAnio(reporteOficinaVo.getAnio());
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex.getMessage());
            return null;
        }
    }

    public List<SelectItem> listaStaff() {
        List<StaffVo> listaStaff = sgStaffImpl.traerStaff();
        List<SelectItem> items = new ArrayList<SelectItem>();
        for (StaffVo staffVo : listaStaff) {
            items.add(new SelectItem(staffVo.getIdStaff(), staffVo.getNombre()));
        }
        return items;
    }

    public List<SelectItem> listaServicio() {
        List<TipoEspecificoVo> listaServicio = sgTipoTipoEspecificoImpl.traerPorTipo(Constantes.TIPO_PAGO_STAFF, Constantes.BOOLEAN_TRUE);
        List<SelectItem> items = new ArrayList<SelectItem>();
        for (TipoEspecificoVo tipoEspecificoVo : listaServicio) {
            items.add(new SelectItem(tipoEspecificoVo.getId(), tipoEspecificoVo.getNombre()));
        }
        return items;
    }

    public List<SelectItem> listaServicioVehiculo() {
        List<TipoEspecificoVo> listaServicio = sgPagoServicioVehiculoImpl.traerConceptosPago();
        List<SelectItem> items = new ArrayList<SelectItem>();
        for (TipoEspecificoVo tipoEspecificoVo : listaServicio) {
            items.add(new SelectItem(tipoEspecificoVo.getId(), tipoEspecificoVo.getNombre()));
        }
        return items;
    }

    public List<SelectItem> listaMoneda() {
        List<MonedaVO> lm = monedaImpl.traerMonedaActiva(Constantes.AP_CAMPO_DEFAULT);
        List<SelectItem> items = new ArrayList<SelectItem>();
        for (MonedaVO moneda : lm) {
            items.add(new SelectItem(moneda.getId(), moneda.getNombre()));
        }
        return items;
    }

    //////
    public List<SelectItem> listaVehiculo() {
        List<SelectItem> items = new ArrayList<SelectItem>();
        if (getIdOficina() > 0) {
            List<VehiculoVO> listaVehi = sgVehiculoImpl.traerVehiculoPorOficina(getIdOficina(), Constantes.NO_ELIMINADO);
            for (VehiculoVO vehiculo : listaVehi) {
                items.add(new SelectItem(vehiculo.getId(), vehiculo.getMarca() + " --> " + vehiculo.getModelo()));
            }
        } else {
            items.add(new SelectItem(Constantes.CERO, "Seleccione . . ."));
        }

        return items;
    }

    public List<PagoServicioVo> buscarTotalPagoStaff() {
        try {
            OficinaVO oficina = sgOficinaImpl.buscarPorNombre(reporteOficinaVo.getOficina());
            return sgPagoServicioStaffImpl.traerTotalPagoStaff(oficina.getId(), reporteOficinaVo.getAnio());
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex.getMessage());
            return null;
        }
    }

    public List<PagoServicioVo> buscarPagoStaff() {
        try {
            //   System.out.println("Staff: :: " + getReporteOficinaVo().getStaffHouse());
            StaffVo staffVo = sgStaffImpl.buscarPorNombre(reporteOficinaVo.getStaffHouse());
            reporteOficinaVo.setIdStaffHouse(staffVo.getIdStaff());
            return sgPagoServicioStaffImpl.traerPago(reporteOficinaVo.getIdStaffHouse(), reporteOficinaVo.getAnio());
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex.getMessage());
            return null;
        }
    }

    public void traerServiciosStaffHouse() {
        mapaConsulta.put("pagoStaff", sgPagoServicioStaffImpl.traerPagoServicio(idStaff, reporteOficinaVo.getServicio(), reporteOficinaVo.getAnio()));
    }

    public List<PagoServicioVo> traerServiciosPorFechas(String claveMapa) {
        List<PagoServicioVo> tmp = new ArrayList<PagoServicioVo>();
        for (Iterator it = mapaConsulta.get(claveMapa).iterator(); it.hasNext();) {
            PagoServicioVo get = (PagoServicioVo) it.next();
            if (get.getInicio().compareTo(reporteOficinaVo.getFechaInicio()) >= 0 && get.getFin().compareTo(reporteOficinaVo.getFechaFin()) <= 0) {
                tmp.add(get);
            }
        }
        return tmp;
    }

    ////////////////////////////// GASTO HOTEL
    public List<PagoServicioVo> totalGastoHotel() {
        try {
            return sgHuespedHotelImpl.totalGastoHotel(reporteOficinaVo.getAnio());
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex.getMessage());
            return null;
        }
    }

    public List<PagoServicioVo> buscarGastoHotel() {
        OficinaVO oficinaVO = sgOficinaImpl.buscarPorNombre(getNombre());
        idOficinaHotel = oficinaVO.getId();
        return sgHuespedHotelImpl.buscarGastoHotelOficina(idOficinaHotel, reporteOficinaVo.getAnio());
    }

    public List<PagoServicioVo> buscarGastoHotelPorAnio() {
        OficinaVO oficinaVO = sgOficinaImpl.buscarPorNombre(getNombre());
        idOficinaHotel = oficinaVO.getId();
        int idH = proveedorImpl.getPorNombre(getServicioHotel(), Constantes.RFC_IHSA).getId();

        return sgHuespedHotelImpl.buscarGastoHotelOficinaPorAnio(idOficinaHotel, idH, reporteOficinaVo.getAnio());
    }

    // HOSPEDADOS EN HOTEL
    public List<PagoServicioVo> totalHospedadosHotel() {
        try {
            return sgHuespedHotelImpl.totalHospedadosHotel(reporteOficinaVo.getAnio());
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex.getMessage());
            return null;
        }
    }

    public List<PagoServicioVo> totalHospedadosHotelOficina() {
        try {
            OficinaVO ofi = sgOficinaImpl.buscarPorNombre(getNombre());
            return sgHuespedHotelImpl.totalHospedadosHotelOficina(ofi.getId(), reporteOficinaVo.getAnio());
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex.getMessage());
            return null;
        }
    }

    public List<PagoServicioVo> totalHospedadosHotelOficinaMes() {
        try {
            OficinaVO ofi = sgOficinaImpl.buscarPorNombre(getNombre());
            return sgHuespedHotelImpl.totalHospedadosHotelOficinaMes(ofi.getId(), reporteOficinaVo.getAnio(), Integer.parseInt(getInicio().substring(0, 2)));
        } catch (NumberFormatException ex) {
            UtilLog4j.log.fatal(this, ex);
            return null;
        }
    }
////////////////////////////// VEHIUULOS

    public List<SelectItem> listaOficina() {
        List<OficinaVO> listaStaff = sgOficinaImpl.traerListaOficina();
        List<SelectItem> items = new ArrayList<SelectItem>();
        for (OficinaVO oficinaVO : listaStaff) {
            items.add(new SelectItem(oficinaVO.getId(), oficinaVO.getNombre()));
        }
        return items;
    }

    public List<PagoServicioVo> totalPagosVehiculos() {
        return sgPagoServicioVehiculoImpl.totalPagosVehiculos(reporteOficinaVo.getAnio());
    }

    public List<PagoServicioVo> totalPagosVehiculosPorOficina() {
        OficinaVO ofi = sgOficinaImpl.buscarPorNombre(getNombre());
        return sgPagoServicioVehiculoImpl.totalPagosVehiculosPorOficina(ofi.getId(), reporteOficinaVo.getAnio());
    }

    public void traerPagoServicio() {
        OficinaVO ofi = sgOficinaImpl.buscarPorNombre(getNombre());
        TipoEspecificoVo teVo = sgTipoEspecificoImpl.buscarPorNombre(getServicioVehiculo());
        mapaConsulta.put("pagoVehiculo", sgPagoServicioVehiculoImpl.traerTotalPagoServioVehiculo(ofi.getId(), teVo.getId(), reporteOficinaVo.getAnio()));
    }

    public void traerMantoPorTipo() {
        System.out.println("Serv: : : " + servicioVehiculo);
        TipoEspecificoVo teVo = sgTipoEspecificoImpl.buscarPorNombre(getServicioVehiculo());
        mapaConsulta.put("pagoVehiculoPorTipo", sgVehiculoMantenimientoImpl.traerMantoPagoPorMes(idOficina, reporteOficinaVo.getAnio(), teVo.getId()));
    }

    public List<PagoServicioVo> buscarPagosVehiculos() {
        int tipo = sgTipoTipoEspecificoImpl.buscarPorTipoEspecifico(getIdServicio());
        System.out.println("Tipo : : : " + tipo);
        if (tipo > 0 && tipo == Constantes.TIPO_PAGO_VEHICULO) {
            return sgPagoServicioVehiculoImpl.buscarPagos(getIdOficina(), getIdServicio());
        } else {
            return sgVehiculoMantenimientoImpl.traerPago(getIdOficina(), getIdServicio());
        }
    }

    public List<PagoServicioVo> totalPagosMantoVehiculos() {
        return sgVehiculoMantenimientoImpl.traerTotalPago(reporteOficinaVo.getAnio());
    }

    public List<PagoServicioVo> totalPagosMantoVehiculosPorOficina() {
        OficinaVO ofi = sgOficinaImpl.buscarPorNombre(getNombre());
        return sgVehiculoMantenimientoImpl.traerMantoPagoPorAnio(ofi.getId(), reporteOficinaVo.getAnio());
    }

    public void traerPagoVehiculos() {
//	System.out.println("Tipo : : : " + getIdServicio());
        int tipo = sgTipoTipoEspecificoImpl.buscarPorTipoEspecifico(getIdServicio());
        //System.out.println("Tipo : : : " + tipo);
        if (tipo > 0 && tipo == Constantes.TIPO_PAGO_VEHICULO) {
            mapaConsulta.put("pagoVehiculo", sgPagoServicioVehiculoImpl.traerPagoVehiculosPorServicio(getIdServicio(), getIdOficina(), getServicioVehiculo())); // servicio es el vehiculo
        } else {
            // System.out.println("Buscando mantenimientosss : : : " + getServicioVehiculo());
            mapaConsulta.put("pagoMantoVehiculo", sgVehiculoMantenimientoImpl.traerMantenimientoPorServicio(getIdServicio(), getIdOficina(), getServicioVehiculo())); // servicio es el vehiculo
        }
    }

    /////////////////////////////////////HOSPEDADOS HOTEL
    public void traerHospedadosHotel() {
        mapaConsulta.put("huespedHotel", sgHuespedHotelImpl.traerHospedadosHotel(getServicioHotel(), getInicio()));
    }

    public List<HuespedVo> traerHuespedesHotelPorFecha() {
        List<HuespedVo> tmp = new ArrayList<HuespedVo>();
        for (Iterator it = mapaConsulta.get("huespedHotel").iterator(); it.hasNext();) {
            HuespedVo get = (HuespedVo) it.next();
            if (get.getFechaIngreso().compareTo(fechaInicio) >= 0 && get.getFechaIngreso().compareTo(fechaFin) <= 0) {
                tmp.add(get);
            }
        }
        return tmp;
    }
    /////////////////////////////////////HOSPEDADOS STAFF

    public List<SgHuespedStaffVo> buscarHuespedadosStaff() {
        return sgHuespedStaffImpl.traerTotalHospedados(reporteOficinaVo.getAnio());
    }

    public List<SgHuespedStaffVo> buscarHuespedesStaffAnio() {
        OficinaVO ofi = sgOficinaImpl.buscarPorNombre(getNombre());
        return sgHuespedStaffImpl.traerTotalHospedadosAnio(ofi.getId(), reporteOficinaVo.getAnio());
    }

    public List<SgHuespedStaffVo> buscarHuespedesStaffAnioStaffH() {
        try {
            OficinaVO ofi = sgOficinaImpl.buscarPorNombre(getNombre());
            String mes = getInicio().substring(0, 2);
            return sgHuespedStaffImpl.traerTotalHospedadosAnioStaffH(ofi.getId(), reporteOficinaVo.getAnio(), Integer.parseInt(mes));
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
        return null;
    }

    public void traerHospedadosStaff() {
        OficinaVO  stVo = sgOficinaImpl.buscarPorNombre(getNombre());
        System.out.println("nombre: " + getNombre());
        System.out.println("staff: " + getServicioStaff());
        mapaConsulta.put("huespedStaff", sgHuespedStaffImpl.traerHospedados(stVo.getId(), getServicioStaff(), getInicio()));
    }

    /////////////////////////////////////// SOL VIAJE
    public List<SelectItem> listaTipoSolicitud() {
        //List<TipoEspecificoVo> lte = sgTipoTipoEspecificoImpl.traerPorTipo(Constantes.SOLICITUDES_TERRESTRES, false);
        List<SelectItem> items = new ArrayList<SelectItem>();
        items.add(new SelectItem(3, "Aéreas"));
        items.add(new SelectItem(21, "A oficina"));
        items.add(new SelectItem(22, "A Ciudad"));

        return items;
    }

    public List<SolicitudViajeVO> buscarSolicitudViaje() {
        return sgSolicitudViajeImpl.buscarTotalSolicitudViaje(getIdOficina(), getTipoSolicitud(), getInicio(), getFin());
    }

    public void traerSolicitudGerencia() {
        listaSolicitud = sgSolicitudViajeImpl.traerSolicitudPorGerencia(getIdOficina(), getTipoSolicitud(), getServicioVehiculo(), getInicio(), getFin());
    }

    /////////////////////////////////////// VIAJE
    public List<ViajeVO> totalViajes() {
        return sgViajeImpl.totalViaje(Constantes.CERO, reporteOficinaVo.getAnio());
    }

    public List<ViajeVO> buscarViaje() {
        OficinaVO oficinaVO = sgOficinaImpl.buscarPorNombre(reporteOficinaVo.getOficina());
        return sgViajeImpl.viajesPorAnioMes(oficinaVO.getId(), reporteOficinaVo.getAnio());
    }

    public void traerViajeMes() {
        OficinaVO oficinaVO = sgOficinaImpl.buscarPorNombre(reporteOficinaVo.getOficina());
        setListaViaje(sgViajeImpl.viajesViajeros(oficinaVO.getId(), reporteOficinaVo.getServicio()));
    }
    //////////////////////////// GASTO VIAJE AEREO //////////////////////

    public List<ViajeFacturaVo> buscarGastoViaje() {
        return sgViajeFacturaImpl.buscarGastoViajes(getInicio(), getFin(), getIdMoneda());

    }

    public void traerViajesPorGerencia() {
        listaViajeFactura = sgViajeFacturaImpl.buscarViajesPorGerencia(getServicioVehiculo(), getIdMoneda());
    }

    public String usuariosJson() {
        return apCampoUsuarioRhPuestoImpl.traerUsuarioJsonPorCampo(sesion.getUsuario().getApCampo().getId());
    }

    public List<ViajeFacturaVo> buscarGastoViajePorEmpleado() {
        return sgViajeFacturaImpl.buscarGastoViajePorEmpleado(getIdUsuario());
    }

    /////VEHICUL
    public Map<String, String> buscarDatosVehiculo() {
        return sgVehiculoImpl.buscarDatosVehiculo(getIdVehiculo(), getIdMoneda());

    }
////////////////////////////////VIAJEROS

    public List<ViajeroVO> buscarViajeros() {
        return sgViajeroImpl.totalViajerosAgregados(getFechaInicio(), getFechaFin());
    }

    public void traerViajerosPorOperacion() {
        setListaViajeros(sgViajeroImpl.traerViajerosPorOperacion(getServicioVehiculo(), getFechaInicio(), getFechaFin()));
    }

    // TOTAL DE GASTOS EN OFICINA
    public List<PagoServicioVo> traerTotalGastoOficina() {
        return sgPagoServicioOficinaImpl.pagoServicioOficinaPorAnio(reporteOficinaVo.getAnio());
    }

    public List<PagoServicioVo> traerServiciosPorOficina() {
        OficinaVO oficinaVO = sgOficinaImpl.buscarPorNombre(getNombre());
        System.out.println("ofi :: : " + oficinaVO.getNombre());
        return sgPagoServicioOficinaImpl.traerTotalPagoOficina(oficinaVO.getId(), reporteOficinaVo.getAnio());
    }

    public void traerServiciosOficina() {
        OficinaVO oficinaVO = sgOficinaImpl.buscarPorNombre(getNombre());
        System.out.println("ofi :: : " + oficinaVO.getNombre());
        mapaConsulta.put("pagoServicioOficina", sgPagoServicioOficinaImpl.traerPagoServicio(oficinaVO.getId(), reporteOficinaVo.getServicio(), reporteOficinaVo.getAnio()));
    }

    /**
     * @param sesion the sesion to set
     */
//////    public void setSesion(Sesion sesion) {
//////        this.sesion = sesion;
//////    }
    /**
     * @return the idStaff
     */
    public int getIdStaff() {
        return idStaff;
    }

    /**
     * @param idStaff the idStaff to set
     */
    public void setIdStaff(int idStaff) {
        this.idStaff = idStaff;
    }

    /**
     * @return the idServicio
     */
    public int getIdServicio() {
        return idServicio;
    }

    /**
     * @param idServicio the idServicio to set
     */
    public void setIdServicio(int idServicio) {
        this.idServicio = idServicio;
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
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
     * @return the inicio
     */
    public String getInicio() {
        return inicio;
    }

    /**
     * @param inicio the inicio to set
     */
    public void setInicio(String inicio) {
        this.inicio = inicio;
    }

    /**
     * @return the fin
     */
    public String getFin() {
        return fin;
    }

    /**
     * @param fin the fin to set
     */
    public void setFin(String fin) {
        this.fin = fin;
    }

    /**
     * @return the listaHuesped
     */
    public List getListaHuesped() {
        return listaHuesped;
    }

    /**
     * @param listaHuesped the listaHuesped to set
     */
    public void setListaHuesped(List listaHuesped) {
        this.listaHuesped = listaHuesped;
    }

    /**
     * @return the listaSolicitud
     */
    public List getListaSolicitud() {
        return listaSolicitud;
    }

    /**
     * @param listaSolicitud the listaSolicitud to set
     */
    public void setListaSolicitud(List listaSolicitud) {
        this.listaSolicitud = listaSolicitud;
    }

    /**
     * @return the listaViaje
     */
    public List getListaViaje() {
        return listaViaje;
    }

    /**
     * @param listaViaje the listaViaje to set
     */
    public void setListaViaje(List listaViaje) {
        this.listaViaje = listaViaje;
    }

    /**
     * @return the tipoSolicitud
     */
    public int getTipoSolicitud() {
        return tipoSolicitud;
    }

    /**
     * @param tipoSolicitud the tipoSolicitud to set
     */
    public void setTipoSolicitud(int tipoSolicitud) {
        this.tipoSolicitud = tipoSolicitud;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
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
     * @return the listaViajeFactura
     */
    public List getListaViajeFactura() {
        return listaViajeFactura;
    }

    /**
     * @param listaViajeFactura the listaViajeFactura to set
     */
    public void setListaViajeFactura(List listaViajeFactura) {
        this.listaViajeFactura = listaViajeFactura;
    }

    /**
     * @return the idMoneda
     */
    public String getIdUsuario() {
        return idUsuario;
    }

    /**
     * @param idMoneda the idMoneda to set
     */
    public void setIdUsuario(String idUsuario) {
        this.idUsuario = idUsuario;
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
     * @return the fechaInicio
     */
    public Date getFechaInicio() {
        return fechaInicio;
    }

    /**
     * @param fechaInicio the fechaInicio to set
     */
    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    /**
     * @return the fechaFin
     */
    public Date getFechaFin() {
        return fechaFin;
    }

    /**
     * @param fechaFin the fechaFin to set
     */
    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    /**
     * @return the listaViajeros
     */
    public List getListaViajeros() {
        return listaViajeros;
    }

    /**
     * @param listaViajeros the listaViajeros to set
     */
    public void setListaViajeros(List listaViajeros) {
        this.listaViajeros = listaViajeros;
    }

    /**
     * @return the servicioStaff
     */
    public String getServicioStaff() {
        return servicioStaff;
    }

    /**
     * @param servicioStaff the servicioStaff to set
     */
    public void setServicioStaff(String servicioStaff) {
        this.servicioStaff = servicioStaff;
    }

    /**
     * @return the servicioHotel
     */
    public String getServicioHotel() {
        return servicioHotel;
    }

    /**
     * @param servicioHotel the servicioHotel to set
     */
    public void setServicioHotel(String servicioHotel) {
        this.servicioHotel = servicioHotel;
    }

    /**
     * @return the servicioVehiculo
     */
    public String getServicioVehiculo() {
        return servicioVehiculo;
    }

    /**
     * @param servicioVehiculo the servicioVehiculo to set
     */
    public void setServicioVehiculo(String servicioVehiculo) {
        this.servicioVehiculo = servicioVehiculo;
    }

    /**
     * @return the mapaConsulta
     */
    public Map<String, List> getMapaConsulta() {
        return mapaConsulta;
    }

    /**
     * @param mapaConsulta the mapaConsulta to set
     */
    public void setMapaConsulta(Map<String, List> mapaConsulta) {
        this.mapaConsulta = mapaConsulta;
    }

    /**
     * @return the idOficinaHotel
     */
    public int getIdOficinaHotel() {
        return idOficinaHotel;
    }

    /**
     * @param idOficinaHotel the idOficinaHotel to set
     */
    public void setIdOficinaHotel(int idOficinaHotel) {
        this.idOficinaHotel = idOficinaHotel;
    }

    /**
     * @return the reporteOficinaVo
     */
    public ReporteVo getReporteOficinaVo() {
        return reporteOficinaVo;
    }

    /**
     * @param reporteOficinaVo the reporteOficinaVo to set
     */
    public void setReporteOficinaVo(ReporteVo reporteOficinaVo) {
        this.reporteOficinaVo = reporteOficinaVo;
    }

}
