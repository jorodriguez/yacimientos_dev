/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.bean.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import sia.catalogos.bean.backing.UsuarioBean;
import sia.constantes.Constantes;
import sia.modelo.Usuario;
import sia.modelo.cadena.aprobacion.vo.CadenaAprobacionVo;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.servicios.campo.nuevo.impl.ApCampoImpl;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.requisicion.impl.CadenasMandoImpl;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.SoporteProveedor;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Named
@ViewScoped
public class CadenaMandoModel implements Serializable {

    @Inject
    private Sesion sesion;
    @Inject
    private CadenasMandoImpl cadenasMandoServicioRemoto;
    @Inject
    private ApCampoImpl apCampoImpl;
    @Inject
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
    @Inject
    private UsuarioImpl usuarioImpl;
//Primitivos
    private int idOrdena = 1;
    private DataModel listaCadena;
    private List<SelectItem> listaUsuarios;
    private String usuario;
    private String solicita;
    private String revisa;
    private String aprueba;
    private int idCampo;
    private String campo;
//    private String agregar = "False";
    private CadenaAprobacionVo cadenaAprobacionVo;
    private boolean modalPop = false;
    private boolean modalModificarPop = false;
    private boolean revisaReq = false;
    private boolean apruebaReq = false;

    /**
     * Creates a new instance of CadenaMandoModel
     */
    public CadenaMandoModel() {
    }
    
    public void iniciar(){
        if(getIdCampo() <= 0){
           setIdCampo(sesion.getUsuarioVo().getIdCampo()); 
        }
        
        setListaCadena(new ListDataModel(cadenasMandoServicioRemoto.traerCadenaAprobacion(null, getIdCampo(), 1, false, false ,false)));
        
    }
    public CadenaAprobacionVo traerCadenaAprobacion(int idCad){
        return  cadenasMandoServicioRemoto.traerPorId(idCad);        
    }
    public String traerUsuarioJson() {
        return apCampoUsuarioRhPuestoImpl.traerUsuarioActivoPorBloque(getIdCampo(),Constantes.CERO);
    }

    public List<SelectItem> listaCampo() {
        List<SelectItem> l = new ArrayList<SelectItem>();
        List<CampoUsuarioPuestoVo> lc;
        try {
            lc = apCampoUsuarioRhPuestoImpl.getAllPorUsurio(sesion.getUsuarioVo().getId());
            for (CampoUsuarioPuestoVo ca : lc) {
                SelectItem item = new SelectItem(ca.getIdCampo(), ca.getCampo());
                l.add(item);
            }
            return l;
        } catch (Exception e) {
            return null;
        }
    }

    public void  llenarListaCadena() {
        setListaCadena(new ListDataModel(this.cadenasMandoServicioRemoto.traerCadenaAprobacion(null , getIdCampo(), getIdOrdena(), false, false, false)));        
    }
    public DataModel buscarCadena() {
        UtilLog4j.log.info(this, "Campo: " + getIdCampo());
        UtilLog4j.log.info(this, "Ordena por: " + getIdOrdena());
        if (getUsuario() != null) {
            UtilLog4j.log.info(this, "U: " + getUsuario());
        } else {
            setUsuario(null);
        }
        UtilLog4j.log.info(this, "Usuario: " + getUsuario());
        setListaCadena(new ListDataModel(this.cadenasMandoServicioRemoto.traerCadenaAprobacion(getUsuario(), getIdCampo(), getIdOrdena(), false, false, false)));
        return getListaCadena();
    }

    public boolean verificaUsuariosRevisa() {
        Usuario ur = usuarioImpl.buscarPorNombre(getRevisa());
        if (ur != null) {
            return true;

        } else {
            return false;
        }
    }

