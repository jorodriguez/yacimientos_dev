/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.factura.bean;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedProperty;

import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import sia.compra.orden.bean.backing.OrdenBean;
import sia.compra.requisicion.bean.backing.FacesUtilsBean;
import sia.compra.requisicion.bean.backing.UsuarioBean;
import sia.constantes.Constantes;
import sia.modelo.sistema.vo.FacturaAdjuntoVo;
import sia.modelo.sistema.vo.FacturaContenidoNacionalVo;
import sia.modelo.sistema.vo.FacturaDetalleVo;
import sia.modelo.sistema.vo.FacturaVo;
import sia.notificaciones.requisicion.impl.NotificacionRequisicionImpl;
import sia.servicios.orden.impl.OcOrdenEtsImpl;
import sia.servicios.sistema.impl.SiFacturaAdjuntoImpl;
import sia.servicios.sistema.impl.SiFacturaContenidoNacionalImpl;
import sia.servicios.sistema.impl.SiFacturaDetalleImpl;
import sia.servicios.sistema.impl.SiFacturaImpl;
import sia.servicios.sistema.impl.SiFacturaStatusImpl;
import sia.util.FacturaEstadoEnum;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Named(value = "historialFacturaBean")
@ViewScoped
public class HistorialFacturaBean implements Serializable {

    private static final UtilLog4j<OrdenBean> LOGGER = UtilLog4j.log;

    @Inject
    private UsuarioBean usuarioBean;
//
    @Inject
    SiFacturaStatusImpl siFacturaStatusImpl;
    @Inject
    SiFacturaImpl siFacturaImpl;
    @Inject
    SiFacturaAdjuntoImpl siFacturaAdjuntoImpl;
    @Inject
    SiFacturaDetalleImpl siFacturaDetalleImpl;
    @Inject
    SiFacturaContenidoNacionalImpl facturaContenidoNacionalImpl;
    @Inject
    NotificacionRequisicionImpl notificacionImpl;
    @Inject
    OcOrdenEtsImpl ocOrdenEtsImpl;
    //
    private List<FacturaVo> listaFactura = new ArrayList<>();
    private FacturaVo facturaVo;
    private List<FacturaVo> listaNotaCredito = new ArrayList<>();
    private List<FacturaContenidoNacionalVo> contenidoNacional = new ArrayList<>();
    private List<FacturaAdjuntoVo> listaArchivosFactura = new ArrayList<>();
    private List<FacturaAdjuntoVo> listaArchivosNotaCredito = new ArrayList<>();
    private Date inicio;
    private Date fin;
    private List<String> proveedores = new ArrayList<>();
    private String provSelected;
    private String motivo;
    private boolean rechazarFactura;
    private String ligaZip = "";
    private boolean mostrarLiga;

    /**
     * Creates a new instance of HistorialFacturaBean
     */
    public HistorialFacturaBean() {
    }

    @PostConstruct
    public void iniciar() {
        Calendar cal = Calendar.getInstance();
        fin = cal.getTime();
        cal.add(Calendar.DAY_OF_YEAR, -30);
        inicio = cal.getTime();
        setFacturaVo(new FacturaVo());
        consultarListaFacturas();
        this.setMostrarLiga(false);
    }

    public void buscarFactura() {
        this.setMostrarLiga(false);
        consultarListaFacturas();
    }

    public String regresarFactura() {
        this.setMostrarLiga(false);
        consultarListaFacturas();
        PrimeFaces.current().executeScript("ocultarDiv('divHistFacturasDesc');mostrarDiv('divHistFacturas');limpiarTodos();");
        return "/vistas/SiaWeb/factura/historiarFacturas.xhtml?faces-redirect=true";
    }

    private void consultarListaFacturas() {
        setListaFactura(siFacturaImpl.traerFacturaPorStatusFecha(
                FacturaEstadoEnum.PROCESO_INTERNO_CLIENTE.getId(),
                usuarioBean.getUsuarioConectado().getApCampo().getId(),
                getProvSelected(),
                inicio,
                fin
        ));
        getListaFactura().addAll(siFacturaImpl.traerFacturaPorStatusFecha(
                FacturaEstadoEnum.CORREO_FACTURA_AVANZIA.getId(),
                usuarioBean.getUsuarioConectado().getApCampo().getId(),
                getProvSelected(),
                inicio,
                fin
        ));

    }

    public void exportarFactura() {
        String facIDs = "";
        File fileTemp = null;

        for (FacturaVo vo : getListaFactura()) {
            if (vo.isSelected()) {
                if (facIDs != null && facIDs.isEmpty()) {
                    facIDs += vo.getId();
                } else {
                    facIDs += ", " + vo.getId();
                }
            }
        }

        if (facIDs != null && !facIDs.isEmpty()) {
            this.setMostrarLiga(true);
            this.setLigaZip("/Compras/GenerarZip?ZWZ2W=0&ZWZ3W=" + facIDs);
            consultarListaFacturas();
        }

        PrimeFaces.current().executeScript("ocultarDiv('divHistFacturas');mostrarDiv('divHistFacturasDesc');");
    }

