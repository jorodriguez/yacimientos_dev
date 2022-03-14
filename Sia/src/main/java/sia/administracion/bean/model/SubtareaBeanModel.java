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
import sia.modelo.OcCodigoSubtarea;
import sia.modelo.requisicion.vo.OcCodigoSubTareaVO;
import sia.servicios.requisicion.impl.OcCodigoSubtareaImpl;
import sia.sistema.bean.backing.Sesion;
import sia.util.UtilLog4j;

/**
 *
 * @author jcarranza
 */
@ManagedBean
@ViewScoped
public class SubtareaBeanModel {

    private static final UtilLog4j LOGGER = UtilLog4j.log;

    @ManagedProperty(value = "#{sesion}")
    private Sesion sesion;
    @EJB
    private OcCodigoSubtareaImpl ocCodigoSubtareaImpl;

    private int idSubTarea;
    private int idCampo;
    private OcCodigoSubtarea subObj;
    private OcCodigoSubTareaVO subVO;
    private List<OcCodigoSubTareaVO> lstSubtareas;

    public SubtareaBeanModel() {

    }

    public void inicia() {
        setIdCampo(getSesion().getUsuario().getApCampo().getId());
        cargarSubtareas();
    }
    
    public void cargarSubtareas() {
        setLstSubtareas(this.ocCodigoSubtareaImpl.getCodigosSubtareas());
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
     * @return the subObj
     */
    public OcCodigoSubtarea getSubObj() {
        return subObj;
    }

    /**
     * @param subObj the subObj to set
     */
    public void setSubObj(OcCodigoSubtarea subObj) {
        this.subObj = subObj;
    }

    /**
     * @return the subVO
     */
    public OcCodigoSubTareaVO getSubVO() {
        return subVO;
    }

    /**
     * @param subVO the subVO to set
     */
    public void setSubVO(OcCodigoSubTareaVO subVO) {
        this.subVO = subVO;
    }

    /**
     * @return the lstSubtareas
     */
    public List<OcCodigoSubTareaVO> getLstSubtareas() {
        return lstSubtareas;
    }

    /**
     * @param lstSubtareas the lstSubtareas to set
     */
    public void setLstSubtareas(List<OcCodigoSubTareaVO> lstSubtareas) {
        this.lstSubtareas = lstSubtareas;
    }

    public void nuevaSubtarea() {
        setSubVO(new OcCodigoSubTareaVO());
    }

    public void editarSubtarea() {
        setSubVO(this.ocCodigoSubtareaImpl.getCodigoSubtarea(getIdSubTarea()));
    }

    public void borrarSubtarea() {
        setSubObj(this.ocCodigoSubtareaImpl.find(getIdSubTarea()));
        if (getSubObj()!= null && getSubObj().getId() > 0) {
            getSubObj().setEliminado(true);
            getSubObj().setModifico(getSesion().getUsuario());
            getSubObj().setFechaModifico(new Date());
            getSubObj().setHoraModifico(new Date());

            this.ocCodigoSubtareaImpl.edit(getSubObj());
        }
    }

    public void guardarSubtarea() {
        if (getSubVO() != null) {
            if (getSubVO().getId() > 0) {
                boolean guardar = false;
                setSubObj(this.ocCodigoSubtareaImpl.find(getSubVO().getId()));

                if (!getSubObj().getNombre().equals(getSubVO().getNombre())) {
                    getSubObj().setNombre(getSubVO().getNombre());
                    guardar = true;
                }

                if (!getSubObj().getCodigo().equals(getSubVO().getCodigo())) {
                    getSubObj().setCodigo(getSubVO().getCodigo());
                    guardar = true;
                }

                getSubObj().setModifico(getSesion().getUsuario());
                getSubObj().setFechaModifico(new Date());
                getSubObj().setHoraModifico(new Date());

                if (guardar) {
                    this.ocCodigoSubtareaImpl.edit(getSubObj());
                }
            } else {
                setSubObj(new OcCodigoSubtarea());

                getSubObj().setNombre(getSubVO().getNombre());
                getSubObj().setCodigo(getSubVO().getCodigo());
                
                getSubObj().setEliminado(false);
                getSubObj().setGenero(getSesion().getUsuario());
                getSubObj().setFechaGenero(new Date());
                getSubObj().setHoraGenero(new Date());

                this.ocCodigoSubtareaImpl.edit(getSubObj());
            }
        }
    }

    /**
     * @return the idSubTarea
     */
    public int getIdSubTarea() {
        return idSubTarea;
    }

    /**
     * @param idSubTarea the idSubTarea to set
     */
    public void setIdSubTarea(int idSubTarea) {
        this.idSubTarea = idSubTarea;
    }

}
