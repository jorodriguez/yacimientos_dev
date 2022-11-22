/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.sistema.bean.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import javax.inject.Named;
import javax.faces.model.SelectItem;
import sia.constantes.Constantes;
import sia.modelo.Proveedor;
import sia.modelo.SgInvitado;
import sia.modelo.Usuario;
import sia.modelo.usuario.vo.UsuarioVO;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.proveedor.impl.ProveedorServicioImpl;
import sia.servicios.sgl.impl.SgInvitadoImpl;
import sia.util.ProveedorEnum;
import sia.util.UtilLog4j;

/**
 *
 * @author mluis
 */
/**/
@Named(value = "soporteProveedor")

public class SoporteProveedor implements Serializable {

    @Inject
    private ProveedorServicioImpl proveedorServicioRemoto;
    @Inject
    private UsuarioImpl usuarioImpl;
    @Inject
    private SgInvitadoImpl sgInvitadoImpl;
    private List<SgInvitado> listInvitado;
    private List<SelectItem> selectItemUsuario;
    private List<Usuario> usuario;
    private List<Proveedor> proveedores;
    private List<SelectItem> selectItems;

    private List<UsuarioVO> listaUsuarioVo;

    //PANEL TOOL TIP
    public SoporteProveedor() {
    }

    public List<Proveedor> getProveedores() {
	if (this.proveedores == null) {
	    this.proveedores = this.proveedorServicioRemoto.findAll();
	    UtilLog4j.log.fatal(this, "Proveedores: " + this.proveedores.size());
	}
	UtilLog4j.log.fatal(this, "proveedor: " + proveedores.size() + " completa");
	return this.proveedores;
    }

    public List<SelectItem> getSelectItems() {
	if (this.selectItems == null) {
	    this.selectItems = new ArrayList<SelectItem>();
//            List<City> cities = getCities();
	    for (Proveedor pr : this.getProveedores()) {
		SelectItem selectItem = new SelectItem(pr, pr.getNombre());
		selectItems.add(selectItem);
	    }
	}
	return this.selectItems;
    }

    public void filterSelectItems(String proNameStartsWith) {
	this.selectItems = new ArrayList<SelectItem>();
	String proveedor;
	for (Proveedor pr : this.getProveedores()) {
	    boolean addPr = false;
	    if (proNameStartsWith == null) {
		addPr = true;
	    } else {
		proveedor = pr.getNombre();
		if (proveedor != null) {
		    if (proveedor.toLowerCase().startsWith(proNameStartsWith.toLowerCase())) {
			addPr = true;
		    }
		}
	    }
	    if (addPr) {
		SelectItem selectItem = new SelectItem(pr, pr.getNombre());
		selectItems.add(selectItem);
	    }
	}
    }

    public Proveedor getProByName(String proName) {
	String proveedor;
	for (Proveedor pr : this.getProveedores()) {
	    proveedor = pr.getNombre();
	    if (proveedor != null) {
		if (proveedor.equals(proName)) {
		    return pr;
		}
	    }

	}
	return null;
    }
    /// Invitado

    public List<SelectItem> regresaInvitado(String cadenaDigitada) {
	List<SelectItem> list = new ArrayList<SelectItem>();
	UtilLog4j.log.fatal(this, "as.");
	for (SgInvitado i : this.getInvitado()) {
	    if (i.getNombre() != null) {
		String cadenaPersona = i.getNombre().toLowerCase();
		cadenaDigitada = cadenaDigitada.toLowerCase();
		if (cadenaPersona.startsWith(cadenaDigitada)) {
		    SelectItem item = new SelectItem(i, i.getNombre());
		    list.add(item);
		}
	    }
	}
	return list;
    }

    public List<SgInvitado> getInvitado() {
	if (this.listInvitado == null) {
	    this.setListInvitado(this.sgInvitadoImpl.getAllInvitado(Constantes.NO_ELIMINADO));
	    UtilLog4j.log.fatal(this, "Lista invitados: " + listInvitado.size());
	}
	return listInvitado;
    }
    //
    // USUARIOS

    public List<SelectItem> regresaUsuario(String cadenaDigitada) {
	List<SelectItem> list = new ArrayList<SelectItem>();
	for (Usuario p : this.getUsuario()) {
	    if (p.getNombre() != null) {
		String cadenaPersona = p.getNombre().toLowerCase();
		cadenaDigitada = cadenaDigitada.toLowerCase();
		if (cadenaPersona.startsWith(cadenaDigitada)) {
		    SelectItem item = new SelectItem(p, p.getNombre());
		    list.add(item);
		}
	    }
	}
	return list;
    }

    public List<SelectItem> regresaUsuarioActivo(String cadenaDigitada, int idApCampo, String orderByField, boolean sortAscending, boolean activo, boolean eliminado) {
	List<SelectItem> list = new ArrayList<SelectItem>();
	for (Usuario p : this.getUsuarioActivo(idApCampo, orderByField, sortAscending, activo, eliminado)) {
	    if (p.getNombre() != null) {
		String cadenaPersona = p.getNombre().toLowerCase();
		cadenaDigitada = cadenaDigitada.toLowerCase();
		if (cadenaPersona.startsWith(cadenaDigitada)) {
		    SelectItem item = new SelectItem(p, p.getNombre());
		    list.add(item);
		}
	    }
	}
	return list;
    }