    public void seleccionar(int id) {
        facturaVo = siFacturaImpl.buscarFactura(id);
        facturaVo.setDetalleFactura(new ArrayList<>());
        facturaVo.setDetalleFactura(siFacturaDetalleImpl.detalleFactura(id));
        //
        listaArchivosFactura = siFacturaAdjuntoImpl.traerSoporteFactura(facturaVo.getId(), true);
        facturaVo.setRepses(ocOrdenEtsImpl.traerEtsPorOrdenCategoria(facturaVo.getIdRelacion(), Constantes.OCS_CATEGORIA_REPSE));
        contenidoNacional = facturaContenidoNacionalImpl.contedinoNacionaPorFactura(facturaVo.getId());
        listaNotaCredito = siFacturaImpl.traerNotaCredito(facturaVo.getId());
        //
        listaArchivosNotaCredito = new ArrayList<>();
        this.setRechazarFactura(usuarioBean.getMapaRoles().containsKey("Finanzas Devolver Factura"));
        PrimeFaces.current().executeScript("$(dialogoDatosFacturaHistorial).modal('show');");
    }

    public void seleccionarNotaCredito(int id) {
        //
        setListaArchivosNotaCredito(siFacturaAdjuntoImpl.traerSoporteFactura(id, Constantes.BOOLEAN_FALSE));
        PrimeFaces.current().executeScript("$(dialogoArchivosNotaCredito).modal('show');");
    }

    /**
     * @param usuarioBean the usuarioBean to set
     */
    public void setUsuarioBean(UsuarioBean usuarioBean) {
        this.usuarioBean = usuarioBean;
    }

    /**
     * @return the listaFactura
     */
    public List<FacturaVo> getListaFactura() {
        return listaFactura;
    }

    /**
     * @param listaFactura the listaFactura to set
     */
    public void setListaFactura(List<FacturaVo> listaFactura) {
        this.listaFactura = listaFactura;
    }

    /**
     * @return the facturaVo
     */
    public FacturaVo getFacturaVo() {
        return facturaVo;
    }

    /**
     * @param facturaVo the facturaVo to set
     */
    public void setFacturaVo(FacturaVo facturaVo) {
        this.facturaVo = facturaVo;
    }

    /**
     * @return the inicio
     */
    public Date getInicio() {
        return inicio;
    }

    /**
     * @param inicio the inicio to set
     */
    public void setInicio(Date inicio) {
        this.inicio = inicio;
    }

    /**
     * @return the fin
     */
    public Date getFin() {
        return fin;
    }

    /**
     * @param fin the fin to set
     */
    public void setFin(Date fin) {
        this.fin = fin;
    }

    /**
     * @return the listaNotaCredito
     */
    public List<FacturaVo> getListaNotaCredito() {
        return listaNotaCredito;
    }

    /**
     * @param listaNotaCredito the listaNotaCredito to set
     */
    public void setListaNotaCredito(List<FacturaVo> listaNotaCredito) {
        this.listaNotaCredito = listaNotaCredito;
    }

    /**
     * @return the contenidoNacional
     */
    public List<FacturaContenidoNacionalVo> getContenidoNacional() {
        return contenidoNacional;
    }

    /**
     * @param contenidoNacional the contenidoNacional to set
     */
    public void setContenidoNacional(List<FacturaContenidoNacionalVo> contenidoNacional) {
        this.contenidoNacional = contenidoNacional;
    }

    /**
     * @return the listaArchivosFactura
     */
    public List<FacturaAdjuntoVo> getListaArchivosFactura() {
        return listaArchivosFactura;
    }

    /**
     * @param listaArchivosFactura the listaArchivosFactura to set
     */
    public void setListaArchivosFactura(List<FacturaAdjuntoVo> listaArchivosFactura) {
        this.listaArchivosFactura = listaArchivosFactura;
    }

    /**
     * @return the listaArchivosNotaCredito
     */
    public List<FacturaAdjuntoVo> getListaArchivosNotaCredito() {
        return listaArchivosNotaCredito;
    }

    /**
     * @param listaArchivosNotaCredito the listaArchivosNotaCredito to set
     */
    public void setListaArchivosNotaCredito(List<FacturaAdjuntoVo> listaArchivosNotaCredito) {
        this.listaArchivosNotaCredito = listaArchivosNotaCredito;
    }

    /**
     * @return the proveedores
     */
    public List<String> getProveedores() {
        return proveedores;
    }

    /**
     * @param proveedores the proveedores to set
     */
    public void setProveedores(List<String> proveedores) {
        this.proveedores = proveedores;
    }

    public List<String> proveedorListener(String cadena) {
        proveedores.clear();
        try {
            if (cadena != null && !cadena.isEmpty() && cadena.length() > 2) {
                List<SelectItem> lp = siFacturaImpl.traerProveedorPorStatusFacturas(cadena, FacturaEstadoEnum.PROCESO_INTERNO_CLIENTE.getId(),
                        usuarioBean.getUsuarioConectado().getApCampo().getId());
                lp.addAll(siFacturaImpl.traerProveedorPorStatusFacturas(cadena, FacturaEstadoEnum.CORREO_FACTURA_AVANZIA.getId(),
                        usuarioBean.getUsuarioConectado().getApCampo().getId()));
                lp.stream().forEach(p -> {
                    proveedores.add(p.getLabel());
                });
            }
        } catch (Exception e) {
            proveedores = new ArrayList<>();
        }
        return proveedores;
    }

