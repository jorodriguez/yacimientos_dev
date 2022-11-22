/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.bean.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;



import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import sia.constantes.Constantes;
import sia.modelo.AutorizacionesOrden;
import sia.modelo.Orden;
import sia.modelo.Usuario;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.sgl.vo.OrdenVO;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.orden.impl.AutorizacionesOrdenImpl;
import sia.servicios.orden.impl.OrdenImpl;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.SoporteProveedor;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Named
@ViewScoped
public class CancelarReenviarOrdenModel implements Serializable{

    @Inject
    private OrdenImpl ordenServicioRemoto;
    @Inject
    private AutorizacionesOrdenImpl autorizacionesOrdenServicioRemoto;
    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
    @Inject
    private UsuarioImpl usuarioImpl;
    //
    @Inject
    private Sesion sesion;
    //
    private Orden orden;
    private String consecutivo;
    private boolean mostrar = false;
    private boolean modal = false;
    private boolean cancelReq = false;
    private boolean mostrarDev = false;
    private String motivoDev;
    private String motivo;
    private String usuarioSolicita;
    private String opcionUsuario = "compra";
    private List<SelectItem> listaSelect;
    //
    private int idCampo;
    private int idGerenciaCompra;

    /**
     * Creates a new instance of CancelarReenviarOrdenModel
     */
    public CancelarReenviarOrdenModel() {
    }

    public void iniciarCampo() {
        setIdCampo(sesion.getUsuario().getApCampo().getId());
        setOrden(null);
    }

    public Orden buscarOrden(String consecutivo) {
        try {
            return this.ordenServicioRemoto.buscarPorConsecutivoBloque(consecutivo, getIdCampo(), this.sesion.getUsuario().getId());
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio una excepción al buscar la OC/S - - -" + e.getMessage());
        }
        return null;

    }

    public AutorizacionesOrden buscarOrdenAutorizacion(int id) {
        try {
            return this.autorizacionesOrdenServicioRemoto.buscarPorOrden(id);
        } catch (Exception e) {
            return null;
        }
    }

    public List<SelectItem> regresaUsuarioActivo(String cadenaDigitada) {//, -1, "nombre", true, null, false){
        return apCampoUsuarioRhPuestoImpl.traerUsurioEnCampoPorCadenaItems(cadenaDigitada, this.getIdCampo());
             //soporteProveedor.regresaUsuarioActivo(cadenaDigitada,this.getIdCampo(),"nombre", true, null, false);
    }