    public List<SelectItem> getSelectItemUsuario() {
	if (this.selectItemUsuario == null) {
	    this.selectItemUsuario = new ArrayList<SelectItem>();
//            List<City> cities = getCities();
	    for (Usuario user : this.getUsuario()) {
		SelectItem selectItem = new SelectItem(user, user.getNombre());
		selectItemUsuario.add(selectItem);
	    }
	}
	return this.selectItemUsuario;
    }

    public List<Usuario> getUsuario() {
	if (this.usuario == null) {
	    this.usuario = this.usuarioImpl.findAll();
	    UtilLog4j.log.fatal(this, "Usuario: " + this.usuario.size() + " completa");
	}
	UtilLog4j.log.fatal(this, "Usuario: " + this.usuario.size() + " despues de teclear");
	return this.usuario;
    }

    public List<Usuario> getUsuarioActivo(int idApCampo, String orderByField, boolean sortAscending, boolean activo, boolean eliminado) {
	if (this.usuario == null) {
//            this.usuario = this.usuarioServicioRemoto.getActivos();
	    this.usuario = this.usuarioImpl.findAll(idApCampo, orderByField, sortAscending, activo, eliminado);
	}
	UtilLog4j.log.fatal(this, "Usuario: " + this.usuario.size() + " completa");
	return this.usuario;
    }

    public String usuarioActivoJson() {
	return usuarioImpl.traerUsuarioActivoJson();
    }

    public Usuario getUsuarioByName(String userName) {
	String u = null;
	for (Usuario user : this.getUsuario()) {
	    u = user.getNombre();
	    if (u != null) {
		if (u.equals(userName)) {
		    return user;
		}
	    }
	}
	return null;
    }

//NUEVA FORMA
    public List<SelectItem> regresaProveedorActivo(String cadenaDigitada) {
	List<SelectItem> list = new ArrayList<SelectItem>();
	for (Proveedor p : this.getProveedores()) {
	    if (p.getNombre() != null) {
		String cadenaProveedor = p.getNombre().toLowerCase();
		cadenaDigitada = cadenaDigitada.toLowerCase();
		if (cadenaProveedor.startsWith(cadenaDigitada)) {
		    SelectItem item = new SelectItem(p, p.getNombre());
		    list.add(item);
		}
	    }
	}
	return list;
    }

    /**
     * @param listInvitado the listInvitado to set
     */
    public void setListInvitado(List<SgInvitado> listInvitado) {
	this.listInvitado = listInvitado;
    }
    //Auto completar proveedor
    private List<String> nombreProveedor;

    public List<SelectItem> regresaNombreProveedorActivo(String cadenaDigitada, String rfcCompania) {
	List<SelectItem> list = new ArrayList<SelectItem>();
	for (String p : this.getNombreProveedor(cadenaDigitada, rfcCompania)) {
	    if (p != null) {
		String cadenaPersona = p.toLowerCase();
		cadenaDigitada = cadenaDigitada.toLowerCase();
		if (cadenaPersona.startsWith(cadenaDigitada)) {
		    SelectItem item = new SelectItem(p);
		    list.add(item);
		}
	    }
	}
	return list;
    }

    private List<String> getNombreProveedor(String cadena, String rfcCompania) {
	if (nombreProveedor == null) {
	    setNombreProveedor(proveedorServicioRemoto.traerNombreProveedorQueryNativo(rfcCompania, ProveedorEnum.ACTIVO.getId()));
	    UtilLog4j.log.fatal(this, "proveedor: " + nombreProveedor.size() + " c y nc");
	} else {
	    setNombreProveedor(proveedorServicioRemoto.traerNombreLikeProveedorQueryNativo(cadena, rfcCompania, ProveedorEnum.ACTIVO.getId()));
	    UtilLog4j.log.fatal(this, "proveedor: " + nombreProveedor.size() + " con like");
	}
	return nombreProveedor;
    }

    /**
     * @param proveedor the proveedor to set
     */
    public void setNombreProveedor(List<String> nombreProveedor) {
	this.nombreProveedor = nombreProveedor;
    }

    //Lista u voç
    /**
     * @return the listaUsuarioVo
     */
    public List<UsuarioVO> getListaUsuarioVo() {
	if (listaUsuarioVo == null) {
	    UtilLog4j.log.fatal(this, "Aqui");
	    listaUsuarioVo = usuarioImpl.usuarioActio(-1);
	    UtilLog4j.log.fatal(this, "despues de aquí");
	}
	return listaUsuarioVo;
    }

    public List<SelectItem> regresaUsuarioActivoVO(String cadenaDigitada) {
	List<SelectItem> list = new ArrayList<SelectItem>();
	for (UsuarioVO p : getListaUsuarioVo()) {
	    if (p.getNombre() != null) {
		String cadenaPersona = p.getNombre().toLowerCase();
		cadenaDigitada = cadenaDigitada.toLowerCase();
		if (cadenaPersona.contains(cadenaDigitada)) {
		    SelectItem item = new SelectItem(p, p.getNombre());
		    list.add(item);
		}
	    }
	}
	return list;
    }

}
