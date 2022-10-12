/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.compra.catalogos.bean.backing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.PrimeFaces;
import org.primefaces.event.SelectEvent;
import sia.compra.requisicion.bean.backing.FacesUtilsBean;
import sia.compra.requisicion.bean.backing.UsuarioBean;
import sia.modelo.proveedor.Vo.ProveedorVo;
import sia.servicios.convenio.impl.ConvenioImpl;
import sia.servicios.convenio.impl.CvConvenioAdjuntoImpl;
import sia.servicios.orden.impl.AutorizacionesOrdenImpl;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;
import sia.util.ProveedorEnum;

/**
 *
 * @author mluis
 */
@Named (value = "consultaProveedorBean")
@ViewScoped
public class ConsultaProveedorBean implements Serializable {

    /**
     * Creates a new instance of ConsultaProveedorBean
     */
    public ConsultaProveedorBean() {
    }
    @Inject
    private UsuarioBean usuarioBean;
    //
    @Inject
    private ProveedorServicioImpl  proveedorImpl;
    @Inject
    private CvConvenioAdjuntoImpl cvConvenioAdjuntoImpl;
    @Inject
    private ConvenioImpl convenioImpl;
    @Inject
    private AutorizacionesOrdenImpl autorizacionesOrdenImpl;

    private List<ProveedorVo> listaProveedor;
    private ProveedorVo proveedorVo;
    private String nombre;
    private Map<String, List> proveedorDatos;

    @PostConstruct
    public void iniciar() {
        setProveedorVo(new ProveedorVo());
        setListaProveedor(new ArrayList<ProveedorVo>());
        if (usuarioBean.getUsuarioConectado() != null) {
            setListaProveedor(proveedorImpl.traerProveedorEstatus(usuarioBean.getUsuarioConectado().getId(), ProveedorEnum.ACTIVO.getId(), 80));
        }
    }

    public void seleccionar(SelectEvent event) {
        proveedorVo = (ProveedorVo) event.getObject();
        //
        proveedorVo = proveedorImpl.traerProveedor(proveedorVo.getIdProveedor(), usuarioBean.getUsuarioConectado().getApCampo().getCompania().getRfc());
        //
        proveedorDatos = new HashMap<>();
        proveedorDatos.put("contratos", convenioImpl.traerConveniosPorProveedor(proveedorVo.getIdProveedor(), usuarioBean.getUsuarioConectado().getApCampo().getId()));
        proveedorDatos.put("compras", autorizacionesOrdenImpl.traerOrdenPorProveedor(proveedorVo.getIdProveedor(), usuarioBean.getUsuarioConectado().getApCampo().getId()));
//

        PrimeFaces.current().executeScript("regresar('divDatosGenerales', 'divGeneral', '', '')");
    }

    public void traerTodos() {
        listaProveedor = proveedorImpl.traerProveedorEstatus(usuarioBean.getUsuarioConectado().getId(), ProveedorEnum.ACTIVO.getId(), 80);
    }

    public void buscarProveedor() {
        if (nombre.length() > 3) {
            listaProveedor = proveedorImpl.traerProveedorPorParteNombre(nombre, usuarioBean.getUsuarioConectado().getId(), ProveedorEnum.ACTIVO.getId());
        } else {
            FacesUtilsBean.addInfoMessage("Agregue más información del proveedor.");
        }
    }

    public void llenarListaConvenio() {
        int idC = Integer.parseInt(FacesUtilsBean.getRequestParameter("id"));
        proveedorDatos.put("archivo", cvConvenioAdjuntoImpl.traerPorConvenio(idC));
        PrimeFaces.current().executeScript("$(dlgContratos).modal('show');");
    }

    /**
     * @param usuarioBean the usuarioBean to set
     */
    public void setUsuarioBean(UsuarioBean usuarioBean) {
        this.usuarioBean = usuarioBean;
    }

    /**
     * @return the listaProveedor
     */
    public List<ProveedorVo> getListaProveedor() {
        return listaProveedor;
    }

    /**
     * @param listaProveedor the listaProveedor to set
     */
    public void setListaProveedor(List<ProveedorVo> listaProveedor) {
        this.listaProveedor = listaProveedor;
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
     * @return the nombre
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * @param nombre the nombre to set
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * @return the proveedorDatos
     */
    public Map<String, List> getProveedorDatos() {
        return proveedorDatos;
    }

    /**
     * @param proveedorDatos the proveedorDatos to set
     */
    public void setProveedorDatos(Map<String, List> proveedorDatos) {
        this.proveedorDatos = proveedorDatos;
    }
}
