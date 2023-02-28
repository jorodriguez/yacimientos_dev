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
    private String domicilio;    
    private String clave;
    private String claveElector;
    private String curp;
    private String estado;
    private String municipio;
    private String localidad;
    private String seccion;
    private Integer anioEmision;
    private int vigencia;
    private Date fechaNacimiento;    
    private String sexo;
    
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
    private String rolPrincipal;
    private int rolId;
    private int cCuenta;    
    private int cTipoContacto;    
    
    private Map<Integer, String> mapaRol = new HashMap<>();
    private List<UsuarioRolVo> roles = new ArrayList<>();    
        
    public UsuarioVO() {
    }

    @Builder
    public UsuarioVO(Integer id, String nombre, String domicilio, String clave, String claveElector, String curp, String estado, String municipio, String localidad, String seccion, Integer anioEmision, int vigencia, Date fechaNacimiento, String sexo, String email, String destinatarios, String telefono, String extension, String urlImagen, String pregunta, String respuesta, String celular, String foto, boolean activo, Integer genero, String rolPrincipal, int rolId, int cCuenta, int cTipoContacto) {
        this.id = id;
        this.nombre = nombre;
        this.domicilio = domicilio;
        this.clave = clave;
        this.claveElector = claveElector;
        this.curp = curp;
        this.estado = estado;
        this.municipio = municipio;
        this.localidad = localidad;
        this.seccion = seccion;
        this.anioEmision = anioEmision;
        this.vigencia = vigencia;
        this.fechaNacimiento = fechaNacimiento;
        this.sexo = sexo;
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
        this.rolPrincipal = rolPrincipal;
        this.rolId = rolId;
        this.cCuenta = cCuenta;
        this.cTipoContacto = cTipoContacto;
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
