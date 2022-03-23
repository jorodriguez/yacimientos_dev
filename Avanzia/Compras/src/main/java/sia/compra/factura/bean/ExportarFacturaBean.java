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

import javax.faces.component.UIData;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import sia.compra.requisicion.bean.backing.FacesUtilsBean;
import sia.compra.requisicion.bean.backing.UsuarioBean;
import sia.constantes.Constantes;
import sia.modelo.contrato.vo.FiltroVo;
import sia.modelo.sgl.vo.Vo;
import sia.modelo.sistema.vo.FacturaAdjuntoVo;
import sia.modelo.sistema.vo.FacturaContenidoNacionalVo;
import sia.modelo.sistema.vo.FacturaDetalleVo;
import sia.modelo.sistema.vo.FacturaVo;
import sia.servicios.sistema.impl.SiFacturaAdjuntoImpl;
import sia.servicios.sistema.impl.SiFacturaContenidoNacionalImpl;
import sia.servicios.sistema.impl.SiFacturaDetalleImpl;
import sia.servicios.sistema.impl.SiFacturaImpl;
import sia.servicios.sistema.impl.SiFacturaStatusImpl;

/**
 *
 * @author jcarranza
 */
@Named (value = "exportarFacturaBean")
@ViewScoped
public class ExportarFacturaBean implements Serializable {

    /**
     * Creates a new instance of ConsultaFacturaBean
     */
    public ExportarFacturaBean() {
    }

   
    @Inject
    private UsuarioBean usuarioBean;
//
    @Inject
    SiFacturaStatusImpl siFacturaStatusImpl;
    @Inject
    SiFacturaImpl siFacturaImpl;
    @Inject
    SiFacturaStatusImpl facturaStatusImpl;
    @Inject
    private SiFacturaAdjuntoImpl siFacturaAdjuntoImpl;
    @Inject
    SiFacturaDetalleImpl siFacturaDetalleImpl;
    @Inject
    private SiFacturaContenidoNacionalImpl facturaContenidoNacionalImpl;
    //
    private List<FacturaVo> facturas;
    private FacturaVo facturaVo;
    private List<FacturaVo> listaNotaCredito;
    private List<FacturaContenidoNacionalVo> contenidoNacional;
    private List<FacturaAdjuntoVo> listaArchivosFactura;
    private List<FacturaAdjuntoVo> listaArchivosNotaCredito;
    private Date fechaMinina;
    private Date fechaMaxima;
    private List<SelectItem> proveedores;
    private List<SelectItem> estatus;
    private List<SelectItem> gerencias;

    //  private FiltroVo filtroVo;
    private List<FiltroVo> listaFiltro;
    private FiltroVo filtroVo; //
    private String ligaZip = "";
    private boolean mostrarLiga;

    @PostConstruct
    public void iniciar() {
        listaFiltro = new ArrayList<FiltroVo>();
        setFacturaVo(new FacturaVo());
        facturas = new ArrayList<FacturaVo>();
        //
        proveedores = new ArrayList<>();
        gerencias = new ArrayList<>();
        setEstatus(new ArrayList<>());
        //
        fechaMinina = siFacturaImpl.fechaPrimerFactura(usuarioBean.getUsuarioConectado().getApCampo().getId());
        fechaMaxima = new Date();
        //
        iniciarLista();
        List<Vo> proveedoresVo = siFacturaImpl.proveedores(usuarioBean.getUsuarioConectado().getApCampo().getId());
        for (Vo vo : proveedoresVo) {
            proveedores.add(new SelectItem(vo.getId(), vo.getNombre()));
        }
        List<Vo> gerenciasVo = siFacturaImpl.gerencias(usuarioBean.getUsuarioConectado().getApCampo().getId());
        for (Vo vo : gerenciasVo) {
            gerencias.add(new SelectItem(vo.getId(), vo.getNombre()));
        }
        getEstatus().add(new SelectItem(710, "Creada"));
        getEstatus().add(new SelectItem(720, "Enviada al cliente"));
        getEstatus().add(new SelectItem(730, "Proceso de validación"));
        getEstatus().add(new SelectItem(740, "Proceso de pago"));

    }

    private void iniciarLista() {
        filtroVo = new FiltroVo();
        setListaFiltro(new ArrayList<FiltroVo>());
        filtroVo.setId(Constantes.CERO);
        filtroVo.setCampos(new ArrayList<String>());
        filtroVo.getCampos().add("Fecha");
        filtroVo.getCampos().add("Gerencia");
        filtroVo.getCampos().add("Proveedor");
        filtroVo.getCampos().add("Orden");
        filtroVo.getCampos().add("Estatus");
        filtroVo.getCampos().add("Folio");
        //
        filtroVo.setCampoSeleccionado("Fecha");
        filtroVo.setOperadorRelacional(new ArrayList<String>());
        filtroVo.getOperadorRelacional().add("Entre");
        filtroVo.setFiltroFechaRango(true);
        //
        filtroVo.setFiltroCaja(false);
        Calendar cal = Calendar.getInstance();
        filtroVo.setFechaFin(cal.getTime());
        cal.add(Calendar.DAY_OF_YEAR, -8);
        filtroVo.setFechaInicio(cal.getTime());
        listaFiltro.add(filtroVo.getId(), filtroVo);
        //        
        buscarFacturaDet();
    }

