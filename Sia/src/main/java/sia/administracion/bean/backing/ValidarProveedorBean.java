/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.bean.backing;

import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.primefaces.PrimeFaces;
import sia.constantes.Constantes;
import sia.modelo.proveedor.Vo.ProveedorVo;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;
import sia.util.ProveedorEnum;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@ManagedBean(name = "validarProveedorBean")
@ViewScoped
public class ValidarProveedorBean {

    /**
     * Creates a new instance of ValidarProveedorBean
     */
    public ValidarProveedorBean() {
    }
    @EJB
    ProveedorServicioImpl proveedorImpl;
    @EJB
    ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
    //
    private List listaProveedor;
    private ProveedorVo proveedorVo;
    private String motivoCanvelacion;
    //Sistema
    @ManagedProperty(value = "#{sesion}")
    private Sesion sesion;

    @PostConstruct
    public void iniciar() {
        proveedorVo = new ProveedorVo();
        listaProveedor = proveedorImpl.traerProveedorEstatus(sesion.getUsuario().getId(), ProveedorEnum.EN_PROCESO.getId(), Constantes.CERO);
    }

    public  void buscarPorCampo(){
        
    }
    public void activarProveedor() {
        try {
            proveedorImpl.activarProveedor(sesion.getUsuario().getId(), proveedorVo, sesion.getUsuario().getApCampo().getId());
            listaProveedor.remove(proveedorVo);
        } catch (Exception e) {
            UtilLog4j.log.error(e);
        }
    }

    public void iniciarDevProveedor() {
        PrimeFaces.current().executeScript("$(dialogoDevolver).modal('show');");
    }

    public void devolverProveedor() {
        try {
            System.out.println("motivo : " + motivoCanvelacion);
            proveedorImpl.devolverProveedor(sesion.getUsuario(), proveedorVo, motivoCanvelacion);
            listaProveedor.remove(proveedorVo);
        } catch (Exception e) {
            FacesUtils.addErrorMessage("Ocurrio un error, favor de reportarlo al equipo del SIA (soportesia@ihsa.mx)");
            UtilLog4j.log.error(e);
        }
        PrimeFaces.current().executeScript("$(dialogoDevolver).modal('hide');");
    }

    /**
     * @return the listaProveedor
     */
    public List getListaProveedor() {
        return listaProveedor;
    }

    /**
     * @param listaProveedor the listaProveedor to set
     */
    public void setListaProveedor(List listaProveedor) {
        this.listaProveedor = listaProveedor;
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

    /**
     * @return the proveedorVo
     */
    public ProveedorVo getProveedorVo() {
        return proveedorVo;
    }

    /**
     * @param proveedorVo the proveedorVo to set
     */
    public void setProveedorVo(ProveedorVo proveedorVo) {
        this.proveedorVo = proveedorVo;
    }

    /**
     * @return the motivoCanvelacion
     */
    public String getMotivoCanvelacion() {
        return motivoCanvelacion;
    }

    /**
     * @param motivoCanvelacion the motivoCanvelacion to set
     */
    public void setMotivoCanvelacion(String motivoCanvelacion) {
        this.motivoCanvelacion = motivoCanvelacion;
    }


}
