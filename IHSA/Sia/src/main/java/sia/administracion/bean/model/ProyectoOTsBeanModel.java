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
import sia.modelo.ApCampo;
import sia.modelo.Compania;
import sia.modelo.OcSubcampo;
import sia.modelo.OcYacimiento;
import sia.modelo.ProyectoOt;
import sia.modelo.proyectoOT.vo.ProyectoOtVo;
import sia.modelo.requisicion.vo.OcSubCampoVO;
import sia.modelo.requisicion.vo.OcYacimientoVO;
import sia.servicios.catalogos.impl.ProyectoOtImpl;
import sia.servicios.requisicion.impl.OcSubcampoImpl;
import sia.servicios.requisicion.impl.OcYacimientoImpl;
import sia.sistema.bean.backing.Sesion;
import sia.util.UtilLog4j;

/**
 *
 * @author jcarranza
 */
@Named
@ViewScoped
public class ProyectoOTsBeanModel implements  Serializable{

    private static final UtilLog4j LOGGER = UtilLog4j.log;

    @Inject
    private Sesion sesion;

    @Inject
    private ProyectoOtImpl proyectoOtImpl;
    @Inject
    private OcYacimientoImpl ocYacimientoImpl;
    @Inject
    private OcSubcampoImpl ocSubcampoImpl;

    private int idProy;
    private int idCampo;
    private String compania;
    private ProyectoOtVo proVO;
    private ProyectoOt proObj;
    private List<ProyectoOtVo> lstProyectos;
    private List<OcSubCampoVO> lstSubcampos;
    private List<OcYacimientoVO> lstYacimientos;

    public ProyectoOTsBeanModel() {

    }

    public void inicia() {
        setIdCampo(getSesion().getUsuario().getApCampo().getId());
        setCompania(getSesion().getUsuario().getApCampo().getCompania().getRfc());
        cargarOts();
    }

    public void cargarOts() {
        setLstProyectos(this.proyectoOtImpl.getListaProyectosOtPorCampo(getIdCampo(), getCompania(), "C", false));
    }

    public void cargarYacimientos() {
        setLstYacimientos(ocYacimientoImpl.getYacimientos());
    }

    public void cargarSubcampo() {
        setLstSubcampos(ocSubcampoImpl.getSubcampos());
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
     * @return the proVO
     */
    public ProyectoOtVo getProVO() {
        return proVO;
    }

    /**
     * @param proVO the proVO to set
     */
    public void setProVO(ProyectoOtVo proVO) {
        this.proVO = proVO;
    }

    /**
     * @return the proObj
     */
    public ProyectoOt getProObj() {
        return proObj;
    }

    /**
     * @param proObj the proObj to set
     */
    public void setProObj(ProyectoOt proObj) {
        this.proObj = proObj;
    }

    /**
     * @return the lstProyectos
     */
    public List<ProyectoOtVo> getLstProyectos() {
        return lstProyectos;
    }

    /**
     * @param lstProyectos the lstProyectos to set
     */
    public void setLstProyectos(List<ProyectoOtVo> lstProyectos) {
        this.lstProyectos = lstProyectos;
    }

    /**
     * @return the compania
     */
    public String getCompania() {
        return compania;
    }

    /**
     * @param compania the compania to set
     */
    public void setCompania(String compania) {
        this.compania = compania;
    }

    public void nuevoProy() {
        setProVO(new ProyectoOtVo());
        getProVO().setId(0);
    }

    public void abrirProy() {
        setProObj(this.proyectoOtImpl.find(getIdProy()));
        if (getProObj() != null && getProObj().getId() > 0) {
            getProObj().setAbierto(true);
            getProObj().setVisible(true);
            getProObj().setModifico(getSesion().getUsuario());
            getProObj().setFechaModifico(new Date());
            getProObj().setHoraModifico(new Date());

            this.proyectoOtImpl.edit(getProObj());
        }
    }

    public void cerrarProy() {
        setProObj(this.proyectoOtImpl.find(getIdProy()));
        if (getProObj() != null && getProObj().getId() > 0) {
            getProObj().setAbierto(false);
            getProObj().setModifico(getSesion().getUsuario());
            getProObj().setFechaModifico(new Date());
            getProObj().setHoraModifico(new Date());

            this.proyectoOtImpl.edit(getProObj());
        }

    }

    public void editarProy() {
        setProVO(this.proyectoOtImpl.getProyectoOtVO(getIdProy()));
    }

    public void borrarProy() {
        setProObj(this.proyectoOtImpl.find(getIdProy()));
        if (getProObj() != null && getProObj().getId() > 0) {
            getProObj().setEliminado(true);
            getProObj().setModifico(getSesion().getUsuario());
            getProObj().setFechaModifico(new Date());
            getProObj().setHoraModifico(new Date());

            this.proyectoOtImpl.edit(getProObj());
        }
    }

    public void guardarProy() {
        if (getProVO() != null) {
            if (getProVO().getId() > 0) {
                boolean guardar = false;
                setProObj(this.proyectoOtImpl.find(getProVO().getId()));

                if (!getProObj().getNombre().equals(getProVO().getNombre())) {
                    getProObj().setNombre(getProVO().getNombre());
                    guardar = true;
                }

                if (getProObj().getOcYacimiento().getId() != getProVO().getIdYacimiento()) {
                    if (getProVO().getIdYacimiento() == 0) {
                        getProObj().setOcYacimiento(null);
                    } else {
                        getProObj().setOcYacimiento(new OcYacimiento(getProVO().getIdYacimiento()));
                    }
                    guardar = true;
                }

                if (getProObj().getOcSubcampo().getId() != getProVO().getIdSubCampo()) {
                    if (getProVO().getIdSubCampo() == 0) {
                        getProObj().setOcSubcampo(null);
                    } else {
                        getProObj().setOcSubcampo(new OcSubcampo(getProVO().getIdSubCampo()));
                    }
                    guardar = true;
                }

//                if (!getProObj().getCodigo().equals(getProVO().getCodigo())) {
//                    getProObj().setCodigo(getProVO().getCodigo());
//                    guardar = true;
//                }
                getProObj().setModifico(getSesion().getUsuario());
                getProObj().setFechaModifico(new Date());
                getProObj().setHoraModifico(new Date());

                if (guardar) {
                    this.proyectoOtImpl.edit(getProObj());
                }
            } else {
                setProObj(new ProyectoOt());

                getProObj().setNombre(getProVO().getNombre());
//                getProObj().setCodigo(getProVO().getCodigo());
                getProObj().setCuentaContable(getProVO().getCuentaContable());
                getProObj().setAbierto(getProVO().isAbierto());
                getProObj().setVisible(getProVO().isAbierto());
                getProObj().setCompania(new Compania(getCompania()));
                getProObj().setApCampo(new ApCampo(getIdCampo()));
                if (getProVO().getIdSubCampo() > 0) {
                    getProObj().setOcSubcampo(new OcSubcampo(getProVO().getIdSubCampo()));
                }
                if (getProVO().getIdYacimiento() > 0) {
                    getProObj().setOcYacimiento(new OcYacimiento(getProVO().getIdYacimiento()));
                }

                getProObj().setEliminado(false);
                getProObj().setGenero(getSesion().getUsuario());
                getProObj().setFechaGenero(new Date());
                getProObj().setHoraGenero(new Date());

                this.proyectoOtImpl.edit(getProObj());
            }
        }
    }

    /**
     * @return the idProy
     */
    public int getIdProy() {
        return idProy;
    }

    /**
     * @param idProy the idProy to set
     */
    public void setIdProy(int idProy) {
        this.idProy = idProy;
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
