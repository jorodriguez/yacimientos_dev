/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.sgl.sistema.bean.model;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import javax.enterprise.context.SessionScoped;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.inject.Named;
import sia.modelo.Usuario;
import sia.servicios.catalogos.impl.UsuarioImpl;
import sia.servicios.sgl.impl.SgOficinaAnalistaImpl;

/**
 *
 * @author hacosta
 */
@Named
@SessionScoped
public class UsuarioListModel implements Serializable {

    @Inject
    private UsuarioImpl servicioUsuario;
    @Inject
    private SgOficinaAnalistaImpl sgOficinaAnalistaImpl;
    /**
     * Creates a new instance of UsuarioListModel
     */
    private DataModel lista;

    public UsuarioListModel() {
    }

    public Usuario buscarPorId(String idUsuario) {
	return this.servicioUsuario.find(idUsuario);
    }

    public Usuario buscarPorNombre(Object nombreUsuario) {
	return this.servicioUsuario.buscarPorNombre(nombreUsuario);
    }

    public List<Usuario> getUsuariosActivos() {
	return this.servicioUsuario.getActivos();
    }

    public boolean enviarClave(Usuario usuario) {
	return this.servicioUsuario.enviarClave(usuario);
    }

    public String traerIdUsuario(String id) {
	try {
	    return this.servicioUsuario.find(id).getId();
	} catch (Exception e) {
	    return null;
	}
    }

    public boolean modificarUsuario(Usuario usuario) {
	boolean v = true;
	try {
	    this.servicioUsuario.edit(usuario);
	    v = true;
	} catch (Exception e) {
	    v = false;
	}
	return v;
    }

    public void modificaUsuarioDatosSinClave(Usuario usuario) {
	this.servicioUsuario.edit(usuario);
    }

    //Validaciones
    public boolean validaMail(String correo) {
	String[] mails = correo.split(",");
	boolean v = false;
	for (String string : mails) {
	    if (this.mail(string.trim())) {
		v = true;
	    } else {
		v = false;
		break;
	    }
	}
	return v;
    }
    //metodo para validar correo electronio

    public boolean mail(String correo) {
	boolean v = false;
	Pattern pat = null;
	Matcher mat = null;
	//pat = Pattern.compile("^([0-9a-zA-Z]([_.w]*[0-9a-zA-Z])*@([0-9a-zA-Z][-w]*[0-9a-zA-Z].)+([a-zA-Z]{2,9}.)+[a-zA-Z]{2,3})$");
	pat = Pattern.compile("^[\\w-\\.]+\\@[\\w\\.-]+\\.[a-z]{2,4}$");
	mat = pat.matcher(correo);
	if (mat.find()) {
	    v = true;
	}
	return v;
    }

    public void traerOficinaPorAnalista(Usuario usuario) throws Exception {
	setLista(new ListDataModel(sgOficinaAnalistaImpl.getOficinasByAnalistaAndStatus(usuario, false)));
    }

    public void cambioPassTodosUsuarios() throws NoSuchAlgorithmException {
	servicioUsuario.cambioPassTodosUsuarios();
    }

    /**
     * @return the lista
     */
    public DataModel getLista() {
	return lista;
    }

    /**
     * idRuta
     *
     * @param lista the lista to set
     */
    public void setLista(DataModel lista) {
	this.lista = lista;
    }
}
