/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.reporte.bean;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.primefaces.PrimeFaces;
import sia.modelo.sgl.estancia.vo.SgHuespedStaffVo;
import sia.modelo.sgl.pago.vo.PagoServicioVo;
import sia.modelo.sgl.viaje.vo.SolicitudViajeVO;
import sia.modelo.sgl.viaje.vo.ViajeFacturaVo;
import sia.modelo.sgl.viaje.vo.ViajeVO;
import sia.modelo.sgl.viaje.vo.ViajeroVO;
import sia.modelo.sgl.vo.ReporteVo;
import sia.sgl.reporte.bean.model.ConsultaBeanModel;
import sia.sgl.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Named(value = "consultaBean")
@RequestScoped
public class ConsultaBean implements Serializable {

    //Beans
    @ManagedProperty(value = "#{consultaBeanModel}")
    private ConsultaBeanModel consultaBeanModel;

    /**
     * Creates a new instance of ConsultaBean
     */
    public ConsultaBean() {
    }

    public void traerTotal(ActionEvent event) {
	try {
	    consultaBeanModel.getReporteOficinaVo().setAnio(0);
	    iniciar();
	} catch (JSONException ex) {
	    Logger.getLogger(ConsultaBean.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    public void traerPorAnio(ValueChangeEvent event) {
	consultaBeanModel.getReporteOficinaVo().setAnio(Integer.parseInt(event.getNewValue().toString()));
	try {
	    iniciar();
	} catch (JSONException ex) {
	    Logger.getLogger(ConsultaBean.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    private void iniciar() throws JSONException {
	       PrimeFaces.current().executeScript(";mostrarDiv('divTotalStaffHouse');");
	PrimeFaces.current().executeScript(";mostrarDiv('divTotalOficina');");
	PrimeFaces.current().executeScript(";mostrarDiv('divTotalHotel');");
	PrimeFaces.current().executeScript(";mostrarDiv('divTotalVehiculo');");
	PrimeFaces.current().executeScript(";mostrarDiv('divTotalMantoVehiculo');");
	PrimeFaces.current().executeScript(";mostrarDiv('divHuespedStaff');");
	PrimeFaces.current().executeScript(";mostrarDiv('divHuespedHotel');");
	// ocultar div's
	PrimeFaces.current().executeScript(";ocultarDiv('divDetallePagoStaffHouse');");
	PrimeFaces.current().executeScript(";ocultarDiv('divDetalleHotel');");
	PrimeFaces.current().executeScript(";ocultarDiv('divDetalleVehiculo');");
	PrimeFaces.current().executeScript(";ocultarDiv('divVehiculoManto');");
	PrimeFaces.current().executeScript(";ocultarDiv('divGraficaHospedadosStaff');");
	PrimeFaces.current().executeScript(";ocultarDiv('divGraficaHospedadosHotel');");
	PrimeFaces.current().executeScript(";ocultarDiv('divDetalleViaje');");

	       JSONArray ja = new JSONArray();
	List<PagoServicioVo> lPago = consultaBeanModel.pagoStaff();
	double t = 0;
	for (PagoServicioVo psv : lPago) {
	           JSONObject j = new JSONObject();
	    j.put("name", psv.getOficinaVO().getNombre());
	    j.put("total", psv.getTotal());
	    t += psv.getTotal();
	    ja.put(j);
	}
	//
        
	PrimeFaces.current().executeScript(
		";graficaPagos(" + ja + ", 'graficaPagoTotalStaff', 'frmOficina' , 'nombreOficina' ,'btnBuscarPorOficina', 'Pagos Staff House', " + t + ", 'Si');");
	// Gasto hotel
	ja = new JSONArray();
	t = 0;
	lPago = consultaBeanModel.totalGastoHotel();
	for (PagoServicioVo psv : lPago) {
	    JSONObject j = new JSONObject();
	    j.put("name", psv.getOficinaVO().getNombre());
	    j.put("total", psv.getTotal());
	    ja.put(j);
	    //
	    t += psv.getTotal();
	}
	//
	PrimeFaces.current().executeScript(
		";graficaPagos(" + ja + ", 'graficaGastoTotalHotel', 'frmHotelServicio' , 'hidenNombreOficina' ,'btnBuscarHotelPorOficina', 'Pagos Hotel', " + t + ", 'Si');");
	// total vehiculos
	ja = new JSONArray();
	t = 0;
	lPago = consultaBeanModel.totalPagosVehiculos();
	for (PagoServicioVo psv : lPago) {
	    JSONObject j = new JSONObject();
	    j.put("name", psv.getOficinaVO().getNombre());
	    j.put("total", psv.getTotal());
	    ja.put(j);
	    t += psv.getTotal();
	}
	PrimeFaces.current().executeScript(
		";graficaPagos(" + ja + ", 'graficaGastoTotalVehiculo', 'frmVehiculo' , 'hidenNombreOficinaVehiculo' ,'btnBuscarVehiculoPorOficina', 'Pagos Vehículo', " + t + ", 'Si');");
	// Total pagos vehiculos
	ja = new JSONArray();
	lPago = consultaBeanModel.totalPagosMantoVehiculos();
	t = 0;
	for (PagoServicioVo psv : lPago) {
	    JSONObject j = new JSONObject();
	    j.put("name", psv.getTipoEspecifico());
	    j.put("total", psv.getTotal());
	    t += psv.getTotal();
	    ja.put(j);
	}
	PrimeFaces.current().executeScript(
		";graficaPagos(" + ja + ", 'graficaGastoTotalMantoVehiculo', 'frmMAntoVehiculo' , 'hidenNombreOficinaMantoVehiculo'"
		+ " ,'btnBuscarMantoVehiculoPorOficina', 'Pagos de mantenimiento', " + t + ", 'Si');");

	// Total de oficina
	ja = new JSONArray();
	lPago = consultaBeanModel.traerTotalGastoOficina();
	t = 0;
	for (PagoServicioVo psv : lPago) {
	    JSONObject j = new JSONObject();
	    j.put("name", psv.getOficinaVO().getNombre());
	    j.put("total", psv.getTotal());
	    t += psv.getTotal();
	    ja.put(j);
	}
	PrimeFaces.current().executeScript(
		";graficaPagos(" + ja + ", 'graficaGastoTotalOficina', 'frmTotalOficina' , 'hidenNombreTotalOficina'"
		+ " ,'btnBuscarTotalPorOficina', 'Pagos oficina', " + t + ", 'Si');");
	// hospedados en staff
	ja = new JSONArray();
	List<SgHuespedStaffVo> lHuespedStaff = consultaBeanModel.buscarHuespedadosStaff();
	t = 0;
	for (SgHuespedStaffVo psv : lHuespedStaff) {
	    JSONObject j = new JSONObject();
	    j.put("name", psv.getOficina());
	    j.put("total", psv.getTotal());
	    t += psv.getTotal();
	    ja.put(j);
	}
	PrimeFaces.current().executeScript(
		";graficaPagos(" + ja + ", 'graficaHuespedStaff', 'frmHuespedStaff' , 'hidenOficina'"
		+ " ,'btnBuscarHuespedesSHAnio', 'Hospedados Staff House', " + t + ", 'No');");
	// Hospedado en hotel
	ja = new JSONArray();
	List<PagoServicioVo> lTotal = consultaBeanModel.totalHospedadosHotel();
	t = 0;
	for (PagoServicioVo psv : lTotal) {
	    JSONObject j = new JSONObject();
	    j.put("name", psv.getOficinaVO().getNombre());
	    j.put("total", psv.getTotalEntero());
	    t += psv.getTotalEntero();
	    ja.put(j);
	}
	PrimeFaces.current().executeScript(
		";graficaPagos(" + ja + ", 'graficaHuespedHotel', 'frmHuespedHotel' , 'hidenOficinaHotel'"
		+ " ,'btnHotelPorOficina', 'Hospedados Hotel', " + t + ", 'No');");
	///
	// Viajes
	ja = new JSONArray();
	List<ViajeVO> lViaje = consultaBeanModel.totalViajes();
	t = 0;
	for (ViajeVO psv : lViaje) {
	    JSONObject j = new JSONObject();
	    j.put("name", psv.getOrigen());
	    j.put("total", psv.getTotal());
	    t += psv.getTotal();
	    ja.put(j);
	}
	PrimeFaces.current().executeScript(
		";graficaPagos(" + ja + ", 'graficaTotalViaje', 'frmViaje' , 'nombreOficina'"
		+ " ,'btnBuscarPorOficina', ' Viajes ', " + t + ", 'No');");
	///

    }

    public void buscarPagosStaffahousePorOficina(ActionEvent event) {
	try {
	    PrimeFaces.current().executeScript(";mostrarDiv('divGraficaStaff');");
	    PrimeFaces.current().executeScript(";mostrarDiv('divDetallePagoStaffHouse');");
	    PrimeFaces.current().executeScript(";ocultarDiv('divTotalHotel');");
	    PrimeFaces.current().executeScript(";ocultarDiv('divTotalVehiculo');");
	    PrimeFaces.current().executeScript(";ocultarDiv('graficaServicioStaff');");
	    PrimeFaces.current().executeScript(";ocultarDiv('graficaFechaServicioStaff');");
	    PrimeFaces.current().executeScript(";ocultarDiv('divTotalMantoVehiculo');");
	    PrimeFaces.current().executeScript(";ocultarDiv('divVehiculoManto');");
	    PrimeFaces.current().executeScript(";ocultarDiv('divTotalOficina');");

	    JSONObject j = new JSONObject();
	    String json;
	    List<String> u = new ArrayList<String>();
	    List<Double> total = new ArrayList<Double>();
	    List<PagoServicioVo> lPago = consultaBeanModel.buscarTotalPagoStaff();
	    if (lPago != null) {
		for (PagoServicioVo psv : lPago) {
		    u.add(psv.getTipoEspecifico());
		    total.add(psv.getTotal());
		}
	    }
	    //
	    j.put("Servicio", u);
	    j.put("total", total);
	    json = j.toString();
	    //System.out.println(json);
	    PrimeFaces.current().executeScript(";llenarPagos(" + json + ",  '"
		    + consultaBeanModel.getReporteOficinaVo().getOficina() + "', 'frmPorStaff', 'graficaPagoStaffHouse', 'btnBuscarPorStaffHouse', 'nombreStaff');");
	} catch (JSONException ex) {
	    Logger.getLogger(ConsultaBean.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    public void seleccionarStaffHouse(ActionEvent event) {
	try {
	    PrimeFaces.current().executeScript(";mostrarDiv('graficaServicioStaff');");
	    PrimeFaces.current().executeScript(";ocultarDiv('graficaFechaServicioStaff');");
	    JSONObject j = new JSONObject();
	    String json;
	    List<String> u = new ArrayList<String>();
	    List<Double> total = new ArrayList<Double>();
	    List<PagoServicioVo> lPago = consultaBeanModel.buscarPagoStaff();
	    if (lPago != null) {
		for (PagoServicioVo psv : lPago) {
		    u.add(psv.getTipoEspecifico());
		    total.add(psv.getTotal());
		}
		//
		j.put("Servicio", u);
		j.put("total", total);
		json = j.toString();
		//    UtilLog4j.log.info(this, "Cad : : : " + json.toString());
		PrimeFaces.current().executeScript(";llenarPagos(" + json + ", '"
			+ consultaBeanModel.getReporteOficinaVo().getStaffHouse() + "', 'frmStaffServicio', 'graficaPorStaffHouse', 'btnBuscarPorServicio', 'hidenServicioStaff');");
	    } else {
		FacesUtils.addInfoMessage("No hay datos");
	    }

	} catch (JSONException ex) {
	    Logger.getLogger(ConsultaBean.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    public void buscarPagoStaff(ValueChangeEvent event) {

    }

    public void limiarLista(ActionEvent event) {
	consultaBeanModel.setListaHuesped(null);
	consultaBeanModel.setListaSolicitud(null);
	consultaBeanModel.setListaViajeros(null);
	//consultaBeanModel.setServicio("");
    }

    public void listaPagosStaff(ActionEvent event) {
	try {
	    PrimeFaces.current().executeScript(";mostrarDiv('graficaFechaServicioStaff');");
	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	    JSONObject j = new JSONObject();
	    String json;
	    List<String> u = new ArrayList<String>();
	    List<Double> total = new ArrayList<Double>();
	    consultaBeanModel.traerServiciosStaffHouse();
	    for (Object object : consultaBeanModel.getMapaConsulta().get("pagoStaff")) {
		PagoServicioVo psv = (PagoServicioVo) object;
		u.add(sdf.format(psv.getFin()));
		total.add(psv.getImporte());
	    }
	    //
	    j.put("fecha", u);
	    j.put("importe", total);
	    json = j.toString();
	    PrimeFaces.current().executeScript(
		    ";llenarPagoStaffServicio(" + json + ", '" + consultaBeanModel.getReporteOficinaVo().getServicio() + "',"
		    + " 'graficaStaffHouseServicio', 'frmStaffServicio', 'btnBuscarPorServicio', 'column');");
	} catch (JSONException ex) {
	    Logger.getLogger(ConsultaBean.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    public void listaPagosStaffPorFechas(ActionEvent event) {
	try {
	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	    JSONObject j = new JSONObject();
	    String json;
	    List<String> u = new ArrayList<String>();
	    List<Double> total = new ArrayList<Double>();

	    for (Object object : consultaBeanModel.traerServiciosPorFechas("pagoStaff")) {
		PagoServicioVo psv = (PagoServicioVo) object;
		u.add(sdf.format(psv.getFin()));
		total.add(psv.getImporte());
	    }
	    //
	    j.put("fecha", u);
	    j.put("importe", total);
	    json = j.toString();
	    System.out.println("Ser  por fechas: " + json);
	    //    UtilLog4j.log.info(this, "Servicio : : : " + json.toString());
	    PrimeFaces.current().executeScript(
		    ";llenarPagoStaffServicio(" + json + ", '" + consultaBeanModel.getReporteOficinaVo().getServicio() + "',"
		    + " 'graficaStaffHouseServicio', 'frmStaffServicio', 'btnBuscarPorServicio', 'column');");

	} catch (JSONException ex) {
	    Logger.getLogger(ConsultaBean.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    ////////////////////////////////////////////////////////////// INICIO GASTO HOTEL ////////////////////
    public void buscarGastoHotelPorOficina(ActionEvent event) {
	try {
	    PrimeFaces.current().executeScript(";mostrarDiv('divDetalleHotel');");
	    PrimeFaces.current().executeScript(";mostrarDiv('divGraficaHotelPorOficina');");
	    PrimeFaces.current().executeScript(";ocultarDiv('divTotalStaffHouse');");
	    PrimeFaces.current().executeScript(";ocultarDiv('divTotalVehiculo');");
	    PrimeFaces.current().executeScript(";ocultarDiv('graficaServicioStaff');");
	    PrimeFaces.current().executeScript(";ocultarDiv('graficaFechaServicioStaff');");
	    PrimeFaces.current().executeScript(";ocultarDiv('divTotalMantoVehiculo');");
	    PrimeFaces.current().executeScript(";ocultarDiv('graficaGastoPorAnioHotelOficina');");
	    PrimeFaces.current().executeScript(";ocultarDiv('divVehiculoManto');");
	    PrimeFaces.current().executeScript(";ocultarDiv('divTotalOficina');");
	    JSONObject j = new JSONObject();
	    String json;
	    List<String> u = new ArrayList<String>();
	    List<Double> total = new ArrayList<Double>();
	    List<PagoServicioVo> lPago = consultaBeanModel.buscarGastoHotel();
	    for (PagoServicioVo psv : lPago) {
		u.add(psv.getTipoEspecifico());
		total.add(psv.getTotal());
	    }
	    //
	    j.put("Servicio", u);
	    j.put("total", total);
	    json = j.toString();
	    //    UtilLog4j.log.info(this, "Cad : : : " + json.toString());
	    PrimeFaces.current().executeScript(";llenarPagos(" + json + ", '" + consultaBeanModel.getNombre()
		    + "', 'frmHotelPorAnio', 'graficaGastoHotelOficina', 'btnBuscarPagoHotelPorAnio', 'hidenNombrePorAnioHotel');");
	} catch (JSONException ex) {
	    Logger.getLogger(ConsultaBean.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    public void huespedesPorHotel(ActionEvent event) {
	try {
	    consultaBeanModel.traerHospedadosHotel();
	} catch (Exception ex) {
	    Logger.getLogger(ConsultaBean.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    public void filtroHuespedesHotel(ActionEvent event) {
	consultaBeanModel.getMapaConsulta().put("huespedHotel", consultaBeanModel.traerHuespedesHotelPorFecha());
    }

////////////////////////////////////////////////////////////// INICIO VEHICULO ////////////////////
    public List<SelectItem> getListaOficina() {
	return consultaBeanModel.listaOficina();
    }

    public void totalPagosVehiculosPorOficina(ActionEvent event) {
	try {
	    PrimeFaces.current().executeScript(";mostrarDiv('divDetalleVehiculo');");
	    PrimeFaces.current().executeScript(";ocultarDiv('divTotalStaffHouse');");
	    PrimeFaces.current().executeScript(";ocultarDiv('divTotalHotel');");
	    PrimeFaces.current().executeScript(";ocultarDiv('divTotalMantoVehiculo');");
	    PrimeFaces.current().executeScript(";ocultarDiv('divVehiculoManto');");
	    PrimeFaces.current().executeScript(";ocultarDiv('divTotalOficina');");
	    PrimeFaces.current().executeScript(";ocultarDiv('divServiciosVehiculo');");
	    JSONArray ja = new JSONArray();
	    List<PagoServicioVo> lPago = consultaBeanModel.totalPagosVehiculosPorOficina();
	    double t = 0;

	    for (PagoServicioVo psv : lPago) {
		JSONObject j = new JSONObject();
		j.put("name", psv.getTipoEspecifico());
		j.put("total", psv.getTotal());
		t += psv.getTotal();
		ja.put(j);
	    }
	    PrimeFaces.current().executeScript(
		    ";graficaPagos(" + ja + ", 'graficaTotalServicioVehiculo', 'frmServicioVehiculo' , 'hidenNombreServicioVehiculo' ,'btnBuscarPorServicioVehiculo', '" + consultaBeanModel.getNombre() + "', " + t + ", 'Si');");
	} catch (JSONException ex) {
	    Logger.getLogger(ConsultaBean.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    public void totalPagosVehiculosPorServicio(ActionEvent event) {
	try {
	    PrimeFaces.current().executeScript(";mostrarDiv('divServiciosVehiculo');");
	    PrimeFaces.current().executeScript(";mostrarDiv('graficaServicioVehiculo');");
	    JSONObject j = new JSONObject();
	    String json;
	    List<String> u = new ArrayList<String>();
	    List<Double> total = new ArrayList<Double>();
	    consultaBeanModel.traerPagoServicio();
	    if (consultaBeanModel.getMapaConsulta().get("pagoVehiculo") != null) {
		for (Iterator iterator = consultaBeanModel.getMapaConsulta().get("pagoVehiculo").iterator(); iterator.hasNext();) {
		    PagoServicioVo psv = (PagoServicioVo) iterator.next();
		    u.add(psv.getTipoEspecifico());
		    total.add(psv.getTotal());
		}
		//
		j.put("Servicio", u);
		j.put("total", total);
		json = j.toString();
		//    UtilLog4j.log.info(this, "Cad : : : " + jso
		PrimeFaces.current().executeScript(";llenarPagos(" + json + ", '"
			+ consultaBeanModel.getServicioVehiculo() + "', 'frmServicioVehiculo', 'graficaServicioVehiculo', "
			+ "'btnBuscarPorServicioVehiculo', 'hidenNombreServicioVehiculo');");
	    } else {
		FacesUtils.addInfoMessage("No hay datos");
	    }

	} catch (Exception e) {
	    UtilLog4j.log.error(e);
	}
    }

    public void traerMantoPorTipo(ActionEvent event) {
	try {
	    PrimeFaces.current().executeScript(";mostrarDiv('graficaDetalleTipoMantenimiento');");
	    JSONObject j = new JSONObject();
	    String json;
	    List<String> u = new ArrayList<String>();
	    List<Double> total = new ArrayList<Double>();
	    consultaBeanModel.traerMantoPorTipo();
	    if (consultaBeanModel.getMapaConsulta().get("pagoVehiculoPorTipo") != null) {
		for (Iterator iterator = consultaBeanModel.getMapaConsulta().get("pagoVehiculoPorTipo").iterator(); iterator.hasNext();) {
		    PagoServicioVo psv = (PagoServicioVo) iterator.next();
		    u.add(psv.getTipoEspecifico());
		    total.add(psv.getTotal());
		}
		//
		j.put("Servicio", u);
		j.put("total", total);
		json = j.toString();
		//    UtilLog4j.log.info(this, "Cad : : : " + jso
		PrimeFaces.current().executeScript(";llenarPagos(" + json + ", '"
			+ consultaBeanModel.getServicioVehiculo() + "', 'frmServicioVehiculoManto', 'graficaDetalleTipoMantenimiento', "
			+ "'btnBuscarPorServicioVehiculoManto', 'hidenServicioVehiculoManto');");
	    } else {
		FacesUtils.addInfoMessage("No hay datos");
	    }

	} catch (Exception e) {
	    UtilLog4j.log.error(e);
	}
    }

    public void traerPagoVehiculos(ActionEvent event) {
	if (getIdServicio() > 0) {
	    consultaBeanModel.traerPagoVehiculos();
	} else {
	    consultaBeanModel.getMapaConsulta().put("pagoVehiculo", null);
	    FacesUtils.addInfoMessage("Para hacer esta consulta, primero seleccione el servicio.");
	}
    }

    public void listaPagosVehiculo(ActionEvent event) {
	try {
	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	    JSONObject j = new JSONObject();
	    String json;
	    List<String> u = new ArrayList<String>();
	    List<Double> total = new ArrayList<Double>();
	    for (Object object : consultaBeanModel.getMapaConsulta().get("pagoVehiculo")) {
		PagoServicioVo psv = (PagoServicioVo) object;
		u.add(sdf.format(psv.getFin()));
		total.add(psv.getImporte());
	    }
	    //
	    j.put("fecha", u);
	    j.put("importe", total);
	    json = j.toString();
	    UtilLog4j.log.info(this, "Servicio Vehiculo: : : " + json);
	    PrimeFaces.current().executeScript(";llenarPagoStaffServicio(" + json + ", '" + getServicioVehiculo() + "', 'graficaVehiculo');");
	} catch (JSONException ex) {
	    Logger.getLogger(ConsultaBean.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    ////
    public void totalPagosMantoVehiculosPorOficina(ActionEvent event) {
	try {
	    PrimeFaces.current().executeScript(";ocultarDiv('divServiciosVehiculo');");
	    PrimeFaces.current().executeScript(";ocultarDiv('divTotalStaffHouse');");
	    PrimeFaces.current().executeScript(";ocultarDiv('divTotalHotel');");
	    PrimeFaces.current().executeScript(";ocultarDiv('divTotalVehiculo');");
	    PrimeFaces.current().executeScript(";ocultarDiv('divDetalleVehiculo');");
	    PrimeFaces.current().executeScript(";ocultarDiv('graficaDetalleTipoMantenimiento');");
	    PrimeFaces.current().executeScript(";mostrarDiv('divTotalMantoVehiculo');");
	    PrimeFaces.current().executeScript(";mostrarDiv('divVehiculoManto');");
	    PrimeFaces.current().executeScript(";ocultarDiv('divTotalOficina');");

	    JSONArray ja = new JSONArray();
	    List<PagoServicioVo> lPago = consultaBeanModel.totalPagosMantoVehiculosPorOficina();
	    double t = 0;
	    for (PagoServicioVo psv : lPago) {
		JSONObject j = new JSONObject();
		j.put("name", psv.getTipoEspecifico());
		j.put("total", psv.getTotal());
		t += psv.getTotal();
		ja.put(j);
	    }
	    PrimeFaces.current().executeScript(
		    ";graficaPagos(" + ja + ", 'graficaTipoMantenimiento', 'frmServicioVehiculoManto' , 'hidenServicioVehiculoManto'"
		    + " ,'btnBuscarPorServicioVehiculoManto', '" + consultaBeanModel.getNombre() + "', " + t + ", 'Si');");
	} catch (JSONException ex) {
	    Logger.getLogger(ConsultaBean.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    ////////////////////////////HOSPEDADOS HOTEL ///////////////////////
    public void buscarHotelOficina(ActionEvent event) {
	try {
	    PrimeFaces.current().executeScript(";ocultarDiv('divHuespedHotelMes');");
	    PrimeFaces.current().executeScript(";mostrarDiv('divGraficaHospedadosHotel');");
	    PrimeFaces.current().executeScript(";ocultarDiv('divHuespedStaff');");
	    PrimeFaces.current().executeScript(";ocultarDiv('divTotalOficina');");

	    List<PagoServicioVo> lPago = consultaBeanModel.totalHospedadosHotelOficina();
	    String json;
	    JSONObject j = new JSONObject();
	    List<String> u = new ArrayList<>();
	    List<Long> total = new ArrayList<>();
	    for (PagoServicioVo psv : lPago) {
		u.add(psv.getTipoEspecifico());
		total.add(psv.getTotalEntero());
	    }
	    //
	    j.put("Servicio", u);
	    j.put("total", total);
	    json = j.toString();
	    PrimeFaces.current().executeScript(
		    ";llenarPagos(" + json + ",  '" + consultaBeanModel.getNombre() + "', "
		    + "'frmHuespedHotelOficina', 'graficaHuespedHotelOficina' , 'btnBuscarHuespedesHotelMes'"
		    + " ,'hidenOficinaHotelMes');");

	} catch (JSONException ex) {
	    Logger.getLogger(ConsultaBean.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    public void traerHospedadoHotelMes(ActionEvent event) {
	try {
	    PrimeFaces.current().executeScript(";mostrarDiv('divHuespedHotelMes');");
	    List<PagoServicioVo> lPago = consultaBeanModel.totalHospedadosHotelOficinaMes();
	    String json;
	    JSONObject j = new JSONObject();
	    List<String> u = new ArrayList<>();
	    List<Long> total = new ArrayList<>();
	    for (PagoServicioVo psv : lPago) {
		u.add(psv.getTipoEspecifico());
		total.add(psv.getTotalEntero());
	    }
	    //
	    j.put("Servicio", u);
	    j.put("total", total);
	    json = j.toString();
	    PrimeFaces.current().executeScript(
		    ";llenarPagos(" + json + ",  '" + consultaBeanModel.getInicio() + "', "
		    + "'frmHuespedPorHotel', 'graficaHuespedHotelMes' , 'btnBuscarPorServicio'"
		    + " ,'hidenServicio');");
            //Limpia la lista de huespedes
            consultaBeanModel.getMapaConsulta().put("huespedHotel", new ArrayList<>());
	} catch (JSONException ex) {
	    Logger.getLogger(ConsultaBean.class.getName()).log(Level.SEVERE, null, ex);
	}

    }

    public void traerHospedadosHotel(ActionEvent event) {
	consultaBeanModel.traerHospedadosHotel();
    }

    public void traerHospedadosHotelPorAnio(ActionEvent event) {
	try {
	    PrimeFaces.current().executeScript(";mostrarDiv('graficaGastoPorAnioHotelOficina');");
	    JSONObject j = new JSONObject();
	    String json;
	    List<String> u = new ArrayList<String>();
	    List<Double> total = new ArrayList<Double>();
	    List<PagoServicioVo> lPago = consultaBeanModel.buscarGastoHotelPorAnio();
	    for (PagoServicioVo psv : lPago) {
		u.add(psv.getTipoEspecifico());
		total.add(psv.getTotal());
	    }
	    //
	    j.put("Servicio", u);
	    j.put("total", total);
	    json = j.toString();
	    //    UtilLog4j.log.info(this, "Cad : : : " + json.toString());
	    PrimeFaces.current().executeScript(";llenarPagos(" + json + ", '" + consultaBeanModel.getServicioHotel()
		    + "', 'frmHospedadosHotel', 'graficaGastoPorAnioHotelOficina', 'btnBuscarHuespedes', 'hidenNombreHotel');");

	} catch (JSONException ex) {
	    Logger.getLogger(ConsultaBean.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    ////////////////////////////HOSPEDADOS STAFF ///////////////////////
    public void buscarHuespedesStaffAnio(ActionEvent event) {
    }

    public void traerHospedadosStaff(ActionEvent event) {
	try {
	    PrimeFaces.current().executeScript(";ocultarDiv('divHuespedStaffHouseMes');");
	    PrimeFaces.current().executeScript(";mostrarDiv('divGraficaHospedadosStaff');");
	    PrimeFaces.current().executeScript(";ocultarDiv('divHuespedHotel');");
	    PrimeFaces.current().executeScript(";ocultarDiv('divTotalOficina');");

	    JSONObject j = new JSONObject();
	    String json;
	    List<String> u = new ArrayList<>();
	    List<Long> total = new ArrayList<>();
	    List<SgHuespedStaffVo> lPago = consultaBeanModel.buscarHuespedesStaffAnio();
	    for (SgHuespedStaffVo psv : lPago) {
		u.add(psv.getOficina());
		total.add(psv.getTotal());
	    }
	    //
	    j.put("Servicio", u);
	    j.put("total", total);
	    json = j.toString();
	    //    UtilLog4j.log.info(this, "Cad : : : " + json.toString());
	    PrimeFaces.current().executeScript(";llenarPagos(" + json + ", '" + consultaBeanModel.getNombre()
		    + "', 'frmHuespedStaffMes', 'graficaHuespedStaffAnio', 'btnBuscarHuespedesSHMes', 'hidenOficinaMes');");

	} catch (JSONException ex) {
	    Logger.getLogger(ConsultaBean.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    public void traerHospedadosStaffMes(ActionEvent event) {
	try {
	    PrimeFaces.current().executeScript(";mostrarDiv('divHuespedStaffHouseMes');");
	    JSONObject j = new JSONObject();
	    String json;
	    List<String> u = new ArrayList<>();
	    List<Long> total = new ArrayList<>();
	    List<SgHuespedStaffVo> lPago = consultaBeanModel.buscarHuespedesStaffAnioStaffH();
	    if (lPago != null) {
		for (SgHuespedStaffVo psv : lPago) {
		    u.add(psv.getOficina());
		    total.add(psv.getTotal());
		}
		//
		j.put("Servicio", u);
		j.put("total", total);
		json = j.toString();
		PrimeFaces.current().executeScript(";llenarPagos(" + json + ", '" + consultaBeanModel.getInicio()
			+ "', 'frmHuespedStaffPorMes', 'graficaHuespedStaffMes', 'btnhuespedesSHMes', 'hidenStaffPorMes');");
	    }
            //
            consultaBeanModel.getMapaConsulta().get("huespedStaff").clear();
	} catch (JSONException ex) {
	    Logger.getLogger(ConsultaBean.class.getName()).log(Level.SEVERE, null, ex);
	}
    }
    public  void hospedadosPorStaffMes(ActionEvent event){
	consultaBeanModel.traerHospedadosStaff();        
    }

    /////////////////////////////////////// SOL VIAJE
    public List<SelectItem> getListaTipoSolicitud() {
	return consultaBeanModel.listaTipoSolicitud();
    }

    public void buscarSolicitudViaje(ActionEvent event) {
	try {
	    JSONObject j = new JSONObject();
	    String json;
	    List<String> u = new ArrayList<String>();
	    List<Integer> total = new ArrayList<Integer>();
	    List<SolicitudViajeVO> lPago = consultaBeanModel.buscarSolicitudViaje();
	    for (SolicitudViajeVO psv : lPago) {
		u.add(psv.getGerencia());
		total.add(psv.getTotal());
	    }
	    //
	    j.put("Servicio", u);
	    j.put("total", total);
	    json = j.toString();
	    consultaBeanModel.setListaSolicitud(null);
//            System.out.println("hospedados totales : : : " + json.toString());
//	    UtilLog4j.log.info(this, "sol viaje totales : : : " + json);
	    PrimeFaces.current().executeScript(";llenarPagos(" + json + ", 'idComboOficina', 'frmSolViaje', 'graficaSolViaje');");
	} catch (JSONException ex) {
	    Logger.getLogger(ConsultaBean.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    public void traerSolicitudGerencia(ActionEvent event) {
	consultaBeanModel.traerSolicitudGerencia();
    }

    /////////////////////////////////////// VIAJES
    public void buscarViaje(ActionEvent event) {
	PrimeFaces.current().executeScript(";mostrarDiv('divDetalleViaje');");

	try {
	    JSONObject j = new JSONObject();
	    String json;
	    List<String> u = new ArrayList<String>();
	    List<Integer> total = new ArrayList<Integer>();
	    List<ViajeVO> lViaje = consultaBeanModel.buscarViaje();
	    for (ViajeVO psv : lViaje) {
		u.add(psv.getOrigen());
		total.add(psv.getTotal());
	    }
	    //
	    j.put("Servicio", u);
	    j.put("total", total);
	    json = j.toString();
	    ///    UtilLog4j.log.info(this, "sol viaje totales : : : " + json);
	    PrimeFaces.current().executeScript(";llenarPagos(" + json + ", '" + consultaBeanModel.getReporteOficinaVo().getOficina()
		    + "', 'frmViajePorOficina', 'graficaViajesPorOficina', 'btnBuscarPorGerencia', 'nombreOficina');");
	} catch (JSONException ex) {
	    Logger.getLogger(ConsultaBean.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    public void traerViaje(ActionEvent event) {
	consultaBeanModel.traerViajeMes();
    }

    //////////////////////////// GASTO VIAJE AEREO //////////////////////
    public void buscarGastoViaje(ActionEvent event) {
	try {
	    JSONObject j = new JSONObject();
	    String json;
	    List<String> u = new ArrayList<String>();
	    List<Double> total = new ArrayList<Double>();
	    List<ViajeFacturaVo> lViaje = consultaBeanModel.buscarGastoViaje();
	    for (ViajeFacturaVo psv : lViaje) {
		u.add(psv.getGerencia());
		total.add(psv.getTotal());
	    }
	    //
	    j.put("Servicio", u);
	    j.put("total", total);
	    json = j.toString();
	    UtilLog4j.log.info(this, "sol viaje totales : : : " + json);
	    PrimeFaces.current().executeScript(";llenarPagos(" + json + ", 'idComboMoneda', 'frmCostoViaje', 'graficaCostoViaje');");
	} catch (JSONException ex) {
	    Logger.getLogger(ConsultaBean.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    public void traerViajesPorGerencia(ActionEvent event) {
	consultaBeanModel.traerViajesPorGerencia();
    }

    public void limiarListaJon(ActionEvent event) {
	limiarLista(event);
	String usuario = consultaBeanModel.usuariosJson();
	PrimeFaces.current().executeScript(";llenarProveedor('frmCostoViaje'," + usuario + ", 'nombreUsuario');");
    }

    public void buscarGastoViajePorEmpleado(ActionEvent event) {
	try {
	    JSONObject j = new JSONObject();
	    String json;
	    List<String> u = new ArrayList<String>();
	    List<Double> total = new ArrayList<Double>();
	    List<ViajeFacturaVo> lViaje = consultaBeanModel.buscarGastoViajePorEmpleado();

	    for (ViajeFacturaVo psv : lViaje) {
		u.add(psv.getGerencia());
		total.add(psv.getTotal());
	    }
	    j.put("Servicio", u);
	    j.put("total", total);
	    json = j.toString();
	    UtilLog4j.log.info(this, "sol viaje totales : : : " + json);
	    PrimeFaces.current().executeScript(";llenarPagos(" + json + ", 'autocomplete', 'frmCostoViaje', 'graficaCostoViaje');");
	} catch (JSONException ex) {
	    Logger.getLogger(ConsultaBean.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    /// VEHICULOS
    public List<SelectItem> getListaVeiculo() {
	return consultaBeanModel.listaVehiculo();
    }

    public void buscarDatosVehiculo(ActionEvent event) {
	try {
	    Map<String, String> mapV = consultaBeanModel.buscarDatosVehiculo();
	    List<String> servicio = new ArrayList<String>();
	    List<Double> totales = new ArrayList<Double>();
	    JSONObject j = new JSONObject();
	    String json;

	    for (Map.Entry<String, String> entrySet : mapV.entrySet()) {
		servicio.add(entrySet.getKey());
		totales.add(Double.parseDouble(entrySet.getValue()));
	    }
	    j.put("Servicio", servicio);
	    j.put("total", totales);
	    json = j.toString();
	    System.out.println("Mapa  :: : : : " + json);
	    PrimeFaces.current().executeScript(";llenarPagos(" + json + ", 'idComboVehiculo', 'frmVehiculoDato', 'graficaVehiculoViaje');");
	} catch (JSONException ex) {
	    System.out.println("Ocurrio un error : : : : : " + ex.getMessage());
	}
    }

    /////////////////////////////VIAJES
    public void buscarViajeros(ActionEvent event) {
	try {
	    JSONObject j = new JSONObject();
	    String json;
	    List<String> u = new ArrayList<String>();
	    List<Integer> total = new ArrayList<Integer>();
	    List<ViajeroVO> viajeros = consultaBeanModel.buscarViajeros();
	    for (ViajeroVO viajero : viajeros) {
		u.add(viajero.getOperacion());
		total.add(viajero.getTotal());
	    }
	    //
	    j.put("Servicio", u);
	    j.put("total", total);
	    json = j.toString();
	    consultaBeanModel.setServicioVehiculo("");
	    consultaBeanModel.setListaHuesped(null);
	    UtilLog4j.log.info(this, " Viajeros totales : : : " + json);
	    PrimeFaces.current().executeScript(";llenarPagos(" + json + ", 'Viajeros agregados', 'frmViajeros', 'graficaViajeros');");
	} catch (JSONException ex) {
	    Logger.getLogger(ConsultaBean.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    public void traerViajerosPorOperacion(ActionEvent event) {
	consultaBeanModel.traerViajerosPorOperacion();
    }

    public void traerServiciosPorOficina(ActionEvent event) {
	try {
	    PrimeFaces.current().executeScript(";ocultarDiv('divDetalleHotel');");
	    PrimeFaces.current().executeScript(";ocultarDiv('divGraficaHotelPorOficina');");
	    PrimeFaces.current().executeScript(";ocultarDiv('divTotalStaffHouse');");
	    PrimeFaces.current().executeScript(";ocultarDiv('divTotalHotel');");
	    PrimeFaces.current().executeScript(";ocultarDiv('divTotalVehiculo');");
	    PrimeFaces.current().executeScript(";ocultarDiv('divVehiculoManto');");
	    PrimeFaces.current().executeScript(";ocultarDiv('graficaServicioStaff');");
	    PrimeFaces.current().executeScript(";ocultarDiv('graficaFechaServicioStaff');");
	    PrimeFaces.current().executeScript(";ocultarDiv('divTotalMantoVehiculo');");
	    PrimeFaces.current().executeScript(";ocultarDiv('graficaGastoPorAnioHotelOficina');");
	    PrimeFaces.current().executeScript(";ocultarDiv('divDetalleServicioOficina');");

	    PrimeFaces.current().executeScript(";mostrarDiv('divDetalleOficina');");

	    JSONObject j = new JSONObject();
	    String json;
	    List<String> u = new ArrayList<String>();
	    List<Double> total = new ArrayList<Double>();
	    List<PagoServicioVo> lPago = consultaBeanModel.traerServiciosPorOficina();
	    for (PagoServicioVo psv : lPago) {
		u.add(psv.getTipoEspecifico());
		total.add(psv.getTotal());
	    }
	    //
	    j.put("Servicio", u);
	    j.put("total", total);
	    json = j.toString();
	    //    UtilLog4j.log.info(this, "Cad : : : " + json.toString());
	    PrimeFaces.current().executeScript(";llenarPagos(" + json + ", '" + consultaBeanModel.getNombre()
		    + "', 'frmOficinaServicio', 'graficaPorOficina', 'btnBuscarPorServicioOficina', 'hidenServicioOficina');");
	} catch (JSONException ex) {
	    Logger.getLogger(ConsultaBean.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    public void listaPagoServicioOficina(ActionEvent event) {
	try {
	    PrimeFaces.current().executeScript(";mostrarDiv('divDetalleServicioOficina');");

	    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	    JSONObject j = new JSONObject();
	    String json;
	    List<String> u = new ArrayList<String>();
	    List<Double> total = new ArrayList<Double>();
	    consultaBeanModel.traerServiciosOficina();
	    for (Object object : consultaBeanModel.getMapaConsulta().get("pagoServicioOficina")) {
		PagoServicioVo psv = (PagoServicioVo) object;
		u.add(sdf.format(psv.getFin()));
		total.add(psv.getImporte());
	    }
	    //
	    j.put("fecha", u);
	    j.put("importe", total);
	    json = j.toString();
	    System.out.println("Ser  por fechas: " + json);
	    //    UtilLog4j.log.info(this, "Servicio : : : " + json.toString());
	    PrimeFaces.current().executeScript(
		    ";llenarPagoStaffServicio(" + json + ", '" + consultaBeanModel.getReporteOficinaVo().getServicio() + "',"
		    + " 'graficaServiciosOficina', 'frmServicioOficina', '', 'column');");

	} catch (JSONException ex) {
	    Logger.getLogger(ConsultaBean.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    /**
     * @return the idStaff
     */
    public int getIdStaff() {
	return consultaBeanModel.getIdStaff();
    }

    /**
     * @param idStaff the idStaff to set
     */
    public void setIdStaff(int idStaff) {
	consultaBeanModel.setIdStaff(idStaff);
    }

    /**
     * @return the idServicio
     */
    public int getIdServicio() {
	return consultaBeanModel.getIdServicio();
    }

    /**
     * @param idServicio the idServicio to set
     */
    public void setIdServicio(int idServicio) {
	consultaBeanModel.setIdServicio(idServicio);
    }

    /**
     * @return the nombre
     */
    public String getNombre() {
	return consultaBeanModel.getNombre();
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
	consultaBeanModel.setNombre(nombre);
    }

    /**
     * @param consultaBeanModel the consultaBeanModel to set
     */
    public void setConsultaBeanModel(ConsultaBeanModel consultaBeanModel) {
	this.consultaBeanModel = consultaBeanModel;
    }

    /**
     * @return the idOficina
     */
    public int getIdOficina() {
	return consultaBeanModel.getIdOficina();
    }

    /**
     * @param idOficina the idOficina to set
     */
    public void setIdOficina(int idOficina) {
	consultaBeanModel.setIdOficina(idOficina);
    }

    /**
     * @return the inicio
     */
    public String getInicio() {
	return consultaBeanModel.getInicio();
    }

    /**
     * @param inicio the inicio to set
     */
    public void setInicio(String inicio) {
	consultaBeanModel.setInicio(inicio);
    }

    /**
     * @return the fin
     */
    public String getFin() {
	return consultaBeanModel.getFin();
    }

    /**
     * @param fin the fin to set
     */
    public void setFin(String fin) {
	consultaBeanModel.setFin(fin);
    }

    /**
     * @return the listaHuesped
     */
    public List getListaHuesped() {
	return consultaBeanModel.getListaHuesped();
    }

    /**
     * @param listaHuesped the listaHuesped to set
     */
    public void setListaHuesped(List listaHuesped) {
	consultaBeanModel.setListaHuesped(listaHuesped);
    }

    /**
     * @return the listaSolicitud
     */
    public List getListaSolicitud() {
	return consultaBeanModel.getListaSolicitud();
    }

    /**
     * @param listaSolicitud the listaSolicitud to set
     */
    public void setListaSolicitud(List listaSolicitud) {
	consultaBeanModel.setListaSolicitud(listaSolicitud);
    }

    /**
     * @return the listaViaje
     */
    public List getListaViaje() {
	return consultaBeanModel.getListaViaje();
    }

    /**
     * @param listaViaje the listaViaje to set
     */
    public void setListaViaje(List listaViaje) {
	consultaBeanModel.setListaViaje(listaViaje);
    }

    /**
     * @return the tipoSolicitud
     */
    public int getTipoSolicitud() {
	return consultaBeanModel.getTipoSolicitud();
    }

    /**
     * @param tipoSolicitud the tipoSolicitud to set
     */
    public void setTipoSolicitud(int tipoSolicitud) {
	consultaBeanModel.setTipoSolicitud(tipoSolicitud);
    }

    /**
     * @return the idMoneda
     */
    public int getIdMoneda() {
	return consultaBeanModel.getIdMoneda();
    }

    /**
     * @param idMoneda the idMoneda to set
     */
    public void setIdMoneda(int idMoneda) {
	consultaBeanModel.setIdMoneda(idMoneda);
    }

    /**
     * @return the listaViajeFactura
     */
    public List getListaViajeFactura() {
	return consultaBeanModel.getListaViajeFactura();
    }

    /**
     * @param listaViajeFactura the listaViajeFactura to set
     */
    public void setListaViajeFactura(List listaViajeFactura) {
	consultaBeanModel.setListaViajeFactura(listaViajeFactura);
    }

    /**
     * @return the idMoneda
     */
    public String getIdUsuario() {
	return consultaBeanModel.getIdUsuario();
    }

    /**
     * @param idMoneda the idMoneda to set
     */
    public void setIdUsuario(String idUsuario) {
	consultaBeanModel.setIdUsuario(idUsuario);
    }

    /**
     * @return the idVehiculo
     */
    public int getIdVehiculo() {
	return consultaBeanModel.getIdVehiculo();
    }

    /**
     * @param idVehiculo the idVehiculo to set
     */
    public void setIdVehiculo(int idVehiculo) {
	consultaBeanModel.setIdVehiculo(idVehiculo);
    }

    /**
     * @return the fechaInicio
     */
    public Date getFechaInicio() {
	return consultaBeanModel.getFechaInicio();
    }

    /**
     * @param fechaInicio the fechaInicio to set
     */
    public void setFechaInicio(Date fechaInicio) {
	consultaBeanModel.setFechaInicio(fechaInicio);
    }

    /**
     * @return the fechaFin
     */
    public Date getFechaFin() {
	return consultaBeanModel.getFechaFin();
    }

    /**
     * @param fechaFin the fechaFin to set
     */
    public void setFechaFin(Date fechaFin) {
	consultaBeanModel.setFechaFin(fechaFin);
    }

    /**
     * @return the listaViajeros
     */
    public List getListaViajeros() {
	return consultaBeanModel.getListaViajeros();
    }

    /**
     * @param listaViajeros the listaViajeros to set
     */
    public void setListaViajeros(List listaViajeros) {
	consultaBeanModel.setListaViajeros(listaViajeros);
    }

    /**
     * @return the servicioStaff
     */
    public String getServicioStaff() {
	return consultaBeanModel.getServicioStaff();
    }

    /**
     * @param servicioStaff the servicioStaff to set
     */
    public void setServicioStaff(String servicioStaff) {
	consultaBeanModel.setServicioStaff(servicioStaff);
    }

    /**
     * @return the servicioHotel
     */
    public String getServicioHotel() {
	return consultaBeanModel.getServicioHotel();
    }

    /**
     * @param servicioHotel the servicioHotel to set
     */
    public void setServicioHotel(String servicioHotel) {
	consultaBeanModel.setServicioHotel(servicioHotel);
    }

    /**
     * @return the servicioVehiculo
     */
    public String getServicioVehiculo() {
	return consultaBeanModel.getServicioVehiculo();
    }

    /**
     * @param servicioVehiculo the servicioVehiculo to set
     */
    public void setServicioVehiculo(String servicioVehiculo) {
	consultaBeanModel.setServicioVehiculo(servicioVehiculo);
    }

    /**
     * @return the mapaConsulta
     */
    public Map<String, List> getMapaConsulta() {
	return consultaBeanModel.getMapaConsulta();
    }

    /**
     * @param mapaConsulta the mapaConsulta to set
     */
    public void setMapaConsulta(Map<String, List> mapaConsulta) {
	consultaBeanModel.setMapaConsulta(mapaConsulta);
    }

    /**
     * @return the idOficinaHotel
     */
    public int getIdOficinaHotel() {
	return consultaBeanModel.getIdOficinaHotel();
    }

    /**
     * @param idOficinaHotel the idOficinaHotel to set
     */
    public void setIdOficinaHotel(int idOficinaHotel) {
	consultaBeanModel.setIdOficinaHotel(idOficinaHotel);
    }

    /**
     * @return the reporteOficinaVo
     */
    public ReporteVo getReporteOficinaVo() {
	return consultaBeanModel.getReporteOficinaVo();
    }

    /**
     * @param reporteOficinaVo the reporteOficinaVo to set
     */
    public void setReporteOficinaVo(ReporteVo reporteOficinaVo) {
	consultaBeanModel.setReporteOficinaVo(reporteOficinaVo);
    }
}
