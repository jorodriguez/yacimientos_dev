package mx.ihsa.dominio.modelo.usuario.vo;

import java.io.Serializable;
import java.util.*;
import mx.ihsa.dominio.vo.UsuarioRolVo;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


/**
 *
 * @author mluis
 */
@Getter @Setter
public class UsuarioVO implements Serializable {

    private Integer id;
    private String nombre;
    private String domicilio;    
    private String clave;    
    
    private String email;
    private String destinatarios;
    private String telefono;
    private String extension;    
    private String urlImagen;
    
    private String pregunta;
    private String respuesta;
    
    private String celular;
    private String foto;
    private boolean activo;
    private Integer genero;
    private Integer registro;
    private String rolPrincipal;
    private int rolId;
    
    
    
    private boolean conFoto;
    
    private Map<Integer, String> mapaRol = new HashMap<>();
    private List<UsuarioRolVo> roles = new ArrayList<>();    
        
    public UsuarioVO() {
    }

    @Builder
    public UsuarioVO(Integer id, String nombre, String domicilio, String clave, String email, String destinatarios, String telefono, String extension, String urlImagen, String pregunta, String respuesta, String celular, String foto, boolean activo, Integer genero, Integer registro, String rolPrincipal, int rolId, boolean conFoto) {
        this.id = id;
        this.nombre = nombre;
        this.domicilio = domicilio;
        this.clave = clave;
        this.email = email;
        this.destinatarios = destinatarios;
        this.telefono = telefono;
        this.extension = extension;
        this.urlImagen = urlImagen;
        this.pregunta = pregunta;
        this.respuesta = respuesta;
        this.celular = celular;
        this.foto = foto;
        this.activo = activo;
        this.genero = genero;
        this.registro = registro;
        this.rolPrincipal = rolPrincipal;
        this.rolId = rolId;
        this.conFoto = conFoto;
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
