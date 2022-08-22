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
import javax.inject.Inject;

import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.primefaces.PrimeFaces;
import sia.constantes.Constantes;
import sia.modelo.proveedor.Vo.ProveedorSinCartaIntencionVo;
import sia.modelo.proveedor.Vo.ProveedorVo;
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
@Named(value = "proveedorSinCartaBean")
@ViewScoped
public class ProveedorSinCartaBean implements Serializable {

    /**
     * Creates a new instance of ProveedorSinCartaBean
     */
    public ProveedorSinCartaBean() {
    }

    @Inject
    private Sesion sesion;

    @Inject
    PvProveedorSinCartaIntencionImpl proveedorSinCartaIntencionLocal;
    @Inject
    ApCampoImpl campoImpl;
    @Inject
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
    @Getter
    @Setter
    private String proveedorSeleccionado;

    @PostConstruct
    public void init() {
        proveedores = new ArrayList<ProveedorSinCartaIntencionVo>();
        //
        campos = new ArrayList<SelectItem>();
        camposProveedores = new ArrayList<SelectItem>();
        campoId = Constantes.CERO;
        //
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

    public List<String> completaProveedor(String query) {
        return proveedorImpl.traerRfcNombreLikeProveedorQueryNativo(query, sesion.getUsuarioVo().getRfcEmpresa(), ProveedorEnum.ACTIVO.getId());

    }

    public void llenarDatosProveedor() {
        ProveedorVo proveedorVo;
        String[] cad = proveedorSeleccionado.split("/");
        proveedorVo = proveedorImpl.traerProveedorPorRFC(cad[0].trim());
        if (proveedorVo != null) {
            proveedorId = proveedorVo.getIdProveedor();
        }
        proveedorSeleccionado = "";
    }

    private void llearProveedores(int campoId) {
        if (campoId == 0) {
            proveedores = proveedorSinCartaIntencionLocal.traerTodos();
        } else {
            proveedores = proveedorSinCartaIntencionLocal.traerPorCampo(campoId);
        }
    }

    public void cambiarCampo() {
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

    public void elimianar(int ind) {
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
