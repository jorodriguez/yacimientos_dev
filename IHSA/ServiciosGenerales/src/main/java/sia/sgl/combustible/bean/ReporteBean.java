/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.combustible.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.primefaces.PrimeFaces;
import sia.modelo.sgl.vo.ReporteVo;
import sia.servicios.sgl.combustible.impl.SgTarjetaOperacionImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Named(value = "reporteBeanConsumo")
@ViewScoped
public class ReporteBean implements Serializable {

    public ReporteBean() {
    }
    @Inject
    private SgTarjetaOperacionImpl sgTarjetaOperacionImpl;
    //
    private ReporteVo reporteVo;
    private List<SelectItem> listaAnio;

    @PostConstruct
    public void iniciar() {
	try {
	    int primerAnio;
	    setListaAnio(new ArrayList<SelectItem>());
	    Date d = sgTarjetaOperacionImpl.traerPrimerRegistro();
	    Calendar c = Calendar.getInstance();
	    if (d != null) {
		c.setTime(d);
		primerAnio = c.get(Calendar.YEAR);
	    } else {
		primerAnio = c.get(Calendar.YEAR);
	    }

	    Calendar fecha = Calendar.getInstance();
	    int anioActual = fecha.get(Calendar.YEAR);
	    List<SelectItem> l = new ArrayList<SelectItem>();
	    l.add(new SelectItem(0, "Todos"));
	    for (int i = primerAnio; i <= anioActual; i++) {
		l.add(new SelectItem(i, "" + i));
	    }
	    setListaAnio(l);
	    reporteVo = new ReporteVo();
	    cargarTotales();
	} catch (JSONException ex) {
	    UtilLog4j.log.warn(ex);
	}
    }

    public void traerPorAnio(ValueChangeEvent event) {
	reporteVo.setAnio(Integer.parseInt(event.getNewValue().toString()));
	try {
	    cargarTotales();
	           PrimeFaces.current().executeScript(";ocultarDiv('divTotalGerencia');");
	    PrimeFaces.current().executeScript(";ocultarDiv('divTotalUsuario');");
	} catch (Exception ex) {
	    Logger.getLogger(ReporteBean.class.getName()).log(Level.SEVERE, null, ex);
	}
    }

    private void cargarTotales() throws JSONException {
	       JSONArray ja = new JSONArray();

	//
	List<ReporteVo> lPago = sgTarjetaOperacionImpl.traerTotales(reporteVo.getAnio());
	double t = 0;
	for (ReporteVo psv : lPago) {
	           JSONObject j = new JSONObject();
	    j.put("name", psv.getNombre());
	    j.put("total", psv.getTotal());
	    t += psv.getTotal();
	    ja.put(j);
	}
	//
        
	PrimeFaces.current().executeScript(
		";graficaPagos(" + ja + ", 'graficaTotalConsumo', 'frmGerencia' , 'gerencia' ,'btnBuscarPorGerencia', 'Consumo de combustible', " + t + ", 'Si');");

    }

    public void desglosarConsumoPorGerencia(ActionEvent event) {
	JSONArray ja = new JSONArray();
	PrimeFaces.current().executeScript(";mostrarDiv('divTotalGerencia');");
	PrimeFaces.current().executeScript(";ocultarDiv('divTotalUsuario');");
	List<ReporteVo> lPago = sgTarjetaOperacionImpl.traerPorGerencia(reporteVo.getAnio(), reporteVo.getGerencia());
	double t = 0;
	try {
	    for (ReporteVo psv : lPago) {
		JSONObject j = new JSONObject();
		j.put("name", psv.getNombre());
		j.put("total", psv.getTotal());
		t += psv.getTotal();
		ja.put(j);
	    }
	} catch (JSONException ex) {
	    Logger.getLogger(ReporteBean.class.getName()).log(Level.SEVERE, null, ex);
	}
	//
	PrimeFaces.current().executeScript(
		";graficaPagos(" + ja + ", 'graficaGastoTotalUsuario', 'frmTotalUsuario' , 'hidenNombreUsuario' ,'btnBuscarTotalPorUsuario', 'Consumo de combustible', " + t + ", 'Si');");

    }

    public void traerConsumoPorUsuario(ActionEvent event) {
	JSONArray ja = new JSONArray();
	List<ReporteVo> lPago = sgTarjetaOperacionImpl.traerPorUsuarioMesAnio(reporteVo.getNombre(), reporteVo.getAnio(), reporteVo.getMes());
	double t = 0;
	try {
	    for (ReporteVo psv : lPago) {
		JSONObject j = new JSONObject();
		j.put("name", psv.getNombre());
		j.put("total", psv.getTotal());
		t += psv.getTotal();
		ja.put(j);
	    }
	} catch (JSONException ex) {
	    Logger.getLogger(ReporteBean.class.getName()).log(Level.SEVERE, null, ex);
	}
	//
	PrimeFaces.current().executeScript(
		";graficaPagos(" + ja + ", 'graficaGastoUsuarioMes', 'frmTotalUsuarioMes' , 'hidenMes' ,'btnBuscarPorUsuarioMes', 'Consumo de combustible', " + t + ", 'Si');");

    }

    /**
     * @return the reporteVo
     */
    public ReporteVo getReporteVo() {
	return reporteVo;
    }

    /**
     * @param repoteVo the reporteVo to set
     */
    public void setReporteVo(ReporteVo repoteVo) {
	this.reporteVo = repoteVo;
    }

    /**
     * @return the listaAnio
     */
    public List<SelectItem> getListaAnio() {
	return listaAnio;
    }

    /**
     * @param listaAnio the listaAnio to set
     */
    public void setListaAnio(List<SelectItem> listaAnio) {
	this.listaAnio = listaAnio;
    }
}