    /**
     * @return the provSelected
     */
    public String getProvSelected() {
        return provSelected;
    }

    /**
     * @param provSelected the provSelected to set
     */
    public void setProvSelected(String provSelected) {
        this.provSelected = provSelected;
    }

    public void devolverFactura() {
        if (!motivo.isEmpty()) {
            if (facturaVo != null && facturaVo.getId() > 0) {
                if (FacturaEstadoEnum.PROCESO_INTERNO_CLIENTE.getId() == facturaVo.getIdStatus()) {
                    siFacturaStatusImpl.rechazarFactura(usuarioBean.getUsuarioConectado().getId(), facturaVo, getMotivo(), usuarioBean.getUsuarioConectado().getEmail(), FacturaEstadoEnum.PROCESO_INTERNO_CLIENTE.getId());
                } else if (FacturaEstadoEnum.CORREO_FACTURA_AVANZIA.getId() == facturaVo.getIdStatus()) {
                    siFacturaStatusImpl.rechazarFactura(usuarioBean.getUsuarioConectado().getId(), facturaVo, getMotivo(), usuarioBean.getUsuarioConectado().getEmail(), FacturaEstadoEnum.CORREO_FACTURA_AVANZIA.getId());
                }
            }
            setMotivo("");
            PrimeFaces.current().executeScript("$(dialogoDatosFacturaHistorial).modal('hide');");
            //
            consultarListaFacturas();
        } else {
            FacesUtilsBean.addErrorMessage("Es necesario agregar el motivo.");
        }

    }

    public void aceptarFactura() {
        List<FacturaVo> lf = new ArrayList<>();
        lf.add(facturaVo);

        if (FacturaEstadoEnum.PROCESO_INTERNO_CLIENTE.getId() == facturaVo.getIdStatus()) {
            siFacturaImpl.aceptarFactura(usuarioBean.getUsuarioConectado().getId(), lf, usuarioBean.getUsuarioConectado().getEmail(), FacturaEstadoEnum.PROCESO_INTERNO_CLIENTE.getId(), FacturaEstadoEnum.PROCESO_DE_PAGO.getId());
        } else if (FacturaEstadoEnum.CORREO_FACTURA_AVANZIA.getId() == facturaVo.getIdStatus()) {
            siFacturaImpl.aceptarFactura(usuarioBean.getUsuarioConectado().getId(), lf, usuarioBean.getUsuarioConectado().getEmail(), FacturaEstadoEnum.CORREO_FACTURA_AVANZIA.getId(), FacturaEstadoEnum.PROCESO_DE_PAGO.getId());
        }
        PrimeFaces.current().executeScript("$(dialogoDatosFacturaHistorial).modal('hide');");

        consultarListaFacturas();

    }

    public void aceptarFacturaAvanzia() {
        List<FacturaVo> lf = new ArrayList<>();
        lf.add(facturaVo);
        if (listaNotaCredito != null && listaNotaCredito.size() > 0) {
            lf.addAll(listaNotaCredito);
        }
        //siFacturaImpl.aceptarFactura(usuarioBean.getUsuarioConectado().getId(), lf, usuarioBean.getUsuarioConectado().getEmail(), FacturaEstadoEnum.PROCESO_INTERNO_CLIENTE.getId(), FacturaEstadoEnum.PROCESO_DE_PAGO.getId());
        siFacturaImpl.aceptarFacturaAvanzia(usuarioBean.getUsuarioConectado().getId(), lf, usuarioBean.getUsuarioConectado().getEmail(), FacturaEstadoEnum.PROCESO_INTERNO_CLIENTE.getId(), FacturaEstadoEnum.CORREO_FACTURA_AVANZIA.getId());
        PrimeFaces.current().executeScript("$(dialogoDatosFacturaHistorial).modal('hide');");

        consultarListaFacturas();

    }

    /**
     * @return the motivo
     */
    public String getMotivo() {
        return motivo;
    }

    /**
     * @param motivo the motivo to set
     */
    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    /**
     * @return the rechazarFactura
     */
    public boolean isRechazarFactura() {
        return rechazarFactura;
    }

    /**
     * @param rechazarFactura the rechazarFactura to set
     */
    public void setRechazarFactura(boolean rechazarFactura) {
        this.rechazarFactura = rechazarFactura;
    }

    /**
     * @return the ligaZip
     */
    public String getLigaZip() {
        return ligaZip;
    }

    /**
     * @param ligaZip the ligaZip to set
     */
    public void setLigaZip(String ligaZip) {
        this.ligaZip = ligaZip;
    }

    /**
     * @return the mostrarLiga
     */
    public boolean isMostrarLiga() {
        return mostrarLiga;
    }

    /**
     * @param mostrarLiga the mostrarLiga to set
     */
    public void setMostrarLiga(boolean mostrarLiga) {
        this.mostrarLiga = mostrarLiga;
    }
}
