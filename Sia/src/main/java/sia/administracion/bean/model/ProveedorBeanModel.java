/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.administracion.bean.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import javax.faces.bean.CustomScoped;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;
import sia.catalogos.bean.backing.UsuarioBean;
import sia.constantes.Constantes;
import sia.modelo.Proveedor;
import sia.modelo.campo.usuario.puesto.vo.CampoUsuarioPuestoVo;
import sia.modelo.proveedor.Vo.ContactoProveedorVO;
import sia.modelo.proveedor.Vo.ProveedorVo;
import sia.modelo.vo.CompaniaVo;
import sia.notificaciones.proveedor.impl.NotificacionProveedorImpl;
import sia.servicios.campo.nuevo.impl.ApCampoUsuarioRhPuestoImpl;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.orden.impl.OcCampoProveedorImpl;
import sia.servicios.orden.impl.OrdenImpl;
import sia.servicios.proveedor.impl.ContactoProveedorImpl;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;
import sia.servicios.proveedor.impl.PvProveedorCompaniaImpl;
import sia.util.ProveedorEnum;
import sia.util.UtilLog4j;

/**
 *
 * @author MLUIS
 */
@ManagedBean(name = "proveedorBeanModel")
@CustomScoped(value = "#{window}")
public class ProveedorBeanModel {

    /**
     * Creates a new instance of ProveedorBeanModel
     */
    @ManagedProperty(value = "#{usuarioBean}")
    private UsuarioBean usuarioBean;
    //
    @EJB
    private ProveedorServicioImpl proveedorImpl;
    @EJB
    private PvProveedorCompaniaImpl proveedorCompaniaImpl;
    @EJB
    private OrdenImpl ordenImpl;
    @EJB
    private UsuarioImpl usuarioImpl;
    @EJB
    private ApCampoUsuarioRhPuestoImpl apCampoUsuarioRhPuestoImpl;
    @EJB
    private OcCampoProveedorImpl ocCampoProveedorLocal;
    @EJB
    private ContactoProveedorImpl contactoProveedorImpl;
    @EJB
    private NotificacionProveedorImpl notificacionImpl;

    public ProveedorBeanModel() {
    }
    private String rfc;
    private String rfcCompania;
    private String correo;
    private String numeroReferencia;
//
    private int idProveedor;
    private String nombreProveedor;
    private Map<String, Boolean> filaSeleccionada = new HashMap<String, Boolean>();
    //
    private int idRelacion;
    private CompaniaVo companiaVo;
    private DataModel lista;
    private DataModel listaContacto;
    private int idCampo;
    private int idContacto;
    private String telefono;
    private String contacto;
    private ProveedorVo proveedorVo;

    public void irAgregarProveedor() {
        setRfcCompania(usuarioBean.getUsuarioVO().getRfcEmpresa());
    }

    public void irRelacionProveedorCompania() {
        setRfcCompania(usuarioBean.getUsuarioVO().getRfcEmpresa());
    }

    public String traerProveedorJson() {
        StringBuilder sb = new StringBuilder();
        List<CompaniaVo> lc = usuarioImpl.traerCompaniaPorUsuario(usuarioBean.getUsuarioVO().getId());
        for (CompaniaVo cmp : lc) {
            if (sb.length() == 0) {
                sb.append("'").append(cmp.getRfcCompania()).append("'");
            } else {
                sb.append(",");
                sb.append("'").append(cmp.getRfcCompania()).append("'");
            }

        }
        return proveedorImpl.traerProveedorPorCompaniaSesionJson(sb.toString(), ProveedorEnum.ACTIVO.getId());
    }

    public List<CompaniaVo> listaEmpresa() {
        try {
            return usuarioImpl.traerCompaniaPorUsuario(usuarioBean.getUsuarioVO().getId());
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al recuperar la empresa por usario " + e.getMessage());
            return null;
        }
    }

    public List<SelectItem> listaCompaniaPorUsuario() {
        try {
            List<SelectItem> ls = null;
            if (getIdProveedor() > 0) {
                ls = new ArrayList<SelectItem>();
                for (CompaniaVo c : usuarioImpl.traerCompaniaPorUsuario(usuarioBean.getUsuarioVO().getId())) {
                    ls.add(new SelectItem(c.getRfcCompania(), c.getNombre()));
                }
            }
            return ls;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al recuperar la empresa por proveedor " + e.getMessage());
            return null;
        }
    }

    public DataModel listaCompania() {
        try {
            if (getIdProveedor() > 0) {
                setLista(new ListDataModel(proveedorCompaniaImpl.traerCompaniaPorProveedor(getIdProveedor())));
                listaContactoPorPorveedor();
                setProveedorVo(proveedorImpl.traerProveedorPorRfc(null, null, getIdProveedor(), usuarioBean.getUsuarioVO().getRfcEmpresa()));
                return getLista();
            }
            return null;
        } catch (Exception e) {
            UtilLog4j.log.fatal(this, "Ocurrio un error al recuperar la empresa por proveedor " + e.getMessage());
            return null;
        }
    }
    
