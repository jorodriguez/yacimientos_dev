/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.ihsa.proveedor.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;


import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import sia.constantes.Constantes;
import sia.ihsa.admin.Sesion;
import sia.modelo.PvArea;
import sia.modelo.proveedor.Vo.ContactoProveedorVO;
import sia.servicios.proveedor.impl.ContactoProveedorImpl;
import sia.servicios.proveedor.impl.PvAreaServicioImpl;
import org.primefaces.PrimeFaces;
import javax.inject.Named;
import javax.inject.Inject;

/**
 *
 * @author mluis
 */
@Named(value = "notificacionBean")
@ViewScoped
public class NotificacionBean implements Serializable {

    @Inject
    Sesion sesion;

    @Inject
    private ContactoProveedorImpl contactoProveedorImpl;
    @Inject
    private PvAreaServicioImpl pvAreaServicioImpl;
    
    private List<ContactoProveedorVO> listaContacto;
    private ContactoProveedorVO contactoProveedorVO;
    private List<SelectItem> listaTipoContacto;
    
    

    @PostConstruct
    public void iniciar() {
        contactoProveedorVO = new ContactoProveedorVO();
        listaContacto = contactoProveedorImpl.traerContactoPorProveedor(sesion.getProveedorVo().getIdProveedor(), Constantes.CERO);
        //
        List<PvArea> lista = pvAreaServicioImpl.findAll();
        listaTipoContacto = new ArrayList<>();
        for (PvArea pvArea : lista) {
            listaTipoContacto.add(new SelectItem(pvArea.getId(), pvArea.getNombre()));
        }
    }

    public void editar(int indice) {        
        listaContacto.get(indice).setEditar(Constantes.TRUE);
    }

    public void completarEditar(int indice) {
        
        //
        contactoProveedorImpl.actualizarContacto(listaContacto.get(indice).getIdContactoProveedor(), listaContacto.get(indice).getNombre(), listaContacto.get(indice).getCorreo(), listaContacto.get(indice).getTelefono(), listaContacto.get(indice).isNotifica(), sesion.getProveedorVo().getRfc());
        //
        listaContacto.get(indice).setEditar(Constantes.FALSE);
    }

    public void eliminar(int indice) {
        
        //
        contactoProveedorImpl.eliminarContacto(listaContacto.get(indice).getIdContactoProveedor(), sesion.getProveedorVo().getRfc());
        listaContacto.remove(indice);
    }

    public void inicioRegistroContacto() {
        contactoProveedorVO = new ContactoProveedorVO();
        PrimeFaces.current().executeScript(";$(dialogoRegContacto).modal('show');");
    }

    public void registroContacto() {
        List<ContactoProveedorVO> lc = new ArrayList<>();
        lc.add(contactoProveedorVO);
        contactoProveedorImpl.guardar(sesion.getProveedorVo().getIdProveedor(), lc, sesion.getProveedorVo().getRfc());
        //
        listaContacto = contactoProveedorImpl.traerContactoPorProveedor(sesion.getProveedorVo().getIdProveedor(), Constantes.CERO);

    }

    public void cerrarRegistroContacto() {
        contactoProveedorVO = new ContactoProveedorVO();
        PrimeFaces.current().executeScript(";$(dialogoRegContacto).modal('hide');");
    }

    /**
     * @param sesion the sesion to set
     */
    public void setSesion(Sesion sesion) {
        this.sesion = sesion;
    }

    /**
     * @return the listaContacto
     */
    public List<ContactoProveedorVO> getListaContacto() {
        return listaContacto;
    }

    /**
     * @param listaContacto the listaContacto to set
     */
    public void setListaContacto(List<ContactoProveedorVO> listaContacto) {
        this.listaContacto = listaContacto;
    }

    /**
     * @return the contactoProveedorVO
     */
    public ContactoProveedorVO getContactoProveedorVO() {
        return contactoProveedorVO;
    }

    /**
     * @param contactoProveedorVO the contactoProveedorVO to set
     */
    public void setContactoProveedorVO(ContactoProveedorVO contactoProveedorVO) {
        this.contactoProveedorVO = contactoProveedorVO;
    }

    /**
     * @return the listaTipoContacto
     */
    public List<SelectItem> getListaTipoContacto() {
        return listaTipoContacto;
    }

    /**
     * @param listaTipoContacto the listaTipoContacto to set
     */
    public void setListaTipoContacto(List<SelectItem> listaTipoContacto) {
        this.listaTipoContacto = listaTipoContacto;
    }
    
}
