/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSF/JSFManagedBean.java to edit this template
 */
package sia.administracion.bean.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.proveedor.Vo.ProveedorVo;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.orden.impl.OcCampoProveedorImpl;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;
import sia.sistema.bean.backing.Sesion;
import sia.sistema.bean.support.FacesUtils;
import sia.util.ProveedorEnum;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Named(value = "proveedorCampoBean")
@ViewScoped
public class ProveedorCampoBean implements Serializable {

    /**
     * Creates a new instance of ProveedorCampoBean
     */
    public ProveedorCampoBean() {
    }

    @Inject
    private Sesion sesion;
    @Getter
    @Setter
    private List<ProveedorVo> lista;
    @Getter
    @Setter
    private int idCampo;
    @Getter
    @Setter
    private int idProveedor;
    @Getter
    @Setter
    private String proveedorSeleccionado;
    @Getter
    @Setter
    private List<SelectItem> campos;

    @Inject
    private OcCampoProveedorImpl ocCampoProveedorLocal;
    @Inject
    ProveedorServicioImpl proveedorImpl;
    @Inject
    ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;

    @PostConstruct
    public void iniciar() {
        lista = new ArrayList<>();
        campos = new ArrayList<>();
        idCampo = sesion.getUsuarioVo().getIdCampo();
        llenarCampos();
        traerProveedorCampo();
        idProveedor = 0;
    }

    private void llenarCampos() {
        try {
            List<CampoUsuarioPuestoVo> le = apCampoUsuarioRhPuestoImpl.getAllPorUsurio(sesion.getUsuarioVo().getId());
            le.stream().forEach(ep -> {
                campos.add(new SelectItem(ep.getIdCampo(), ep.getCampo()));
            });
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al recuperar la lista de empresa por usuario");
        }
    }

    public void eliminarProveedorCampo(int idProvC) {
        ocCampoProveedorLocal.eliminarProveedorCampo(idProvC, sesion.getUsuarioVo().getId());
        traerProveedorCampo();
    }

    public void traerProveedorCampo() {
        setLista((ocCampoProveedorLocal.traerProveedor(getIdCampo())));
    }

    public void agregarProveedor() {
        if (idProveedor > 0) {
        ocCampoProveedorLocal.agregarProveedor(getIdCampo(), getIdProveedor(), sesion.getUsuarioVo().getId());
            traerProveedorCampo();
            FacesUtils.addInfoMessage("Se registró el proveedor.  ");
            idProveedor = 0;
        } else {
            FacesUtils.addInfoMessage("Se registró el proveedor.  ");
        }
    }

    private boolean estaProveedorCampo() {
        return ocCampoProveedorLocal.buscarProveedorCampo(getIdCampo(), getIdProveedor());
    }

    public void cambiarCampoSeleccionado() {
        traerProveedorCampo();
    }

    public List<String> completaProveedor(String query) {
        return proveedorImpl.traerRfcNombreLikeProveedorQueryNativo(query, sesion.getUsuarioVo().getRfcEmpresa(), ProveedorEnum.ACTIVO.getId());

    }

    public void llenarDatosProveedor() {
        ProveedorVo proveedorVo;
        String[] cad = proveedorSeleccionado.split("/");
        proveedorVo = proveedorImpl.traerProveedorPorRFC(cad[0].trim());
        if (proveedorVo != null) {
            idProveedor = proveedorVo.getIdProveedor();
            traerProveedorCampo();
        }
        proveedorSeleccionado = "";
    }

}