    public boolean verificaUsuarioAprueba() {
        Usuario uA = usuarioImpl.buscarPorNombre(getAprueba());
        if (uA != null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean registroCadenaMando() {
        return this.cadenasMandoServicioRemoto.registroCadenaMando(getIdCampo(), getUsuario(), getRevisa(), getAprueba(), sesion.getUsuarioVo().getId());
    }

    public boolean completarModificacion() {
        UtilLog4j.log.info(this, "Solicita: " + getUsuario());
        UtilLog4j.log.info(this, "REvisa: " + getRevisa());
        UtilLog4j.log.info(this, "Aprueba: " + getAprueba());
        return this.cadenasMandoServicioRemoto.completarModificacion(getCadenaAprobacionVo().getId(), getRevisa(), getAprueba(), sesion.getUsuarioVo().getId());
    }

    public void eliminarCadena() {
        this.cadenasMandoServicioRemoto.eliminar(getCadenaAprobacionVo().getId(), sesion.getUsuarioVo().getId());
    }


    public int getIdOrdena() {
        return idOrdena;
    }

    /**
     * @param idOperacion the idOperacion to set
     */
    public void setIdOrdena(int idOrdena) {
        this.idOrdena = idOrdena;
    }

    /**
     * @return the listaCadena
     */
    public DataModel getListaCadena() {
        return listaCadena;
    }

    /**
     * @param listaCadena the listaCadena to set
     */
    public void setListaCadena(DataModel listaCadena) {
        this.listaCadena = listaCadena;
    }

    /**
     * @return the listaUsuarios
     */
    public List<SelectItem> getListaUsuarios() {
        return listaUsuarios;
    }

    /**
     * @param listaUsuarios the listaUsuarios to set
     */
    public void setListaUsuarios(List<SelectItem> listaUsuarios) {
        this.listaUsuarios = listaUsuarios;
    }

    /**
     * @return the usuario
     */
    public String getUsuario() {
        return usuario;
    }

    /**
     * @param usuario the usuario to set
     */
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    /**
     * @return the solicita
     */
    public String getSolicita() {
        return solicita;
    }

    /**
     * @param solicita the solicita to set
     */
    public void setSolicita(String solicita) {
        this.solicita = solicita;
    }

    /**
     * @return the revisa
     */
    public String getRevisa() {
        return revisa;
    }

    /**
     * @param revisa the revisa to set
     */
    public void setRevisa(String revisa) {
        this.revisa = revisa;
    }

    /**
     * @return the aprueba
     */
    public String getAprueba() {
        return aprueba;
    }

    /**
     * @param aprueba the aprueba to set
     */
    public void setAprueba(String aprueba) {
        this.aprueba = aprueba;
    }

    /**
     * @return the modalPop
     */
    public boolean isModalPop() {
        return modalPop;
    }

    /**
     * @param modalPop the modalPop to set
     */
    public void setModalPop(boolean modalPop) {
        this.modalPop = modalPop;
    }

    /**
     * @return the modalModificarPop
     */
    public boolean isModalModificarPop() {
        return modalModificarPop;
    }

    /**
     * @param modalModificarPop the modalModificarPop to set
     */
    public void setModalModificarPop(boolean modalModificarPop) {
        this.modalModificarPop = modalModificarPop;
    }

    /**
     * @return the cadenaAprobacionVo
     */
    public CadenaAprobacionVo getCadenaAprobacionVo() {
        return cadenaAprobacionVo;
    }

    /**
     * @param cadenaAprobacionVo the cadenaAprobacionVo to set
     */
    public void setCadenaAprobacionVo(CadenaAprobacionVo cadenaAprobacionVo) {
        this.cadenaAprobacionVo = cadenaAprobacionVo;
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
     * @return the campo
     */
    public String getCampo() {
        setCampo(apCampoImpl.find(getIdCampo()).getNombre());
        return campo;
    }

    /**
     * @param campo the campo to set
     */
    public void setCampo(String campo) {
        this.campo = campo;
    }
    
    public <T> List<T> getDataModelAsList(DataModel dm) {
        return (List<T>) dm.getWrappedData();
    }
    
    public void eliminarVariasCadenas(){
        List <CadenaAprobacionVo> listCadena = getDataModelAsList(getListaCadena());
        
        if (listCadena != null){
            for(CadenaAprobacionVo vo : listCadena){
                if(vo.isSelected()){
                    System.out.println(vo.getAprueba()+" "+vo.getRevisa());
                }
            }
        }
    }
    
    public void cadenaByApruebaOrAndRevisa (boolean rev, boolean ap, String user){
        
        setListaCadena(new ListDataModel(cadenasMandoServicioRemoto.traerCadenaAprobacion( user, getIdCampo(), getIdOrdena(), false, rev, ap)));
    } 

    /**
     * @return the revisaReq
     */
    public boolean isRevisaReq() {
        return revisaReq;
    }

    /**
     * @param revisaReq the revisaReq to set
     */
    public void setRevisaReq(boolean revisaReq) {
        this.revisaReq = revisaReq;
    }

    /**
     * @return the apruebaReq
     */
    public boolean isApruebaReq() {
        return apruebaReq;
    }

    /**
     * @param apruebaReq the apruebaReq to set
     */
    public void setApruebaReq(boolean apruebaReq) {
        this.apruebaReq = apruebaReq;
    }
}
