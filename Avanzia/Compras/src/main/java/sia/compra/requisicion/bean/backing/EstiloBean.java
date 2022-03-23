/*
 * EstiloBean.java
 * Creado el 27/06/2009, 01:10:46 AM
 * Managed Bean desarrollado por: Héctor Acosta Sierra para: MPG-IHSA
 *
 * Para información sobre el uso de este Managed Bean, asi como bugs, actualizaciones o mejoras
 * enviar un mail a: hacosta@ihsa.mx o a: hacosta.0505@gmail.com
 */
package sia.compra.requisicion.bean.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.faces.bean.CustomScoped;

import javax.faces.model.ArrayDataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.inject.Inject;
import javax.inject.Named;
import sia.compra.orden.bean.backing.NotaOrdenBean;
import sia.compra.orden.bean.backing.OrdenBean;
import sia.modelo.Orden;
import sia.modelo.Requisicion;
import sia.modelo.sgl.vo.RequisicionVO;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.proveedor.impl.PvProveedorSinCartaIntencionImpl;
import sia.servicios.requisicion.impl.RequisicionImpl;
import sia.util.UtilLog4j;

/**
 *
 * @author Héctor Acosta Sierra
 * @version 1.0
 * @author-mail hacosta.0505@gmail.com @date 27/06/2009
 */
@Named (value = EstiloBean.BEAN_NAME)
@CustomScoped(value = "#{window}")
public class EstiloBean implements Serializable {

    //------------------------------------------------------
    public static final String BEAN_NAME = "estiloBean";
    //
    @Inject
    private OrdenImpl ordenImpl;
    @Inject
    private RequisicionImpl requisicionImpl;
    @Inject
    private PvProveedorSinCartaIntencionImpl pvProveedorSinCartaIntencionImpl;
    //------------------------------------------------------
    @Inject
    private RequisicionBean requisicionBean;
    @Inject
    private NotaOrdenBean notaOrdenBean ;
    @Inject
    private CargaEtsBean cargaEtsBean ;
    private String codigo;
    private String seleccion = "REQUISICIÓN.";
    private String ejemplo;

    /**
     * Creates a new instance of EstiloBean
     */
    public EstiloBean() {
    }
//Campos

    public List getListaOpciones() {
        List resultList = new ArrayList();
        try {
            SelectItem item = new SelectItem("Req.");
            resultList.add(item);

            SelectItem item2 = new SelectItem("OC/S.");
            resultList.add(item2);

            return resultList;
        } catch (RuntimeException ex) {
            UtilLog4j.log.fatal(this, ex.getMessage());
        }
        return resultList;
    }

