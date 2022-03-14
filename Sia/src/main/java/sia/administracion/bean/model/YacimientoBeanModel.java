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
import sia.modelo.OcYacimiento;
import sia.modelo.requisicion.vo.OcYacimientoVO;
import sia.servicios.requisicion.impl.OcYacimientoImpl;
import sia.sistema.bean.backing.Sesion;
import sia.util.UtilLog4j;

/**
 *
 * @author jcarranza
 */
@ManagedBean
@ViewScoped
public class YacimientoBeanModel {

    private static final UtilLog4j LOGGER = UtilLog4j.log;

    @ManagedProperty(value = "#{sesion}")
    private Sesion sesion;
    @EJB
    private OcYacimientoImpl ocYacimientoImpl;

    private int idYacimiento;
    private int idCampo;
    private OcYacimiento yacObj;
    private OcYacimientoVO yacVO;
    private List<OcYacimientoVO> lstYacimientos;

    public YacimientoBeanModel() {

    }

    public void inicia() {
        setIdCampo(getSesion().getUsuario().getApCampo().getId());
        cargarYacimientos();
    }
    
    public void cargarYacimientos() {
        setLstYacimientos(this.ocYacimientoImpl.getYacimientos());
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

    public void nuevoYacimiento() {
        setYacVO(new OcYacimientoVO());
    }

    public void editarYacimiento() {
        setYacVO(this.ocYacimientoImpl.getYacimiento(getIdYacimiento()));
    }

    public void borrarYacimiento() {
        setYacObj(this.ocYacimientoImpl.find(getIdYacimiento()));
        if (getYacObj() != null && getYacObj().getId() > 0) {
            getYacObj().setEliminado(true);
            getYacObj().setModifico(getSesion().getUsuario());
            getYacObj().setFechaModifico(new Date());
            getYacObj().setHoraModifico(new Date());

            this.ocYacimientoImpl.edit(getYacObj());
        }
    }

    public void guardarYacimiento() {
        if (getYacVO() != null) {
            if (getYacVO().getId() > 0) {
                boolean guardar = false;
                setYacObj(this.ocYacimientoImpl.find(getYacVO().getId()));

                if (!getYacObj().getNombre().equals(getYacVO().getNombre())) {
                    getYacObj().setNombre(getYacVO().getNombre());
                    guardar = true;
                }

                if (!getYacObj().getCodigo().equals(getYacVO().getCodigo())) {
                    getYacObj().setCodigo(getYacVO().getCodigo());
                    guardar = true;
                }

                getYacObj().setModifico(getSesion().getUsuario());
                getYacObj().setFechaModifico(new Date());
                getYacObj().setHoraModifico(new Date());

                if (guardar) {
                    this.ocYacimientoImpl.edit(getYacObj());
                }
            } else {
                setYacObj(new OcYacimiento());

                getYacObj().setNombre(getYacVO().getNombre());
                getYacObj().setCodigo(getYacVO().getCodigo());
                
                getYacObj().setEliminado(false);
                getYacObj().setGenero(getSesion().getUsuario());
                getYacObj().setFechaGenero(new Date());
                getYacObj().setHoraGenero(new Date());

                this.ocYacimientoImpl.edit(getYacObj());
            }
        }
    }

    /**
     * @return the idYacimiento
     */
    public int getIdYacimiento() {
        return idYacimiento;
    }

    /**
     * @param idYacimiento the idYacimiento to set
     */
    public void setIdYacimiento(int idYacimiento) {
        this.idYacimiento = idYacimiento;
    }

    /**
     * @return the yacObj
     */
    public OcYacimiento getYacObj() {
        return yacObj;
    }

    /**
     * @param yacObj the yacObj to set
     */
    public void setYacObj(OcYacimiento yacObj) {
        this.yacObj = yacObj;
    }

    /**
     * @return the yacVO
     */
    public OcYacimientoVO getYacVO() {
        return yacVO;
    }

    /**
     * @param yacVO the yacVO to set
     */
    public void setYacVO(OcYacimientoVO yacVO) {
        this.yacVO = yacVO;
    }

    /**
     * @return the lstYacimientos
     */
    public List<OcYacimientoVO> getLstYacimientos() {
        return lstYacimientos;
    }

    /**
     * @param lstYacimientos the lstYacimientos to set
     */
    public void setLstYacimientos(List<OcYacimientoVO> lstYacimientos) {
        this.lstYacimientos = lstYacimientos;
    }

   
}

