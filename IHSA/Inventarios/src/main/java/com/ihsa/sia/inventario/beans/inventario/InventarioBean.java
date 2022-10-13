package com.ihsa.sia.inventario.beans.inventario;

import com.ihsa.sia.commons.Messages;
import com.ihsa.sia.commons.SaveObservable;
import com.ihsa.sia.commons.SaveObserver;
import com.ihsa.sia.commons.SessionBean;
import com.ihsa.sia.inventario.beans.AlmacenBean;
import com.ihsa.sia.inventario.beans.ArticuloBean;
import java.io.Serializable;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.faces.context.FacesContext;

import javax.faces.event.AjaxBehaviorEvent;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import sia.constantes.Constantes;
import sia.excepciones.SIAException;
import sia.inventarios.service.AlmacenRemote;
import sia.inventarios.service.ArticuloRemote;
import sia.inventarios.service.InvCeldaImpl;
import sia.inventarios.service.InvInventarioCeldaImpl;
import sia.inventarios.service.InventarioImpl;
import sia.inventarios.service.Utilitarios;
import sia.modelo.InvRack;
import sia.modelo.vo.inventarios.AlmacenVO;
import sia.modelo.vo.inventarios.ArticuloVO;
import sia.modelo.vo.inventarios.CeldaVo;
import sia.modelo.vo.inventarios.InventarioVO;

/**
 *
 * @author Aplimovil SA de CV
 */
@Named(value = "inventario")
@ViewScoped
public class InventarioBean implements Serializable, SaveObserver {

    @Inject
    protected InventarioImpl servicio;
    @Inject
    protected ArticuloRemote articuloRemote;
    @Inject
    protected InvCeldaImpl invCeldaImpl;
    @Inject
    private AlmacenRemote almacenServicio;
    @Inject
    protected InvInventarioCeldaImpl inventarioCeldaImpl;
    //
    @Inject
    private AlmacenBean almacenBean;
    @Inject
    private ArticuloBean articuloBean;

    private List<InventarioVO> inventarios;

    private List<AlmacenVO> almacenes;
    private CeldaVo celdaVo;
    private List<CeldaVo> celdas;
    private List<InvRack> racks;
    private List<CeldaVo> pisos;
    private InventarioVO inventarioVO;
    @Getter
    @Setter
    private ArticuloVO articuloVo;
    private boolean esNuevoElemento;
    final protected SessionBean principal = (SessionBean) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get("principal");

    public InventarioBean() {
//        super(InventarioVO.class);
    }

    @PostConstruct
    protected void init() {
        //super.init();
        inventarios = new ArrayList<>();
        racks = new ArrayList<>();
        pisos = new ArrayList<>();
        celdas = new ArrayList<>();
        almacenBean.addObserver(this);
        articuloBean.addObserver(this);
        setCeldaVo(new CeldaVo());
        articuloVo = new ArticuloVO();
        //
        cargarAlmacenes();
        //
        inventarios = servicio.traerInventario(inventarioVO, principal.getUser().getIdCampo());
    }

    private void cargarAlmacenes() {
        try {
            inventarioVO = new InventarioVO();
            almacenes = almacenServicio.buscarPorFiltros(new AlmacenVO(), principal.getUser().getIdCampo());
            //
            celdaVo.setId(0);
            celdaVo.setIdPiso(0);
            //
            //setLista(new ListDataModel(getServicio().traerInventario(inventarioVO, principal.getUser().getIdCampo())));
        } catch (Exception ex) {
            ManejarExcepcion(ex);
        }
    }

    public List<ArticuloVO> completarArticulo(String cadena) {
        return articuloRemote.buscarPorPalabras(cadena, principal.getUser().getCampo());
    }

    public void seleccionarAlmacen(AjaxBehaviorEvent event) {
        racks = invCeldaImpl.racksPorAlmacen(inventarioVO.getAlmacenId());
    }

    public void seleccionarRack(AjaxBehaviorEvent event) {
        pisos = invCeldaImpl.pisoPorRack(celdaVo.getIdRack());
        inventarioVO.setCeldas(invCeldaImpl.celdaPorRackPiso(celdaVo.getIdRack(), celdaVo.getIdPiso()));
    }

    public void seleccionarPiso(AjaxBehaviorEvent event) {
        inventarioVO.setCeldas(invCeldaImpl.celdaPorRackPiso(celdaVo.getIdRack(), celdaVo.getIdPiso()));
    }

