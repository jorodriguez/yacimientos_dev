/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.orden.reporte.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedProperty;


import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import sia.compra.requisicion.bean.backing.UsuarioBean;
import sia.constantes.Constantes;
import sia.modelo.proveedor.Vo.ProveedorVo;
import sia.modelo.sgl.vo.OrdenVO;
import sia.servicios.catalogos.impl.MonedaImpl;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;
import sia.servicios.proveedor.impl.PvProveedorCompaniaImpl;
import sia.util.OrdenEstadoEnum;
import sia.util.ProveedorEnum;

/**
 *
 * @author mluis
 */
@Named (value = "reporteContabilidadBean")
@ViewScoped
public class ReporteContabilidadBean implements Serializable{

    /**
     * Creates a new instance of ReporteContabilidadBean
     */
    public ReporteContabilidadBean() {
    }
    @Inject
    private UsuarioBean sesion;
    @Inject
    OrdenImpl ordenImpl;
    @Inject
    MonedaImpl monedaImpl;
    @Inject
    PvProveedorCompaniaImpl proveedorCompaniaImpl;
    @Inject
    ProveedorServicioImpl proveedorImpl;

    private Map<String, List<?>> mapaTotales;
    private int idMonOcs, idMonFac, idAnio, idMonedaProveedores;
    private String proveedor, proveedorSeleccionado;
    private int proceso;
    private List<SelectItem> proveedoresDisponibles;
    private List<ProveedorVo> proveedores;

    @PostConstruct
    public void iniciar() {
        //
        proveedor = Constantes.VACIO;
        mapaTotales = new HashMap<>();
        cargarDatos(proveedor);
        //
        proceso = Constantes.UNO;
        //

        proveedoresDisponibles = new ArrayList<>();
        proveedores = new ArrayList<>();
        proveedores = proveedorCompaniaImpl.traerProveedorPorCompania(sesion.getUsuarioConectado().getApCampo().getCompania().getRfc(), ProveedorEnum.ACTIVO.getId());

        //
    }

    public void cargarDatos(String prov) {
        getMapaTotales().put("proyecto", ordenImpl.totalCompraProceso(sesion.getUsuarioConectado().getApCampo().getId(), OrdenEstadoEnum.POR_VOBO.getId(), OrdenEstadoEnum.POR_ENVIAR_PROVEEDOR.getId(), prov));
        getMapaTotales().put("proyectoPorRecibir", ordenImpl.totalCompraProceso(sesion.getUsuarioConectado().getApCampo().getId(), OrdenEstadoEnum.POR_RECIBIR.getId(), OrdenEstadoEnum.POR_RECIBIR_FACTURA.getId(), prov));
//
        getMapaTotales().put("comprasProceso", ordenImpl.totalCompras(sesion.getUsuarioConectado().getApCampo().getId(), OrdenEstadoEnum.POR_VOBO.getId(), OrdenEstadoEnum.POR_ENVIAR_PROVEEDOR.getId(), prov));
        getMapaTotales().put("comprasPorRecibir", ordenImpl.totalCompras(sesion.getUsuarioConectado().getApCampo().getId(), OrdenEstadoEnum.POR_RECIBIR.getId(), OrdenEstadoEnum.POR_RECIBIR_FACTURA.getId(), prov));
        //

    }

    public void seleccionarCompraProceso(SelectEvent event) {
        OrdenVO o = (OrdenVO) event.getObject();
        mapaTotales.put("proyectoDetalleProceso", ordenImpl.comprasPorProyecto(o.getProyectoOt(), sesion.getUsuarioConectado().getApCampo().getId(),
                OrdenEstadoEnum.POR_VOBO.getId(), OrdenEstadoEnum.POR_ENVIAR_PROVEEDOR.getId(), getProveedor()));
        //
        PrimeFaces.current().executeScript( "$(dialogDetalleComprasProc).modal('show')");
    }

