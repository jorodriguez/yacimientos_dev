/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package lector.modelo;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
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
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Getter;
import lombok.Setter;
import lector.constantes.Constantes;

/**
 *
 * @author hacosta
 */
@Entity
@Table(name = "SI_ROL")
@SequenceGenerator(sequenceName = "si_rol_id_seq", name = "si_rol_seq", allocationSize = 1)
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "SiRol.findAll", query = "SELECT s FROM SiRol s"),
    @NamedQuery(name = "SiRol.findByCode", query = "SELECT s FROM SiRol s where s.codigo = ?1 and s.eliminado = false")
})
@Setter
@Getter
public class SiRol implements Serializable {

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
    @JoinColumn(name = "MODIFICO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario modifico;
    @JoinColumn(name = "SI_MODULO", referencedColumnName = "ID")
    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Size(max = 25)
    @Column(name = "NOMBRE")
    private String nombre;
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "siRol")
    private Collection<SiUsuarioRol> siUsuarioRolCollection;
    @OneToMany(mappedBy = "siRol")
    private Collection<SiRelRolOpcion> siRelRolOpcionCollection;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne
    private Usuario genero;
    @Size(max = 25)
    @Column(name = "CODIGO")
    private String codigo;

    public SiRol() {
    }

    public SiRol(int id) {
	this.id = id;
    }

    
    public int hashCode() {
	int hash = 0;
	hash += (id != null ? id.hashCode() : 0);
	return hash;
    }

    
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof SiRol)) {
	    return false;
	}
	SiRol other = (SiRol) object;
	if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
	    return false;
	}
	return true;
    }

    
    public String toString() {
	StringBuilder sb = new StringBuilder();
	sb.append(getClass().getSimpleName());
	sb.append("{");
	sb.append("id=").append(id);
	sb.append(", nombre").append(nombre != null ? nombre : null);
	sb.append(", codigo").append(codigo != null ? codigo : null);
	sb.append(", genero=").append(this.genero != null ? this.genero.getId() : null);
	sb.append(", fechaGenero=").append(this.fechaGenero != null ? (Constantes.FMT_ddMMyyy.format(this.fechaGenero)) : null);
	sb.append(", horaGenero=").append(this.horaGenero != null ? (Constantes.FMT_HHmmss.format(this.horaGenero)) : null);
	sb.append(", modifico=").append(this.modifico != null ? this.modifico.getId() : null);
	sb.append(", fechaModifico=").append(this.fechaModifico != null ? (Constantes.FMT_ddMMyyy.format(this.fechaModifico)) : null);
	sb.append(", horaModifico=").append(this.horaModifico != null ? (Constantes.FMT_HHmmss.format(this.horaModifico)) : null);
	sb.append(", eliminado=").append(this.eliminado);
	sb.append("}");

	return sb.toString();
    }
}