    public boolean enviarArchivosPortal() {
        boolean ret = false;
        if(this.getProveedorVo() != null && this.getProveedorVo().getIdProveedor() > 0){
           ret = notificacionImpl.notificacionArchivosPortal(this.getProveedorVo(), usuarioBean.getUsuarioVO().getMail(), Constantes.VACIO);
        }        
        return ret;
    }

    public Proveedor buscarProveedor() {
        return proveedorImpl.traerPorRFC(getRfc(), "");
    }

    public boolean buscarRelacionProveedorCompania() {
        UtilLog4j.log.info(this, "pro b:  " + getIdProveedor());
        UtilLog4j.log.info(this, "rfcc b:   " + getRfcCompania());
        return proveedorCompaniaImpl.buscarRelacionProveedorCompania(getIdProveedor(), getRfcCompania());
    }

    public void agregarRelacion() {
        UtilLog4j.log.info(this, "pro:  " + getIdProveedor());
        UtilLog4j.log.info(this, "rfcc:  " + getRfcCompania());
        UtilLog4j.log.info(this, "num:  " + getNumeroReferencia());
        proveedorCompaniaImpl.guardarRelacionProveedor(getIdProveedor(), getRfcCompania(), getNumeroReferencia(), usuarioBean.getUsuarioVO().getId());
        listaCompania();
    }

    public void completarModificarRelProvCompania() {
        List<CompaniaVo> lc = (List<CompaniaVo>) getLista().getWrappedData();
        CompaniaVo cvo = lc.get(getIdRelacion());
        lc.get(getIdRelacion()).setEditar(false);
        setLista(new ListDataModel(lc));
        proveedorCompaniaImpl.modificarRel(usuarioBean.getUsuarioVO().getId(), cvo);
    }

    public boolean buscarProveedorPorId() {
        List<CompaniaVo> lc = (List<CompaniaVo>) getLista().getWrappedData();
        CompaniaVo cvo = lc.get(getIdRelacion());
        long t = ordenImpl.totalOrdenesPorProveedor(cvo.getIdProveedor());
        return t <= 0;
    }

    public void completarEliminarRelProvCompania() {
        List<CompaniaVo> lc = (List<CompaniaVo>) getLista().getWrappedData();
        CompaniaVo cvo = lc.get(getIdRelacion());
        setLista(new ListDataModel(lc));
        proveedorCompaniaImpl.eliminarRel(usuarioBean.getUsuarioVO().getId(), cvo);
        setLista(new ListDataModel(proveedorCompaniaImpl.traerCompaniaPorProveedor(getIdProveedor())));
    }

    /////////////
    public List<SelectItem> listaCampo() {
        List<SelectItem> l = new ArrayList<SelectItem>();
        List<CampoUsuarioPuestoVo> lc;
        try {
            //lc = apCampoImpl.getAllField();
            lc = apCampoUsuarioRhPuestoImpl.getAllPorUsurio(usuarioBean.getUsuarioVO().getId());
            for (CampoUsuarioPuestoVo ca : lc) {
                SelectItem item = new SelectItem(ca.getIdCampo(), ca.getCampo());
                l.add(item);
            }
            return l;
        } catch (Exception e) {
            return null;
        }
    }

    public void traerProveedorCampo() {
        setLista(new ListDataModel(ocCampoProveedorLocal.traerProveedor(getIdCampo())));
    }

    public boolean estaProveedorCampo() {
        return ocCampoProveedorLocal.buscarProveedorCampo(getIdCampo(), getIdProveedor());
    }

    public void agregarProveedor() {
        ocCampoProveedorLocal.agregarProveedor(getIdCampo(), getIdProveedor(), usuarioBean.getUsuarioVO().getId());
    }

    public void eliminarProveedorCampo() {
        UtilLog4j.log.info(this, "Relacion : : :: " + getIdRelacion());
        ocCampoProveedorLocal.eliminarProveedorCampo(getIdRelacion(), usuarioBean.getUsuarioVO().getId());
    }
/////////////////////

    public void listaContactoPorPorveedor() {
        setListaContacto(new ListDataModel(contactoProveedorImpl.traerContactoPorProveedor(getIdProveedor(), Constantes.CONTACTO_REP_COMPRAS)));
    }

    public void eliminarContacto() {
        contactoProveedorImpl.eliminarContacto(getIdContacto(), usuarioBean.getUsuarioVO().getId());
    }

    public void completarModificarContacto() {
        List<ContactoProveedorVO> lc = (List<ContactoProveedorVO>) getListaContacto().getWrappedData();
        ContactoProveedorVO contactoProveedor = lc.get(getIdContacto());
        contactoProveedorImpl.actualizarContacto(contactoProveedor.getIdContactoProveedor(), contactoProveedor.getNombre(), contactoProveedor.getCorreo(), contactoProveedor.getTelefono(), Boolean.FALSE, usuarioBean.getUsuarioVO().getId());
        setListaContacto(new ListDataModel(contactoProveedorImpl.traerContactoPorProveedor(getIdProveedor(), Constantes.CONTACTO_REP_COMPRAS)));
    }

