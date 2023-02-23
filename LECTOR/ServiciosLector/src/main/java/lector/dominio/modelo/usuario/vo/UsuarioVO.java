package lector.dominio.modelo.usuario.vo;

import java.io.Serializable;
import java.util.*;
import lector.dominio.vo.UsuarioRolVo;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


/**
 *
 * @author mluis
 */
@Getter
@Setter
public class UsuarioVO implements Serializable {

    private Integer id;
    private String nombre;
    private String clave;
    private String puesto;
    private String pregunta;
    private String respuesta;
    private String email;
    private String destinatarios;
    private String telefono;
    private String extension;
    private String rfc;
    private String campo;
    private String urlImagen;
    private String sexo;
    private Date fechaNacimiento;    
    private String celular;
    private String foto;
    private boolean activo;
    private String genero;
    private String rolPrincipal;
    private int rolId;
    private int cCuenta;    
    private Map<Integer, String> mapaRol = new HashMap<>();
    private List<UsuarioRolVo> roles = new ArrayList<>();    
        
    public UsuarioVO() {
    }

    @Builder
    public UsuarioVO(Integer id, String nombre, String clave, String puesto, String pregunta, String respuesta, String email, String destinatarios, String telefono, String extension, String rfc, String campo, String urlImagen, String sexo, Date fechaNacimiento, String celular, String foto, boolean activo, String genero, String rolPrincipal, int rolId, int cCuenta) {
        this.id = id;
        this.nombre = nombre;
        this.clave = clave;
        this.puesto = puesto;
        this.pregunta = pregunta;
        this.respuesta = respuesta;
        this.email = email;
        this.destinatarios = destinatarios;
        this.telefono = telefono;
        this.extension = extension;
        this.rfc = rfc;
        this.campo = campo;
        this.urlImagen = urlImagen;
        this.sexo = sexo;
        this.fechaNacimiento = fechaNacimiento;
        this.celular = celular;
        this.foto = foto;
        this.activo = activo;
        this.genero = genero;
        this.rolPrincipal = rolPrincipal;
        this.rolId = rolId;
        this.cCuenta = cCuenta;
    }

    
    
   
    
    

    public UsuarioVO(Integer id) {
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

    public UsuarioVO(Integer id, String nombre, String email, List<UsuarioRolVo> roles) {
	this.id = id;
	this.nombre = nombre;
	this.email = email;
	this.roles = roles;
    }

    public UsuarioVO(Integer id, String nombre, String email) {
	this.id = id;
	this.nombre = nombre;
	this.email = email;
    }
}
