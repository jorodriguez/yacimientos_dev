/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lector.modelo;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 *
 */
@Entity
@Table(name = "USUARIO")
@SequenceGenerator(sequenceName = "usuario_id_seq", name = "usuario_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "Usuario.findById", query = "SELECT u FROM Usuario u where u.id = ?1 and  u.interno = true and u.eliminado = false and u.activo = true"),
    @NamedQuery(name = "Usuario.findByIdRH", query = "SELECT u FROM Usuario u where u.id = ?1"),
            @NamedQuery(name = "Usuario.findAll", query = "SELECT u FROM Usuario u where  u.interno = true and u.eliminado = false and u.activo = true")
})
@Getter
@Setter
public class Usuario implements Serializable {

    @Column(name = "FECHANACIMIENTO")
    @Temporal(TemporalType.DATE)
    private Date fechanacimiento;
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Column(name = "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;
    @Column(name = "FECHA_MODIFICO")
    @Temporal(TemporalType.DATE)
    private Date fechaModifico;
    @Column(name = "HORA_MODIFICO")
    @Temporal(TemporalType.TIME)
    private Date horaModifico;
    @Column(name = "FECHA_INGRESO")
    @Temporal(TemporalType.DATE)
    private Date fechaIngreso;
    @Column(name = "FECHA_BAJA")
    @Temporal(TemporalType.DATE)
    private Date fechaBaja;
    @Column(name = "HORA_BAJA")
    @Temporal(TemporalType.TIME)
    private Date horaBaja;
    
    @Column(name = "REQUIERE_CORREO")
    private boolean requiereCorreo;
//
    @Column(name = "MOTIVO_BAJA")
    private String motivoBaja;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    
    @Size(min = 1, max = 20)
    @Column(name = "ID")
    private String id;
    @Size(max = 128)
    @Column(name = "NOMBRE")
    private String nombre;
    @Size(max = 50)
    @Column(name = "CLAVE")
    private String clave;
    @Size(max = 120)
    @Column(name = "EMAIL")
    private String email;
    @Size(max = 300)
    @Column(name = "DESTINATARIOS")
    private String destinatarios;
    @Size(max = 25)
    @Column(name = "TELEFONO")
    private String telefono;
    @Size(max = 6)
    @Column(name = "EXTENSION")
    private String extension;
    @Size(max = 1)
    @Column(name = "SEXO")
    private String sexo;
    @Size(max = 25)
    @Column(name = "CELULAR")
    private String celular;
    @Size(max = 13)
    @Column(name = "RFC")
    private String rfc;
    @Size(max = 600)
    @Column(name = "FOTO")
    private String foto;
    
    @Size(max = 50)
    @Column(name = "PREGUNTA_SECRETA")
    private String preguntaSecreta;
    @Size(max = 50)
    @Column(name = "RESPUESTA_PREGUNTA_SECRETA")
    private String respuestaPreguntaSecreta;
    
    @Column(name = "ACTIVO")
    private boolean activo;
    
    @Column(name = "SEGURIDAD")
    private boolean seguridad;   
    
    @Column(name = "GAFETE")
    private boolean gafete;

    @OneToMany(mappedBy = "usuario")
    private Collection<SiUsuarioRol> siUsuarioRolCollection;
    //
    @Transient
    private Collection<SiRol> siRolCollection;
    
    @Column(name = "INTERNO")
    private boolean interno;

    @Column(name = "USUARIO_DIRECTORIO")
    private String usuarioDirectorio;
    
    @Column(name = "MONITOR_CML_VISIBLE")
    private boolean monitorCmlVisible;
    
    
    public Usuario() {
    }

    public Usuario(String sesion) {
	this.id = sesion;
    }

    
    public int hashCode() {
	int hash = 0;
	hash += (getId() != null ? getId().hashCode() : 0);
	return hash;
    }

    
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof Usuario)) {
	    return false;
	}
	Usuario other = (Usuario) object;
	if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
	    return false;
	}
	return true;
    }

    
    public String toString() {
	return "sia.modelo.Usuario[ id=" + id + " ]";
    }


}