    public void agregarContacto() {
        if (getCorreo() != null && !getCorreo().isEmpty()) {
            String newCorreo = getCorreo().trim();
            int ascii = newCorreo.codePointAt(newCorreo.length() - 1);
            if (ascii < 65 || (ascii > 90 && ascii < 97) || ascii > 122) {
                setCorreo(newCorreo.substring(Constantes.CERO, newCorreo.length() - 1));
            }
        }
        contactoProveedorImpl.guardarContacto(getIdProveedor(), getContacto(), getTelefono(), getCorreo(), Constantes.CONTACTO_REP_COMPRAS, usuarioBean.getUsuarioVO().getId());
    }

    public void modificarDatosProveedor() {
        proveedorImpl.modificarDatos(getProveedorVo(), usuarioBean.getUsuarioVO().getId());
    }

    //
    public boolean enviarContraseña() {
       return proveedorImpl.notificaCambioPassword(proveedorVo.getIdProveedor());
    }

    /*
     *
     */
    /**
     * @return the rfc
     */
    public String getRfc() {
        return rfc;
    }

    /**
     * @param rfc the rfc to set
     */
    public void setRfc(String rfc) {
        this.rfc = rfc;
    }

    /**
     * @return the correo
     */
    public String getCorreo() {
        return correo;
    }

    /**
     * @param correo the correo to set
     */
    public void setCorreo(String correo) {
        this.correo = correo;
    }

    /**
     * @return the numeroReferencia
     */
    public String getNumeroReferencia() {
        return numeroReferencia;
    }

    /**
     * @param numeroReferencia the numeroReferencia to set
     */
    public void setNumeroReferencia(String numeroReferencia) {
        this.numeroReferencia = numeroReferencia;
    }

    /**
     * @return the rfcCompania
     */
    public String getRfcCompania() {
        return rfcCompania;
    }

    /**
     * @param rfcCompania the rfcCompania to set
     */
    public void setRfcCompania(String rfcCompania) {
        this.rfcCompania = rfcCompania;
    }

    /**
     * @param usuarioBean the usuarioBean to set
     */
    public void setUsuarioBean(UsuarioBean usuarioBean) {
        this.usuarioBean = usuarioBean;
    }

    /**
     * @return the filaSeleccionada
     */
    public Map<String, Boolean> getFilaSeleccionada() {
        return filaSeleccionada;
    }

    /**
     * @param filaSeleccionada the filaSeleccionada to set
     */
    public void setFilaSeleccionada(Map<String, Boolean> filaSeleccionada) {
        this.filaSeleccionada = filaSeleccionada;
    }

    /**
     * @return the idProveedor
     */
    public int getIdProveedor() {
        return idProveedor;
    }

    /**
     * @param idProveedor the idProveedor to set
     */
    public void setIdProveedor(int idProveedor) {
        this.idProveedor = idProveedor;
    }

    /**
     * @return the nombreProveedor
     */
    public String getNombreProveedor() {
        return nombreProveedor;
    }

    /**
     * @param nombreProveedor the nombreProveedor to set
     */
    public void setNombreProveedor(String nombreProveedor) {
        this.nombreProveedor = nombreProveedor;
    }

    /**
     * @return the idProveedorCompania
     */
    public int getIdRelacion() {
        return idRelacion;
    }

    /**
     * @param idRelacion the idProveedorCompania to set
     */
    public void setIdRelacion(int idRelacion) {
        this.idRelacion = idRelacion;
    }

    /**
     * @return the companiaVo
     */
    public CompaniaVo getCompaniaVo() {
        return companiaVo;
    }

    /**
     * @param companiaVo the companiaVo to set
     */
    public void setCompaniaVo(CompaniaVo companiaVo) {
        this.companiaVo = companiaVo;
    }

    /**
     * @return the lista
     */
    public DataModel getLista() {
        return lista;
    }

    /**
     * @param lista the lista to set
     */
    public void setLista(DataModel lista) {
        this.lista = lista;
    }

    /**
     * @return the idCampo
     */
    public int getIdCampo() {
        return idCampo;
    }

    /**
     * @param idCampo the idCampo to set
     */
    public void setIdCampo(int idCampo) {
        this.idCampo = idCampo;
    }

    /**
     * @return the listaContacto
     */
    public DataModel getListaContacto() {
        return listaContacto;
    }

    /**
     * @param listaContacto the listaContacto to set
     */
    public void setListaContacto(DataModel listaContacto) {
        this.listaContacto = listaContacto;
    }

    /**
     * @return the idContacto
     */
    public int getIdContacto() {
        return idContacto;
    }

    /**
     * @param idContacto the idContacto to set
     */
    public void setIdContacto(int idContacto) {
        this.idContacto = idContacto;
    }

    /**
     * @return the telefono
     */
    public String getTelefono() {
        return telefono;
    }

    /**
     * @param telefono the telefono to set
     */
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    /**
     * @return the contacto
     */
    public String getContacto() {
        return contacto;
    }

    /**
     * @param contacto the contacto to set
     */
    public void setContacto(String contacto) {
        this.contacto = contacto;
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
}