    public String buscarElemento() {
        String pagina = "";
        try {
            OrdenBean ordenBean = (OrdenBean) FacesUtilsBean.getManagedBean("ordenBean");
            requisicionBean.setRequisicionActual(null);//---Limpiar la requisicion actual---//
            ordenBean.setOrdenActual(null);//---Limpiar la orden actual---//
            if (this.codigo.equals("")) {
                pagina = "";
                FacesUtilsBean.addErrorMessage("No introdujo ningún valor.");
            } else {
                UsuarioBean usuarioBean = (UsuarioBean) FacesUtilsBean.getManagedBean("usuarioBean");
                if (this.seleccion.equals("REQUISICIÓN.")) {
                    pagina = "/vistas/SiaWeb/Requisiciones/DetalleHistorial";
                    Requisicion requisicion;
                    requisicion = requisicionImpl.buscarPorConsecutivoBloque(codigo.toUpperCase(), usuarioBean.getUsuarioConectado().getId());
                    //requisicion = requisicionImpl.buscarPorConsecutivoEmpresa(codigo.toUpperCase(), usuarioBean.getCompania().getRfc());
                    if (requisicion == null) {
                        requisicionBean.setRequisicionActual(null);//---Limpiar la requisicion actual---//
                        pagina = "";
                        FacesUtilsBean.addErrorMessage("Requisición no encontrada.");
                    } else if(requisicion.getApCampo().getId() != usuarioBean.getUsuarioConectado().getApCampo().getId()){
                        requisicionBean.setRequisicionActual(null);//---Limpiar la requisicion actual---//
                        pagina = "";
                        FacesUtilsBean.addErrorMessage("La Requisición no coincide con el Bloque.");                    
                    } else {
                        //this.idRequisicionBuscar = this.getDigitosAño(new Date()) + " - " ;
                        this.requisicionBean.setRequisicionActual(requisicion);
                        //         -----Esto es para Quitar las respuestas de la nota seleccionada
                        if (requisicion.isMultiproyecto()) {
                            requisicionBean.itemsProcesoAprobarMulti();
                        } else {
                            requisicionBean.getItemsActualizar();
                        }
                        requisicionBean.rechazosRequisicion();
                        //
                        if (notaOrdenBean.getNotaActual() != null) {
                            notaOrdenBean.cambiarNotaOrden(0);
                        }
                        if (ordenBean.getOrdenActual() != null) {
                            ordenBean.quitarSeleccionOrden(true);
                        }
                        //                        
                      //  ordenBean.getMapaOrdenes().put("ordenRequisicion", ordenImpl.traerOrdenPorRequisicion(requisicion.getId()));
                    }
                } else {
                    pagina = "/vistas/SiaWeb/Orden/DetalleOrden";
                    Orden orden = ordenImpl.buscarPorOrdenConsecutivo(codigo.toUpperCase().trim(), usuarioBean.getUsuarioConectado().getId());
                    if (orden == null) {
                        pagina = "";
                        ordenBean.setOrdenActual(null); //---Limpiar la orden actual---//
                        FacesUtilsBean.addErrorMessage("Orden no encontrada en la base de datos.");
                    } else if (orden.getApCampo().getId() != usuarioBean.getUsuarioConectado().getApCampo().getId()) {
                        pagina = "";
                        ordenBean.setOrdenActual(null); //---Limpiar la orden actual---//
                        FacesUtilsBean.addErrorMessage("La Orden no coincide con el Bloque.");
                    } else {
                        ordenBean.setOrdenActual(orden);
                        ordenBean.itemsPorOrden();
                        ordenBean.rechazos();
                        //
                        ordenBean.formatosEntradaOrden();
                        //
                        cargaEtsBean.ordenEtsPorCategoria();
                        cargaEtsBean.traerTablaComparativa();
                        //
                        cargaEtsBean.etsPorOrdenRequisicion();
                        if (notaOrdenBean.getNotaActual() != null) {
                            notaOrdenBean.cambiarNotaOrden(0);
                        }
                        ordenBean.cargarFacturas(orden.getId());
                        ordenBean.setProveedorCI(pvProveedorSinCartaIntencionImpl.existeProveedorCI(orden.getApCampo().getId(), orden.getProveedor().getId()));
                    }
                }
            }
            return pagina;
        } catch (RuntimeException ex) {
            UtilLog4j.log.fatal(this, ex.getMessage());
        }
        return pagina;
    }

    //
    public void buscarRequisicionPorConsecutivoBloque() {
        UsuarioBean usuarioBean = (UsuarioBean) FacesUtilsBean.getManagedBean("usuarioBean");
        RequisicionVO requisicionVO = requisicionImpl.buscarPorConsecutivoBloque(codigo.toUpperCase(), usuarioBean.getUsuarioConectado().getApCampo().getId(), true, true);
        if (requisicionVO != null) {
            requisicionBean.setRequisicionVO(requisicionVO);
        } else {
            requisicionBean.setRequisicionVO(null);//---Limpiar la requisicion actual---//
            FacesUtilsBean.addInfoMessage("Requisición no encontrada.");
        }
    }

    /**
     * @return the codigo
     */
    public String getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    /**
     * @return the seleccion
     */
    public String getSeleccion() {
        return seleccion;
    }

    /**
     * @param seleccion the seleccion to set
     */
    public void setSeleccion(String seleccion) {
        this.seleccion = seleccion;
    }

    /**
     * @return the ejemplo
     */
    public String getEjemplo() {
        if (this.seleccion.equals("REQUISICIÓN.")) {
            this.ejemplo = "10-1234";
        } else {
            this.ejemplo = "OC10-1234";
        }
        return ejemplo;
    }
}
