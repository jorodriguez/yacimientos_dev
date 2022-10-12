/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author ihsa
 */
@Entity
@Table(name = "Si_Usuario_Codigo")
@SequenceGenerator(sequenceName = "si_usuario_codigo_id_seq", name = "si_usuario_codigo_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SiUsuarioCodigo.findAll", query = "SELECT u FROM SiUsuarioCodigo u"),
    @NamedQuery(name = "SiUsuarioCodigo.findByUsuario", query = "SELECT u FROM SiUsuarioCodigo u where u.usuario.id = ?1 and u.eliminado = 'False' and u.token <> 'null' and u.token is not null ")
})
@Getter
@Setter
public class SiUsuarioCodigo implements Serializable {

    @Id
    @Basic(optional = false)
    
    @Column(name = "ID")
@GeneratedValue(generator =  "si_usuario_codigo_seq", strategy = GenerationType.SEQUENCE)
    private Integer id;
    @JoinColumn(name = "USUARIO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario usuario;
    @Size(max = 256)
    @Column(name = "CODIGO")
    private String codigo;
    @Size(max = 512)
    @Column(name = "TOKEN")
    private String token;
    //
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
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
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;

    public SiUsuarioCodigo() {
    }

    public SiUsuarioCodigo(int id) {
	this.id = id;
    }

    
    public int hashCode() {
	int hash = 0;
	hash += (getId() != null ? getId().hashCode() : 0);
	return hash;
    }

    
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof SiUsuarioCodigo)) {
	    return false;
	}
	SiUsuarioCodigo other = (SiUsuarioCodigo) object;
	return !((this.getId() == null && other.getId() != null) || (this.getId() != null));
    }

    
    public String toString() {
	return "sia.modelo.Usuario[ id=" + id + " ]";
    }

}
