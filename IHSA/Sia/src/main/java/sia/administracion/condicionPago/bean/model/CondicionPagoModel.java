/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.condicionPago.bean.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import sia.constantes.Constantes;
import sia.modelo.CondicionPago;
import sia.modelo.OcProveedorConPago;
import sia.modelo.Proveedor;
import sia.modelo.orden.vo.ProveedorConPagoVo;
import sia.modelo.vo.CompaniaVo;
import sia.servicios.catalogos.impl.CondicionPagoImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.orden.impl.OcProveedorConPagoImpl;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;
import sia.sistema.bean.backing.Sesion;
import sia.util.ProveedorEnum;

/**
 *
 * @author mluis
 */
@Named
@ViewScoped
public class CondicionPagoModel implements Serializable {

    //Servicios
    @Inject
    private CondicionPagoImpl condicionPagoServicioRemoto;
    @Inject
    private OcProveedorConPagoImpl ocProveedorConPagoImpl;
    @Inject
    private ProveedorServicioImpl proveedorServicioRemoto;
    @Inject
    private UsuarioImpl usuarioImpl;

    //ManagedBeans
    @Inject
    private Sesion sesion;

    //Entidades
    private OcProveedorConPago ocProveedorConPago;
    private CondicionPago condicionPago;
    private Proveedor proveedor;

    //Vo
    private ProveedorConPagoVo proveedorConPagoVo;

    //Colecciones
    private List<SelectItem> listaProveedor;
    private List<SelectItem> listaConPago;
    private List<String> listaConPagoEdit;
    private List<String> listaAllConPago;
    private DataModel listData;

    //Clases
    private int pro;
    private String nomConPago = null;
    private String conPagoEdit = null;
    private boolean volver;
    private boolean visibleConPago;
    //
    private String rfcCompania;

    /**
     * Creates a new instance of CondicionPagoModel
     */
    public CondicionPagoModel() {
    }

    public String regresaRfcCompaniaSesion() {
        return sesion.getRfcCompania();
    }

    /* Recupera el objeto proveedor de la tabla PROVEEDOR */
    public void buscarProveedorPorNombre() {
        //this.setProveedor(proveedorServicioRemoto.getPorNombre(this.getPro(), sesion.getUsuario().getApCampo().getCompania().getRfc()));       
        this.setProveedor(proveedorServicioRemoto.getPorNombre(this.getPro(), sesion.getUsuario().getApCampo().getCompania().getRfc()));
    }

    /* Busca una Condicion de Pago en la tabla CONDICION_PAGO y la retorna*/
    public CondicionPago buscarConPagoPorNombre() {
        if (getConPagoEdit() != null) {
            this.setCondicionPago(getCondicionPagoServicioRemoto().buscarPorNombre(getConPagoEdit(), Constantes.BOOLEAN_FALSE));
        } else {
            this.setCondicionPago(getCondicionPagoServicioRemoto().buscarPorNombre(getNomConPago(), Constantes.BOOLEAN_FALSE));
        }

        return getCondicionPago();
    }

    /* Recupera las condiciones de pago de un proveedor */
    public String llenarProveedor() {
        return proveedorServicioRemoto.getProveedorJson(getRfcCompania(), ProveedorEnum.ACTIVO.getId());
    }

    public DataModel getAllConPagoProveedor() {
        this.setListData(new ListDataModel(this.getOcProveedorConPagoImpl().traerCondicionPorIdProveedor(getPro(), getRfcCompania())));
        return getListData();
    }

    public List<SelectItem> listaCompania() {
        List<SelectItem> ls = new ArrayList<SelectItem>();
        for (CompaniaVo cv : usuarioImpl.traerCompaniaPorUsuario(sesion.getUsuario().getId())) {
            ls.add(new SelectItem(cv.getRfcCompania(), cv.getNombre()));
        }
        return ls;
    }

    /* Busca una Condicion de Pago en la tabla OC_PROVEEDOR_CONDICION_PAGO y la retorna*/
    public ProveedorConPagoVo buscarConPagoProPorNombre() {
        if (getConPagoEdit() != null) {
            return this.getOcProveedorConPagoImpl().findByIds(getPro(), getConPagoEdit(), getRfcCompania());
        } else {
            return this.getOcProveedorConPagoImpl().findByIds(getPro(), getNomConPago(), getRfcCompania());
        }
    }

    /* Graba en la tabla CONDICION_PAGO y devuelve el objeto insertado*/
    public void guardarAltaCondicionReturn() {
        if (getNomConPago().toUpperCase().contains("ANTICIP")) {
            this.setCondicionPago(getCondicionPagoServicioRemoto().guardarAltaCondicionReturn(getNomConPago(), Constantes.BOOLEAN_TRUE, sesion.getUsuario()));

        } else {
            this.setCondicionPago(getCondicionPagoServicioRemoto().guardarAltaCondicionReturn(getNomConPago(), Constantes.BOOLEAN_FALSE, sesion.getUsuario()));
        }
    }

    public void guardarProveedorConPago() {
        this.getOcProveedorConPagoImpl().guardarProveedorConPago(this.getCondicionPago(), this.getPro(), sesion.getUsuario(), getRfcCompania());
        this.getAllConPagoProveedor();
    }

    public void actualizarProveedorConPago() {
        this.ocProveedorConPagoImpl.actualizarProveedorConPago(this.getProveedorConPagoVo(), this.getCondicionPago(), sesion.getUsuario());
        this.getAllConPagoProveedor();
    }