    public void agregarOperadorLogicoCondiciones(ValueChangeEvent event) {
        UIData data = (UIData) event.getComponent().findComponent("tblFiltro");
        int rowIndex = data.getRowIndex();
        listaFiltro.get(rowIndex).setOperadorLogicoSeleccionado((String) event.getNewValue());
    }

    public void mostrarCondiciones(ValueChangeEvent event) {
        UIData data = (UIData) event.getComponent().findComponent("tblFiltro");
        int rowIndex = data.getRowIndex();
        filtroVo = listaFiltro.get(rowIndex);
        filtroVo.setCampoSeleccionado((String) event.getNewValue());
        if (filtroVo.getCampoSeleccionado().equals("Fecha")) {
            // mostrar fecha
            filtroVo.setFiltroCombo(false);
            filtroVo.setFiltroCaja(false);
            filtroVo.setOperadorRelacional(new ArrayList<String>());
            //
            filtroVo.setFiltroFechaRango(true);
            filtroVo.getOperadorRelacional().add("Entre ");
        } else if (filtroVo.getCampoSeleccionado().equals("Proveedor")) {
            filtroVo.setFiltroCaja(false);
            filtroVo.setFiltroFechaRango(false);
            filtroVo.setFiltroCombo(true);
            filtroVo.setListaProveedores(new ArrayList<>());
            filtroVo.setListaProveedores(proveedores);
            filtroVo.setOperadorRelacional(new ArrayList<String>());
            filtroVo.getOperadorRelacional().add("Igual a  ");

        } else if (filtroVo.getCampoSeleccionado().equals("Gerencia")) {
            // mostrar combo
            filtroVo.setListaGerencia(new ArrayList<>());
            filtroVo.setListaGerencia(gerencias);
            //
            filtroVo.setFiltroCombo(true);
            filtroVo.setFiltroCaja(false);
            filtroVo.setFiltroFechaRango(false);
            filtroVo.setOperadorRelacional(new ArrayList<String>());
            //
            filtroVo.getOperadorRelacional().add("Igual a ");
        } else if (filtroVo.getCampoSeleccionado().equals("Estatus")) {
            // mostrar combo
            filtroVo.setListaEstatus(new ArrayList<>());
            filtroVo.setListaEstatus(getEstatus());
            //
            filtroVo.setFiltroCombo(true);
            filtroVo.setFiltroCaja(false);
            filtroVo.setFiltroFechaRango(false);
            filtroVo.setOperadorRelacional(new ArrayList<String>());
            //
            filtroVo.getOperadorRelacional().add("Igual a ");
        } else {
            // mostrar caja y operador cadena
            filtroVo.setOperadorRelacional(new ArrayList<String>());
            //
            filtroVo.setFiltroFechaRango(false);
            filtroVo.setFiltroCaja(true);
            filtroVo.setFiltroCombo(false);
            filtroVo.setFiltroFecha(false);
            filtroVo.getOperadorRelacional().add("Igual a  ");
        }
        filtroVo.setOperadorRelacionalCadena(new ArrayList<String>());
        ///
        listaFiltro.set(filtroVo.getId(), filtroVo);
    }

    public void agregarNuevoFiltro() {
        filtroVo = new FiltroVo();
        filtroVo.setId(listaFiltro.size());
        filtroVo.setCampos(new ArrayList<String>());
        filtroVo.getCampos().add("Fecha");
        filtroVo.getCampos().add("Gerencia");
        filtroVo.getCampos().add("Proveedor");
        filtroVo.getCampos().add("Orden");
        filtroVo.getCampos().add("Estatus");
        filtroVo.getCampos().add("Folio");
//
        filtroVo.setOperadorLogico(new ArrayList<String>());
        filtroVo.getOperadorLogico().add(" Y ");
        filtroVo.getOperadorLogico().add(" O ");
        //
        filtroVo.setOperadorRelacional(new ArrayList<String>());
        filtroVo.getOperadorRelacional().add("Entre ");
        //
        Calendar cal = Calendar.getInstance();
        filtroVo.setFechaFin(cal.getTime());
        filtroVo.setFiltroFechaRango(true);
        listaFiltro.add(filtroVo.getId(), filtroVo);
    }

    public void quitarFiltro() {
        int id = Integer.parseInt(FacesUtilsBean.getRequestParameter("indice"));
        //
        reasiganarId(listaFiltro, id);
        // llenar la lista
    }

    private void reasiganarId(List<FiltroVo> filtros, int idBorrado) {
        listaFiltro.remove(filtros.get(idBorrado));
        //List<FiltroVo> temList = new ArrayList<FiltroVo>();
        for (int i = idBorrado; i < filtros.size(); i++) {
            FiltroVo filtro = filtros.get(i);
            filtro.setId(i);
            listaFiltro.set(i, filtro);
        }
    }

