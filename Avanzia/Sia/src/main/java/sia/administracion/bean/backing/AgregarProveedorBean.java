/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.bean.backing;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.primefaces.PrimeFaces;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.file.UploadedFile;
import sia.catalogos.bean.backing.UsuarioBean;
import sia.constantes.Constantes;
import sia.modelo.proveedor.Vo.ContactoProveedorVO;
import sia.modelo.proveedor.Vo.ProveedorVo;
import sia.servicios.proveedor.impl.ContactoProveedorImpl;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;
import sia.sistema.bean.support.FacesUtils;
import sia.util.ProveedorEnum;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
@Named
@ViewScoped
public class AgregarProveedorBean implements Serializable {

    /**
     * Creates a new instance of AgregarProveedorBean
     */
    public AgregarProveedorBean() {
    }
    @Inject
    private UsuarioBean usuarioBean;
    //
    @Inject
    private ProveedorServicioImpl proveedorImpl;
    @Inject
    private ContactoProveedorImpl contactoProveedorImpl;

    private List<ProveedorVo> listaProveedor;
    private ProveedorVo proveedor;
    private String nombre;
    private List<ContactoProveedorVO> listaContactoProveedor;

    private UploadedFile fileUpload;

    @PostConstruct
    public void iniciar() {
        proveedor = new ProveedorVo();
        listaProveedor = new ArrayList<>();
        if (usuarioBean.getUsuarioVO() != null) {
            listaProveedor = proveedorImpl.traerProveedorEstatus(usuarioBean.getUsuarioVO().getId(), ProveedorEnum.ACTIVO.getId(), 80);
        }
    }

    public void traerTodos() {
        listaProveedor = proveedorImpl.traerProveedorEstatus(usuarioBean.getUsuarioVO().getId(), ProveedorEnum.ACTIVO.getId(), 80);
    }

    public void buscarProveedor() {
        if (getNombre().length() > 3) {
            listaProveedor = proveedorImpl.traerProveedorPorParteNombre(getNombre(), usuarioBean.getUsuarioVO().getId(), ProveedorEnum.ACTIVO.getId());
        } else {
            FacesUtils.addInfoMessage("Agregue más información del proveedor.");
        }

    }

    public void subirArchivo(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        try {
            if (file.getContent() != null) {
                File fileTmp = new File("/tpm/" + file.getFileName());
                try (FileOutputStream outputStream = new FileOutputStream(fileTmp)) {
                    outputStream.write(file.getContent());
                }
                proveedorImpl.guardarProveedorDesdeArchivo(fileTmp, usuarioBean.getUsuarioVO().getId());
                //
                listaProveedor = proveedorImpl.traerProveedorEstatus(usuarioBean.getUsuarioVO().getId(), ProveedorEnum.ACTIVO.getId(), 80);
            }

            FacesUtils.addInfoMessage("Se agregaron los proveedores");
        } catch (Exception e) {
            FacesUtils.addErrorMessage("Ocurrio un error: " + e.getMessage());
            UtilLog4j.log.fatal(this, "+ + + ERROR + + +" + e.getMessage(), e);
        }
    }

    public void seleccionar(SelectEvent event) {
        proveedor = (ProveedorVo) event.getObject();
        listaContactoProveedor = contactoProveedorImpl.traerTodosContactoPorProveedor(proveedor.getIdProveedor());
        //

        PrimeFaces.current().executeScript("$(dialogoContacto).modal('show');");
    }

    public void editarProveedor() {
        proveedor.setEditar(Constantes.BOOLEAN_TRUE);
    }

    public void guardarDatosGenerales() {
        proveedorImpl.modificarDatos(proveedor, usuarioBean.getUsuarioVO().getId());
        proveedor.setEditar(Constantes.BOOLEAN_FALSE);
        //
        proveedor = proveedorImpl.traerProveedor(proveedor.getIdProveedor(), usuarioBean.getUsuarioVO().getRfcEmpresa());
    }

    public void iniciarModificarContacto() {
        int id = Integer.parseInt(FacesUtils.getRequestParameter("idContacto"));
        listaContactoProveedor.get(id).setEditar(Constantes.BOOLEAN_TRUE);
    }

    public void modificarContacto() {
        int id = Integer.parseInt(FacesUtils.getRequestParameter("idContacto"));
        ContactoProveedorVO contactoProveedorVO = listaContactoProveedor.get(id);
        contactoProveedorImpl.actualizarContacto(contactoProveedorVO.getIdContactoProveedor(), contactoProveedorVO.getNombre(),
                contactoProveedorVO.getCorreo(), contactoProveedorVO.getTelefono(), Boolean.FALSE, usuarioBean.getUsuarioVO().getId());
        listaContactoProveedor.get(id).setEditar(Constantes.BOOLEAN_FALSE);

    }

    public void eliminarContacto() {
        int id = Integer.parseInt(FacesUtils.getRequestParameter("idContacto"));
        contactoProveedorImpl.eliminarContacto(listaContactoProveedor.get(id).getIdContactoProveedor(), usuarioBean.getUsuarioVO().getId());
        listaContactoProveedor.remove(id);
    }

    public void cerrarContacto() {
        PrimeFaces.current().executeScript("$(dialogoContacto).modal('hide');");

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
     * @return the proveedor
     */
    public ProveedorVo getProveedor() {
        return proveedor;
    }

    /**
     * @param proveedor the proveedor to set
     */
    public void setProveedor(ProveedorVo proveedor) {
        this.proveedor = proveedor;
    }

    /**
     * @return the listaContactoProveedor
     */
    public List<ContactoProveedorVO> getListaContactoProveedor() {
        return listaContactoProveedor;
    }

    /**
     * @param listaContactoProveedor the listaContactoProveedor to set
     */
    public void setListaContactoProveedor(List<ContactoProveedorVO> listaContactoProveedor) {
        this.listaContactoProveedor = listaContactoProveedor;
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
}
