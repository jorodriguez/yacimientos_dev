/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.bean.backing;

import java.io.Serializable;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;

import org.primefaces.PrimeFaces;
import sia.administracion.proveedor.bean.model.ProveedorVetadoBeanModel;
import sia.servicios.sistema.vo.ProveedorVetadoVO;
import sia.sistema.bean.support.FacesUtils;
import sia.util.UtilLog4j;

/**
 *
 * @author ihsa
 */
@ManagedBean(name = "proveedorVetadoBean")
@RequestScoped
public class ProveedorVetadoBean implements Serializable {

    @ManagedProperty(value = "#{proveedorVetadoBeanModel}")
    private ProveedorVetadoBeanModel proveedorVetadoBeanModel;

    private ProveedorVetadoVO newVetado;

    /**
     * Creates a new instance of ProveedorVetadoBean
     */
    public ProveedorVetadoBean() {
    }

    @PostConstruct
    public void iniciar() {
        getListaVetados();
    }

    public List<ProveedorVetadoVO> getListaVetados() {
        return this.getProveedorVetadoBeanModel().getLstVetado();
    }

    public ProveedorVetadoVO getNewVetado() {
        return this.proveedorVetadoBeanModel.getNewVetado();
    }

    public void setNewVetado(ProveedorVetadoVO newVetado) {
        this.proveedorVetadoBeanModel.setNewVetado(newVetado);
    }

    public ProveedorVetadoBeanModel getProveedorVetadoBeanModel() {
        return proveedorVetadoBeanModel;
    }

    public void setProveedorVetadoBeanModel(ProveedorVetadoBeanModel proveedorVetadoBeanModel) {
        this.proveedorVetadoBeanModel = proveedorVetadoBeanModel;
    }

    
     public void editarVetado() {
        try {
          int idVetado = Integer.parseInt(FacesUtils.getRequestParameter("idVetado"));
          String rfc = FacesUtils.getRequestParameter("rfc");
          String descr = FacesUtils.getRequestParameter("descr");
          String activo = FacesUtils.getRequestParameter("activo");
          String nom = FacesUtils.getRequestParameter("nombre");
          ProveedorVetadoVO vo= new ProveedorVetadoVO();
          vo.setRfc(rfc.toUpperCase());
          vo.setDescripcion(descr.toUpperCase());
          vo.setNombre(nom.toUpperCase());
          vo.setId(idVetado);
          vo.setActivo(activo.equals("true")?true:false);

            if (idVetado > 0) {
                cargarVetado(vo);
                String metodo = ";abrirDialogoCrearVetado();";
                PrimeFaces.current().executeScript(metodo);
            }
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }
    
    
 
    public void deleteVetado() {
        try {
            int idVetado = Integer.parseInt(FacesUtils.getRequestParameter("idVetado"));
            this.proveedorVetadoBeanModel.desactivarVetado(idVetado);
            this.refrescarTabla();
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }

    }

    public void crearVetado() {
        try {
            this.setNewVetado(new ProveedorVetadoVO());

            String metodo = ";abrirDialogoCrearVetado();";
            PrimeFaces.current().executeScript(metodo);
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }

    public void refrescarTabla() {
        this.proveedorVetadoBeanModel.refrescarTabla();
    }

    public void guardarVetado() {
        try {
            this.proveedorVetadoBeanModel.guardarVetado();
            this.refrescarTabla();
            this.setNewVetado(null);
            String metodo = ";cerrarDialogoCrearVetado();";
            PrimeFaces.current().executeScript( metodo);
        } catch (Exception ex) {
            UtilLog4j.log.fatal(this, ex);
        }
    }
    
     public void cargarVetado(ProveedorVetadoVO vo){
        this.proveedorVetadoBeanModel.cargarVetado(vo);
    }

}