    public boolean verificaUsuarioSolicita() {
        Usuario u = usuarioImpl.buscarPorNombre(getUsuarioSolicita());
        if (u != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean completarCancelacionOrden() throws Exception {
        return this.ordenServicioRemoto.cancelarOrden(getOrden(),  getUsuarioSolicita(), sesion.getUsuario().getId(), (Object) getMotivo(), true, isCancelReq());
    }

    public boolean completarDevoluccionOrden() throws Exception {
        return this.ordenServicioRemoto.devolverOrden(getOrden(), getUsuarioSolicita(), sesion.getUsuario().getId(),  getMotivoDev());
    }

    public boolean reenviarOrden(Orden orden) throws Exception {
        return ordenServicioRemoto.reenviarOrdenCompras(orden, this.sesion.getUsuario());
    }

    public boolean reenviarCodigos(Orden orden) throws Exception {
        return ordenServicioRemoto.reenviarCodigos(orden);
    }

    public List<SelectItem> listaCampoTareas() {
        List<SelectItem> l = new ArrayList<SelectItem>();
        List<CampoUsuarioPuestoVo> lc;
        try {
            //lc = apCampoImpl.getAllField();
            lc = apCampoUsuarioRhPuestoImpl.getAllPorUsurioCategoria(sesion.getUsuario().getId(), Constantes.ROL_ADMIN_SIA);
            for (CampoUsuarioPuestoVo ca : lc) {
                SelectItem item = new SelectItem(ca.getIdCampo(), ca.getCampo());
                l.add(item);
            }
            return l;
        } catch (Exception e) {
            return null;
        }
    }
    
    public List<SelectItem> listaCampoCancelar() {
        List<SelectItem> l = new ArrayList<SelectItem>();
        List<CampoUsuarioPuestoVo> lc;
        try {
            //lc = apCampoImpl.getAllField();
            lc = apCampoUsuarioRhPuestoImpl.getAllPorUsurioCategoriaCodigo(sesion.getUsuario().getId(), Constantes.CODIGO_ROL_CANCELAR_OCS);
            for (CampoUsuarioPuestoVo ca : lc) {
                SelectItem item = new SelectItem(ca.getIdCampo(), ca.getCampo());
                l.add(item);
            }
            return l;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * *
     *
     */
    public boolean cambiarAnalistaOCS() {
        return ordenServicioRemoto.cambiarAnalistaOCS(sesion.getUsuario().getId(), getOrden().getId(), getUsuarioSolicita());
    }

    public List<SelectItem> listaAnalista() {
        List<SelectItem> resultList = new ArrayList<SelectItem>();
        try {
            //List<Usuario> tempList = usuarioServicioRemoto.getAnalistas();
            List<UsuarioVO> tempList = usuarioImpl.traerListaRolPrincipalUsuarioRolModulo(Constantes.ROL_COMPRADOR, Constantes.MODULO_COMPRA, getIdCampo());
            for (UsuarioVO usuarioVO : tempList) {
                SelectItem item = new SelectItem(usuarioVO.getId(), usuarioVO.getNombre());
                // esta linea es por si quiero agregar mas de un valoritem.setValue(Lista.getId());
                resultList.add(item);
            }

            return resultList;
        } catch (RuntimeException ex) {
            UtilLog4j.log.fatal(this, "Error  : :  :" + ex.getMessage());
        }
        return resultList;
    }

//    public boolean generarExcel(Orden orden)  throws Exception{                
//        return ordenServicioRemoto.generarExcel(orden);
//    }
    /**
     * @return the orden
     */
    public Orden getOrden() {
        return orden;
    }

    /**
     * @param orden the orden to set
     */
    public void setOrden(Orden orden) {
        this.orden = orden;
    }

    /**
     * @return the consecutivo
     */
    public String getConsecutivo() {
        return consecutivo;
    }

    /**
     * @param consecutivo the consecutivo to set
     */
    public void setConsecutivo(String consecutivo) {
        this.consecutivo = consecutivo;
    }

    /**
     * @return the mostrar
     */
    public boolean isMostrar() {
        return mostrar;
    }

    /**
     * @param mostrar the mostrar to set
     */
    public void setMostrar(boolean mostrar) {
        this.mostrar = mostrar;
    }

    /**
     * @return the modal
     */
    public boolean isModal() {
        return modal;
    }

    /**
     * @param modal the modal to set
     */
    public void setModal(boolean modal) {
        this.modal = modal;
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
     * @return the usuarioSolicita
     */
    public String getUsuarioSolicita() {
        return usuarioSolicita;
    }

    /**
     * @param usuarioSolicita the usuarioSolicita to set
     */
    public void setUsuarioSolicita(String usuarioSolicita) {
        this.usuarioSolicita = usuarioSolicita;
    }

    /**
     * @return the listaSelect
     */
    public List<SelectItem> getListaSelect() {
        return listaSelect;
    }

    /**
     * @param listaSelect the listaSelect to set
     */
    public void setListaSelect(List<SelectItem> listaSelect) {
        this.listaSelect = listaSelect;
    }

    /**
     * @return the opcionUsuario
     */
    public String getOpcionUsuario() {
        return opcionUsuario;
    }

    /**
     * @param opcionUsuario the opcionUsuario to set
     */
    public void setOpcionUsuario(String opcionUsuario) {
        this.opcionUsuario = opcionUsuario;
    }

    /**
     * @return the cancelReq
     */
    public boolean isCancelReq() {
        return cancelReq;
    }

    public boolean puedoCancelarRequisicion() {        
        boolean cancelar = true;
        try {            
            List<OrdenVO> l = this.ordenServicioRemoto.getOrdenesPorRequisicion(getOrden().getRequisicion().getId(), "and e.id <> 100");
            if (!l.isEmpty() && l.size() > 1) {                
                for (OrdenVO objects : l) {                    
                    if (objects.getId() != getOrden().getId() && this.ordenServicioRemoto.find(objects.getId()).getAutorizacionesOrden().getEstatus().getId() > 100) {
                        cancelar = false;
                        break;
                    }
                }
            }            
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex.getMessage());
        }
        return cancelar;
    }

    /**
     * @param cancelReq the cancelReq to set
     */
    public void setCancelReq(boolean cancelReq) {
        this.cancelReq = cancelReq;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

    /**
     * @return the mostrarDev
     */
    public boolean isMostrarDev() {
        return mostrarDev;
    }

    /**
     * @param mostrarDev the mostrarDev to set
     */
    public void setMostrarDev(boolean mostrarDev) {
        this.mostrarDev = mostrarDev;
    }

    /**
     * @return the motivoDev
     */
    public String getMotivoDev() {
        return motivoDev;
    }

    /**
     * @param motivoDev the motivoDev to set
     */
    public void setMotivoDev(String motivoDev) {
        this.motivoDev = motivoDev;
    }

    /**
     * @return the idCampo
     */
    public int getIdCampo() {
        return idCampo;
    }

    /**
     * @param idCampo the idCampo to set
     */
    public void setIdCampo(int idCampo) {
        this.idCampo = idCampo;
    }

    /**
     * @return the idGerenciaCompra
     */
    public int getIdGerenciaCompra() {
        return idGerenciaCompra;
    }

    /**
     * @param idGerenciaCompra the idGerenciaCompra to set
     */
    public void setIdGerenciaCompra(int idGerenciaCompra) {
        this.idGerenciaCompra = idGerenciaCompra;
    }
}
