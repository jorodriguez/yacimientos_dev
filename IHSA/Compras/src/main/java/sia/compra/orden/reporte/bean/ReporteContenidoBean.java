/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.orden.reporte.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedProperty;

import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.shaded.json.JSONException;
import org.primefaces.shaded.json.JSONObject;
import sia.compra.requisicion.bean.backing.UsuarioBean;
import sia.modelo.sgl.vo.OrdenVO;
import sia.modelo.sistema.vo.FacturaVo;
import sia.servicios.catalogos.impl.MonedaImpl;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.sistema.impl.SiFacturaContenidoNacionalImpl;
import sia.servicios.sistema.impl.SiFacturaImpl;
import sia.servicios.sistema.vo.MonedaVO;

/**
 *
 * @author mluis
 */
@Named (value = "reporteContenidoBean")
@ViewScoped
public class ReporteContenidoBean implements Serializable {

    /**
     * @return the idMonedaProveedores
     */
    public int getIdMonedaProveedores() {
        return idMonedaProveedores;
    }

    /**
     * @param idMonedaProveedores the idMonedaProveedores to set
     */
    public void setIdMonedaProveedores(int idMonedaProveedores) {
        this.idMonedaProveedores = idMonedaProveedores;
    }

    /**
     * Creates a new instance of ReporteContenidoBean
     */
    public ReporteContenidoBean() {
    }

    @Inject
    private UsuarioBean sesion;
    //    
    @Inject
    OrdenImpl ordenImpl;
    @Inject
    SiFacturaImpl siFacturaImpl;
    @Inject
    SiFacturaContenidoNacionalImpl facturaContenidoNacionalImpl;
    @Inject
    MonedaImpl monedaImpl;

    private Map<String, List<?>> mapaTotales;
    private Map<String, List<SelectItem>> mapaSeleccion;
    private int idMonOcs, idMonFac, idAnio, idMonedaProveedores;

    @PostConstruct
    public void iniciar() {
        mapaSeleccion = new HashMap<>();
        //
        List<MonedaVO> lm = monedaImpl.traerPorCompania(sesion.getUsuarioConectado().getApCampo().getCompania().getRfc());
        List<SelectItem> ls = new ArrayList<>();
        for (MonedaVO monedaVO : lm) {
            ls.add(new SelectItem(monedaVO.getId(), monedaVO.getNombre()));
        }
        mapaSeleccion.put("monOcs", ls);
        mapaSeleccion.put("monFac", ls);
        mapaSeleccion.put("monProveedores", ls);

        //
        List<Integer> l = ordenImpl.traerAniosOrden(sesion.getUsuarioConectado().getApCampo().getCompania().getRfc());
        List<SelectItem> la = new ArrayList<>();
        for (Integer integer : l) {
            la.add(new SelectItem(integer));
        }
        mapaSeleccion.put("selAnio", la);
        //
        idMonOcs = sesion.getUsuarioConectado().getApCampo().getCompania().getMoneda().getId();
        idMonFac = sesion.getUsuarioConectado().getApCampo().getCompania().getMoneda().getId();
        idMonedaProveedores = sesion.getUsuarioConectado().getApCampo().getCompania().getMoneda().getId();
        //
        Calendar calendar = Calendar.getInstance();
        idAnio = calendar.get(calendar.YEAR);
        // totalde ocs
        mapaTotales = new HashMap<>();
        //
        cargarDatos();
//
        graficaOcs();
        graficaFac();
        graficaContNac();
        //
        graficaTopProveedores();
    }

    public void cargarDatos() {
//        System.out.println("anio: " + idAnio);
        getMapaTotales().put("ocs", ordenImpl.traerTotalesPorCompania(sesion.getUsuarioConectado().getApCampo().getCompania().getRfc(), idAnio, idMonOcs));
        getMapaTotales().put("factura", siFacturaImpl.traerTotalesPorCompania(sesion.getUsuarioConectado().getApCampo().getCompania().getRfc(), idAnio, idMonFac));
        getMapaTotales().put("cn", facturaContenidoNacionalImpl.traerTotalContNacPorCompania(sesion.getUsuarioConectado().getApCampo().getCompania().getRfc(), idAnio));

    }

    public void cambiarAnio(AjaxBehaviorEvent event) {
        cargarDatos();
        //
        graficaOcs();
        graficaFac();
        graficaContNac();
        //
        graficaTopProveedores();

    }

    public void buscarTotalOcs(AjaxBehaviorEvent event) {
        getMapaTotales().put("ocs", ordenImpl.traerTotalesPorCompania(sesion.getUsuarioConectado().getApCampo().getCompania().getRfc(), idAnio, idMonOcs));
        //
        graficaOcs();
    }

    public void buscarTotalFac(AjaxBehaviorEvent event) {
        getMapaTotales().put("factura", siFacturaImpl.traerTotalesPorCompania(sesion.getUsuarioConectado().getApCampo().getCompania().getRfc(), idAnio, idMonFac));
        //
        graficaFac();
    }

