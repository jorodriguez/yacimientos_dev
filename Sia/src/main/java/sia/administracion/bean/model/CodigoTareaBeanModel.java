/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.bean.model;

import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import sia.modelo.OcCodigoTarea;
import sia.servicios.requisicion.impl.OcCodigoTareaImpl;
import sia.sistema.bean.backing.Sesion;
import sia.util.UtilLog4j;

/**
 *
 * @author jcarranza
 */
@ManagedBean
@ViewScoped
public class CodigoTareaBeanModel {
    
    private static final UtilLog4j LOGGER = UtilLog4j.log;

    @ManagedProperty(value = "#{sesion}")
    private Sesion sesion;
    @EJB
    private OcCodigoTareaImpl ocCodigoTareaLocal;
    
    private int idCampo;
    private int idCodigo;
    private List<OcCodigoTarea> codigos;
    private OcCodigoTarea codigo;
    
    public CodigoTareaBeanModel(){
    
    }
    
    public void inicia() {
        setIdCampo(getSesion().getUsuario().getApCampo().getId());
        cargarCodigos();
    }
    
    public void cargarCodigos(){
        setCodigos(ocCodigoTareaLocal.getAllActive());
    }

    public void nuevoCodigo() {
        setCodigo(new OcCodigoTarea());
        getCodigo().setId(0);
    }

    public void editarCodigo() {
        setCodigo(this.ocCodigoTareaLocal.find(getIdCodigo()));
    }

    public void borrarCodigo() {
        setCodigo(this.ocCodigoTareaLocal.find(getIdCodigo()));
        if (getCodigo()!= null && getCodigo().getId() > 0) {
            getCodigo().setEliminado(true);
            getCodigo().setModifico(getSesion().getUsuario());
            getCodigo().setFechaModifico(new Date());
            getCodigo().setHoraModifico(new Date());
            this.ocCodigoTareaLocal.edit(getCodigo());
        }
    }

    public void guardarCodigo() {
        if (getCodigo() != null) {
            if (getCodigo().getId() > 0) {
                getCodigo().setModifico(getSesion().getUsuario());
                getCodigo().setFechaModifico(new Date());
                getCodigo().setHoraModifico(new Date());                
                this.ocCodigoTareaLocal.edit(getCodigo());                
            } else {                
                getCodigo().setEliminado(false);
                getCodigo().setGenero(getSesion().getUsuario());
                getCodigo().setFechaGenero(new Date());
                getCodigo().setHoraGenero(new Date());
                this.ocCodigoTareaLocal.edit(getCodigo());
            }
        }
    }
    
    /**
     * @return the sesion
     */
    public Sesion getSesion() {
        return sesion;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
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
     * @return the idCodigo
     */
    public int getIdCodigo() {
        return idCodigo;
    }

    /**
     * @param idCodigo the idCodigo to set
     */
    public void setIdCodigo(int idCodigo) {
        this.idCodigo = idCodigo;
    }

    /**
     * @return the codigos
     */
    public List<OcCodigoTarea> getCodigos() {
        return codigos;
    }

    /**
     * @param codigos the codigos to set
     */
    public void setCodigos(List<OcCodigoTarea> codigos) {
        this.codigos = codigos;
    }

    /**
     * @return the codigo
     */
    public OcCodigoTarea getCodigo() {
        return codigo;
    }

    /**
     * @param codigo the codigo to set
     */
    public void setCodigo(OcCodigoTarea codigo) {
        this.codigo = codigo;
    }
    
}
