/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.bean.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;

import javax.inject.Inject;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;

import org.primefaces.PrimeFaces;
import sia.constantes.Constantes;
import sia.modelo.ProveedorVetado;
import sia.modelo.Usuario;
import sia.servicios.catalogos.impl.ProveedorVetadoImpl;
import sia.servicios.sistema.vo.ProveedorVetadoVO;
import sia.sistema.bean.backing.Sesion;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@Named(value = "proveedorVetadoBean")
@RequestScoped
public class ProveedorVetadoBean implements Serializable {

    @Inject
    private Sesion sesion;
    @Getter
    @Setter
    private List<ProveedorVetadoVO> lstVetado;
    @Getter
    @Setter
    private ProveedorVetadoVO newVetado;

    @Inject
    private ProveedorVetadoImpl proveedorVetadoImpl;

    @PostConstruct
    public void init() {
        lstVetado = new ArrayList<>();
        setNewVetado(null);
        refrescarTabla();
    }

    /**
     * Creates a new instance of ProveedorVetadoBean
     */
    public ProveedorVetadoBean() {
    }

    public void refrescarTabla() {
        this.setLstVetado(proveedorVetadoImpl.traerProveedoresVetados());
    }

    public void editarVetado(int idVetado, String rfc, String descr, String activo, String nom) {
        try {
            ProveedorVetadoVO vo = new ProveedorVetadoVO();
            vo.setRfc(rfc.toUpperCase());
            vo.setDescripcion(descr.toUpperCase());
            vo.setNombre(nom.toUpperCase());
            vo.setId(idVetado);
            vo.setActivo(activo.equals("true") ? true : false);

            if (idVetado > 0) {
                cargarVetado(vo);
                String metodo = ";abrirDialogoCrearVetado();";
                PrimeFaces.current().executeScript(metodo);
            }
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public void deleteVetado(int idVetado) {
        try {
            if (idVetado > 0) {
                ProveedorVetado vetado = proveedorVetadoImpl.find(idVetado);
                if (vetado != null) {
                    vetado.setEliminado(Constantes.BOOLEAN_TRUE);
                    vetado.setFechaModifico(new Date());
                    vetado.setHoraModifico(new Date());
                    vetado.setModifico(new Usuario(sesion.getUsuarioVo().getId()));
                    proveedorVetadoImpl.edit(vetado);
                }
                refrescarTabla();
            }
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }

    }

    public void crearVetado() {
        try {
            setNewVetado(new ProveedorVetadoVO());

            String metodo = ";abrirDialogoCrearVetado();";
            PrimeFaces.current().executeScript(metodo);
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public void guardarVetado() {
        try {
            saveVetado();;
            refrescarTabla();
            setNewVetado(null);
            String metodo = ";cerrarDialogoCrearVetado();";
            PrimeFaces.current().executeScript(metodo);
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public void cargarVetado(ProveedorVetadoVO vetado) {
        setNewVetado(vetado);
    }

    private void saveVetado() {
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
}