    public void eliminarProveedorConPago() {
        this.ocProveedorConPagoImpl.eliminarProveedorConPago(this.getProveedorConPagoVo(), sesion.getUsuario());
        this.getAllConPagoProveedor();
    }

    public void regresaListaConPagoEdit() {
        setListaConPagoEdit(condicionPagoServicioRemoto.traerNombreConPagoQueryNativo());
    }

    /**
     * ********** AUTO-COMPLETAR Proveedor ******************************************************
     */
    public List<SelectItem> regresaProveedorActivo(String cadenaDigitada) {
        //setListaProveedor(getSoporteProveedor().regresaNombreProveedorActivo(cadenaDigitada, getRfcCompania()));
        return getListaProveedor();
    }

    public List<SelectItem> getListaProveedor() {
        return listaProveedor;
    }

    public void setListaProveedor(List<SelectItem> listaProveedor) {
        this.listaProveedor = listaProveedor;
    }

    /**
     * ********** AUTO-COMPLETAR Condicion de Pago ****************************************************
     */
    public List<SelectItem> regresaConPagoActiva(String cadenaVista) {
        listaConPago = new ArrayList<SelectItem>();

        for (String p : this.regresaNombreConPago(cadenaVista)) {
            if (p != null) {
                String cadenaConPago = p.toLowerCase();
                cadenaVista = cadenaVista.toLowerCase();
                if (cadenaConPago.startsWith(cadenaVista)) {
                    SelectItem item = new SelectItem(p);
                    listaConPago.add(item);
                }
            }
        }
        return getListaConPago();
    }

    private List<String> regresaNombreConPago(String cadenaVista) {
        if (listaAllConPago == null) {
            setListaAllConPago(condicionPagoServicioRemoto.traerNombreConPagoQueryNativo());
            //System.out.println("Condiciones de Pago recuperadas: " + listaAllConPago.size() + " c y nc");
        } else {
            setListaAllConPago(condicionPagoServicioRemoto.traerNombreLikeConPagoQueryNativo(cadenaVista));
            //System.out.println("Condiciones de Pago recuperadas: " + listaAllConPago.size() + " con like");
        }
        return getListaAllConPago();
    }

    public List<String> getListaAllConPago() {
        return listaAllConPago;
    }

    public void setListaAllConPago(List<String> listaAllConPago) {
        this.listaAllConPago = listaAllConPago;
    }

    public List<SelectItem> getListaConPago() {
        return listaConPago;
    }

    public void setListaConPago(List<SelectItem> listaConPago) {
        this.listaConPago = listaConPago;
    }

    /**
     * ***********************************************************************************************
     */

    public int getPro() {
        return pro;
    }

    public void setPro(int pro) {
        this.pro = pro;
    }

    public String getNomConPago() {
        return nomConPago;
    }

    public void setNomConPago(String nomConPago) {
        this.nomConPago = nomConPago;
    }

    public String getConPagoEdit() {
        return conPagoEdit;
    }

    public void setConPagoEdit(String conPagoEdit) {
        this.conPagoEdit = conPagoEdit;
    }

    public List<String> getListaConPagoEdit() {
        return listaConPagoEdit;
    }

    public void setListaConPagoEdit(List<String> listaConPagoEdit) {
        this.listaConPagoEdit = listaConPagoEdit;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public CondicionPago getCondicionPago() {
        return condicionPago;
    }

    public void setCondicionPago(CondicionPago condicionPago) {
        this.condicionPago = condicionPago;
    }

    public ProveedorConPagoVo getProveedorConPagoVo() {
        return proveedorConPagoVo;
    }

    public void setProveedorConPagoVo(ProveedorConPagoVo proveedorConPagoVo) {
        this.proveedorConPagoVo = proveedorConPagoVo;
    }

    public OcProveedorConPago getOcProveedorConPago() {
        return ocProveedorConPago;
    }

    public void setOcProveedorConPago(OcProveedorConPago ocProveedorConPago) {
        this.ocProveedorConPago = ocProveedorConPago;
    }

    public CondicionPagoImpl getCondicionPagoServicioRemoto() {
        return condicionPagoServicioRemoto;
    }

    public void setCondicionPagoServicioRemoto(CondicionPagoImpl condicionPagoServicioRemoto) {
        this.condicionPagoServicioRemoto = condicionPagoServicioRemoto;
    }

    public OcProveedorConPagoImpl getOcProveedorConPagoImpl() {
        return ocProveedorConPagoImpl;
    }

    public void setOcProveedorConPagoImpl(OcProveedorConPagoImpl ocProveedorConPagoImpl) {
        this.ocProveedorConPagoImpl = ocProveedorConPagoImpl;
    }

    public DataModel getListData() {
        return listData;
    }

    public void setListData(DataModel listData) {
        this.listData = listData;
    }

    public boolean isVolver() {
        return volver;
    }

    public void setVolver(boolean volver) {
        this.volver = volver;
    }

    public boolean isVisibleConPago() {
        return visibleConPago;
    }

    public void setVisibleConPago(boolean visibleConPago) {
        this.visibleConPago = visibleConPago;
    }

    /**
     * @return the rfcCompania
     */
    public String getRfcCompania() {
        return rfcCompania;
    }

    /**
     * @param rfcCompania the rfcCompania to set
     */
    public void setRfcCompania(String rfcCompania) {
        this.rfcCompania = rfcCompania;
    }

}