    public void agregarUbicacion() {
        for (CeldaVo celda : inventarioVO.getCeldas()) {
            if (celda.isSelected()) {
                celdas.add(invCeldaImpl.ubicacion(celda.getId()));
            }
            celdaVo.setSelected(Constantes.BOOLEAN_FALSE);
        }
        //
        inventarioVO.setCeldas(invCeldaImpl.celdaPorRackPiso(celdaVo.getIdRack(), celdaVo.getIdPiso()));
    }

    //public void eliminarCeldaTemporal(CeldaVo id) {
    public void eliminarCeldaTemporal(int idC) {
        celdas.remove(idC);
    }

    public void guardarInventario() {
        try {
            if (!getCeldas().isEmpty()) {
                inventarioVO.setCeldas(celdas);
                if (esNuevoElemento) {
                    servicio.crear(inventarioVO, principal.getUser().getId(), principal.getUser().getIdCampo());
                    celdaVo.setId(0);
                    celdaVo.setIdRack(0);
                    celdaVo.setIdPiso(0);
                    inventarioVO.setAlmacenId(0);
                } else {
                    servicio.actualizar(inventarioVO, principal.getUser().getId(), principal.getUser().getIdCampo());
                }
                inventarios = servicio.traerInventario(new InventarioVO(), principal.getUser().getIdCampo());
                celdas = new ArrayList<>();
            } else {
                PrimeFaces.current().executeScript("alert('Seleccione una celda');");
                // addErrorMessage("Seleccione al menos una celda");
            }
        } catch (SIAException ex) {
            Logger.getLogger(InventarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void reestablecer() {
        inventarioVO = new InventarioVO();
        inventarios = servicio.traerInventario(inventarioVO, principal.getUser().getIdCampo());

    }

    public void buscarInventario() {
        if (articuloVo != null && articuloVo.getId() > 0) {
            inventarioVO.setArticuloId(articuloVo.getId());
            inventarioVO.setArticuloNombre(articuloVo.getNombre());
        }
        inventarios = servicio.traerInventario(inventarioVO, principal.getUser().getIdCampo());
        articuloVo = new ArticuloVO();
    }

    public void reestablecerTabla() {
        inventarios = servicio.traerInventario(new InventarioVO(), principal.getUser().getIdCampo());
    }

    protected String mensajeCrearKey() {
        return "sia.inventarios.inventarios.crearMensaje";
    }

    protected String mensajeEditarKey() {
        return "sia.inventarios.inventarios.editarMensaje";
    }

    protected String mensajeEliminarKey() {
        return "sia.inventarios.inventarios.eliminarMensaje";
    }

    public void cargarElementParaEditar(int id) {
        try {
            celdas = new ArrayList<>();
            getAlmacenBean().setEmbedded(false);
            getArticuloBean().setEmbedded(false);
            inventarioVO = servicio.buscar(id);
            esNuevoElemento = false;
            //super.cargarElementParaEditar(id);          
            racks = invCeldaImpl.racksPorAlmacen(inventarioVO.getAlmacenId());
            //
            celdas = inventarioCeldaImpl.celdas(inventarioVO.getId());
            //
            PrimeFaces.current().executeScript("$(dialogoInventario).modal('show');");
        } catch (SIAException ex) {
            Logger.getLogger(InventarioBean.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void agregarNuevo() {
        getAlmacenBean().setEmbedded(false);
        getArticuloBean().setEmbedded(false);
        inventarioVO = new InventarioVO();
        esNuevoElemento = true;
        inventarioVO.setMaximoDeInventario(0);
        inventarioVO.setPuntoDeReorden(0);
        //
        celdas = new ArrayList<>();
    }

    public void nuevoAlamacen() {
        almacenBean.agregarNuevo();
        getAlmacenBean().setEmbedded(true);
    }

    public void nuevoArticulo() {
        articuloBean.agregarNuevo();
        getArticuloBean().setEmbedded(true);
    }

    public List<AlmacenVO> getAlmacenes() {
        return almacenes;
    }

    public AlmacenBean getAlmacenBean() {
        return almacenBean;
    }

    public void setAlmacenBean(AlmacenBean almacenBean) {
        this.almacenBean = almacenBean;
    }

    public ArticuloBean getArticuloBean() {
        return articuloBean;
    }

    public void setArticuloBean(ArticuloBean articuloBean) {
        this.articuloBean = articuloBean;
    }

    public String fechaConFormato(Date date) {
        if (date == null) {
            return "";
        }
        long diff = new Date().getTime() - date.getTime();
        return MessageFormat.format(obtenerCadenaDeRecurso("sia.inventarios.comun.fechaFormato"),
                new SimpleDateFormat("dd/MM/yyyy").format(date),
                TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS));
    }

    public Date getCurrentDate() {
        return new Date();
    }

    @Override
    public void update(SaveObservable observable, String event) {
        if (observable instanceof AlmacenBean) {
            getAlmacenBean().setEmbedded(false);
            cargarAlmacenes();
            inventarioVO.setAlmacenId(getAlmacenBean().getElemento().getId());

        } else {
            getArticuloBean().setEmbedded(false);
            inventarioVO.setArticuloId(getArticuloBean().getElemento().getId());
            inventarioVO.setArticuloNombre(getArticuloBean().getElemento().getNombre());
        }
    }

    public String eliminar(Integer id) {
        try {
            servicio.eliminar(id, principal.getUser().getId(), principal.getUser().getIdCampo());
            inventarios = servicio.traerInventario(inventarioVO, principal.getUser().getIdCampo());
        } catch (SIAException ex) {
            ManejarExcepcion(ex);
        }
        return null;
    }

    protected void ManejarExcepcion(Exception ex) {
        String mensaje = ex.getMessage();
        if (Utilitarios.esNuloOVacio(ex.getMessage())) {
            mensaje = obtenerCadenaDeRecurso("sia.inventarios.mobile.mensaje.error");
            ex.printStackTrace();
        }
        addErrorMessage(mensaje);
    }

    public void addErrorMessage(String string) {
        FacesMessage facesMessage = new FacesMessage(FacesMessage.SEVERITY_ERROR, string, null);
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
    }

    protected String obtenerCadenaDeRecurso(String key) {
        return Messages.getString(key);
    }

    /**
     * @return the celdaVo
     */
    public CeldaVo getCeldaVo() {
        return celdaVo;
    }

    /**
     * @param celdaVo the celdaVo to set
     */
    public void setCeldaVo(CeldaVo celdaVo) {
        this.celdaVo = celdaVo;
    }

    /**
     * @return the racks
     */
    public List<InvRack> getRacks() {
        return racks;
    }

    /**
     * @param racks the racks to set
     */
    public void setRacks(List<InvRack> racks) {
        this.racks = racks;
    }

    /**
     * @return the pisos
     */
    public List<CeldaVo> getPisos() {
        return pisos;
    }

    /**
     * @param pisos the pisos to set
     */
    public void setPisos(List<CeldaVo> pisos) {
        this.pisos = pisos;
    }

//    /**
//     * @return the celdas
//     */
//    public List<CeldaVo> getCeldas() {
//        return celdas;
//    }
//
//    /**
//     * @param celdas the celdas to set
//     */
//    public void setCeldas(List<CeldaVo> celdas) {
//        this.celdas = celdas;
//    }
    /**
     * @return the inventarioVO
     */
    public InventarioVO getInventarioVO() {
        return inventarioVO;
    }

    /**
     * @param inventarioVO the inventarioVO to set
     */
    public void setInventarioVO(InventarioVO inventarioVO) {
        this.inventarioVO = inventarioVO;
    }

    /**
     * @return the inventarios
     */
    public List<InventarioVO> getInventarios() {
        return inventarios;
    }

    /**
     * @param inventarios the inventarios to set
     */
    public void setInventarios(List<InventarioVO> inventarios) {
        this.inventarios = inventarios;
    }

    /**
     * @return the esNuevoElemento
     */
    public boolean isEsNuevoElemento() {
        return esNuevoElemento;
    }

    /**
     * @param esNuevoElemento the esNuevoElemento to set
     */
    public void setEsNuevoElemento(boolean esNuevoElemento) {
        this.esNuevoElemento = esNuevoElemento;
    }

    /**
     * @return the celdas
     */
    public List<CeldaVo> getCeldas() {
        return celdas;
    }

    /**
     * @param celdas the celdas to set
     */
    public void setCeldas(List<CeldaVo> celdas) {
        this.celdas = celdas;
    }

}