    public void limpiarBusqueda() {
        facturas = new ArrayList<>();
        iniciarLista();
    }

    public void buscarFactura() {
        buscarFacturaDet();
        //
    }

    public void buscarFacturaDet() {
        facturas = siFacturaImpl.traerFacturas(usuarioBean.getUsuarioConectado().getApCampo().getId(), listaFiltro);
        //
    }

    public void seleccionar() {
        int idFactura = Integer.parseInt(FacesUtilsBean.getRequestParameter("idFac"));
        facturaVo = siFacturaImpl.buscarFactura(idFactura);
        facturaVo.setDetalleFactura(new ArrayList<FacturaDetalleVo>());
        listaArchivosFactura = new ArrayList<FacturaAdjuntoVo>();
        listaNotaCredito = new ArrayList<FacturaVo>();
        contenidoNacional = new ArrayList<FacturaContenidoNacionalVo>();
        //
        facturaVo.setDetalleFactura(siFacturaDetalleImpl.detalleFactura(idFactura));
        listaArchivosFactura = siFacturaAdjuntoImpl.traerSoporteFactura(idFactura, Boolean.TRUE);
        listaNotaCredito = siFacturaImpl.traerNotaCredito(idFactura);
        contenidoNacional = facturaContenidoNacionalImpl.contedinoNacionaPorFactura(idFactura);
        //
        PrimeFaces.current().executeScript("$(dialogoConsultaFactura).modal('show');");
    }

    public void seleccionarNotaCredito() {
        int idFacNota = Integer.parseInt(FacesUtilsBean.getRequestParameter("idNotaCredito"));
        //
        listaArchivosNotaCredito = new ArrayList<>();
        listaArchivosNotaCredito = siFacturaAdjuntoImpl.traerSoporteFactura(idFacNota, Boolean.FALSE);
        PrimeFaces.current().executeScript("$(dialogoNotaCredito).modal('show');");
    }

    public void exportarFactura() {
        String facIDs = "";
        File fileTemp = null;

        for (FacturaVo vo : getFacturas()) {
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
            buscarFacturaDet();
        }

        PrimeFaces.current().executeScript("ocultarDiv('divExpFacturas');mostrarDiv('divExpFacturasDesc');");
    }

    public void regresarFactura() {
        this.setMostrarLiga(false);
        buscarFacturaDet();
        PrimeFaces.current().executeScript("ocultarDiv('divExpFacturasDesc');mostrarDiv('divExpFacturas');limpiarTodos();");
    }

    /**
     * @param usuarioBean the usuarioBean to set
     */
    public void setUsuarioBean(UsuarioBean usuarioBean) {
        this.usuarioBean = usuarioBean;
    }

    /**
     * @return the facturas
     */
    public List<FacturaVo> getFacturas() {
        return facturas;
    }

    /**
     * @param facturas the facturas to set
     */
    public void setFacturas(List<FacturaVo> facturas) {
        this.facturas = facturas;
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
    public List<SelectItem> getProveedores() {
        return proveedores;
    }

    /**
     * @param proveedores the proveedores to set
     */
    public void setProveedores(List<SelectItem> proveedores) {
        this.proveedores = proveedores;
    }

    /**
     * @return the gerencias
     */
    public List<SelectItem> getGerencias() {
        return gerencias;
    }

    /**
     * @param gerencias the gerencias to set
     */
    public void setGerencias(List<SelectItem> gerencias) {
        this.gerencias = gerencias;
    }

    /**
     * @return the listaFiltro
     */
    public List<FiltroVo> getListaFiltro() {
        return listaFiltro;
    }

    /**
     * @param listaFiltro the listaFiltro to set
     */
    public void setListaFiltro(List<FiltroVo> listaFiltro) {
        this.listaFiltro = listaFiltro;
    }

    /**
     * @return the filtroVo
     */
    public FiltroVo getFiltroVo() {
        return filtroVo;
    }

    /**
     * @param filtroVo the filtroVo to set
     */
    public void setFiltroVo(FiltroVo filtroVo) {
        this.filtroVo = filtroVo;
    }

    /**
     * @return the fechaMinina
     */
    public Date getFechaMinina() {
        return fechaMinina;
    }

    /**
     * @param fechaMinina the fechaMinina to set
     */
    public void setFechaMinina(Date fechaMinina) {
        this.fechaMinina = fechaMinina;
    }

    /**
     * @return the fechaMaxima
     */
    public Date getFechaMaxima() {
        return fechaMaxima;
    }

    /**
     * @param fechaMaxima the fechaMaxima to set
     */
    public void setFechaMaxima(Date fechaMaxima) {
        this.fechaMaxima = fechaMaxima;
    }

    /**
     * @return the estatus
     */
    public List<SelectItem> getEstatus() {
        return estatus;
    }

    /**
     * @param estatus the estatus to set
     */
    public void setEstatus(List<SelectItem> estatus) {
        this.estatus = estatus;
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
