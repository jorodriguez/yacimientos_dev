package lector.dominio.modelo.usuario.vo;

import java.io.Serializable;
import java.util.*;
import lector.dominio.vo.UsuarioRolVo;
import lombok.Getter;
import lombok.Setter;


/**
 *
 * @author mluis
 */
@Getter
@Setter
public class UsuarioVO implements Serializable {

    private String id;
    private String nombre;
    private String clave;
    private String puesto;
    private String pregunta;
    private String respuesta;
    private String mail;
    private String destinatarios;
    private String telefono;
    private String extension;
    private String rfc;
    private String campo;
    private String urlImagen;
    private String sexo;
    private Date fechaNacimiento;
    private boolean selected;
    private boolean liberaUsuarios;
    private int idCampo;
    private int idPuesto;
    private String celular;
    private String gerencia;
    private int idGerencia;
    private String foto;
    private boolean activo;
    private String genero;
    //
    private int idOficina;
    private String oficina;
    private int idNomina;
    private String idJefe;
    private String nombreJefe;
    private Date fechaIngreso;
    private String gafete;
    private boolean usuarioInSessionGerente;
    //para privilegios que corresponden  a ti
    private boolean administraTI;
    private boolean flag;
    private boolean requiereConfiguracionCorreo;
    private String rolPrincipal;
    private int rolId;
    private String rfcEmpresa;    
    private Map<Integer, String> mapaRol = new HashMap<>();
    private List<UsuarioRolVo> roles = new ArrayList<>();    
    private boolean interno;
    
    public UsuarioVO() {
    }

    public UsuarioVO(String id) {
	this.id = id;
    }

    
    public final boolean equals(Object obj) {
	if (obj == null) {
	    return false;
	}
	if (getClass() != obj.getClass()) {
	    return false;
	}
	final UsuarioVO other = (UsuarioVO) obj;
	if ((this.id == null) ? (other.id != null) : !this.id.equals(other.id)) {
	    return false;
	}
	return true;
    }

    
    public final int hashCode() {
	int hash = 7;
	hash = 71 * hash + (this.id != null ? this.id.hashCode() : 0);
	return hash;
    }

    public UsuarioVO(String id, String nombre, String email, List<UsuarioRolVo> roles) {
	this.id = id;
	this.nombre = nombre;
	this.mail = email;
	this.roles = roles;
    }

    public UsuarioVO(String id, String nombre, String email) {
	this.id = id;
	this.nombre = nombre;
	this.mail = email;
    }
}