    private void graficaOcs() {
        try {
            JSONObject j = new JSONObject();
            String json;
            List<String> u = new ArrayList<>();
            List<Double> total = new ArrayList<>();
            for (Iterator iterator = getMapaTotales().get("ocs").iterator(); iterator.hasNext();) {
                OrdenVO psv = (OrdenVO) iterator.next();
                u.add(psv.getBloque());
                total.add(psv.getTotal());
            }
            //
            j.put("name", u);
            j.put("y", total);
            json = j.toString();
            // println("json: " + j.toString());
            PrimeFaces.current().executeScript(
                    ";graficaTotales(" + json + ", 'graficaTotalOcs', 'Total oc/s');");
            //
        } catch (JSONException ex) {
            Logger.getLogger(ReporteContenidoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void graficaFac() {
        try {
            JSONObject j = new JSONObject();
            String json;
            List<String> u = new ArrayList<>();
            List<BigDecimal> total = new ArrayList<>();
            for (Iterator iterator = getMapaTotales().get("factura").iterator(); iterator.hasNext();) {
                FacturaVo psv = (FacturaVo) iterator.next();
                u.add(psv.getCampo());
                total.add(psv.getMonto());
            }
            //
            j.put("name", u);
            j.put("y", total);
            json = j.toString();

            //
            // println("json: " + j.toString());
            PrimeFaces.current().executeScript(
                    ";graficaTotales(" + json + ", 'graficaTotalFac', 'Total facturas');");
            //
        } catch (JSONException ex) {
            Logger.getLogger(ReporteContenidoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void graficaContNac() {
        try {
            JSONObject j = new JSONObject();
            String json;
            List<String> u = new ArrayList<>();
            List<BigDecimal> total = new ArrayList<>();
            for (Iterator iterator = getMapaTotales().get("cn").iterator(); iterator.hasNext();) {
                FacturaVo psv = (FacturaVo) iterator.next();
                u.add(psv.getCampo());
                total.add(psv.getMonto());
            }
            j.put("name", u);
            j.put("y", total);
            //
            json = j.toString();
            // println("json: " + j.toString());
            PrimeFaces.current().executeScript(
                    ";graficaTotales(" + json + ", 'graficaTotalCn', 'Contenido Nacional');");
            //
        } catch (JSONException ex) {
            Logger.getLogger(ReporteContenidoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void graficaProveedoresTopMoneda(AjaxBehaviorEvent event) {
        graficaTopProveedores();
    }

    private void graficaTopProveedores() {
        try {

            JSONObject j = new JSONObject();
            String json;
            List<String> proveedor = new ArrayList<>();
            List<BigDecimal> facturado = new ArrayList<>();
            List<BigDecimal> contenido = new ArrayList<>();
            List<BigDecimal> porcentaje = new ArrayList<>();

            for (FacturaVo psv : siFacturaImpl.traerTotalFacturaContenido(sesion.getUsuarioConectado().getApCampo().getCompania().getRfc(), idAnio)) {
                proveedor.add(psv.getProveedor());
                facturado.add(psv.getMonto());
                contenido.add(psv.getSubTotal());
                porcentaje.add(psv.getPorcentaje());
            }
            j.put("proveedores", proveedor);
            j.put("facturado", facturado);
            j.put("contenido", contenido);
            j.put("porcentaje", porcentaje);
            //
            json = j.toString();
            // println("json: " + j.toString());
            PrimeFaces.current().executeScript(
                    ";graficaFacturadContenido('graficaProveedoresTop', " + json + ", 'Facturado y Contenido Nacional (MXN)')");
            //
        } catch (JSONException ex) {
            Logger.getLogger(ReporteContenidoBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(UsuarioBean sesion) {
        this.sesion = sesion;
    }

    /**
     * @return the mapaTotales
     */
    public Map<String, List<?>> getMapaTotales() {
        return mapaTotales;
    }

    /**
     * @param mapaTotales the mapaTotales to set
     */
    public void setMapaTotales(Map<String, List<?>> mapaTotales) {
        this.mapaTotales = mapaTotales;
    }

    /**
     * @return the mapaSeleccion
     */
    public Map<String, List<SelectItem>> getMapaSeleccion() {
        return mapaSeleccion;
    }

    /**
     * @param mapaSeleccion the mapaSeleccion to set
     */
    public void setMapaSeleccion(Map<String, List<SelectItem>> mapaSeleccion) {
        this.mapaSeleccion = mapaSeleccion;
    }

    /**
     * @return the idMonOcs
     */
    public int getIdMonOcs() {
        return idMonOcs;
    }

    /**
     * @param idMonOcs the idMonOcs to set
     */
    public void setIdMonOcs(int idMonOcs) {
        this.idMonOcs = idMonOcs;
    }

    /**
     * @return the idMonFac
     */
    public int getIdMonFac() {
        return idMonFac;
    }

    /**
     * @param idMonFac the idMonFac to set
     */
    public void setIdMonFac(int idMonFac) {
        this.idMonFac = idMonFac;
    }

    /**
     * @return the idAnio
     */
    public int getIdAnio() {
        return idAnio;
    }

    /**
     * @param idAnio the idAnio to set
     */
    public void setIdAnio(int idAnio) {
        this.idAnio = idAnio;
    }

}
