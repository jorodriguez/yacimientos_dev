/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sia.modelo;

import java.io.Serializable;
import java.text.SimpleDateFormat;
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
 * @author b75ckd35th
 */
@Entity
@Table(name = "SG_CARACTERISTICA")
@SequenceGenerator(sequenceName = "sg_caracteristica_id_seq", name = "sg_caracteristica_seq", allocationSize = 1)
@NamedQueries({
    @NamedQuery(name = "SgCaracteristica.findAll", query = "SELECT s FROM SgCaracteristica s")})
@Setter
@Getter
public class SgCaracteristica implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
@GeneratedValue(generator =  "sg_caracteristica_seq", strategy = GenerationType.SEQUENCE)
    @Basic(optional = false)
    
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 128)
    @Column(name = "NOMBRE")
    private String nombre;
    
    @Column(name = "PRINCIPAL")
    private boolean principal;
    @Basic(optional = false)
    @NotNull
    @Column(name = "FECHA_GENERO")
    @Temporal(TemporalType.DATE)
    private Date fechaGenero;
    @Basic(optional = false)
    @NotNull
    @Column(name = "HORA_GENERO")
    @Temporal(TemporalType.TIME)
    private Date horaGenero;
    @JoinColumn(name = "SG_TIPO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private SgTipo sgTipo;
    @Basic(optional = false)
    @NotNull
    
    @Column(name = "ELIMINADO")
    private boolean eliminado;
    @JoinColumn(name = "GENERO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Usuario genero;

    public SgCaracteristica() {
    }

    public SgCaracteristica(int id) {
	this.id = id;
    }

    
    public int hashCode() {
	int hash = 0;
	hash += (getId() != null ? getId().hashCode() : 0);
	return hash;
    }

    
    public boolean equals(Object object) {
	// TODO: Warning - this method won't work in the case the id fields are not set
	if (!(object instanceof SgCaracteristica)) {
	    return false;
	}
	SgCaracteristica other = (SgCaracteristica) object;
	if ((this.getId() == null && other.getId() != null) || (this.getId() != null && !this.id.equals(other.id))) {
	    return false;
	}
	return true;
    }

    
    public String toString() {
	SimpleDateFormat sdfd = new SimpleDateFormat("dd/MM/yyyy");
	SimpleDateFormat sdft = new SimpleDateFormat("HH:mm:ss");
	StringBuilder sb = new StringBuilder();
	sb.append(this.getClass().getName())
		.append("{")
		.append("id=").append(this.getId())
		.append(", nombre=").append(this.nombre)
		.append(", principal=").append(this.principal)
		.append(", sgTipo=").append(this.sgTipo != null ? this.sgTipo.getId() : null)
		.append(", genero=").append(this.getGenero() != null ? this.getGenero().getId() : null)
		.append(", fechaGenero=").append(this.getFechaGenero() != null ? (sdfd.format(this.getFechaGenero())) : null)
		.append(", horaGenero=").append(this.getHoraGenero() != null ? (sdft.format(this.getHoraGenero())) : null)
		.append(", eliminado=").append(this.isEliminado())
		.append("}");

	return sb.toString();
    }
}
