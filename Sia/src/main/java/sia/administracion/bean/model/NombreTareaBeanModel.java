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
import sia.modelo.OcNombreTarea;
import sia.servicios.requisicion.impl.OcNombreTareaImpl;
import sia.sistema.bean.backing.Sesion;
import sia.util.UtilLog4j;

/**
 *
 * @author jcarranza
 */
@ManagedBean
@ViewScoped
public class NombreTareaBeanModel {

    private static final UtilLog4j LOGGER = UtilLog4j.log;

    @ManagedProperty(value = "#{sesion}")
    private Sesion sesion;
    @EJB
    private OcNombreTareaImpl ocNombreTareaImpl;
    

    private int idCampo;
    private int idNombre;
    private List<OcNombreTarea>  nombres;
    private OcNombreTarea nombre;

    public NombreTareaBeanModel() {

    }

    public void inicia() {
        setIdCampo(getSesion().getUsuario().getApCampo().getId());
        cargarNombres();
    }

    public void cargarNombres() {
        setNombres(ocNombreTareaImpl.getAllActive());
    }

    public void nuevoNombre() {
        setNombre(new OcNombreTarea());
        getNombre().setId(0);
    }

    public void editarNombre() {
        setNombre(this.ocNombreTareaImpl.find(getIdNombre()));
    }

    public void borrarNombre() {
        setNombre(this.ocNombreTareaImpl.find(getIdNombre()));
        if (getNombre()!= null && getNombre().getId() > 0) {
            getNombre().setEliminado(true);
            getNombre().setModifico(getSesion().getUsuario());
            getNombre().setFechaModifico(new Date());
            getNombre().setHoraModifico(new Date());

            this.ocNombreTareaImpl.edit(getNombre());
        }
    }

    public void guardarNombre() {
        if (getNombre() != null) {
            if (getNombre().getId() > 0) {                
                getNombre().setModifico(getSesion().getUsuario());
                getNombre().setFechaModifico(new Date());
                getNombre().setHoraModifico(new Date());
                    this.ocNombreTareaImpl.edit(getNombre());
            } else {                
                getNombre().setEliminado(false);
                getNombre().setGenero(getSesion().getUsuario());
                getNombre().setFechaGenero(new Date());
                getNombre().setHoraGenero(new Date());

                this.ocNombreTareaImpl.create(getNombre());
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
     * @return the idNombre
     */
    public int getIdNombre() {
        return idNombre;
    }

    /**
     * @param idNombre the idNombre to set
     */
    public void setIdNombre(int idNombre) {
        this.idNombre = idNombre;
    }

    /**
     * @return the nombres
     */
    public List<OcNombreTarea> getNombres() {
        return nombres;
    }

    /**
     * @param nombres the nombres to set
     */
    public void setNombres(List<OcNombreTarea> nombres) {
        this.nombres = nombres;
    }

    /**
     * @return the nombre
     */
    public OcNombreTarea getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(OcNombreTarea nombre) {
        this.nombre = nombre;
    }

}
