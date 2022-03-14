/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.proveedor.bean.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import sia.constantes.Constantes;
import sia.modelo.ProveedorVetado;
import sia.servicios.catalogos.impl.ProveedorVetadoImpl;
import sia.servicios.sistema.vo.ProveedorVetadoVO;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@ManagedBean
@CustomScoped(value = "#{window}")
public class ProveedorVetadoBeanModel implements Serializable {

    private Sesion sesion;
    private List<ProveedorVetadoVO> lstVetado;
    private ProveedorVetadoVO newVetado;

    @EJB
    private ProveedorVetadoImpl proveedorVetadoImpl;

    @PostConstruct
    public void init() {
        this.setSesion((Sesion) FacesUtils.getManagedBean("sesion"));
        this.setNewVetado(null);
        refrescarTabla();
    }

    public void refrescarTabla() {
        this.setLstVetado(proveedorVetadoImpl.traerProveedoresVetados());
    }

    public void cargarVetado(ProveedorVetadoVO vetado) {
        setNewVetado(vetado);
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

    public List<ProveedorVetadoVO> getLstVetado() {
        return lstVetado;
    }

    /**
     * @param lstMoneda the lstMoneda to set
     */
    public void setLstVetado(List<ProveedorVetadoVO> lstVetado) {
        this.lstVetado = lstVetado;
    }

    public ProveedorVetadoVO getNewVetado() {
        return newVetado;
    }

    public void setNewVetado(ProveedorVetadoVO newVetado) {
        this.newVetado = newVetado;
    }

    public void guardarVetado() {
        try {

            if (getNewVetado().getId() > 0) {

                ProveedorVetado proveedorV = proveedorVetadoImpl.find(getNewVetado().getId());
                boolean guardar = false;
                if (proveedorV != null && !proveedorV.getRfc().equals(getNewVetado().getRfc())) {
                    proveedorV.setRfc(getNewVetado().getRfc().toUpperCase());
                    guardar = true;
                }
                if (proveedorV != null && !proveedorV.getNombre().equals(getNewVetado().getNombre())) {
                    proveedorV.setNombre(getNewVetado().getNombre().toUpperCase());
                    guardar = true;
                }

                if (proveedorV != null && !proveedorV.getDescripcion().equals(getNewVetado().getDescripcion())) {
                    proveedorV.setDescripcion(getNewVetado().getDescripcion().toUpperCase());
                    guardar = true;
                }

                if (proveedorV != null
                        && (getNewVetado().isActivo() && proveedorV.isEliminado())) {
                    proveedorV.setEliminado(Constantes.BOOLEAN_FALSE);
                    guardar = true;
                }
                if (proveedorV != null
                        && (!getNewVetado().isActivo() && proveedorV.isEliminado())) {
                    proveedorV.setEliminado(Constantes.BOOLEAN_TRUE);
                    guardar = true;
                }
                if (guardar) {
                    proveedorVetadoImpl.edit(proveedorV);
                }
            } else {
                ProveedorVetado pv = new ProveedorVetado();
                pv.setRfc(getNewVetado().getRfc().toUpperCase());
                pv.setNombre(getNewVetado().getNombre().toUpperCase());
                pv.setDescripcion(getNewVetado().getDescripcion().toUpperCase());
                pv.setEliminado(Constantes.BOOLEAN_FALSE);
                pv.setFechaGenero(new Date());
                pv.setHoraGenero(new Date());
                proveedorVetadoImpl.create(pv);
//                }
            }

        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
    }

    public void desactivarVetado(Integer idVetado) {
        try {
            if (idVetado > 0) {
                ProveedorVetado vetado = proveedorVetadoImpl.find(idVetado);
                if (vetado != null) {
                    vetado.setEliminado(Constantes.BOOLEAN_TRUE);
                    vetado.setFechaModifico(new Date());
                    vetado.setHoraModifico(new Date());
                    vetado.setModifico(getSesion().getUsuario());
                    proveedorVetadoImpl.edit(vetado);
                }
            }
        } catch (Exception e) {
            UtilLog4j.log.fatal(e);
        }
    }

}
