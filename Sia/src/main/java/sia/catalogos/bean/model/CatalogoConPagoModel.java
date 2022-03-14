/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.catalogos.bean.model;

import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import sia.constantes.Constantes;
import sia.modelo.CondicionPago;
import sia.modelo.orden.vo.CondicionPagoVO;
import sia.modelo.orden.vo.ProveedorConPagoVo;
import sia.modelo.sgl.vo.OrdenVO;
import sia.servicios.catalogos.impl.CondicionPagoImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.orden.impl.OcProveedorConPagoImpl;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;
import sia.sistema.bean.backing.Sesion;

/**
 *
 * @author ines
 */
@ManagedBean
@CustomScoped(value = "#{window}")
public class CatalogoConPagoModel {

    //Servicios
    @EJB
    private CondicionPagoImpl condicionPagoServicioRemoto;
    @EJB
    private OcProveedorConPagoImpl ocProveedorConPagoImpl;
    @EJB
    private UsuarioImpl usuarioImpl;
    @EJB
    private OrdenImpl ordenImpl;
    @EJB
    private ProveedorServicioImpl proveedorImpl;
    //ManagedBeans   
    @ManagedProperty(value = "#{sesion}")
    private Sesion sesion;
    //Entidades    
    private CondicionPagoVO condicionPago;
    //Colecciones        
    private DataModel listData;
    //Clases    
    private String nomConPago = null;
    private boolean notifica = false;
    //
    private String rfcCompania;

    public CatalogoConPagoModel() {
    }


    public CondicionPago buscarConPagoPorNombre() {
        return getCondicionPagoServicioRemoto().buscarPorNombre(getNomConPago(), Constantes.BOOLEAN_FALSE);
    }

    public String buscarProConPagoPorNombre() {
        boolean primeraVez = true;

        StringBuilder proveedores = new StringBuilder();

        List<ProveedorConPagoVo> lpc = this.getOcProveedorConPagoImpl().findByNombreConPago(this.getCondicionPagoVO().getNombre());

        if (lpc != null) {
            for (ProveedorConPagoVo reg : lpc) {
                if (primeraVez) {
                    primeraVez = false;
                    proveedores.append(reg.nombreProveedor);
                } else {
                    proveedores.append(" ; ").append(reg.nombreProveedor);
                }
            }
            return proveedores.toString();
        }

        return null;
    }
    
    public boolean listaOrdenesPorCondicionPago(){
        List<OrdenVO> lo = ordenImpl.ordenesPorCondicionPago(getCondicionPagoVO().getId(), Constantes.CERO);
        return lo.size() > 0 ? false : true;
    }
    
    public String regresaRfcCompaniaSesion(){
        return sesion.getRfcCompania();
    }

    public DataModel getAllConPago() {
        this.setListData(new ListDataModel(getCondicionPagoServicioRemoto().trearCondicionPago(Constantes.BOOLEAN_FALSE)));
        return getListData();
    }

    public void guardarAltaCondicion() {
        this.evaluaNotifica(getNomConPago());
        this.getCondicionPagoServicioRemoto().guardarAltaCondicion(getNomConPago(), isNotifica(), getSesion().getUsuario(), getRfcCompania());
        this.getAllConPago();
    }

    public void actualizarConPago() {
        this.evaluaNotifica(getNomConPago());
        this.getCondicionPagoServicioRemoto().actualizarConPago(getCondicionPagoVO().getId(), getNomConPago(), isNotifica(), getSesion().getUsuario(), getRfcCompania());
        this.getAllConPago();
    }

    public void eliminarConPago() {//Antes comprueba que la condicion de pago no este ligada a ningun proveedor               
        this.getCondicionPagoServicioRemoto().eliminarConPago(getCondicionPagoVO().getId(), getSesion().getUsuario());
        this.getAllConPago();
    }

    public void evaluaNotifica(String nombreConPago) {
        if (nombreConPago.toUpperCase().contains("ANTICIP")) {
            setNotifica(Constantes.BOOLEAN_TRUE);
        } else {
            setNotifica(Constantes.BOOLEAN_FALSE);
        }
    }

    public CondicionPagoImpl getCondicionPagoServicioRemoto() {
        return condicionPagoServicioRemoto;
    }

    public void setCondicionPagoServicioRemoto(CondicionPagoImpl condicionPagoServicioRemoto) {
        this.condicionPagoServicioRemoto = condicionPagoServicioRemoto;
    }

    public String getNomConPago() {
        return nomConPago;
    }

    public void setNomConPago(String nomConPago) {
        this.nomConPago = nomConPago;
    }

    public CondicionPagoVO getCondicionPagoVO() {
        return condicionPago;
    }

    public void setCondicionPagoVO(CondicionPagoVO condicionPago) {
        this.condicionPago = condicionPago;
    }

    public Sesion getSesion() {
        return sesion;
    }

    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

    public DataModel getListData() {
        return listData;
    }

    public void setListData(DataModel listData) {
        this.listData = listData;
    }

    public OcProveedorConPagoImpl getOcProveedorConPagoImpl() {
        return ocProveedorConPagoImpl;
    }

    public void setOcProveedorConPagoImpl(OcProveedorConPagoImpl ocProveedorConPagoImpl) {
        this.ocProveedorConPagoImpl = ocProveedorConPagoImpl;
    }

    public boolean isNotifica() {
        return notifica;
    }

    public void setNotifica(boolean notifica) {
        this.notifica = notifica;
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
