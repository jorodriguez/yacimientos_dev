/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.bean.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.faces.view.ViewScoped;



import javax.inject.Inject;
import javax.inject.Named;
import sia.modelo.OcSubcampo;
import sia.modelo.requisicion.vo.OcSubCampoVO;
import sia.servicios.requisicion.impl.OcSubcampoImpl;
import sia.sistema.bean.backing.Sesion;
import sia.util.UtilLog4j;

/**
 *
 * @author jcarranza
 */
@Named
@ViewScoped
public class SubCampoBeanModel implements Serializable{

    private static final UtilLog4j LOGGER = UtilLog4j.log;

    @Inject
    private Sesion sesion;
    @Inject
    private OcSubcampoImpl ocSubcampoImpl;

    private int idSubcampo;
    private int idCampo;
    private OcSubcampo subObj;
    private OcSubCampoVO subVO;
    private List<OcSubCampoVO> lstSubcampos;

    public SubCampoBeanModel() {

    }

    public void inicia() {
        setIdCampo(getSesion().getUsuario().getApCampo().getId());
        cargarSubcampos();
    }
    
    public void cargarSubcampos() {
        setLstSubcampos(this.ocSubcampoImpl.getSubcampos());
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

    

    public void nuevoSubcampo() {
        setSubVO(new OcSubCampoVO());
    }

    public void editarSubcampo() {
        setSubVO(this.ocSubcampoImpl.getSubCampo(getIdSubcampo()));
    }

    public void borrarSubcampo() {
        setSubObj(this.ocSubcampoImpl.find(getIdSubcampo()));
        if (getSubObj()!= null && getSubObj().getId() > 0) {
            getSubObj().setEliminado(true);
            getSubObj().setModifico(getSesion().getUsuario());
            getSubObj().setFechaModifico(new Date());
            getSubObj().setHoraModifico(new Date());

            this.ocSubcampoImpl.edit(getSubObj());
        }
    }

    public void guardarSubcampo() {
        if (getSubVO() != null) {
            if (getSubVO().getId() > 0) {
                boolean guardar = false;
                setSubObj(this.ocSubcampoImpl.find(getSubVO().getId()));

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
                    this.ocSubcampoImpl.edit(getSubObj());
                }
            } else {
                setSubObj(new OcSubcampo());

                getSubObj().setNombre(getSubVO().getNombre());
                getSubObj().setCodigo(getSubVO().getCodigo());
                
                getSubObj().setEliminado(false);
                getSubObj().setGenero(getSesion().getUsuario());
                getSubObj().setFechaGenero(new Date());
                getSubObj().setHoraGenero(new Date());

                this.ocSubcampoImpl.edit(getSubObj());
            }
        }
    }

    /**
     * @return the idSubcampo
     */
    public int getIdSubcampo() {
        return idSubcampo;
    }

    /**
     * @param idSubcampo the idSubcampo to set
     */
    public void setIdSubcampo(int idSubcampo) {
        this.idSubcampo = idSubcampo;
    }

    /**
     * @return the subObj
     */
    public OcSubcampo getSubObj() {
        return subObj;
    }

    /**
     * @param subObj the subObj to set
     */
    public void setSubObj(OcSubcampo subObj) {
        this.subObj = subObj;
    }

    /**
     * @return the subVO
     */
    public OcSubCampoVO getSubVO() {
        return subVO;
    }

    /**
     * @param subVO the subVO to set
     */
    public void setSubVO(OcSubCampoVO subVO) {
        this.subVO = subVO;
    }

    /**
     * @return the lstSubcampos
     */
    public List<OcSubCampoVO> getLstSubcampos() {
        return lstSubcampos;
    }

    /**
     * @param lstSubcampos the lstSubcampos to set
     */
    public void setLstSubcampos(List<OcSubCampoVO> lstSubcampos) {
        this.lstSubcampos = lstSubcampos;
    }

    
}