    public void cerrarSeleccionarCompraProceso(SelectEvent event) {
        PrimeFaces.current().executeScript( "$(dialogDetalleComprasProc).modal('hide')");
    }

    public void seleccionarCompraConProveedor(SelectEvent event) {
        OrdenVO o = (OrdenVO) event.getObject();
        mapaTotales.put("detalleProyectoComprasProveedor", ordenImpl.comprasPorProyecto(o.getProyectoOt(), sesion.getUsuarioConectado().getApCampo().getId(), OrdenEstadoEnum.POR_RECIBIR.getId(), OrdenEstadoEnum.POR_RECIBIR_FACTURA.getId(), getProveedor()));
        //
        PrimeFaces.current().executeScript( "$(dialogDetalleComprasProveedor).modal('show')");
    }

    public void cerrarSeleccionarCompraConProveedor(SelectEvent event) {
        PrimeFaces.current().executeScript( "$(dialogDetalleComprasProveedor).modal('hide')");
    }

    public void buscarCompras() {
        System.out.println("id provee" + proveedor);
        cargarDatos(getProveedor().trim());
        proveedorSeleccionado = proveedor;
    }

    public void limpiarConsulta() {
        cargarDatos(Constantes.VACIO);
        proveedor = "";
        proveedorSeleccionado = "";
        //
    }

    public void updateList(String event) {
        // Filter the list of cities based on what the user has typed so far
        //availableCities = AutocompleteData.wrapList(event.getNewValue().toString());
        proveedoresDisponibles.clear();
        for (ProveedorVo pVo : proveedores) {
            if (pVo.getNombre().toLowerCase().contains(event.toLowerCase())) {
                proveedoresDisponibles.add(new SelectItem(pVo.getIdProveedor(), pVo.getNombre()));
            }
        }
    }

    public void mostrarComprasProcesoProveedor() {
        getMapaTotales().put("comprasProveedorEnProceso", ordenImpl.comprasPorProveedor(sesion.getUsuarioConectado().getApCampo().getId(), OrdenEstadoEnum.POR_VOBO.getId(), OrdenEstadoEnum.POR_ENVIAR_PROVEEDOR.getId(), getProveedor()));
        PrimeFaces.current().executeScript( "$(dialogComprasProveedorEnProceso).modal('show')");
    }

    public void mostrarComprasEnviadasProveedor() {
        getMapaTotales().put("comprasProveedorEnviadas", ordenImpl.comprasPorProveedor(sesion.getUsuarioConectado().getApCampo().getId(), OrdenEstadoEnum.POR_RECIBIR.getId(), OrdenEstadoEnum.POR_RECIBIR_FACTURA.getId(), getProveedor()));
        PrimeFaces.current().executeScript( "$(dialogComprasProveedorEnviadas).modal('show')");
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
     * @return the proveedor
     */
    public String getProveedor() {
        return proveedor;
    }

    /**
     * @param proveedor the proveedor to set
     */
    public void setProveedor(String proveedor) {
        this.proveedor = proveedor;
    }

    /**
     * @return the proceso
     */
    public int getProceso() {
        return proceso;
    }

    /**
     * @param proceso the proceso to set
     */
    public void setProceso(int proceso) {
        this.proceso = proceso;
    }

    /**
     * @return the proveedoresDisponibles
     */
    public List<SelectItem> getProveedoresDisponibles() {
        return proveedoresDisponibles;
    }

    /**
     * @param proveedoresDisponibles the proveedoresDisponibles to set
     */
    public void setProveedoresDisponibles(List<SelectItem> proveedoresDisponibles) {
        this.proveedoresDisponibles = proveedoresDisponibles;
    }

    /**
     * @return the proveedorSeleccionado
     */
    public String getProveedorSeleccionado() {
        return proveedorSeleccionado;
    }

    /**
     * @param proveedorSeleccionado the proveedorSeleccionado to set
     */
    public void setProveedorSeleccionado(String proveedorSeleccionado) {
        this.proveedorSeleccionado = proveedorSeleccionado;
    }
}
