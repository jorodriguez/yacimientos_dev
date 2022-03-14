/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.proveedor.bean.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import javax.faces.event.AjaxBehaviorEvent;
import javax.faces.model.SelectItem;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import sia.constantes.Constantes;
import sia.modelo.proveedor.Vo.ProveedorSinCartaIntencionVo;
import sia.modelo.vo.ApCampoVo;
import sia.servicios.campo.nuevo.impl.ApCampoImpl;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;
import sia.servicios.proveedor.impl.PvProveedorSinCartaIntencionImpl;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;
import sia.util.ProveedorEnum;

/**
 *
 * @author mluis
 */
@ManagedBean(name = "proveedorSinCartaBean")
@ViewScoped
public class ProveedorSinCartaBean implements Serializable {

    /**
     * Creates a new instance of ProveedorSinCartaBean
     */
    public ProveedorSinCartaBean() {
    }

    @ManagedProperty(value = "#{sesion}")
    private Sesion sesion;

    @EJB
    PvProveedorSinCartaIntencionImpl proveedorSinCartaIntencionLocal;
    @EJB
    ApCampoImpl campoImpl;
    @EJB
    ProveedorServicioImpl proveedorImpl;
    //
    @Getter
    @Setter
    private List<ProveedorSinCartaIntencionVo> proveedores;
    @Getter
    @Setter
    private ProveedorSinCartaIntencionVo proveedorVo;
    @Getter
    @Setter
    private List<SelectItem> campos;
    @Getter
    @Setter
    private List<SelectItem> camposProveedores;
    @Getter
    @Setter
    private int campoProveedorId;
    @Getter
    @Setter
    private int campoId;
    @Getter
    @Setter
    private int proveedorId;
    @Getter
    @Setter
    private boolean aplicarTodosCampos;

    @PostConstruct
    public void init() {
        proveedores = new ArrayList<ProveedorSinCartaIntencionVo>();
        //
        campos = new ArrayList<SelectItem>();
        camposProveedores = new ArrayList<SelectItem>();
        campoId = Constantes.CERO;
        //
        llenarJson();
        llearProveedores(campoId);
        //
        List<ApCampoVo> camposUser = campoImpl.traerCampoConCartaIntencion();
        for (ApCampoVo cpVo : camposUser) {
            campos.add(new SelectItem(cpVo.getId(), cpVo.getNombre()));
        }
        llenarCamposTabla();
        //
    }

    private void llenarCamposTabla() {
        List<ApCampoVo> cps = proveedorSinCartaIntencionLocal.traerDistintosCampos();
        camposProveedores.clear();
        for (ApCampoVo cp : cps) {
            camposProveedores.add(new SelectItem(cp.getId(), cp.getNombre()));
        }

    }

    public void llenarJson() {
        String jsonProveedores
                = proveedorImpl.traerProveedorPorCompaniaSesionJson("'" + sesion.getUsuario().getApCampo().getCompania().getRfc() + "'",
                        ProveedorEnum.ACTIVO.getId());
        //
        PrimeFaces.current().executeScript(";setJson(" + jsonProveedores + ");");
    }

    private void llearProveedores(int campoId) {
        if (campoId == 0) {
            proveedores = proveedorSinCartaIntencionLocal.traerTodos();
        } else {
            proveedores = proveedorSinCartaIntencionLocal.traerPorCampo(campoId);
        }
    }

    public void cambiarCampo(AjaxBehaviorEvent event) {
        llearProveedores(campoProveedorId);
    }

    public void inicioGuardar() {
        campoId = Constantes.CERO;
        proveedorId = Constantes.CERO;
        aplicarTodosCampos = false;

        PrimeFaces.current().executeScript("$(dialogoAgregarProveedor).modal('show');");
    }

    public void cerrarGuardar() {
        campoId = Constantes.CERO;
        proveedorId = Constantes.CERO;
        PrimeFaces.current().executeScript("$(dialogoAgregarProveedor).modal('hide');");
    }

    public void guardar() {
        if (proveedorId > Constantes.CERO) {
            if (aplicarTodosCampos) {
                for (SelectItem campo : campos) {
                    guardarProveedorCampo(proveedorId, Integer.parseInt(campo.getValue().toString()));
                }
                PrimeFaces.current().executeScript("$(dialogoAgregarProveedor).modal('hide');");
                llearProveedores(campoProveedorId);
                llenarCamposTabla();
                aplicarTodosCampos = false;
                FacesUtils.addErrorMessage("Se agregó el proveedor a todos los campos");
            } else if (campoId > Constantes.CERO) {
                guardarProveedorCampo(proveedorId, campoId);
                PrimeFaces.current().executeScript("$(dialogoAgregarProveedor).modal('hide');");
                llearProveedores(campoProveedorId);
                llenarCamposTabla();
                FacesUtils.addErrorMessage("Se agregó el proveedor");
            } else {
                FacesUtils.addErrorMessage("Seleccione un campo");
            }
        } else {
            FacesUtils.addErrorMessage("Seleccione un proveedor");
        }
    }

    private void guardarProveedorCampo(int provId, int cpId) {
        proveedorSinCartaIntencionLocal.guardar(sesion.getUsuario().getId(), provId, cpId);

    }

    public void elimianar() {
        int ind = Integer.parseInt(FacesUtils.getRequestParameter("indice"));
        ProveedorSinCartaIntencionVo ppVo = proveedores.get(ind);
        proveedorSinCartaIntencionLocal.eliminar(sesion.getUsuario().getId(), ppVo.getId());
//
        llearProveedores(campoProveedorId);
        llenarCamposTabla();
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

}
